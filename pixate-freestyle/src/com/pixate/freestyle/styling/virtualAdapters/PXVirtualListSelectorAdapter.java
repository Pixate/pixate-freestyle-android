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

import android.graphics.drawable.Drawable;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXStylerContext;

/**
 * Virtual list selector adapter for {@link AbsListView}<code>s</code>
 * descendants like {@link GridView} and {@link ListView}. Handles the
 * <code>selector</code> declaration.<br/>
 * 
 * For example:
 * <pre>
 * #myList selector {
 *     background-color: linear-gradient(black, orange);
 * }
 * </pre>
 * 
 * @author Bill Dawson
 */
public class PXVirtualListSelectorAdapter extends PXVirtualChildAdapter {

    private static PXVirtualListSelectorAdapter instance;
    private static String ELEMENT_NAME = "selector";

    protected PXVirtualListSelectorAdapter() {
    }

    public static synchronized PXVirtualListSelectorAdapter getInstance() {
        if (instance == null) {
            instance = new PXVirtualListSelectorAdapter();
        }

        return instance;
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
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#updateStyle(java.util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (super.updateStyle(ruleSets, contexts)) {
            PXStylerContext context = contexts.get(0);
            Object styleable = context.getStyleable();
            if (styleable instanceof AbsListView) {
                AbsListView view = (AbsListView) styleable;
                Drawable background = context.getBackgroundImage();
                view.setSelector(background);
            }
            return true;
        } else {
            return false;
        }
    }
}
