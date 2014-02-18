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
import android.widget.EditText;
import android.widget.TextView;

import com.pixate.freestyle.PixateFreestyle;

/**
 * Creates {@link EditText} views and adds them into a layout.
 * 
 * @author shalom
 */
public class TextViewSample extends ViewSampleBase {

    @Override
    public void createViews(Context context, ViewGroup layout) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        textView.setClickable(true);
        PixateFreestyle.setStyleClass(textView, "myTextView");
        layout.addView(textView);

        addView(textView);
    }

}
