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

import android.app.ActionBar;
import android.widget.ImageButton;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualActionBarOverflowImage;

/**
 * A style adapter for {@link ActionBar} 'overflow' menu button. This adapter is
 * very similar to the {@link PXImageButtonStyleAdapter}, as the Android's
 * internal implementation for the menu button extends {@link ImageButton}.
 * 
 * <pre>
 * - scale-type: center | center-crop | center-inside | fit-center | fit-end | fit-start | fit-xy | matrix
 * - max-height: px
 * - max-width: px
 * - view-bounds: adjust | none
 * - tint: color
 * - transform: matrix (inherited from the view styles, but used here when the scale-type is set to 'matrix')
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * action-bar-overflow {
 *     tint: #450022FF;
 *     transform: matrix(0.8660254037844387, 0.49999999999999994, -0.49999999999999994, 0.8660254037844387, 0, 0);
 *     scale-type: matrix;
 *     background-image: url(default-bg.svg);
 * }
 * 
 * </pre>
 * 
 * And to set the image properties (virtual child of 'image'):
 * 
 * <pre>
 * action-bar-overflow image {
 *     background-image: url(mic-on.svg);
 *     background-size: 300px;
 * }
 * 
 * action-bar-overflow image:pressed {
 *     background-image: url(mic-off.svg);
 *     background-size: 300px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXActionBarOverflowStyleAdapter extends PXImageViewStyleAdapter {

    private static String ELEMENT_NAME = "action-bar-overflow";
    private static PXActionBarOverflowStyleAdapter instance;

    /**
     * Returns an instance of this {@link PXActionBarOverflowStyleAdapter}
     */
    public static PXActionBarOverflowStyleAdapter getInstance() {
        synchronized (PXActionBarOverflowStyleAdapter.class) {

            if (instance == null) {
                instance = new PXActionBarOverflowStyleAdapter();
            }
        }
        return instance;
    }

    protected PXActionBarOverflowStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXImageViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getVirtualChildren
     * (java.lang.Object)
     */
    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        // Intentionally don't add the super to avoid conflict.
        List<Object> result = new ArrayList<Object>(1);
        result.add(new PXVirtualActionBarOverflowImage(styleable));
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // These are the states that are set for the default implementation of
        // that menu button. Note that the 'pressed' state are actually taking
        // TransitionDrawables, which we don't support yet.
        // @formatter:off
        // { android.R.attr.state_focused, -android.R.attr.state_enabled, android.R.attr.state_pressed }
        // { android.R.attr.state_focused, -android.R.attr.state_enabled }
        // { android.R.attr.state_focused, android.R.attr.state_pressed }
        // { -android.R.attr.state_focused, android.R.attr.state_pressed }
        // { android.R.attr.state_focused }
        // {  } - default 'android.R.attr.drawable'
        // @formatter:on
        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases for the image button.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_enabled });
                break;
            case android.R.attr.state_pressed:
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_enabled, android.R.attr.state_pressed});
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_pressed});
                states.add(new int[] { -android.R.attr.state_focused, android.R.attr.state_pressed});
                break;
            case android.R.attr.drawable:
                states.add(new int[] { android.R.attr.state_focused, -android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_enabled });
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
