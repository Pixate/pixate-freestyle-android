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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixate.freestyle.R;

/**
 * The fragment that displays detail screen of each list view item
 */
public class ListViewDetailFragment extends Fragment {

    /** The key for getting item info from bundle. */
    public static final String KEY_ITEM_INFO = "KEY_ITEM_INFO";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview_detail, null);

        Bundle bundle = this.getArguments();
        ((TextView) view.findViewById(R.id.txtDetail)).setText(bundle.getString(KEY_ITEM_INFO));

        view.findViewById(R.id.btnPrev).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ContainerFragment parentFragment = (ContainerFragment) getParentFragment();
                parentFragment.popFragment();
            }
        });

        return view;
    }

}
