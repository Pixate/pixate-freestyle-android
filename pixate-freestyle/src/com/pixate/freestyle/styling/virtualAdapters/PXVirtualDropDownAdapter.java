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
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Spinner;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXFillStyler;
import com.pixate.freestyle.styling.stylers.PXSpinnerDropDownStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * Virtual drop-down adapter for {@link Spinner}<code>s</code>. Handles
 * <code>dropdown-xxx</code> declarations.
 * 
 * @author Shalom Gibly
 */
public class PXVirtualDropDownAdapter extends PXStyleAdapter {

    private static String ELEMENT_NAME = "dropdown";
    private static PXVirtualDropDownAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualDropDownAdapter}
     */
    public static PXVirtualDropDownAdapter getInstance() {
        synchronized (PXVirtualDropDownAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualDropDownAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualDropDownAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        // FIXME - Note that we return the entire list of possible pseudo
        // classes,
        // although only two can actually be used here ('default' and
        // 'above-anchor'). Eventually we'll need to do a smarter map from the
        // pseudo class name to the Android integer number for that state.
        return new ArrayList<String>(PXDrawableUtil.getSupportedStates().keySet());
    }

    @Override
    public String getDefaultPseudoClass(Object styleable) {
        return PXStyleInfo.DEFAULT_STYLE;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getParent(java.lang
     * .Object)
     */
    @Override
    public Object getParent(Object styleable) {
        // Make sure we return the virtual styleable parent, which is a 'real'
        // view.
        if (styleable instanceof PXVirtualStyleable) {
            return ((PXVirtualStyleable) styleable).getParent();
        }
        return super.getParent(styleable);
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = new ArrayList<PXStyler>();
        stylers.add(PXFillStyler.getInstance());
        stylers.add(PXSpinnerDropDownStyler.getInstance());
        return stylers;
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // The fill styler should read any background image to the context, so
        // we are grabbing it to be used as the popup background.
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setPopupBackgroundImage(ruleSets, contexts);
        } else {
            if (PXLog.isLogging()) {
                PXLog.w(PXVirtualDropDownAdapter.class.getSimpleName(),
                        "Spinner's pop-up background image setting requires API 16");
            }
        }
        return true;
    }

    /**
     * Sets the popup window background image. Note that the size cannot be
     * determined at this point, so it's also recommended to set the
     * background-size in the CSS (batter scaling down from a bigger image than
     * scale up from a default 32x32 image).
     * 
     * @param ruleSets
     * @param contexts
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setPopupBackgroundImage(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        Spinner spinner = (Spinner) contexts.get(0).getStyleable();
        // Get the spinner adapter. We'll use it to create the states, although
        // its default implementation does not create any additional state-sets
        // (which is fine).
        PXStyleAdapter spinnerAdapter = PXStyleAdapter.getStyleAdapter(spinner);
        Map<int[], Drawable> existingPopupStates = PXDrawableUtil.getExistingStates(spinner
                .getPopupBackground());
        Drawable newPopupBackground = null;
        if (existingPopupStates == null || existingPopupStates.isEmpty()) {
            // create a new background for the popup
            newPopupBackground = PXDrawableUtil.createNewStateListDrawable(spinnerAdapter,
                    ruleSets, contexts);
        } else {
            // merge or replace the states with what we have in the CSS
            newPopupBackground = PXDrawableUtil.createDrawable(spinnerAdapter,
                    existingPopupStates, ruleSets, contexts);
        }
        if (newPopupBackground != null) {
            spinner.setPopupBackgroundDrawable(newPopupBackground);
        }
    }
}
