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
package com.pixate.freestyle.viewdemo.viewsamples;

import android.content.Context;
import android.view.ViewGroup;

/**
 * A view sample interface.
 * 
 * @author shalom
 */
public interface ViewSample {

    /**
     * Creates and insert the views into the layout.
     * 
     * @param context
     * @param layout
     */
    void createViews(Context context, ViewGroup layout);

    /**
     * Called when layout view is being destroyed and thus its children can be
     * disposed.
     */
    void destroyViews();

    /**
     * Make a Pixate call to style the created view with the given CSS.
     * 
     * @param css
     */
    void style(String css);
}
