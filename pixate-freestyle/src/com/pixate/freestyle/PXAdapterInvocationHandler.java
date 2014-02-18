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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.pixate.freestyle.util.ViewUtil;

/**
 * An adapter {@link InvocationHandler} that calls
 * {@link java.lang.reflect.Proxy#newProxyInstance(ClassLoader, Class[], java.lang.reflect.InvocationHandler)}
 * in order to allow us to intercept the getView calls for adapters. The
 * {@link Adapter#getView(int, View, ViewGroup)} is called when a {@link View}
 * is being recycled, so we re-apply the styling at that point.
 * 
 * @author Shalom Gibly
 */
public class PXAdapterInvocationHandler implements java.lang.reflect.InvocationHandler {

    // The methods we would like to intercept.
    private static Set<String> interceptedMethods = new HashSet<String>(Arrays.asList("getView",
            "getDropDownView"));
    private Adapter adapter;
    private WeakReference<AdapterView<? extends Adapter>> viewRef;

    /**
     * Creates a new proxied instance of the given adapter.
     * 
     * @param obj An {@link AdapterView} instance. The proxy will be made for
     *            its {@link Adapter}.
     * @param adapterInterfaces The interfaces that will be implemented on the
     *            fly by the proxy
     * @return A new proxy for the {@link Adapter}
     */
    public static Object newInstance(AdapterView<Adapter> adapterView, Class<?>[] adapterInterfaces) {
        Adapter adapter = adapterView.getAdapter();
        return java.lang.reflect.Proxy.newProxyInstance(adapter.getClass().getClassLoader(),
                adapterInterfaces, new PXAdapterInvocationHandler(adapterView));
    }

    private PXAdapterInvocationHandler(AdapterView<Adapter> adapterView) {
        this.adapter = adapterView.getAdapter();
        this.viewRef = new WeakReference<AdapterView<? extends Adapter>>(adapterView);
    }

    /**
     * Returns the original {@link Adapter} that is being proxied.
     * 
     * @return The original {@link Adapter} instance.
     */
    public Adapter getOriginal() {
        return adapter;
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        try {
            result = m.invoke(adapter, args);
            if (interceptedMethods.contains(m.getName()) && result instanceof View) {
                // Unfortunately, at this point this view is not yet
                // connected to its parent, so we can't style it yet.
                // We tag the view with its location in the adapter and the
                // current child count. We'll try use those values in cases
                // where we can't locate a parent.
                View view = (View) result;
                view.setTag(ViewUtil.TAG_ELEMENT_INDEX, args[0]);
                view.setTag(ViewUtil.TAG_ELEMENTS_COUNT, adapter.getCount());
                if (viewRef.get() != args[2]) {
                    // change the view-reference to what we get here (this will
                    // happen when dealing with Spinners)
                    @SuppressWarnings("unchecked")
                    AdapterView<? extends Adapter> newAdapterView = (AdapterView<? extends Adapter>) args[2];
                    if (viewRef.get() instanceof Spinner) {
                        // Set the current spinner reference as a 'future
                        // parent'. We may need that later for size
                        // computations.
                        newAdapterView.setTag(ViewUtil.TAG_ELEMENT_FUTURE_PARENT, viewRef);

                        // Make a call to style the 'new' adapter view. This is
                        // the ListView that appears when the Spinner is
                        // clicked. We avoid styling the children of that
                        // adapter here. Those will be taken care later.
                        PixateFreestyle.style(newAdapterView, false);
                    }
                    viewRef = new WeakReference<AdapterView<? extends Adapter>>(newAdapterView);
                }
                view.setTag(ViewUtil.TAG_ELEMENT_FUTURE_PARENT, viewRef);
                // Call to style
                PixateFreestyle.style(view);
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        }
        return result;
    }
}
