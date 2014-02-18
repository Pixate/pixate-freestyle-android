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

import java.util.Arrays;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ToggleButton;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXToggleButtonStyleAdapter;
import com.pixate.freestyle.styling.stylers.PXStylerContext;

/**
 * A virtual toggle adapter. This toggle handled "on" and "off" states (pseudo
 * classes) for the "toggle" virtual child.<br>
 * For example:
 * 
 * <pre>
 * #myButton toggle:on {
 *   background-image: url(on.svg);
 * }
 * #myButton toggle:off {
 *   background-image: url(off.svg);
 *   background-size: 50px;
 * }
 * </pre>
 * 
 * @see {@link PXVirtualChildAdapter} for the complete list of supported
 *      attributes in every state
 * @author Shalom Gibly
 */
public class PXVirtualToggleAdapter extends PXVirtualChildAdapter {

    private static String ON = "on";
    private static String OFF = "off";
    private static String ELEMENT_NAME = "toggle";
    private static PXVirtualToggleAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualToggleAdapter}
     */
    public static PXVirtualToggleAdapter getInstance() {
        synchronized (PXVirtualOverscrollListAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualToggleAdapter();
            }
        }
        return instance;
    }

    /**
     * Constructs a new {@link PXVirtualToggleAdapter}
     */
    protected PXVirtualToggleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getSupportedPseudoClasses
     * (java.lang.Object)
     */
    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        return Arrays.asList(ON, OFF);
    }

    @Override
    public String getDefaultPseudoClass(Object styleable) {
        return ON;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementName(java
     * .lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        return PXToggleButtonStyleAdapter.getInstance()
                .createAdditionalDrawableStates(initialValue);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#updateStyle(java.
     * util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        // Apply the toggle Drawables wrapped in a LayerDrawable. The drawable
        // that will go into this LayerDrawable has to be a StateListDrawable,
        // so we construct it using the 'on' and 'off' states to provide the
        // toggle images in its different states.
        if (!contexts.isEmpty()) {
            // same styleable for all contexts
            Object styleable = contexts.get(0).getStyleable();
            if (styleable instanceof ToggleButton) {
                // prepare the StateListDrawable
                StateListDrawable toggleDrawable = new StateListDrawable();
                for (PXStylerContext context : contexts) {
                    Drawable drawable = context.getBackgroundImage();
                    if (drawable != null) {
                        if (ON.equals(context.getActiveStateName())) {
                            toggleDrawable.addState(new int[] { android.R.attr.state_checked },
                                    drawable);
                        } else {
                            toggleDrawable.addState(new int[] { -android.R.attr.state_checked },
                                    drawable);
                        }
                    }
                }
                ToggleButton toggleButton = (ToggleButton) styleable;
                Drawable background = toggleButton.getBackground();
                if (toggleDrawable != null) {
                    if (background instanceof LayerDrawable) {
                        LayerDrawable layerDrawable = (LayerDrawable) background;
                        layerDrawable.setDrawableByLayerId(android.R.id.toggle, toggleDrawable);
                    } else if (background instanceof StateListDrawable) {
                        // just replace it
                        toggleButton.setBackgroundDrawable(toggleDrawable);
                    }
                }
            }
        }
        return true;
    }
}
