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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.pixate.freestyle.styling.PXStyleUtils;
import com.pixate.freestyle.styling.PXStylesheet;
import com.pixate.freestyle.styling.PXStylesheet.PXStyleSheetOrigin;
import com.pixate.freestyle.util.PXLog;
import com.pixate.freestyle.util.ViewUtil;

/**
 * <p>
 * Application developers use the static methods in this class to initialize and
 * use Pixate in their applications.
 * </p>
 * <p>
 * If the developer targets only Ice Cream Sandwich and above, he/she can
 * initialize Pixate simply be calling {@link #init(Context) init} one time in
 * the {@link android.app.Application#onCreate() onCreate()} of his
 * {@link android.app.Application Application} class. For example:
 * </p>
 * 
 * <pre>
 * public void onCreate() {
 *     PixateFreestyle.init(this);
 * }
 * </pre>
 * <p>
 * If targeting all versions of Android, then {@link #init(Context) init} needs
 * to be called in each {@link android.app.Activity Activity}'s
 * {@link android.app.Activity#onCreate(android.os.Bundle) onCreate} method. For
 * example:
 * </p>
 * 
 * <pre>
 * public void onCreate(Bundle savedInstanceState) {
 *     PixateFreestyle.init(this);
 * }
 * </pre>
 * <p>
 * Views defined in layout XML files can use the attributes <code>class</code>,
 * <code>android:style</code> and <code>android:id</code> to emulate HTML
 * <code>class</code>, <code>style</code> and <code>id</code> element
 * attributes. Note that <code>class</code> does not contain the
 * <code>android:</code> prefix. Example:
 * </p>
 * 
 * <pre>
 * &lt;Button
 *     android:id="@+id/myButton"
 *     class="critical"
 *     (etc.)
 *     /&gt;
 * </pre>
 * <p>
 * When the layout is inflated, Pixate intercepts the inflation and then has an
 * opportunity to apply the appropriate styling based on the values of those
 * attributes.
 * </p>
 * <p>
 * If a view is created at runtime - and is therefore not going to be styled via
 * Pixate's interception of the layout inflater - the application developer can
 * call {@link #init(View, String, String, String)} to tell Pixate the id, class
 * and style of the view, or set them individually via
 * {@link #setStyleId(View, String)}, {@link #setStyleClass(View, String)}
 * and/or {@link #setStyle(View, String)}.
 * </p>
 * 
 * @author Bill Dawson
 */
public class PixateFreestyle {

    private static final String TAG = PixateFreestyle.class.getSimpleName();
    public static final boolean ICS_OR_BETTER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static String DEFAULT_CSS = "default.css";
    private static AtomicBoolean cssLoaded = new AtomicBoolean(false);

    private static Object mLifecycleCallbacks = null;

    private static boolean mAppInited = false;
    private static Context mAppContext = null;

    /**
     * Initialize Pixate with the given {@link Context}.
     * 
     * @param context
     */
    public static void init(Context context) {
        if (mAppContext == null) {
            mAppContext = context.getApplicationContext();
            // log a version
            Log.i(TAG, String.format("Pixate Freestyle version %s (API version %d)", getVersion(),
                    getApiVersion()));
        }

        if (!cssLoaded.getAndSet(true)) {
            // try to load the default CSS ones.
            PXStylesheet stylesheet = PXStylesheet.getStyleSheetFromFilePath(
                    context.getApplicationContext(), DEFAULT_CSS, PXStyleSheetOrigin.APPLICATION);
            if (stylesheet != null) {
                logErrors(stylesheet.getErrors());
            }
        }

        // Disabled, because we may not even need a class loader.
        // CustomClassLoader.useFor(context);

        if (ICS_OR_BETTER && !mAppInited) {
            initApp(context);
        }

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            WrappedInflaterFactory.applyTo(activity);
            // Grab the 'decorView' which contains the ActionBar and the
            // content.
            View decorView = activity.getWindow().getDecorView();
            ViewUtil.prepareViewGroupListeners((ViewGroup) decorView);

            // We have to get to the tabs in case the ActionBar mode has them
            // enabled.
            ActionBar actionBar = activity.getActionBar();
            // Call the style whatever we can using the ActionBar instance
            // itself (a non-view styling).
            style(actionBar);

        }
    }

    /**
     * Initialize Pixate with the given Fragment.
     * 
     * @param fragment
     */
    public static void init(Fragment fragment) {
        init(fragment.getActivity());
    }

    public static void init(View view, String cssId, String cssClass, String cssStyle) {
        ViewUtil.initView(view, cssId, cssClass, cssStyle);
    }

    /**
     * Returns the Pixate Freestyle product version.
     * 
     * @return The product version.
     */
    public static String getVersion() {
        return Version.PIXATE_FREESTYLE_VERSION;
    }

    /**
     * Returns the Pixate Freestyle API version.
     * 
     * @return The API version.
     */
    public static int getApiVersion() {
        return Version.PIXATE_FREESTYLE_API_VERSION;
    }

    public static void setStyleId(View view, String cssId) {
        ViewUtil.setStyleId(view, cssId);
    }

    public static void setStyleId(View view, String cssId, boolean restyle) {
        ViewUtil.setStyleId(view, cssId, restyle);
    }

    public static String getStyleId(View view) {
        return ViewUtil.getStyleId(view);
    }

    public static void setStyleClass(View view, String cssClass) {
        ViewUtil.setStyleClass(view, cssClass);
    }

    public static void setStyleClass(View view, String cssClass, boolean restyle) {
        ViewUtil.setStyleClass(view, cssClass, restyle);
    }

    public static String getStyleClass(View view) {
        return ViewUtil.getStyleClass(view);
    }

    public static void setStyle(View view, String cssStyle) {
        ViewUtil.setStyle(view, cssStyle);
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    /**
     * Returns the {@link Adapter} that is nested in the given
     * {@link AdapterView}. In case the adapter is 'proxied', try to extract the
     * original {@link Adapter} from the proxy instance.
     * 
     * @param view
     * @return The original {@link Adapter} that was set for the
     *         {@link AdapterView} (can be null)
     */
    public static Adapter getAdapter(AdapterView<?> view) {
        Adapter adapter = view.getAdapter();
        if (adapter != null && Proxy.isProxyClass(adapter.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(adapter);
            if (handler instanceof PXAdapterInvocationHandler) {
                adapter = ((PXAdapterInvocationHandler) handler).getOriginal();
            }
        }
        return adapter;
    }

    /**
     * Styles a styleable.
     * 
     * @param styleable
     * @see #style(Object, boolean)
     */
    protected static void style(Object styleable) {
        // Assumes view has already been init'd and its
        // css class / id / style properties set for
        // Pixate to read.
        PXStyleUtils.updateStyles(styleable, true);
    }

    /**
     * Style a styleable instance (usually a View, but can be other instances,
     * like ActionBar).
     * 
     * @param styleable
     * @param styleChildren In case <code>true</code>, styles the children.
     */
    protected static void style(Object styleable, boolean styleChildren) {
        PXStyleUtils.updateStyles(styleable, styleChildren);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void initApp(Context context) {

        Application application = (Application) mAppContext;

        if (application != null) {
            mAppInited = true;
            if (mLifecycleCallbacks == null) {
                mLifecycleCallbacks = new PXLifecycleCallbacks();
            }
            application
                    .unregisterActivityLifecycleCallbacks((ActivityLifecycleCallbacks) mLifecycleCallbacks);
            application
                    .registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks) mLifecycleCallbacks);
        }

    }

    private static void logErrors(List<String> errors) {
        if (PXLog.isLogging() && errors != null) {
            for (String e : errors) {
                PXLog.e(TAG, e);
            }
        }
    }
}
