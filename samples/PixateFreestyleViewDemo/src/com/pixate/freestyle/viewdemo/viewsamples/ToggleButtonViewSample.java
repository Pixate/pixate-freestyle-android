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
import android.view.ViewGroup.LayoutParams;
import android.widget.ToggleButton;

import com.pixate.freestyle.PixateFreestyle;

/**
 * Creates {@link ToggleButton} views and adds them into a layout.
 * 
 * @author shalom
 */
public class ToggleButtonViewSample extends ViewSampleBase {

    @Override
    public void createViews(Context context, ViewGroup layout) {
        ToggleButton tb = new ToggleButton(context);
        tb.setText("Vibrate off");
        tb.setTextOff("Vibrate Off");
        tb.setTextOn("Vibrate On");
        PixateFreestyle.setStyleClass(tb, "myToggleButton");

        layout.addView(tb, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        addView(tb);
    }

}
