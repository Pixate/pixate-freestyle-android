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
package com.pixate.freestyle.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixate.freestyle.R;

/**
 * The fragment that displays icons list
 */
public class IconsFragment extends Fragment {

    /** Grid view that will contain all the icons */
    GridView grid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icons, null);

        grid = (GridView) view.findViewById(R.id.gridicon);
        LayoutIconAdapter adapter = new LayoutIconAdapter(getActivity());
        grid.setAdapter(adapter);

        return view;
    }
}

/**
 * LayoutIconAdapter for icon grid views data
 */
class LayoutIconAdapter extends BaseAdapter {

    /** Mainframe Activity context */
    private Context mContext;

    /** String array for Icon resource names */
    String[] icons;

    /** Inflater for item layout */
    LayoutInflater mInflater;

    public LayoutIconAdapter(Context c) {
        // TODO Auto-generated constructor stub
        mContext = c;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        icons = mContext.getResources().getStringArray(R.array.icons);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return icons[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        /** LinearLayout for each grid view icon item */
        LinearLayout iconLayout;
        if (convertView == null) {
            iconLayout = (LinearLayout) mInflater.inflate(R.layout.view_icon_item, null);
        } else {
            iconLayout = (LinearLayout) convertView;
        }

        ((ImageView) iconLayout.findViewById(R.id.image_view_icon)).setImageResource(mContext
                .getResources().getIdentifier(icons[position], "drawable",
                        mContext.getPackageName()));
        ((TextView) iconLayout.findViewById(R.id.text_view_name)).setText(icons[position]);

        return iconLayout;
    }

}
