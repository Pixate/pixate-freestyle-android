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
import android.widget.GridView;
import android.widget.ListAdapter;

import com.pixate.freestyle.PixateFreestyle;

/**
 * Creates {@link GridView} view and adds it into a layout.
 * 
 * @author shalom
 */
public class GridViewSample extends ViewSampleBase {

    @Override
    public void createViews(Context context, ViewGroup layout) {
        GridView gridView = new GridView(context);
        PixateFreestyle.setStyleClass(gridView, "myGridView");

        gridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        String[] vals = new String[] { "Apples", "Pears", "Oranges", "Watermelon", "Kumquats",
                "Cherries", "Strawberries", "Kiwi" };

        ListAdapter adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1, vals);
        gridView.setAdapter(adapter);

        layout.addView(gridView);

        addView(gridView);
    }
}
