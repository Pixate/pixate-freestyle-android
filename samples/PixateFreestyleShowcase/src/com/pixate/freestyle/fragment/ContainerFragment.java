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
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixate.freestyle.R;

/**
 * Base class that handles fragment transaction with child fragment manager.
 */
public class ContainerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, null);
    }

    /**
     * Replace an existing fragment that was added to a container. This is
     * essentially the same as calling
     * {@link FragmentTransaction#remove(Fragment)} for all currently added
     * fragments that were added with the same containerViewId and then
     * {@link FragmentTransaction#add(int, Fragment, String)} with the same
     * arguments given here.
     * 
     * @param fragment The fragment to be added
     */
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, true);
    }

    /**
     * Replace an existing fragment that was added to a container. This is
     * essentially the same as calling
     * {@link FragmentTransaction#remove(Fragment)} for all currently added
     * fragments that were added with the same containerViewId and then
     * {@link FragmentTransaction#add(int, Fragment, String)} with the same
     * arguments given here.
     * 
     * @param fragment The fragment to be added
     * @param addToBackStack {@code true} if this transaction should be added to
     *            back stack
     */
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.framelayout_container, fragment);
        ft.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    /**
     * Pop the top state off the back stack with child fragment manager.
     * 
     * @return {@code true} if there was one to pop, else {@code false}.
     */
    public boolean popFragment() {
        return getChildFragmentManager().popBackStackImmediate();
    }
}
