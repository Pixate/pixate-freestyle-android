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
package com.pixate.freestyle.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.pixate.freestyle.PXHierarchyListener;
import com.pixate.freestyle.styling.PXStyleUtils;

public class ViewUtil {

    public static final int TAG_ID = Integer.MIN_VALUE;
    public static final int TAG_CLASS = TAG_ID + 1;
    public static final int TAG_STYLE = TAG_ID + 2;
    public static final int TAG_ELEMENT_NAME = TAG_ID + 3;
    public static final int TAG_ELEMENT_INDEX = TAG_ID + 4;
    public static final int TAG_ELEMENTS_COUNT = TAG_ID + 5;
    public static final int TAG_ELEMENT_FUTURE_PARENT = TAG_ID + 6;
    public static final int TAG_TAGGED = TAG_ID + 7;

    public static void initView(View view, AttributeSet attrs) {

        String cssClass = null, cssStyle = null;

        if (attrs != null) {
            cssClass = attrs.getClassAttribute();
            cssStyle = attrs.getAttributeValue(null, "style");
        }

        initView(view, null, cssClass, cssStyle);
    }

    public static void initView(final View view, String cssId, String cssClass, String cssStyle) {
        initTags(view, cssId, cssClass, cssStyle);
        if (view instanceof ViewGroup) {
            prepareViewGroupListeners((ViewGroup) view);
        }
    }

    public static void initTags(View view, String cssId, String cssClass, String cssStyle) {

        if (cssId == null) {
            cssId = getViewId(view);
        }
        setStyleId(view, cssId, false);
        setStyleClass(view, cssClass, false);
        setStyle(view, cssStyle, false);
    }

    public static void initTags(View view) {
        initTags(view, null, null, null);
    }

    private static void markTagged(View view) {
        view.setTag(TAG_TAGGED, Boolean.TRUE);
    }

    public static boolean isTagged(View view) {
        return view.getTag(TAG_TAGGED) != null;
    }

    /**
     * Check the View's id, then use that to find the name given to it in XML.
     * 
     * @param view
     * @return String the name, if found, else null.
     */
    private static String getViewId(View view) {
        try {
            int id = view.getId();
            if (id == View.NO_ID) {
                return null;
            }
            return view.getContext().getResources().getResourceEntryName(id);
        } catch (Resources.NotFoundException e) {
            // No-op
        }
        return null;
    }

    public static void style(View view) {
        PXStyleUtils.updateStyles(view, true);
    }

    public static void style(View view, boolean styleChildren) {
        PXStyleUtils.updateStyles(view, styleChildren);
    }

    public static void prepareViewGroupListeners(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }

        PXHierarchyListener.setFor(viewGroup);

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                prepareViewGroupListeners((ViewGroup) child);
            }
        }
    }

    public static void prepareViewGroupListeners(Activity activity) {
        if (activity == null) {
            return;
        }

        View contentView = activity.findViewById(android.R.id.content);
        if (contentView instanceof ViewGroup) {
            prepareViewGroupListeners((ViewGroup) contentView);
        }
    }

    public static void setStyleId(View view, String cssId) {
        setStyleId(view, cssId, view.getParent() != null);
    }

    public static void setStyleId(View view, String cssId, boolean restyleNow) {
        markTagged(view);
        view.setTag(TAG_ID, cssId);
        if (restyleNow) {
            style(view);
        }
    }

    public static void setStyleClass(View view, String cssClass) {
        setStyleClass(view, cssClass, view.getParent() != null);
    }

    public static void setStyleClass(View view, String cssClass, boolean restyleNow) {
        markTagged(view);
        view.setTag(TAG_CLASS, cssClass);
        if (restyleNow) {
            style(view);
        }
    }

    public static void setStyle(View view, String cssStyle) {
        setStyle(view, cssStyle, view.getParent() != null);
    }

    public static void setStyle(View view, String cssStyle, boolean restyleNow) {
        markTagged(view);
        view.setTag(TAG_STYLE, cssStyle);
        if (restyleNow) {
            style(view);
        }
    }

    public static String getStyleId(View view) {
        String result = null;

        if (view != null) {
            result = (String) view.getTag(TAG_ID);
        }

        return result;
    }

    public static String getStyleClass(View view) {
        String result = null;

        if (view != null) {
            result = (String) view.getTag(TAG_CLASS);
        }

        return result;
    }

    public static String getElementName(View view) {
        String result = null;

        if (view != null) {
            // TODO figure out and cache
            result = (String) view.getTag(TAG_ELEMENT_NAME);
            if (result == null) {
                result = view.getClass().getSimpleName();
            }
        }

        return result;
    }

    public static RectF getBounds(View view) {
        if (view != null) {
            Rect r = new Rect();
            view.getDrawingRect(r);
            return new RectF(r);
        }
        return null;
    }

    /**
     * Returns the View's width. In case the {@link View#getWidth()} returns
     * zero, we try to get the width via the layout params.
     * 
     * @param view
     * @return the View's width
     */
    public static int getWidth(View view) {
        int w = view.getWidth();
        if (w == 0) {
            // try to get it from the layout
            LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                w = layoutParams.width;
            }
        }
        return w;
    }

    /**
     * Returns the View's height. In case the {@link View#getHeight()} returns
     * zero, we try to get the height via the layout params.
     * 
     * @param view
     * @return the View's height
     */
    public static int getHeight(View view) {
        int h = view.getHeight();
        if (h == 0) {
            // try to get it from the layout
            LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                h = layoutParams.height;
            }
        }
        return h;
    }
}
