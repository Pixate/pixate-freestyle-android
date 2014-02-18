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
package com.pixate.freestyle.viewdemo.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pixate.freestyle.viewdemo.viewsamples.ButtonViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.CheckBoxViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.EditTextViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.GridViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.ImageButtonViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.ImageViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.ListViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.RadioButtonViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.SpinnerViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.TextViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.ToggleButtonViewSample;
import com.pixate.freestyle.viewdemo.viewsamples.ViewSample;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class ViewsData {

    private static final String STYLES_DIR = "styles/";

    /**
     * An array of View items.
     */
    public static List<ViewItem> ITEMS = new ArrayList<ViewItem>();

    /**
     * A map of View items
     */
    public static Map<String, ViewItem> ITEM_MAP = new HashMap<String, ViewItem>();

    static {
        // @formatter:off
        addItem(new ViewItem(Button.class.getSimpleName(), "button.css", new ButtonViewSample()));
        addItem(new ViewItem(CheckBox.class.getSimpleName(), "checkbox.css", new CheckBoxViewSample()));
        addItem(new ViewItem(RadioButton.class.getSimpleName(), "radio_button.css",  new RadioButtonViewSample()));
        addItem(new ViewItem(ToggleButton.class.getSimpleName(), "toggle_button.css", new ToggleButtonViewSample()));
        addItem(new ViewItem(ImageView.class.getSimpleName(), "image_view.css", new ImageViewSample()));
        addItem(new ViewItem(ImageButton.class.getSimpleName(), "image_button.css", new ImageButtonViewSample()));
        addItem(new ViewItem(ListView.class.getSimpleName(), "list_view.css", new ListViewSample()));
        addItem(new ViewItem(GridView.class.getSimpleName(), "grid_view.css", new GridViewSample()));
        addItem(new ViewItem(TextView.class.getSimpleName(), "text-view.css", new TextViewSample()));
        addItem(new ViewItem(EditText.class.getSimpleName(), "edit-text.css", new EditTextViewSample()));
        addItem(new ViewItem(Spinner.class.getSimpleName(), "spinner.css", new SpinnerViewSample()));
        // @formatter:on
    }

    private static void addItem(ViewItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A view item representing a piece of content.
     */
    public static class ViewItem {
        private String id;
        private String cssResource;
        private ViewSample sample;

        public ViewItem(String id, String cssFileName, ViewSample sample) {
            this.id = id;
            this.cssResource = cssFileName;
            this.sample = sample;
        }

        public String getId() {
            return id;
        }

        public String getCSSFileName() {
            return cssResource;
        }

        public ViewSample getViewSample() {
            return sample;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    /**
     * Returns the CSS content for the given item.
     * 
     * @param context
     * @param item
     * @return The CSS content from the assets.
     */
    public static String getCSS(Context context, ViewItem item) {
        try {
            InputStream inputStream = context.getAssets().open(STYLES_DIR + item.getCSSFileName());
            ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray());
        } catch (IOException e) {
            Log.e(ViewsData.class.getSimpleName(),
                    "Can't read the CSS data from " + item.getCSSFileName(), e);
            return "/* Error reading the CSS content */";
        }
    }
}
