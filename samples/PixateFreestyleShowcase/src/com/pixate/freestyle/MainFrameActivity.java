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
package com.pixate.freestyle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;

import com.pixate.freestyle.fragment.ContainerFragment;
import com.pixate.freestyle.fragment.containers.ButtonsContainer;
import com.pixate.freestyle.fragment.containers.GridViewContainer;
import com.pixate.freestyle.fragment.containers.IconsContainer;
import com.pixate.freestyle.fragment.containers.ListViewContainer;
import com.pixate.freestyle.fragment.containers.TypographyContainer;

/**
 * The main frame activity for application.
 * 
 * @author AlexHong
 */
public class MainFrameActivity extends FragmentActivity implements OnTabChangeListener {

    /** Tag for log. */
    public static final String TAG = MainFrameActivity.class.getSimpleName();

    /** The tab host, supports fragment. */
    private FragmentTabHost tabHost;

    /** The tab widget, located at the bottom of activity. */
    private TabWidget tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        /** Set this theme for default switch box */
        setTheme(R.style.AppThemeLight);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initTabs();

        com.pixate.freestyle.PixateFreestyle.init(this);
    }

    @SuppressLint("NewApi")
    private void initTabs() {
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.setOnTabChangedListener(this);
        tabs = (TabWidget) findViewById(android.R.id.tabs);
        // tabs.setShowDividers(TabWidget.SHOW_DIVIDER_NONE);

        addTab(R.drawable.tab_icon_typography, TypographyContainer.class);
        addTab(R.drawable.tab_icon_listview, ListViewContainer.class);
        addTab(R.drawable.tab_icon_gridview, GridViewContainer.class);
        addTab(R.drawable.tab_icon_buttons, ButtonsContainer.class);
        addTab(R.drawable.tab_icon_icons, IconsContainer.class);
    }

    /**
     * Convenient method to add a tab to the tab widget at bottom.
     * 
     * @param resId The resource id of image shown on indicator
     * @param content The class type of content
     */
    private void addTab(int resId, Class<? extends Fragment> content) {
        ImageView indicator = new ImageView(this);
        indicator.setScaleType(ScaleType.CENTER_INSIDE);
        indicator.setBackgroundResource(R.drawable.tab_bg);
        indicator.setImageResource(resId);
        TabSpec spec = tabHost.newTabSpec(content.getSimpleName()).setIndicator(indicator);
        tabHost.addTab(spec, content, null);
    }

    @Override
    public void onTabChanged(String tabId) {
        // TODO tab changed, do extra stuff here, such as changing icon shown in
        // title
        Log.i(TAG, "onTabChanged: " + tabId);
    }

    @Override
    public void onBackPressed() {
        String currTag = tabHost.getCurrentTabTag();
        ContainerFragment container = (ContainerFragment) getSupportFragmentManager()
                .findFragmentByTag(currTag);
        if (!container.popFragment()) {
            super.onBackPressed();
        }
    }

}
