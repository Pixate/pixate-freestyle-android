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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXTabViewStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * Virtual adapter for {@link ActionBar} {@link Tab} icons.
 * 
 * <pre>
 * <code>
 *   - icon
 * </code>
 * </pre>
 * 
 * For example, styling the nth tab can be done like that:
 * 
 * <pre>
 * action-bar action-bar-tab:nth-child(n) icon {
 *   background-image: url(tab1.svg);
 * }
 * 
 * action-bar action-bar-tab:nth-child(n + 1) icon {
 *     background-image: url(tab2.svg);
 * }
 * 
 * action-bar action-bar-tab:nth-child(n + 2) icon {
 *     background-image: url(tab3.svg);
 * }
 * </pre>
 * 
 * TODO - add an example with states
 * 
 * @author Shalom Gibly
 */
public class PXVirtualActionBarTabIconAdapter extends PXVirtualChildAdapter {

    private static String ELEMENT_NAME = "icon";
    private static PXVirtualActionBarTabIconAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualActionBarTabIconAdapter}
     */
    public static PXVirtualActionBarTabIconAdapter getInstance() {
        synchronized (PXVirtualActionBarTabIconAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualActionBarTabIconAdapter();
            }
        }
        return instance;
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
        return ELEMENT_NAME;
    }

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        // Extract the ActionBar's TabView we are styling.
        View tabView = (View) contexts.get(0).getStyleable();
        // Use the 'setIcon' on the TabView's assigned tab. To get to this tab,
        // we need reflection.
        try {
            Method getTabMethod = tabView.getClass().getMethod("getTab", (Class<?>[]) null);
            getTabMethod.setAccessible(true);
            Tab tab = (Tab) getTabMethod.invoke(tabView, (Object[]) null);
            if (tab != null) {
                // Grab the existing icon. In case it's a StatesListDrawable
                // we'll append to it.
                Map<int[], Drawable> existingStates = PXDrawableUtil.getExistingStates(tab
                        .getIcon());
                if (existingStates == null || existingStates.isEmpty()) {
                    Drawable drawable = PXDrawableUtil.createNewDrawable(
                            PXTabViewStyleAdapter.getInstance(), ruleSets, contexts);
                    if (drawable != null) {
                        tab.setIcon(drawable);
                    }
                } else {
                    tab.setIcon(PXDrawableUtil.createDrawable(PXTabViewStyleAdapter.getInstance(),
                            existingStates, ruleSets, contexts));
                }
                // Adding an icon is actually adding an ImageView child to the
                // TabView. We have to set the selection state for this
                // ImageView, otherwise it will not display any existing
                // selection icon in case the tab is selected.
                // To make sure we get the child, no matter what is the order,
                // we traverse all direct children and set their selection
                // state.
                ViewGroup tabsGroup = ((ViewGroup) tabView);
                for (int i = 0; i < tabsGroup.getChildCount(); i++) {
                    tabsGroup.getChildAt(i).setSelected(tabView.isSelected());
                }
            }
        } catch (Exception e) {
            if (PXLog.isLogging()) {
                PXLog.w(getClass().getSimpleName(), e, "Error setting the ActionBar's Tab icon");
            }
        }

        return true;
    }

}
