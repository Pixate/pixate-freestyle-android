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

import java.util.List;

import android.widget.GridView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.stylers.PXGridStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;

/**
 * GridView styling.<br>
 * A {@link GridView} styler controls column- and
 * row-related attributes
 * 
 * <pre>
 * - column-count: number
 * - column-width: length
 * - column-gap: length
 * - column-stretch-mode: none|spacing|spacing-uniform|column-width
 * - row-gap: length
 * 
 * (Defined in the AbsListView styler, but applicable for this list)
 * - selection-mode: single|multiple
 * - selector: virtual-child
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * #grid {
 *     column-count: 3;
 *     column-gap: 5;
 *     column-width: 170;
 *     column-stretch-mode: none;
 *     row-gap: 20;
 *     selection-mode: single;
 * }
 * 
 * #grid selector {
 *     background-color: linear-gradient(black, orange);
 * }
 * </pre>
 * @author Bill Dawson
 */
@PXDocElement
public class PXGridViewStyleAdapter extends PXAbsListViewStyleAdapter {

    private static String ELEMENT_NAME = "grid-view";

    private static PXGridViewStyleAdapter sInstance;

    protected PXGridViewStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = super.createStylers();
        stylers.add(PXGridStyler.getInstance());
        return stylers;
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

    public static PXGridViewStyleAdapter getInstance() {
        synchronized (PXGridViewStyleAdapter.class) {

            if (sInstance == null) {
                sInstance = new PXGridViewStyleAdapter();
            }
        }

        return sInstance;
    }

}
