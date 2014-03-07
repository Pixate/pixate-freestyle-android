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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixate.freestyle.R;

/**
 * The fragment that displays list view
 */
public class ListViewFragment extends Fragment {

    /** Linear layouts in scroll view to express list view items */
    LinearLayout layoutLorem;
    LinearLayout layoutIpsum;
    LinearLayout layoutDolor;

    /** Inflater to inflate each row item */
    LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, null);

        layoutLorem = (LinearLayout) view.findViewById(R.id.layoutListLorem);
        layoutIpsum = (LinearLayout) view.findViewById(R.id.layoutListIpsum);
        layoutDolor = (LinearLayout) view.findViewById(R.id.layoutListDolor);

        mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        InitList();

        return view;
    }

    /** Initialize list view - Inflating all list items to scroll views */
    private void InitList() {
        String[] listlorem = getActivity().getResources().getStringArray(R.array.list_lorem);
        for (int i = 0; i < listlorem.length; i++) {
            AddRowItem(layoutLorem, i, listlorem[i]);
        }

        String[] listipsum = getActivity().getResources().getStringArray(R.array.list_ipsum);
        for (int i = 0; i < listipsum.length; i++) {
            AddRowItem(layoutIpsum, i, listipsum[i]);
        }

        String[] listdolor = getActivity().getResources().getStringArray(R.array.list_dolor);
        for (int i = 0; i < listdolor.length; i++) {
            AddRowItem(layoutDolor, i, listdolor[i]);
        }
    }

    /**
     * To add item into parent view.
     */
    private void AddRowItem(LinearLayout parentView, int nIndex, String sItemTitle) {
        View view = mInflater.inflate(R.layout.view_list_item, null);

        /** Set background with back1 and back2 according to the index number */
        view.findViewById(R.id.layoutListItem)
                .setBackgroundResource(
                        nIndex % 2 == 0 ? R.drawable.list_view_item_back1
                                : R.drawable.list_view_item_back2);
        ((TextView) view.findViewById(R.id.txtItem)).setText(sItemTitle);
        view.findViewById(R.id.layoutListItem).setTag(sItemTitle);
        view.findViewById(R.id.layoutListItem).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ContainerFragment parentFragment = (ContainerFragment) getParentFragment();
                ListViewDetailFragment detailFragment = new ListViewDetailFragment();
                Bundle args = new Bundle();
                args.putString(ListViewDetailFragment.KEY_ITEM_INFO, v.getTag().toString());
                detailFragment.setArguments(args);
                parentFragment.replaceFragment(detailFragment);
            }
        });

        parentView.addView(view);
    }

}
