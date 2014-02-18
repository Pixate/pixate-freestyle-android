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

import android.widget.ListView;

import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.stylers.PXOverscrollStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualStyleable;

/**
 * Virtual over-scroll adapter for {@link ListView}<code>s</code>. Handles
 * <code>header</code> and <code>footer</code> declarations.
 * 
 * @author Shalom Gibly
 */
public class PXVirtualOverscrollListAdapter extends PXStyleAdapter {

    private static String ELEMENT_NAME = "overscroll";

    private static PXVirtualOverscrollListAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualOverscrollListAdapter}
     */
    public static PXVirtualOverscrollListAdapter getInstance() {
        synchronized (PXVirtualOverscrollListAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualOverscrollListAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualOverscrollListAdapter() {
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
        stylers.add(PXOverscrollStyler.getInstance());
        return stylers;
    }
}
