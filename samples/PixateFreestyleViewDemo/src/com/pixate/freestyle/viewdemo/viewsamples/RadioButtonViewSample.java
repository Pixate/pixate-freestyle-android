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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pixate.freestyle.PixateFreestyle;

/**
 * Creates {@link RadioButton} views and adds them into a layout.
 * 
 * @author shalom
 */
public class RadioButtonViewSample extends ViewSampleBase {

    @Override
    public void createViews(Context context, ViewGroup layout) {
        RadioGroup group = new RadioGroup(context);

        TextView tv = new TextView(context);
        PixateFreestyle.setStyleId(tv, "rgTitle");
        tv.setText("Will you be attending?");
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        group.addView(tv);

        String[] titles = new String[] { "Yes", "No", "Maybe" };

        Integer id = null;
        for (String title : titles) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            RadioButton rb = new RadioButton(context);

            PixateFreestyle.setStyleClass(rb, "myRadioButton");
            PixateFreestyle.setStyleId(rb, "myRadioButton" + title);
            rb.setText(title);
            group.addView(rb, params);

            if (id == null) {
                id = rb.getId();
            }
        }

        layout.addView(group,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        group.check(id);
        addView(group);
    }

}
