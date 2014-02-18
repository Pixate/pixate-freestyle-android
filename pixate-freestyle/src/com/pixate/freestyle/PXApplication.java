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

import android.app.Application;

/**
 * <p>
 * A convenience class for application developers who are targeting only ICS and
 * above and who don't need their own {@link android.app.Application
 * Application} class. In their AndroidManifest.xml, they can simply set the
 * name of their application to <code>com.pixate.freestyle.PXApplication</code>, then this
 * class will be instantiated automatically when their application starts. That
 * means {@link PixateFreestyle#init(android.content.Context) PixateFreestyle.init()} will be
 * called automatically.
 * </p>
 * <p>
 * AndroidManifest.xml example:
 * </p>
 * 
 * <pre>
 * &lt;application
 *     android:name="com.pixate.freestyle.PXApplication"
 *     (etc. etc.)
 *     /&gt;
 * </pre>
 * 
 * @author Bill Dawson
 */
public class PXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PixateFreestyle.init(this);
    }
}
