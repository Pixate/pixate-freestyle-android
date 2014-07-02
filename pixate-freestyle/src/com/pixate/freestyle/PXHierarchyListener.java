/*******************************************************************************
 * Copyright 2012-present Pixate, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pixate.freestyle;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.WrapperListAdapter;

import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.ViewUtil;

public class PXHierarchyListener implements OnHierarchyChangeListener {

    private static final String TAG = PXHierarchyListener.class.getSimpleName();
    private static String NO_SAVE_STATE_FRAME_LAYOUT = "NoSaveStateFrameLayout";
    private static Field sHierarchyListenerField = null;

    private OnHierarchyChangeListener mExistingListener = null;
    private static Map<View, Boolean> viewsWithGlobalLayoutListeners = new WeakHashMap<View, Boolean>();

    public PXHierarchyListener(OnHierarchyChangeListener existingListener) {
        mExistingListener = existingListener;
    }

    public void onChildViewAdded(View parent, View child) {
        addGlobalLayoutListener(child);
        if (child instanceof ViewGroup) {
            // Catches when a ViewGroup type of View is added
            // after the Activity's initial init() is done. In that
            // case, this child won't have our hierarchy listener
            // set on it, and we need that.
            ViewUtil.prepareViewGroupListeners((ViewGroup) child);
        }

        // In case this view was never tagged (can happen with Fragments),
        // tag it.
        if (!ViewUtil.isTagged(child)) {
            ViewUtil.initTags(child);
        }
        if (NO_SAVE_STATE_FRAME_LAYOUT.equals(child.getClass().getSimpleName())) {
            // This class is coming from the Android support library. We don't
            // want to create a dependency on this library, so we check by name.
            // We need to traverse the children for any AdapterViews that need
            // to be applied with our proxy.
            LinkedList<View> views = new LinkedList<View>();
            views.add(child);
            traverseFragmentChildren(views);
        }

        if (mExistingListener != null) {
            mExistingListener.onChildViewAdded(parent, child);
        }
    }

    public void onChildViewRemoved(View parent, View child) {
        if (mExistingListener != null) {
            mExistingListener.onChildViewRemoved(parent, child);
        }
    }

    public static void setFor(ViewGroup group) {
        if (group == null) {
            return;
        }

        OnHierarchyChangeListener currentListener = getCurrentListener(group);
        if (currentListener instanceof PXHierarchyListener) {
            // We've already added ours to it, so nothing more to do.
            return;
        }

        // RadioGroup check to avoid StackOverflow issues. The RadioGroup uses a
        // similar delegation technique that trigger a StackOverflow, so we
        // should not pass the currentListener to it.
        if (!(group instanceof RadioGroup)) {
            group.setOnHierarchyChangeListener(new PXHierarchyListener(currentListener));
        } else {
            group.setOnHierarchyChangeListener(new PXHierarchyListener(null));
        }
        addGlobalLayoutListener(group);
    }

    private static void initReflection() {
        try {
            sHierarchyListenerField = ViewGroup.class
                    .getDeclaredField("mOnHierarchyChangeListener");
            sHierarchyListenerField.setAccessible(true);
        } catch (Exception e) {
            sHierarchyListenerField = null;
        }
    }

    public static OnHierarchyChangeListener getCurrentListener(ViewGroup viewGroup) {
        if (sHierarchyListenerField == null) {
            initReflection();
        }

        if (sHierarchyListenerField == null) {
            PXLog.w(TAG, "Unable to fetch the ViewGroup's current hierarchy listener. "
                    + "Reflection failed.");
            return null;
        }

        Object listener;

        try {
            listener = sHierarchyListenerField.get(viewGroup);
        } catch (Exception e) {
            PXLog.e(TAG, "Unable to fetch the ViewGroup's current hierarchy listener. "
                    + "Reflection failed.");
            return null;
        }

        if (listener == null || listener instanceof OnHierarchyChangeListener) {
            return (OnHierarchyChangeListener) listener;
        } else {
            PXLog.w(TAG, "A hierarchy listener was found, but its type is unexpected: %s", listener
                    .getClass().getName());
            return null;
        }

    }

    /**
     * Just used for debug purposes. TODO delete.
     * 
     * @param viewGroup
     */
    public static void logCurrentListener(ViewGroup viewGroup) {

        OnHierarchyChangeListener currentListener = getCurrentListener(viewGroup);

        if (currentListener == null) {
            PXLog.d(TAG, "%s has no hierarchy listener.", viewGroup.getClass().getSimpleName());
        } else {
            PXLog.d(TAG, "%s has hierarchy listener {1}", viewGroup.getClass().getSimpleName(),
                    currentListener.getClass().getSimpleName());
        }
    }

    /*
     * Adds a global layout listener for the given view hierarchy. The listener
     * will be removed when the onGlobalLayout is called, and then we'll call
     * the Pixate.style(view) after the layout set the bounds and we are good to
     * go.
     */

    private static synchronized void addGlobalLayoutListener(final View view) {
        // Only once
        if (viewsWithGlobalLayoutListeners.containsKey(view)) {
            return;
        }
        // If an ancestor has one, don't need to add another.
        // We use the layout listener to kick off styling, and
        // when the ancestor styling gets kicked off, this view
        // will be styled as well -- no need to kick it off again.
        // An exception is AdapterView and its subclasses, because
        // we really need to know when the layout pass has occurred
        // in order to successfully finish setting them up for styling.
        if (!(view instanceof AdapterView) && ancestorHasGlobalLayoutListener(view)) {
            return;
        }
        viewsWithGlobalLayoutListeners.put(view, true);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new PXLayoutListener(view));
    }

    private static boolean ancestorHasGlobalLayoutListener(final View view) {
        ViewParent parent = view.getParent();

        while (parent instanceof View) {
            View parentView = (View) parent;
            if (viewsWithGlobalLayoutListeners.containsKey(parentView)) {
                return true;
            }
            parent = parentView.getParent();
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes" })
    private void traverseFragmentChildren(Queue<View> views) {
        while (!views.isEmpty()) {
            View view = views.poll();
            if (view instanceof AdapterView) {
                setAdapterProxy((AdapterView) view);
            }
            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    views.offer(group.getChildAt(i));
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void setAdapterProxy(final AdapterView adapterView) {
        Adapter adapter = adapterView.getAdapter();
        if (adapter == null || Proxy.isProxyClass(adapter.getClass())) {
            // Thou shalt not Proxy a Proxy!
            return;
        }
        if (adapterView instanceof ExpandableListView) {
            // FIXME - Right now, skip the support for ExpandableListView.
            // This class throws exceptions on setAdapter(Adapter), and requires
            // a special adapter that only works with it.... Lame API break!
            return;
        }
        // Collect the Adapter sub-interfaces that we
        // would like to proxy.
        List<Class<?>> interfaces = new ArrayList<Class<?>>(4);
        interfaces.add(Adapter.class);
        if (adapter instanceof ListAdapter) {
            interfaces.add(ListAdapter.class);
        }
        if (adapter instanceof WrapperListAdapter) {
            interfaces.add(WrapperListAdapter.class);
        }
        if (adapter instanceof SpinnerAdapter) {
            interfaces.add(SpinnerAdapter.class);
        }

        // Create a proxy for the adapter to intercept
        // the 'getView'
        Adapter newAdapter = (Adapter) PXAdapterInvocationHandler.newInstance(adapterView,
                interfaces.toArray(new Class<?>[interfaces.size()]));

        // Set the proxy as the adapter
        adapterView.setAdapter(newAdapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void removeGlobalLayoutListenerJB(ViewTreeObserver observer,
            OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }

    private static class PXLayoutListener implements OnGlobalLayoutListener {

        private WeakReference<View> viewRef;

        private PXLayoutListener(View view) {
            this.viewRef = new WeakReference<View>(view);
        }

        @SuppressWarnings({ "deprecation", "rawtypes" })
        @Override
        public void onGlobalLayout() {
            View view = viewRef.get();
            if (view == null) {
                return;
            }
            // Remove the listener.
            ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    removeGlobalLayoutListenerJB(viewTreeObserver, this);
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                }
                viewsWithGlobalLayoutListeners.remove(view);
            }

            // Do the styling. The layout already set the bounds for
            // the view.
            PixateFreestyle.style(view);

            // For views with adapters, like lists, we create a
            // proxy that intercept the View recycling and style the
            // view after it's being recycled.
            if (view instanceof AdapterView) {
                synchronized (view) {
                    setAdapterProxy((AdapterView) view);
                }
            }
        }

    }
}
