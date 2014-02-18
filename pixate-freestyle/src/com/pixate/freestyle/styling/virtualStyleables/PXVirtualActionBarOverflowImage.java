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
package com.pixate.freestyle.styling.virtualStyleables;

import android.app.ActionBar;

/**
 * A virtual {@link ActionBar} child for styling the 'overflow' background image
 * (e.g. the menu image).
 * 
 * @see {@linkplain http://developer.android.com/reference/android/R.attr.html#
 *      actionOverflowButtonStyle}
 * @author Shalom Gibly
 */
public class PXVirtualActionBarOverflowImage extends PXVirtualStyleable {

    /**
     * Constructs a new {@link PXVirtualActionBarOverflowImage}
     * 
     * @param parent The parent view
     */
    public PXVirtualActionBarOverflowImage(Object parent) {
        super(parent);
    }
}
