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
package com.pixate.freestyle.actionbardemo;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import com.pixate.freestyle.PixateFreestyle;
import com.pixate.freestyle.actionbardemo.R;

/**
 * Main activity
 * 
 * @author shalom
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Tab tab = actionBar.newTab().setText(R.string.tab1_title)
                .setTabListener(new TabListener(this, Fragment1.class.getName()));
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab2_title)
                .setTabListener(new TabListener(this, Fragment2.class.getName()));
        actionBar.addTab(tab);
        tab = actionBar.newTab().setText(R.string.tab3_title)
                .setTabListener(new TabListener(this, Fragment3.class.getName()));
        actionBar.addTab(tab);

        // Initiate Pixate
        PixateFreestyle.init(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Tab listener implementation
     */
    private class TabListener implements ActionBar.TabListener {
        private Fragment fragment;
        private final Activity activity;
        private final String fragmentName;

        public TabListener(Activity activity, String fragmentName) {
            this.activity = activity;
            this.fragmentName = fragmentName;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // no-op
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            fragment = Fragment.instantiate(activity, fragmentName);
            ft.add(android.R.id.content, fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
            fragment = null;
        }
    }
}
