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
/**
 * 
 */
package com.pixate.freestyle.styling.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.widget.TextView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarTabIcon;

/**
 * {@link Tab} style adapter for tabs on the {@link ActionBar} instance. The Tab
 * is accessible via the <code>action-bar-tab</code> selector.<br>
 * There are a few other {@link ActionBar} elements that can be styled. Here is
 * how to access those internal views via their Android-assigned IDs. You may
 * use these IDs in the CSS to access those widgets directly:
 * 
 * <pre>
 * Home view up image .... => 'up' (ImageView)
 * Home view home image .. => 'home' (ImageView)
 * Title ................. => 'action_bar_title' (TextView)
 * Subtitle .............. => 'action_bar_subtitle' (TextView)
 * The action bar view ... => 'action_bar' (ActionBarView)
 * </pre>
 * 
 * For example, this will style the first tab in the ActionBar, and may also
 * style the rest of the tabs by accessing them by order (e.g. n + 1, n + 2
 * ...):
 * 
 * <pre>
 * action-bar-tab:nth-child(n) icon {
 *   background-image: url(inactive.svg);
 *   background-size: 48px;
 * }
 * 
 * action-bar-tab:nth-child(n) icon:selected {
 *   background-image: url(active.svg);
 *   background-size: 48px;
 * }
 * 
 * </pre>
 * 
 * In order to style the inner view of the tab, you'll need to provide the path
 * for it. For example, the default implementation uses a {@link TextView}, so
 * it's accessible like that:
 * 
 * <pre>
 * 
 * action-bar-tab text-view {
 *   color: green;
 * }
 * action-bar-tab text-view:pressed {
 *   color: red;
 * }
 * 
 * Or even...
 * 
 * action-bar-tab:nth-child(n) text-view {
 *   background-image: linear-gradient(red, green);
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXTabViewStyleAdapter extends PXViewStyleAdapter {

    private static String ELEMENT_NAME = "action-bar-tab";

    private static PXTabViewStyleAdapter instance;

    public static PXTabViewStyleAdapter getInstance() {
        synchronized (PXTabViewStyleAdapter.class) {
            if (instance == null) {
                instance = new PXTabViewStyleAdapter();
            }
        }
        return instance;
    }

    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }


    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> result = new ArrayList<Object>();
        result.add(new PXVirtualActionBarTabIcon(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default Tab states list (doing the same thing as we do with a
        // Button)
        // @formatter:off
        // { -android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed }
        // { -android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed }
        // { android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed }
        // { android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed }
        // { -android.R.attr.state_focused, -android.R.attr.state_selected, android.R.attr.state_pressed }
        // { -android.R.attr.state_focused, android.R.attr.state_selected, android.R.attr.state_pressed }
        // { android.R.attr.state_focused, -android.R.attr.state_selected, android.R.attr.state_pressed }
        // { android.R.attr.state_focused, android.R.attr.state_selected, android.R.attr.state_pressed }
        // @formatter:on

        List<int[]> states = new ArrayList<int[]>(4);
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed });
                break;
            case android.R.attr.state_pressed:
                // we give priority to 'pressed' over anything else
                states.add(new int[] { -android.R.attr.state_focused, -android.R.attr.state_selected, android.R.attr.state_pressed });
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_selected, android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_selected, android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_selected, android.R.attr.state_pressed });
                break;
            case android.R.attr.state_selected:
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_selected, -android.R.attr.state_pressed });
                break;
            case android.R.attr.drawable:
                // map the default state to an recognized state set
                states.add(new int[] { -android.R.attr.state_focused, -android.R.attr.state_selected, -android.R.attr.state_pressed });
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
