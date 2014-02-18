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

import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.RectF;
import android.os.Build;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXActionBarStyleAdapter;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * Virtual adapter for {@link ActionBar} stacked background.
 * 
 * <pre>
 * <code>
 *   - stacked
 * </code>
 * </pre>
 * 
 * For example, setting the stacked background for an {@link ActionBar} is done
 * like this:
 * 
 * <pre>
 * action-bar stacked {
 *   background-image: url(stacked_bg.svg);
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
public class PXVirtualActionBarStackedAdapter extends PXVirtualChildAdapter {

    private static String ELEMENT_NAME = "stacked";
    private static PXVirtualActionBarStackedAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualActionBarStackedAdapter}
     */
    public static PXVirtualActionBarStackedAdapter getInstance() {
        synchronized (PXVirtualActionBarStackedAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualActionBarStackedAdapter();
            }
        }
        return instance;
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

        PXStylerContext context = contexts.get(0);
        ActionBar actionBar = (ActionBar) context.getStyleable();
        // We just overwrite the exiting background. No states merging here.
        RectF bounds = PXActionBarStyleAdapter.getActionBarBounds(actionBar);
        if (PixateFreestyle.ICS_OR_BETTER) {
            setBackgroundICS(context, actionBar, bounds);
        } else {
            // TODO - add a compatible call for earlier API
            if (PXLog.isLogging()) {
                PXLog.w(getClass().getSimpleName(),
                        "Unable to set ActionBar stacked background. API version < 14.");
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setBackgroundICS(PXStylerContext context, ActionBar actionBar, RectF bounds) {
        actionBar.setStackedBackgroundDrawable(PXDrawableUtil.createDrawable(bounds,
                context.getCombinedPaints()));
    }
}
