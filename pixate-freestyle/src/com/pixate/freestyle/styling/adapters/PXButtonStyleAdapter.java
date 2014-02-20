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

import com.pixate.freestyle.annotations.PXDocElement;

import android.widget.Button;
import android.widget.TextView;

/**
 * A {@link Button} style adapter. Note that this class doesn't do much at the
 * moment, and it's mainly here to distinguish between a {@link Button} and a
 * {@link TextView} styling when such an element is referred as the generic
 * 'button' or 'text-view' in the CSS.
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXButtonStyleAdapter extends PXTextViewStyleAdapter {

    private static String ELEMENT_NAME = "button";
    private static PXButtonStyleAdapter sInstance;

    protected PXButtonStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXButtonStyleAdapter}
     */
    public static PXButtonStyleAdapter getInstance() {
        synchronized (PXButtonStyleAdapter.class) {

            if (sInstance == null) {
                sInstance = new PXButtonStyleAdapter();
            }
        }
        return sInstance;
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

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#
     * createAdditionalStates (int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default Button states list contains the following values.
        // Here, we try to generate what's missing when we only get a single
        // 'state' value from our pseudo class. Note that a state will still be
        // applied when the other values in the array are negative (which
        // implied a 'not').
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled}
        // { -android.R.attr.state_window_focused, -android.R.attr.state_enabled}
        // { android.R.attr.state_pressed}
        // { android.R.attr.state_focused, android.R.attr.state_enabled}
        // { android.R.attr.state_enabled}
        // { android.R.attr.state_focused}
        // { } (default - android.R.attr.drawable)
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_enabled:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.drawable:
                // add anything that will be treated as the default. Note that
                // in case an additional pseudo ruleset appears to deal with
                // specific cases, it will take over.
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled});
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_enabled});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused });
                states.add(new int[] {});
            default:
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }
}
