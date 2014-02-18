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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PXLifecycleCallbacks implements ActivityLifecycleCallbacks {

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        PixateFreestyle.init(activity);
    }

    public void onActivityPaused(Activity activity) {
        // No-op
    }

    public void onActivityResumed(Activity activity) {
        // No-op
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // No-op
    }

    public void onActivityStarted(Activity activity) {
        // No-op
    }

    public void onActivityStopped(Activity activity) {
        // No-op
    }

    public void onActivityDestroyed(Activity activity) {
        // TODO Auto-generated method stub

    }

}
