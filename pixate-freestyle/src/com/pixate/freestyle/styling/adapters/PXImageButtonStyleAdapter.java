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

import android.widget.ImageButton;
import android.widget.ImageView;

import com.pixate.freestyle.annotations.PXDocElement;

/**
 * A style adapter for {@link ImageButton} widgets. This adapter supports the
 * regular view styling properties, plus a few special properties that are
 * unique for the {@link ImageButton} and to its parent {@link ImageView}.
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
 * .imageButton {
 *     tint: #450022FF;
 *     transform: matrix(0.8660254037844387, 0.49999999999999994, -0.49999999999999994, 0.8660254037844387, 0, 0);
 *     scale-type: matrix;
 *     background-image: url(default-bg.svg);
 * }
 * 
 * .imageButton:pressed {
 *     background-image: url(pressed-bg.svg);
 * }
 * </pre>
 * 
 * And to set the image properties (virtual child of 'image'):
 * 
 * <pre>
 * .imageButton image {
 *     background-image: url(mic-on.svg);
 *     background-size: 300px;
 * }
 * 
 * .imageButton image:pressed {
 *     background-image: url(mic-off.svg);
 *     background-size: 300px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement
public class PXImageButtonStyleAdapter extends PXImageViewStyleAdapter {

    private static String ELEMENT_NAME = "image-button";
    private static PXImageButtonStyleAdapter instance;

    /**
     * Returns an instance of this {@link PXImageButtonStyleAdapter}
     */
    public static PXImageButtonStyleAdapter getInstance() {
        synchronized (PXImageButtonStyleAdapter.class) {

            if (instance == null) {
                instance = new PXImageButtonStyleAdapter();
            }
        }
        return instance;
    }

    protected PXImageButtonStyleAdapter() {
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
     * @see com.pixate.freestyle.styling.adapters.PXCompoundButtonStyleAdapter#
     * createAdditionalDrawableStates(int)
     */
    @Override
    public int[][] createAdditionalDrawableStates(int initialValue) {
        // A default ImageButton instance states contain the following
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
        // { android.R.attr.state_focused }
        // {  } - default 'android.R.attr.drawable'
        // @formatter:on
        List<int[]> states = new ArrayList<int[]>(4);
        // check for some special cases for the image button.
        // @formatter:off
        switch (initialValue) {
            case android.R.attr.state_enabled:
                states.add(new int[] { -android.R.attr.state_window_focused, android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.state_focused:
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
                break;
            case android.R.attr.drawable:
                states.add(new int[] { -android.R.attr.state_window_focused, -android.R.attr.state_enabled });
                states.add(new int[] { android.R.attr.state_focused, android.R.attr.state_enabled });
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
