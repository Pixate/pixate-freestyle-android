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
package com.pixate.freestyle.styling.virtualAdapters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.CheckedTextView;

import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.styling.cache.PXStyleInfo;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.util.MapUtil;
import com.pixate.freestyle.util.PXDrawableUtil;
import com.pixate.freestyle.util.PXLog;

/**
 * A virtual adapter for styling the {@link CheckedTextView} check mark
 * drawable. For example:
 * 
 * <pre>
 * #myListView checked-text-view icon:checked {
 *     background-image: url(check-on.svg);
 *     background-size: 42;
 * }
 * 
 * #myListView checked-text-view icon {
 *     background-image: url(check-off.svg);
 *     background-size: 42;
 * }
 * </pre>
 * 
 * @author Bill Dawson
 */
public class PXVirtualCheckedTextViewIconAdapter extends PXVirtualChildAdapter {

    private static PXVirtualCheckedTextViewIconAdapter instance;
    private static String ELEMENT_NAME = "icon";
    private static String TAG = PXVirtualCheckedTextViewIconAdapter.class.getSimpleName();

    // For pre-JB reflection
    private static String DRAWABLE_FIELD_NAME = "mCheckMarkDrawable";
    private static Field drawableField;

    /**
     * Returns a singleton instance of this class.
     * 
     * @return An instance of {@link PXVirtualCheckedTextViewIconAdapter}
     */
    public static synchronized PXVirtualCheckedTextViewIconAdapter getInstance() {
        if (instance == null) {
            instance = new PXVirtualCheckedTextViewIconAdapter();
        }
        return instance;
    }

    protected PXVirtualCheckedTextViewIconAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getSupportedPseudoClasses
     * (java.lang.Object)
     */
    @Override
    public List<String> getSupportedPseudoClasses(Object styleable) {
        return new ArrayList<String>(PXDrawableUtil.getSupportedStates().keySet());
    }

    @Override
    public String getDefaultPseudoClass(Object styleable) {
        return PXStyleInfo.DEFAULT_STYLE;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getElementName(java
     * .lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#updateStyle(java.
     * util.List, java.util.List)
     */
    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!super.updateStyle(ruleSets, contexts)) {
            return false;
        }
        // Style the check mark. We will construct the
        // drawable from the context states, and then set the constructed
        // drawable as the check mark using the
        // CheckedTextView#setCheckMarkDrawable call. Note that this is
        // different than the View#setBackground(Drawable) call that is handled
        // in the default View adapter.

        CheckedTextView view = (CheckedTextView) contexts.get(0).getStyleable();
        Drawable currentDrawable = getCheckMarkDrawable(view);

        Map<int[], Drawable> existingStates = PXDrawableUtil.getExistingStates(currentDrawable);

        Drawable newDrawable = null;

        if (MapUtil.isEmpty(existingStates)) {
            // create a new StateListDrawable for the icon,
            // using the checked text view's adapter
            // as the source of possible drawable states.
            if (contexts.size() == 1) {
                newDrawable = contexts.get(0).getBackgroundImage();
            } else {
                newDrawable = PXDrawableUtil.createNewStateListDrawable(
                        PXStyleAdapter.getStyleAdapter(view), ruleSets, contexts);
            }
        } else {
            // create a drawable that will hold a merge of the existing states
            // and the new states. Use the checked text view's
            // adapter as the source of possible drawable states.
            newDrawable = PXDrawableUtil.createDrawable(PXStyleAdapter.getStyleAdapter(view),
                    existingStates, ruleSets, contexts);
        }

        view.setCheckMarkDrawable(newDrawable);
        return true;
    }

    private Drawable getCheckMarkDrawable(CheckedTextView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return getCheckMarkDrawableJB(view);
        } else {
            try {
                // Need reflection
                if (drawableField == null) {
                    drawableField = CheckedTextView.class.getDeclaredField(DRAWABLE_FIELD_NAME);
                    drawableField.setAccessible(true);
                }
                return (Drawable) drawableField.get(view);
            } catch (Exception e) {
                PXLog.e(TAG, e, "Unable to get check mark drawable using reflection");
                return null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Drawable getCheckMarkDrawableJB(CheckedTextView view) {
        return view.getCheckMarkDrawable();
    }

}
