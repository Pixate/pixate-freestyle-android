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

import android.widget.CheckedTextView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCheckedTextViewIcon;

/**
 * A {@link CheckedTextView} style adapter.<br>
 * Styling a {@link CheckedTextView} is done by setting the checked state
 * (pseudo class) and the default state in the CSS. For example:
 * 
 * <pre>
 * Style just the check mark icon:
 * 
 * #myListView checked-text-view icon:checked {
 *     background-image: url(check-on.svg);
 *     background-size: 60;
 * }
 * 
 * #myListView checked-text-view icon {
 *     background-image: url(check-off.svg);
 *     background-size: 60;
 * }
 * 
 * While this will style the entire CheckedTextView button background and text color:
 * 
 * #myListView checked-text-view:checked {
 *     background-image: url(checked-bg.svg);
 *     color: white;
 * }
 * 
 * #myListView checked-text-view {
 *     background-image: url(unchecked-bg.svg);
 *     color: green;
 * }
 * </pre>
 * 
 * @author Bill Dawson (via shalom)
 */
@PXDocElement
public class PXCheckedTextViewStyleAdapter extends PXTextViewStyleAdapter {
    private static String ELEMENT_NAME = "checked-text-view";
    private static PXCheckedTextViewStyleAdapter instance;

    protected PXCheckedTextViewStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXCheckedTextViewStyleAdapter}
     */
    public static synchronized PXCheckedTextViewStyleAdapter getInstance() {

        if (instance == null) {
            instance = new PXCheckedTextViewStyleAdapter();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXTextViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXTextViewStyleAdapter#
     * getVirtualChildren(java.lang.Object)
     */
    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualCheckedTextViewIcon(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#
     * shouldAdjustDrawableBounds()
     */
    @Override
    public boolean shouldAdjustDrawableBounds() {
        // CheckedTextView has to adjust the StateListDrawable that it's
        // holding. Otherwise, resizing events make the drawables disappear.
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default CheckedTextView instance states contain the following
        // values. Here, we try to generate what's missing when we only get a
        // single 'state' value from our pseudo class. Note that a state will
        // still be applied when the other values in the array are negative
        // (which implied a 'not').
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled, android.R.attr.state_checked}
        // { -android.R.attr.state_window_focused, android.R.attr.state_enabled, -android.R.attr.state_checked}
        // { android.R.attr.state_enabled, android.R.attr.state_checked, android.R.attr.state_pressed}
        // { android.R.attr.state_enabled, -android.R.attr.state_checked, android.R.attr.state_pressed}
        // { android.R.attr.state_focused, android.R.attr.state_enabled, android.R.attr.state_checked}
        // { android.R.attr.state_focused, android.R.attr.state_enabled, -android.R.attr.state_checked}
        // { android.R.attr.state_enabled, -android.R.attr.state_checked}
        // { android.R.attr.state_enabled, android.R.attr.state_checked}
        // { -android.R.attr.state_window_focused, android.R.attr.state_checked}
        // { -android.R.attr.state_window_focused, -android.R.attr.state_checked}
        // { android.R.attr.state_focused, android.R.attr.state_checked}
        // { android.R.attr.state_focused, -android.R.attr.state_checked}
        // { -android.R.attr.state_checked}
        // { android.R.attr.state_checked}
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>();

        // check for some special cases for the CheckedTextView.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_checked:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled, android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked, android.R.attr.state_pressed});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled, android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked });
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_checked});
                break;
            case android.R.attr.state_pressed:
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked, android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_enabled, -android.R.attr.state_checked, android.R.attr.state_pressed });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled, android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled, -android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_checked});
                break;
            case android.R.attr.drawable:
                // default states
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled, -android.R.attr.state_checked});
                states.add(new int[] { android.R.attr.state_enabled, -android.R.attr.state_checked });
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_checked });
                states.add(new int[] { -android.R.attr.state_checked });
                states.add(new int[] {});
                break;
            default:
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        return states.toArray(new int[states.size()][]);
    }
}
