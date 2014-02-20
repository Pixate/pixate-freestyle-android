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

import android.widget.Spinner;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualDropdown;

/**
 * A {@link Spinner} style adapter.
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXSpinnerStyleAdapter extends PXViewStyleAdapter {
    private static String ELEMENT_NAME = "spinner";
    private static PXSpinnerStyleAdapter instance;

    protected PXSpinnerStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXSpinnerStyleAdapter}
     */
    public static PXSpinnerStyleAdapter getInstance() {
        synchronized (PXButtonStyleAdapter.class) {

            if (instance == null) {
                instance = new PXSpinnerStyleAdapter();
            }
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXTextViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualDropdown(styleable));

        return result;
    }

    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A Spinner requires some unique states of its own to function
        // properly.
        // A default Spinner instance states contain the following
        // values. Here, we try to generate what's missing when we only get a
        // single 'state' value from our pseudo class. Note that a state will
        // still be applied when the other values in the array are negative
        // (which implied a 'not').
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled }
        // { -android.R.attr.state_window_focused, -android.R.attr.state_enabled }
        // { android.R.attr.state_pressed }
        // { android.R.attr.state_focused, android.R.attr.state_enabled }
        // { android.R.attr.state_enabled }
        // { android.R.attr.state_focused}
        // {  } (default)
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases for the radio button.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_enabled:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_pressed:
                states.add(new int[] { -android.R.attr.state_checked, android.R.attr.state_pressed });
                break;
            case android.R.attr.drawable:
                // default state: add additional non-checked states.
                // The Spinner default state will be on 'enabled', so we load
                // the enabled states here too.
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_enabled });
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_enabled });
                states.add(new int[] { });
                break;
            default:
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }
}
