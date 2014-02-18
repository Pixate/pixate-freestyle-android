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
package com.pixate.freestyle.viewdemo.viewsamples;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.PXStylesheet;
import com.pixate.freestyle.styling.PXStylesheet.PXStyleSheetOrigin;

/**
 * Base implementation for {@link ViewSample} objects.
 * 
 * @author shalom
 */
public abstract class ViewSampleBase implements ViewSample {

    private List<View> views;

    /**
     * Add the view that was created by this sample into a view list that will
     * be later styled by the {@link #style(String)} method.
     * 
     * @param view
     */
    protected void addView(View view) {
        if (views == null) {
            views = new ArrayList<View>(2);
        }
        views.add(view);
    }

    /**
     * Clear our internal collection of views. Needs to be called from
     * fragment's onDestroyView, else next time we go back to the same sample
     * we'll try styling those views all over again using {@link #style(String)}
     * .
     */
    @Override
    public void destroyViews() {
        if (views != null) {
            views.clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.viewdemo.viewsamples.ViewSample#style(java.lang.String)
     */
    @Override
    public void style(String css) {
        if (views != null) {
            PXStylesheet.getStyleSheetFromSource(css, PXStyleSheetOrigin.APPLICATION);
            for (View view : views) {
                PXStyleUtils.updateStyles(view, true);
            }
        }
    }
}
