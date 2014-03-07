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
package com.pixate.freestyle.fragment.containers;

import android.os.Bundle;

import com.pixate.freestyle.fragment.ContainerFragment;

/**
 * Base container class, subclasses need to specified how to initialize content.
 */
public abstract class BaseContainer extends ContainerFragment {

    protected boolean contentInited = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!contentInited) {
            contentInited = true;
            initContent();
        }
    }

    protected abstract void initContent();
}
