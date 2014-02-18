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
import android.os.Build;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.PXLog;

/**
 * Virtual adapter for {@link ActionBar} home logo. Note that this adapter will
 * make a call to {@link ActionBar#setDisplayUseLogoEnabled(boolean)} to have
 * the logo visible in case a 'logo' is requested in the style script.
 * 
 * <pre>
 * <code>
 *   - logo
 * </code>
 * </pre>
 * 
 * For example, setting the logo for an {@link ActionBar} is done like this:
 * 
 * <pre>
 * action-bar logo {
 *   background-image: url(logo.svg);
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
public class PXVirtualActionBarLogoAdapter extends PXVirtualChildAdapter {

    private static String ELEMENT_NAME = "logo";
    private static PXVirtualActionBarLogoAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualActionBarLogoAdapter}
     */
    public static PXVirtualActionBarLogoAdapter getInstance() {
        synchronized (PXVirtualActionBarLogoAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualActionBarLogoAdapter();
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
        if (PixateFreestyle.ICS_OR_BETTER) {
            setLogoICS(context, actionBar);
        } else {
            // TODO - add a compatible call for earlier API
            if (PXLog.isLogging()) {
                PXLog.w(getClass().getSimpleName(),
                        "Unable to set ActionBar logo. API version < 14.");
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setLogoICS(PXStylerContext context, ActionBar actionBar) {
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(context.getBackgroundImage());
    }
}
