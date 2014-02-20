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

import android.widget.CompoundButton;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCompoundButtonIcon;

/**
 * A {@link CompoundButton} style adapter. A compound button has two virtual
 * children that deals with its check-states.
 * 
 * @author Shalom Gibly
 */
@PXDocElement(hide=true)
public class PXCompoundButtonStyleAdapter extends PXButtonStyleAdapter {

    private static String ELEMENT_NAME = "compound-button";
    private static PXCompoundButtonStyleAdapter sInstance;

    protected PXCompoundButtonStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXCompoundButtonStyleAdapter}
     */
    public static PXCompoundButtonStyleAdapter getInstance() {
        synchronized (PXCompoundButtonStyleAdapter.class) {

            if (sInstance == null) {
                sInstance = new PXCompoundButtonStyleAdapter();
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

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        // Add a virtual child for an icon only if the current instance is not a
        // subclass of this class.
        if (this.getClass() == PXCompoundButtonStyleAdapter.class) {
            List<Object> superVirtuals = super.getVirtualChildren(styleable);
            List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
            result.addAll(superVirtuals);
            result.add(new PXVirtualCompoundButtonIcon(styleable));
            return result;
        } else {
            return super.getVirtualChildren(styleable);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#
     * createAdditionalStates (int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default CompoundButton instance states contain the following
        // values. Here, we try to generate what's missing when we only get a
        // single 'state' value from our pseudo class. Note that a state will
        // still be applied when the other values in the array are negative
        // (which implied a 'not').
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_checked}
        // { -android.R.attr.state_window_focused, -android.R.attr.state_checked}
        // { android.R.attr.state_checked, android.R.attr.state_pressed}
        // { -android.R.attr.state_checked, android.R.attr.state_pressed}
        // { android.R.attr.state_focused, android.R.attr.state_checked}
        // { android.R.attr.state_focused, -android.R.attr.state_checked}
        // { -android.R.attr.state_checked}
        // { android.R.attr.state_checked}
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_checked:
                // add 'pressed' and 'focused'
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_checked });
                // add 'enabled', required if on fragment.
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked });
                break;
            case android.R.attr.state_pressed:
                states.add(new int[] { android.R.attr.state_checked, android.R.attr.state_pressed });
                states.add(new int[] { -android.R.attr.state_checked, android.R.attr.state_pressed });
                // add 'enabled', required if on fragment.
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked, android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_enabled, -android.R.attr.state_checked, android.R.attr.state_pressed });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled, android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled, -android.R.attr.state_checked });
                break;
            default:
                // TODO - investigate how to get rid of this 'default' case, and if we can just use 
                // an 'android.R.attr.drawable' case instead 
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_checked });
                states.add(new int[] { -android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_enabled, -android.R.attr.state_checked });
                break;
        }
        // @formatter:on
        states.add(new int[] { initialValue });
        if (initialValue == android.R.attr.drawable) {
            states.add(new int[] {});
        }
        return states.toArray(new int[states.size()][]);
    }
}
