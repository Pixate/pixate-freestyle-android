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
package com.pixate.freestyle.styling.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import com.pixate.freestyle.PXAdapterInvocationHandler;
import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.styling.stylers.PXDividerStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualListOverscroll;
import com.pixate.freestyle.util.ViewUtil;

/**
 * ListView styling.<br>
 * A {@link ListView} styler controls the divider settings, selection mode and
 * over-scrolling attributes (as a virtual child).
 * 
 * <pre>
 * - divider: paint
 * - divider-height: length
 * - overscroll: virtual-child
 * 
 * (Defined in the AbsListView styler, but applicable for this list)
 * - selection-mode: single|multiple
 * - selector: virtual-child
 * 
 * (Defined in the View styler, but applicable for this list)
 * - android-fading-edge-length: length
 * - android-vertical-fading-edge: enabled | disabled
 * - android-horizontal-fading-edge: enabled | disabled
 * </pre>
 * 
 * For example:
 * 
 * <pre>
 * #list {
 *     divider: linear-gradient(black, orange);
 *     divider-height: 5px;
 * }
 * 
 * -- show drawables when overscrolling up or down --
 * #list overscroll {
 *     distance: 200px;
 *     footer: linear-gradient(red, white);
 *     header: url(top-overscroll.png);
 * }
 * </pre>
 * 
 * @author Shalom Gibly
 */
@PXDocElement(hide=true)
public class PXListViewStyleAdapter extends PXAbsListViewStyleAdapter {

    private static String ELEMENT_NAME = "list-view";

    private static PXListViewStyleAdapter sInstance;

    protected PXListViewStyleAdapter() {
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#createStylers()
     */
    @Override
    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = super.createStylers();
        stylers.add(PXDividerStyler.getInstance());
        return stylers;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXViewStyleAdapter#getElementName
     * (java.lang.Object)
     */
    @Override
    public String getElementName(Object object) {
        return ELEMENT_NAME;
    }

    public static PXListViewStyleAdapter getInstance() {
        synchronized (PXListViewStyleAdapter.class) {

            if (sInstance == null) {
                sInstance = new PXListViewStyleAdapter();
            }
        }

        return sInstance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.adapters.PXStyleAdapter#getVirtualChildren
     * (java.lang.Object)
     */
    @Override
    protected List<Object> getVirtualChildren(Object styleable) {
        List<Object> superVirtuals = super.getVirtualChildren(styleable);
        List<Object> result = new ArrayList<Object>(superVirtuals.size() + 1);
        result.addAll(superVirtuals);
        result.add(new PXVirtualListOverscroll(styleable));
        return result;
    }

    /**
     * Special handling for list views used in the popup window for a
     * {@link Spinner}. Often they will be embedded inside a special container
     * on the popup window's surface. This container will not have the Spinner
     * anywhere above it in its view hierarchy, which, without the workaround
     * below, would mean that the list view children could not be styled via a
     * rule such as this:
     * 
     * <pre>
     * #mySpinner list-view #text1 {
     *   ...
     * }
     * </pre>
     * 
     * In that example and without our workaround below, #mySpinner would never
     * be matched as an ancestor of the list view if the list view is in fact
     * embedded in one of these special popup containers. This in turn means
     * that when the text views within the list are evaluated for rule matching,
     * the matching won't succeed. Styling the list view alone, such as with
     * this rule, will actually work:
     * 
     * <pre>
     * #mySpinner list-view {
     *   ...
     * }
     * </pre>
     * 
     * That works because the first pass at styling the list view comes when its
     * parent in the view hierarchy is null and therefore its "future parent",
     * which we set to the Spinner via the {@link PXAdapterInvocationHandler},
     * will be used, causing a match and immediate styling. Later, when the
     * Spinner is no longer the parent because the special popup view container
     * has come into existence, it's okay because the list view was already
     * styled via the previous matches. <br>
     * But it won't work for the recycled children of the list view, because
     * different and multiple instances are created, re-used, etc., and at a
     * time when the special popup container has intervened and become the real
     * parent of the list view. <br>
     * To overcome this, we check if the special container class is the actual
     * parent of the list view and, if so, we then check if the list view's
     * "future parent" had been set by {@link PXAdapterInvocationHandler} to a
     * Spinner. When these conditions are met, it's safe to assume that the
     * Spinner is the parent for styling purposes, so we return it.
     */
    @Override
    public Object getParent(Object styleable) {
        Object result = super.getParent(styleable);
        // These checks handle the list views that are displayed by the Spinner
        // in its drop-down and dialog modes.
        if (result != null
                && styleable instanceof View
                && (result.getClass().getSimpleName().contains("PopupViewContainer") || styleable
                        .getClass().getSimpleName().contains("RecycleListView"))) {
            Object futureParent = ((View) styleable).getTag(ViewUtil.TAG_ELEMENT_FUTURE_PARENT);
            if (futureParent instanceof WeakReference<?>) {
                Object referent = ((WeakReference<?>) futureParent).get();
                if (referent instanceof Spinner) {
                    result = referent;
                }
            }
        }

        return result;
    }
}
