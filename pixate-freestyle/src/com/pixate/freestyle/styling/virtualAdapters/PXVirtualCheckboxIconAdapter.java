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

import android.widget.CheckBox;

import com.pixate.freestyle.styling.adapters.PXCheckboxStyleAdapter;

/**
 * A virtual adapter for {@link CheckBox}<code>es</code>.
 * 
 * @author Shalom Gibly
 */
public class PXVirtualCheckboxIconAdapter extends PXVirtualCompoundButtonIconAdapter {

    private static PXVirtualCheckboxIconAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualCheckboxIconAdapter}
     */
    public static PXVirtualCheckboxIconAdapter getInstance() {
        synchronized (PXVirtualCheckboxIconAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualCheckboxIconAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualCheckboxIconAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // grab the states from the checkbox adapter
        return PXCheckboxStyleAdapter.getInstance().createAdditionalDrawableStates(initialValue);
    }
}
