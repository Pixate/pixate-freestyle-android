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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * A base class for virtual children for compound buttons icons. Every widget
 * that required this kind of support has to extend this class in order to
 * provide specific {@link #createAdditionalDrawableStates(int)} implementation
 * that is required when creating the state-drawables.
 * 
 * @author Shalom Gibly
 */
public class PXVirtualCompoundButtonIconAdapter extends PXVirtualChildAdapter {

    private static String TAG = PXVirtualCompoundButtonIconAdapter.class.getSimpleName();
    private static String ELEMENT_NAME = "icon";
    private static PXVirtualCompoundButtonIconAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return a {@link PXVirtualCompoundButtonIconAdapter}
     */
    public static PXVirtualCompoundButtonIconAdapter getInstance() {
        synchronized (PXVirtualCompoundButtonIconAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualCompoundButtonIconAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualCompoundButtonIconAdapter() {
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
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementName(java
     * .lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
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
        // Style the compound button background image. We will construct the
        // drawable from then context states, and then set the constructed
        // drawable as a buttons-background using the
        // CompoundButton#setButtonBackground(Drawable) call. Note that this is
        // different than the View#setBackground(Drawable) call that is handled
        // in the default View adapter.

        CompoundButton view = (CompoundButton) contexts.get(0).getStyleable();
        // Grab the existing states.
        Map<int[], Drawable> existingStates = PXDrawableUtil
                .getExistingStates(getBackgroundDrawable(view));
        // Generate the new StateListDrawable and set it as the button's
        // drawable.
        view.setButtonDrawable(PXDrawableUtil.createDrawable(this, existingStates, ruleSets,
                contexts));
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#
     * createAdditionalStates (int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // Grab the additional states from the compound button adapter.
        return PXCompoundButtonStyleAdapter.getInstance().createAdditionalDrawableStates(
                initialValue);
    }

    /**
     * Returns the button-background drawable of a view. Note that
     * {@link CompoundButton} views have a different way to set the background
     * than over views.
     * 
     * @param view
     * @return A button's background drawable (can be null)
     */
    protected static Drawable getBackgroundDrawable(CompoundButton view) {
        if (view == null) {
            return null;
        }
        // We have to get the drawable value through reflection...
        try {
            Field field = CompoundButton.class.getDeclaredField("mButtonDrawable");
            field.setAccessible(true);
            return (Drawable) field.get(view);
        } catch (Exception e) {
            PXLog.e(TAG, e, "Error getting the mButtonDrawable value.");
        }
        return null;
    }
}
