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
package com.pixate.freestyle.styling.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXAnimationStyler;
import com.pixate.freestyle.styling.stylers.PXBorderStyler;
import com.pixate.freestyle.styling.stylers.PXBoxShadowStyler;
import com.pixate.freestyle.styling.stylers.PXFadingEdgeStyler;
import com.pixate.freestyle.styling.stylers.PXFillStyler;
import com.pixate.freestyle.styling.stylers.PXLayoutStyler;
import com.pixate.freestyle.styling.stylers.PXOpacityStyler;
import com.pixate.freestyle.styling.stylers.PXShapeStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.stylers.PXTransformStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext.FadingEdgeStyle;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.ViewUtil;

@PXDocElement(hide=true)
public class PXViewStyleAdapter extends PXStyleAdapter {

    private static PXViewStyleAdapter sInstance;

    protected PXViewStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#createStylers()
     */
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = new ArrayList<PXStyler>();
        stylers.add(PXTransformStyler.getInstance());
        stylers.add(PXLayoutStyler.getInstance());
        stylers.add(PXOpacityStyler.getInstance());
        stylers.add(PXShapeStyler.getInstance());
        stylers.add(PXFillStyler.getInstance());
        stylers.add(PXBorderStyler.getInstance());
        stylers.add(PXBoxShadowStyler.getInstance());
        stylers.add(PXAnimationStyler.getInstance());
        stylers.add(PXFadingEdgeStyler.getInstance());
        return stylers;
    }

    /**
     * Base implementation. Returns the lower-case name of the view.
     * 
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementName(java
     *      .lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        View view = (View) object;
        String result = null;

        if (view != null) {
            result = ViewUtil.getElementName(view);
            if (result != null) {
                result = result.toLowerCase(Locale.US);
            }
        }

        return result;
    }

    @Override
    public String getStyleId(Object object) {
        View view = (View) object;
        String result = null;

        if (view != null) {
            result = ViewUtil.getStyleId(view);
        }

        // That view was never tagged, so try to resolve the ID.
        if (result == null && !ViewUtil.isTagged(view)) {
            ViewUtil.initTags(view);
            result = ViewUtil.getStyleId(view);
        }
        return result;
    }

    @Override
    public String getStyleClass(Object object) {
        View view = (View) object;
        String result = null;

        if (view != null) {
            result = ViewUtil.getStyleClass(view);
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getParent(Object styleable) {
        View view = (View) styleable;
        ViewParent parent = view.getParent();
        // In case the parent is null, we may be dealing with a recycled element
        // that will be attached to a future parent (like when dealing with
        // ListViews). At this point, we check if there is a live weak reference
        // to such a parent.
        if (parent == null) {
            WeakReference<ViewParent> futureParent = (WeakReference<ViewParent>) view
                    .getTag(ViewUtil.TAG_ELEMENT_FUTURE_PARENT);
            if (futureParent != null) {
                parent = futureParent.get();
            }
        }
        return parent;
    }

    @Override
    public int getIndexInParent(Object styleable) {
        View view = (View) styleable;
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            return ((ViewGroup) parent).indexOfChild(view);
        }
        Integer taggedIndex = (Integer) view.getTag(ViewUtil.TAG_ELEMENT_INDEX);
        if (taggedIndex != null) {
            return taggedIndex.intValue();
        }
        return -1;
    }

    @Override
    public Object getSiblingAt(Object styleable, int offset) {
        View view = (View) styleable;
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            // We don't check the index validity here. The API specify that
            // the call returns the view at the specified position or null
            // if the position does not exist within the group
            return viewGroup.getChildAt(viewGroup.indexOfChild(view) + offset);
        }
        return null;
    }

    @Override
    public int getChildCount(Object element) {
        if (!(element instanceof ViewGroup)) {
            return 0;
        }
        return ((ViewGroup) element).getChildCount();
    }

    @Override
    public int getSiblingsCount(Object element) {
        View view = (View) element;
        ViewParent parent = view.getParent();
        if (parent != null) {
            return getChildCount(parent);
        }
        // the parent is null, try to get the value from the tag
        Integer taggedCount = (Integer) view.getTag(ViewUtil.TAG_ELEMENTS_COUNT);
        if (taggedCount != null) {
            return taggedCount.intValue();
        }
        return super.getSiblingsCount(element);
    }

    /*
     * Add the actual children of this styleable to the super's result.
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementChildren
     * (java.lang.Object)
     */
    @Override
    public List<Object> getElementChildren(Object styleable) {
        List<Object> children = super.getElementChildren(styleable);
        if (!(styleable instanceof ViewGroup)) {
            return children;
        }
        ViewGroup viewGroup = (ViewGroup) styleable;
        List<Object> result = new ArrayList<Object>(children.size() + viewGroup.getChildCount());
        result.addAll(children);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child != null) {
                result.add(child);
            }
        }

        return result;
    }

    @Override
    public RectF getBounds(Object styleable) {
        View view = (View) styleable;
        Rect r = new Rect();
        view.getDrawingRect(r);
        return new RectF(r);
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }

        PXStylerContext context = contexts.get(0);
        // Grab the first view. In case we are dealing with state-list
        // drawables, this view will be the same for all contexts that we get
        // here.
        View view = (View) context.getStyleable();

        // Grab the existing states, in case the background is a
        // StatesListDrawable
        Map<int[], Drawable> existingStates = PXDrawableUtil
                .getExistingStates(view.getBackground());

        if (existingStates == null || existingStates.isEmpty()) {
            updateWithNewStates(ruleSets, contexts);
            return true;
        }

        PXDrawableUtil.setBackgroundDrawable(view,
                PXDrawableUtil.createDrawable(this, existingStates, ruleSets, contexts));
        updateFadingEdgeStyle(context, view);
        return true;
    }

    /**
     * This method is called when the view's background was null, or did not
     * have any states assigned. The call will directly update the views in the
     * context list with the background image generated by each of the contexts.
     * A new StateListDrawable that will be applied as the View's background.
     * Subclasses may overwrite.
     * 
     * @param ruleSets
     * @param contexts
     */
    protected void updateWithNewStates(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // We got to this method because the view did not have a stateful
        // drawable, so in case we have only one context (e.g. one state), we
        // keep the drawable simple. In case there are multiple contexts, we
        // create and assign a new StateListDrawable.
        Drawable drawable = PXDrawableUtil.createNewDrawable(this, ruleSets, contexts);
        if (drawable != null) {
            PXDrawableUtil.setBackgroundDrawable((View) contexts.get(0).getStyleable(), drawable);
        }
    }

    // Private

    /**
     * Update the View's fading-edge properties. We only set fading properties
     * that were changed via the CSS, as each view subclass may have its own
     * defaults.
     * 
     * @param context
     * @param view
     */
    private void updateFadingEdgeStyle(PXStylerContext context, View view) {
        FadingEdgeStyle fadingStyle = context.getFadingStyle();
        if (fadingStyle != null) {
            if (fadingStyle.verticalEnabled != null) {
                view.setVerticalFadingEdgeEnabled(fadingStyle.verticalEnabled);
            }
            if (fadingStyle.horizontalEnabled != null) {
                view.setVerticalFadingEdgeEnabled(fadingStyle.horizontalEnabled);
            }
            if (fadingStyle.edgeLength != null) {
                view.setFadingEdgeLength(fadingStyle.edgeLength);
            }
        }
    }

    /**
     * Returns the supported pseudo classes that maps to the {@link Drawable}
     * states. General possible values that are acceptable by a {@link Drawable}
     * are:
     * <ul>
     * <li>"state_focused"
     * <li>"state_window_focused"
     * <li>"state_enabled"
     * <li>"state_checked"
     * <li>"state_selected"
     * <li>"state_active"
     * <li>"state_single"
     * <li>"state_first"
     * <li>"state_mid"
     * <li>"state_last"
     * <li>"state_pressed"
     * <li>"state_activated"
     * <li>"state_hovered"
     * <li>"state_drag_can_accept"
     * <li>"state_drag_hovered"
     * </ul>
     * Note: The returned View list of pseudo class will omit the "state_"
     * prefix from those values.
     * 
     * @return A list of supported pseudo classes.
     * @see PXDrawableUtil#getSupportedStates()
     */
    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        return new ArrayList<String>(PXDrawableUtil.getSupportedStates().keySet());
    }

    @Override
    public String getDefaultPseudoClass(Object styleable) {
        // Note: This is just a string we place to indicate the default state of
        // the view. This key can later be mapped into an integer that
        // represents a state. The state can be applied as a Drawable state, a
        // Color state, or any other type of state for that matter.
        return PXStyleInfo.DEFAULT_STYLE;
    }

    public static PXViewStyleAdapter getInstance() {
        synchronized (PXViewStyleAdapter.class) {
            if (sInstance == null) {
                sInstance = new PXViewStyleAdapter();
            }
        }

        return sInstance;
    }
}
