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

import android.widget.RadioButton;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualCompoundButtonIcon;

/**
 * A {@link RadioButton} style adapter.<br>
 * Styling a {@link RadioButton} button is done by setting the checked state
 * (pseudo class) and the default state in the CSS. For example:
 * 
 * <pre>
 * Style just the radio button icon (the default round radio):
 * 
 * #radio icon:checked {
 *     background-image: url(check-on.svg);
 *     background-size: 60;
 *     color: white;
 * }
 * 
 * #radio icon  {
 *     background-image: url(check-off.svg);
 *     background-size: 60;
 *     color: green;
 * }
 * 
 * While this will style the entire radio button background:
 * 
 * #radio:checked {
 *     background-image: url(checked-bg.svg);
 * }
 * 
 * #radio {
 *     background-image: url(unchecked-bg.svg);
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXRadioButtonStyleAdapter extends PXCompoundButtonStyleAdapter {
    private static String ELEMENT_NAME = "radio-button";
    private static PXRadioButtonStyleAdapter instance;

    protected PXRadioButtonStyleAdapter() {
    }

    /**
     * Returns an instance of this {@link PXRadioButtonStyleAdapter}
     */
    public static PXRadioButtonStyleAdapter getInstance() {
        synchronized (PXButtonStyleAdapter.class) {

            if (instance == null) {
                instance = new PXRadioButtonStyleAdapter();
            }
        }
        return instance;
    }

    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        // Note that to avoid forcing a PXVirtualCompoundButtonIcon into every
        // compound adapter, the super class only adds one when dealing with
        // actual PXCompoundButtoStyleAdapters and not its subclasses.
        // In this case, we want to use one, so we have to add it.
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualCompoundButtonIcon(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * getElementName(java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A radio button requires some unique states of its own to function
        // properly.
        // A default RadioButton instance states contain the following
        // values. Here, we try to generate what's missing when we only get a
        // single 'state' value from our pseudo class. Note that a state will
        // still be applied when the other values in the array are negative
        // (which implied a 'not').
        // @formatter:off
        // { -android.R.attr.state_window_focused, android.R.attr.state_checked }
        // { -android.R.attr.state_window_focused, -android.R.attr.state_checked }
        // { android.R.attr.state_checked, android.R.attr.state_pressed }
        // { -android.R.attr.state_checked, android.R.attr.state_pressed }
        // { android.R.attr.state_focused, android.R.attr.state_checked }
        // { android.R.attr.state_focused, -android.R.attr.state_checked }
        // { -android.R.attr.state_checked}
        // { android.R.attr.state_checked}
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // Check for some special cases for the radio button.
        // Note that the focused state is tricky, since we don't know what to
        // paint in checked/non-checked state. For that we'll need some support
        // for a combined focused&checked state. So for now, we'll only take
        // care of focused and not-checked.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_checked:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_checked, android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_enabled, android.R.attr.state_checked });
                break;
            case android.R.attr.state_pressed:
                states.add(new int[] { -android.R.attr.state_checked, android.R.attr.state_pressed });
                break;
            case android.R.attr.state_focused:
                // see comment above
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_checked });
                break;
            case android.R.attr.drawable:
                // default state: add additional non-checked states
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_checked });
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_checked });
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
