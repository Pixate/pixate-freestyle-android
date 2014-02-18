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
package com.pixate.freestyle.viewdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.util.StringUtil;
import com.pixate.freestyle.viewdemo.R;
import com.pixate.freestyle.viewdemo.data.ViewsData;
import com.pixate.freestyle.viewdemo.viewsamples.ViewSample;

/**
 * A fragment representing a single view detail screen. This fragment is either
 * contained in a {@link ViewListActivity} in two-pane mode (on tablets) or a
 * {@link ViewDetailActivity} on handsets.
 */
public class ViewDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private ViewsData.ViewItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This fragment must be initialized as well (just like the Activity)
        PixateFreestyle.init(getActivity());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ViewsData.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view_detail, container, false);

        if (mItem != null) {
            // set the views
            ViewSample viewSample = mItem.getViewSample();
            final ViewGroup viewsHolder = (ViewGroup) rootView.findViewById(R.id.holder);
            viewSample.createViews(getActivity(), viewsHolder);

            // load the CSS styling for the sample
            String css = ViewsData.getCSS(getActivity(), mItem);

            // Set up syntax highlighting
            WebView cssView = (WebView) rootView.findViewById(R.id.css_style);
            WebSettings s = cssView.getSettings();
            s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            s.setUseWideViewPort(false);
            s.setAllowFileAccess(true);
            s.setBuiltInZoomControls(true);
            s.setSupportZoom(true);
            s.setSupportMultipleWindows(false);
            s.setJavaScriptEnabled(true);

            StringBuilder contentString = new StringBuilder();
            contentString.append("<html><head>");
            contentString
                    .append("<link href='file:///android_asset/prettify/prettify.css' rel='stylesheet' type='text/css'/> ");
            contentString
                    .append("<script src='file:///android_asset/prettify/prettify.js' type='text/javascript'></script> ");
            contentString
                    .append("<script src='file:///android_asset/prettify/lang-css.js' type='text/javascript'></script> ");
            contentString
                    .append("</head><body onload='prettyPrint()'><code class='prettyprint lang-css'>");
            contentString.append(TextUtils.htmlEncode(css).replaceAll("\n", "<br>")
                    .replaceAll(" ", "&nbsp;").replaceAll("\t", "&nbsp;&nbsp;"));
            contentString.append("</code> </html> ");
            cssView.getSettings().setUseWideViewPort(true);
            cssView.loadDataWithBaseURL("file:///android_asset/prettify/",
                    contentString.toString(), "text/html", StringUtil.EMPTY, StringUtil.EMPTY);

            // to aid in styling the css text shows in the textview, set its
            // ID. Eventually will not be needed.
            if (!"css-style".equals(PixateFreestyle.getStyleId(cssView))) {
                PixateFreestyle.setStyleId(cssView, "css-style", true);
            }

            // Style
            viewSample.style(css);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Need to remove views from our internal collection, else
        // we keep trying to style them.
        if (mItem != null) {
            ((ViewSample) mItem.getViewSample()).destroyViews();
        }
    }
}
