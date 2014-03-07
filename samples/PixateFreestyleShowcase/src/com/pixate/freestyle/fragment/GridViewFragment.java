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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pixate.freestyle.R;

/**
 * The fragment that displays grid view
 */
public class GridViewFragment extends Fragment {

    /** Grid view which will show on GridView tab screen */
    GridView grid;

    /** Grid item width and height according to screen resolution */
    int m_nItemWidth;
    int m_nItemHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gridview, null);

        /**
         * Getting grid item width and height, column numbers are fixed as 2 and
         * calculate each item's width and height
         */
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int _nScreenWidth = display.getWidth();
        int _nRealSpaceWidth = _nScreenWidth
                - getResources().getDimensionPixelSize(R.dimen.grid_item_spacing)
                * (getResources().getInteger(R.integer.grid_col_count) + 1);
        m_nItemWidth = (_nRealSpaceWidth / getResources().getInteger(R.integer.grid_col_count));
        m_nItemHeight = m_nItemWidth;

        grid = (GridView) view.findViewById(R.id.gridview);
        ImageAdapter adapter = new ImageAdapter(getActivity(), m_nItemWidth, m_nItemHeight);
        grid.setAdapter(adapter);

        return view;
    }

}

/**
 * ImageAdapter for grid data
 */
class ImageAdapter extends BaseAdapter {

    /** Mainframe Activity context */
    private Context mContext;

    /** Grid view item size */
    int mWidth;
    int mHeight;

    /** Total item count is set as 50 */
    private static int nTotal = 50;

    public ImageAdapter(Context c, int nWid, int nHet) {
        // TODO Auto-generated constructor stub
        mContext = c;
        mWidth = nWid;
        mHeight = nHet;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return nTotal;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return R.drawable.grid_item_background;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        /** ImageView for each grid view item */
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setBackgroundResource(R.drawable.grid_item_background);

        return imageView;
    }

}
