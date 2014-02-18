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
package com.pixate.freestyle.styling.virtualAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.widget.Button;
import android.widget.TextView;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXCompoundIconStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.stylers.PXStylerContext.IconPosition;
import com.pixate.freestyle.util.PXDrawableUtil;

/**
 * Virtual icon adapter for views that support icons (such as {@link TextView}
 * and {@link Button}. It handles the following icon positions (as virtual
 * children):
 * 
 * <pre>
 * <code>
 *   - icon-top
 *   - icon-right
 *   - icon-bottom
 *   - icon-left
 * </code>
 * </pre>
 * 
 * For example, this will draw a left icon on a button with a class of
 * <code>"myButton"</code>:
 * 
 * <pre>
 * .myButton icon-left {
 *   background-image: url(default.svg);
 *   background-size: 48px;
 * }
 * 
 * .myButton icon-left:pressed {
 *   background-image: url(pressed.svg);
 *   background-size: 48px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
public class PXVirtualIconAdapter extends PXVirtualChildAdapter {

    private static Map<IconPosition, PXVirtualIconAdapter> instances = new HashMap<IconPosition, PXVirtualIconAdapter>(
            4);
    private IconPosition position;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualIconAdapter}
     */
    public static PXVirtualIconAdapter getInstance(IconPosition position) {
        PXVirtualIconAdapter instance = instances.get(position);
        synchronized (PXVirtualIconAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualIconAdapter(position);
                instances.put(position, instance);
            }
        }
        return instance;
    }

    protected PXVirtualIconAdapter(IconPosition position) {
        this.position = position;
    }

    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        return new ArrayList<String>(PXDrawableUtil.getSupportedStates().keySet());
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getDefaultPseudoClass
     * (java.lang.Object)
     */
    @Override
    public String getDefaultPseudoClass(Object styleable) {
        return PXStyleInfo.DEFAULT_STYLE;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return position.getElementName();
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> superStylers = super.createStylers();
        List<PXStyler> stylers = new ArrayList<PXStyler>(superStylers.size() + 1);
        stylers.addAll(superStylers);
        stylers.add(PXCompoundIconStyler.getInstance(position));
        return stylers;
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        // Extract the existing compound icons
        TextView styleable = (TextView) contexts.get(0).getStyleable();
        Drawable[] compoundDrawables = styleable.getCompoundDrawables();
        Drawable iconDrawable = compoundDrawables[position.ordinal()];
        Map<int[], Drawable> existingStates = PXDrawableUtil.getExistingStates(iconDrawable);
        Drawable newDrawable = null;
        if (existingStates == null || existingStates.isEmpty()) {
            // create a new StateListDrawable for that icon with the original
            // style adapter
            if (contexts.size() == 1) {
                newDrawable = contexts.get(0).getBackgroundImage();
            } else {
                newDrawable = PXDrawableUtil.createNewStateListDrawable(
                        PXStyleAdapter.getStyleAdapter(styleable), ruleSets, contexts);
            }
        } else {
            // create a drawable that will hold a merge of the existing states
            // and the new states.
            newDrawable = PXDrawableUtil.createDrawable(
                    PXStyleAdapter.getStyleAdapter(styleable), existingStates, ruleSets, contexts);
        }

        // Set the drawable.
        if (newDrawable != null
                && !PXDrawableUtil.isEquals(newDrawable, compoundDrawables[position.ordinal()])) {
            compoundDrawables[position.ordinal()] = newDrawable;

            if (newDrawable instanceof DrawableContainer && newDrawable.getCurrent() == null) {
                // We have to select a Drawable in the StateListDrawables.
                // Otherwise, the bounds will not be set correctly.
                DrawableContainer container = (DrawableContainer) newDrawable;
                container.selectDrawable(0);
            }
            newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(),
                    newDrawable.getIntrinsicHeight());
            styleable.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1],
                    compoundDrawables[2], compoundDrawables[3]);
        }
        return true;
    }

}
