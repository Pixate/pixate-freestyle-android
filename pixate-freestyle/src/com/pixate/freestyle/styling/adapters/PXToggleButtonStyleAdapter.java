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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.ToggleButton;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualToggle;
import com.pixate.freestyle.util.PXDrawableUtil;

/**
 * A {@link ToggleButton} style adapter. A toggle button adapter can style the
 * button's background, as we as the button's internal toggle image. Usually, a
 * user will pick one or the other, however it's possible to layer a
 * semi-transparent 'toggle' icon. For example:
 * 
 * <pre>
 * Style the toggle's background to reflect the selection state.
 * In this case, the button will still render the second drawable layer 
 * that mapped to android.R.id.toggle (for example, a green bar indicator)
 * 
 * .toggle {
 *     background-image: url(off.svg);
 * }
 * 
 * .toggle:checked {
 *     background-image: url(on.svg);
 * }
 * 
 * Style the android.R.id.toggle layer using a virtual 'toggle' child:
 * 
 * .toggle toggle:off {
 *     background-image: url(off.svg);
 *     background-size:  200px;
 * }
 * 
 * .toggle toggle:on {
 *     background-image: url(on.svg);
 *     background-size: 200px;
 * }
 * 
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXToggleButtonStyleAdapter extends PXCompoundButtonStyleAdapter {

    private static final String ELEMENT_NAME = "toggle-button";
    private static PXToggleButtonStyleAdapter instance;

    protected PXToggleButtonStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXToggleButtonStyleAdapter}
     */
    public static PXToggleButtonStyleAdapter getInstance() {
        synchronized (PXToggleButtonStyleAdapter.class) {

            if (instance == null) {
                instance = new PXToggleButtonStyleAdapter();
            }
        }
        return instance;
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }

        PXStylerContext context = contexts.get(0);

        // Grab the existing states from the ToggleButton.
        // The ToggleButton returns a LayerDrawable that holds the background
        // drawable and the toggle drawable. Here, we are only interested in the
        // button's background, while the toggle will be handled by the virtual
        // toggle child.
        View view = (View) context.getStyleable();
        Drawable background = view.getBackground();
        LayerDrawable layerDrawable = null;
        if (background instanceof LayerDrawable) {
            layerDrawable = (LayerDrawable) background;
            background = layerDrawable.findDrawableByLayerId(android.R.id.background);
        }

        Map<int[], Drawable> existingStates = PXDrawableUtil.getExistingStates(background);
        if (existingStates == null || existingStates.isEmpty()) {
            int rulesetSize = ruleSets.size();
            // No state-lists here, so simply loop and set the backgrounds
            for (int i = 0; i < rulesetSize; i++) {
                context = contexts.get(i);
                PXDrawableUtil.setBackgroundDrawable((View) context.getStyleable(),
                        context.getBackgroundImage());
            }
            return true;
        }

        // Update the layer
        Drawable drawable = PXDrawableUtil.createDrawable(this, existingStates, ruleSets, contexts);
        if (layerDrawable != null) {
            layerDrawable.setDrawableByLayerId(android.R.id.background, drawable);
        } else {
            PXDrawableUtil.setBackgroundDrawable(view, drawable);
        }
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
        // We should have the same view in all contexts, so grab the first
        // and set it with the new background
        StateListDrawable drawable = PXDrawableUtil.createNewStateListDrawable(this, ruleSets,
                contexts);
        if (drawable != null) {
            // Set the background on the right level of the toggle button
            ToggleButton toggleButton = (ToggleButton) contexts.get(0).getStyleable();
            Drawable background = toggleButton.getBackground();
            if (background instanceof LayerDrawable) {
                LayerDrawable layerDrawable = (LayerDrawable) background;
                layerDrawable.setDrawableByLayerId(android.R.id.background, drawable);
            } else {
                // we don't deal with any other background type (non-default)
                // (TODO: We may need to hack our way to create a new
                // LayerDrawable with the right layers id's)
                PXDrawableUtil.setBackgroundDrawable((View) toggleButton, drawable);
            }
        }
    }

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualToggle(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXTextViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default ToggleButton instance states contain the following
        // values. Here, we try to generate what's missing when we only get a
        // single 'state' value from our pseudo class. Note that a state will
        // still be applied when the other values in the array are negative
        // (which implied a 'not').
        // Note that these are the states for the backround of the button, not
        // for the toggle icon inside it (a second layer). That icon is handled
        // as a virtual child named 'toggle'.

        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled }
        // { -android.R.attr.state_window_focused, -android.R.attr.state_enabled }
        // { android.R.attr.state_pressed }
        // { android.R.attr.state_focused, android.R.attr.state_enabled }
        // { android.R.attr.state_enabled }
        // { android.R.attr.state_focused }
        // { } (default android.r.attr.drawable)
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_enabled:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.drawable:
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_enabled });
                states.add(new int[] {});
                break;
            default:
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }
}
