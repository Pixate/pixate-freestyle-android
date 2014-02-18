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

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.pixate.freestyle.styling.adapters.PXActionBarOverflowStyleAdapter;
import com.pixate.freestyle.styling.adapters.PXImageViewStyleAdapter;

/**
 * Virtual adapter for {@link ActionBar} 'overflow' button background (the
 * action-bar menu).
 * 
 * <pre>
 * <code>
 *   - image
 * </code>
 * </pre>
 * 
 * For example, setting the overflow button background for an {@link ActionBar}
 * is done like this:
 * 
 * <pre>
 * action-bar-overflow image {
 *   background-image: url(overflow_bg.svg);
 *   background-size: 110px;
 * }
 * 
 * action-bar-overflow image {
 *   background-image: url(overflow_pressed_bg.svg);
 *   background-size: 110px;
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
public class PXVirtualActionBarOverflowAdapter extends PXVirtualImageViewImageAdapter {

    private static PXVirtualActionBarOverflowAdapter instance;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualActionBarOverflowAdapter}
     */
    public static PXVirtualActionBarOverflowAdapter getInstance() {
        synchronized (PXVirtualActionBarOverflowAdapter.class) {
            if (instance == null) {
                instance = new PXVirtualActionBarOverflowAdapter();
            }
        }
        return instance;
    }

    protected PXVirtualActionBarOverflowAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.virtualAdapters.PXVirtualImageViewImageAdapter
     * #getViewDrawable(android.widget.ImageView)
     */
    @Override
    protected Drawable getViewDrawable(ImageView view) {
        // The states we need exist on the ImageView background (not on the
        // getDrawable(), like in a 'regular' image view).
        return view.getBackground();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.virtualAdapters.PXVirtualImageViewImageAdapter
     * #getAdapter()
     */
    protected PXImageViewStyleAdapter getAdapter() {
        return PXActionBarOverflowStyleAdapter.getInstance();
    }
}
