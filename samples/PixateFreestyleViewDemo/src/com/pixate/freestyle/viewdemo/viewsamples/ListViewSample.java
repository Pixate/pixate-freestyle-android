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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.pixate.freestyle.PixateFreestyle;

/**
 * Creates {@link ListView} and adds it into a layout.
 * 
 * @author shalom
 */
public class ListViewSample extends ViewSampleBase {

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.viewdemo.viewsamples.ViewSample#createViews(android.content
     * .Context, android.view.ViewGroup)
     */
    @Override
    public void createViews(Context context, ViewGroup layout) {
        String[] vals = new String[] { "apple", "banana", "cherry", "date", "eggfruit", "fig",
                "grapefruit", "honeydew", "ilama", "jambolan", "kepel", "lemon", "mango",
                "nactarine", "orange", "pineapple", "quandong", "rambutan", "strawberry", "tomato",
                "ugli", "voavanga", "watermelon", "xigua", "yellow", "watermelon", "zucchini" };

        ListAdapter adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, vals);
        ListView listView = new ListView(context);
        PixateFreestyle.setStyleClass(listView, "myListView");
        listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        listView.setAdapter(adapter);
        layout.addView(listView);

        addView(listView);
    }
}
