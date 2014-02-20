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

import android.app.ActionBar;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXFillStyler;
import com.pixate.freestyle.styling.stylers.PXShapeStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarIcon;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarLogo;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarSplit;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarStacked;
import com.pixate.freestyle.util.PXDrawableUtil;

/**
 * A style adapter for {@link ActionBar}
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXActionBarStyleAdapter extends PXStyleAdapter {

    private static String ELEMENT_NAME = "action-bar";
    private static PXActionBarStyleAdapter instance;

    /**
     * Returns an instance of this {@link PXActionBarStyleAdapter}
     */
    public static PXActionBarStyleAdapter getInstance() {
        synchronized (PXActionBarStyleAdapter.class) {

            if (instance == null) {
                instance = new PXActionBarStyleAdapter();
            }
        }
        return instance;
    }

    protected PXActionBarStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXImageViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> virtuals = new ArrayList<Object>();
        virtuals.add(new PXVirtualActionBarStacked(styleable));
        virtuals.add(new PXVirtualActionBarSplit(styleable));
        virtuals.add(new PXVirtualActionBarIcon(styleable));
        virtuals.add(new PXVirtualActionBarLogo(styleable));
        // virtuals.add(new PXActionBarDivider(styleable));
        return virtuals;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        // We only add stylers that update the context. None of these stylers
        // should update a View, as this adapter is a non-view adapter.
        List<PXStyler> stylers = new ArrayList<PXStyler>();
        stylers.add(PXShapeStyler.getInstance());
        stylers.add(PXFillStyler.getInstance());
        return stylers;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getBounds(java.lang
     * .Object)
     */
    @Override
    public RectF getBounds(Object styleable) {
        return getActionBarBounds((ActionBar) styleable);
    }

    /**
     * Compute the {@link ActionBar} bounds. The computation is taking the
     * action-bar height value from the theme (in case the ActionBar does not
     * hold height information), and the actual screen width, to compute and
     * return the bounds.
     * 
     * @param ab An {@link ActionBar}
     * @return A {@link RectF} description of the {@link ActionBar} bounds
     *         (dimensions)
     */
    public static RectF getActionBarBounds(ActionBar ab) {
        float height = ab.getHeight();
        if (height <= 0) {
            // Unfortunately, we have to compute the ActionBar dimensions. Even
            // the height we get from the ActionBar styleable instance via
            // getHeight() is returned as zero. We get the height value from the
            // theme, and the width value from the screen.
            final TypedArray styledAttributes = PixateFreestyle.getAppContext().getTheme()
                    .obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
            height = (int) styledAttributes.getDimension(0, 0);
        }
        // compute the width of the screen.
        Context context = PixateFreestyle.getAppContext();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dimension);
        return new RectF(0, 0, dimension.widthPixels, height);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#updateStyle(java.
     * util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        // For the default case where we style just the action-bar, with no
        // virtual child involved, we will generate and set a background
        // drawable that will act as the default background.
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        PXStylerContext context = contexts.get(0);
        ActionBar actionBar = (ActionBar) context.getStyleable();
        // We just overwrite the exiting background. No states merging here.
        RectF bounds = getBounds(actionBar);
        actionBar.setBackgroundDrawable(PXDrawableUtil.createDrawable(bounds,
                context.getCombinedPaints()));
        return true;
    }
}
