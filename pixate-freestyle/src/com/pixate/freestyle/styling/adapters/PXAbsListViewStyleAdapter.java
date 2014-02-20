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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;

import com.pixate.freestyle.annotations.PXDocElement;
import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXRuleSet;
import com.pixate.freestyle.styling.stylers.PXGenericStyler;
import com.pixate.freestyle.styling.stylers.PXStyler;
import com.pixate.freestyle.styling.stylers.PXStylerBase.PXDeclarationHandler;
import com.pixate.freestyle.styling.stylers.PXStylerContext;
import com.pixate.freestyle.styling.virtualStyleables.PXVirtualListSelector;
import com.pixate.freestyle.util.CollectionUtil;

/**
 * AbsListView styling. This is abstract, with functionality common to ListView
 * and GridView.<br>
 * An {@link AbsListView} styler controls the selection mode and list selector
 * attributes (as a virtual child).
 * 
 * <pre>
 * - selection-mode: single|multiple
 * - selector: virtual-child
 * </pre>
 * 
 * For examples, see concrete classes {@link PXListViewStyleAdapter} and
 * {@link PXGridViewStyleAdapter}.
 * 
 * @author Bill Dawson
 */
@PXDocElement(hide = true, properties = { @PXDocProperty(name = "selection-mode", syntax = "single | multiple") })
public abstract class PXAbsListViewStyleAdapter extends PXViewStyleAdapter {

    @Override
    public boolean updateStyle(List<PXRuleSet> ruleSets, List<PXStylerContext> contexts) {
        if (!CollectionUtil.isEmpty(contexts)) {
            PXStylerContext context = contexts.get(0);
            if (context.getCombinedPaints() != null) {
                // Make sure we set the cache color hint to zero when we have
                // custom background.
                AbsListView styleable = (AbsListView) context.getStyleable();
                styleable.setCacheColorHint(0);
            }
        }
        return super.updateStyle(ruleSets, contexts);
    }

    protected List<PXStyler> createStylers() {
        List<PXStyler> stylers = super.createStylers();
        final PXDeclarationHandler handler = new PXDeclarationHandler() {
            @Override
            public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                String value = declaration.getStringValue();
                AbsListView view = (AbsListView) stylerContext.getStyleable();
                if ("single".equals(value)) {
                    view.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                } else if ("multiple".equals(value)) {
                    view.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                }
            }
        };
        Map<String, PXDeclarationHandler> handlers = new HashMap<String, PXDeclarationHandler>(1);
        handlers.put("selection-mode", handler);

        stylers.add(new PXGenericStyler(handlers));
        return stylers;
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
        result.add(new PXVirtualListSelector(styleable));
        return result;
    }

    /**
     * Override the super implementation to provide the real index in the
     * {@link AbsListView} data since the list items are being recycled.
     */
    @Override
    public int getIndexInParent(Object styleable) {
        View view = (View) styleable;
        ViewParent parent = view.getParent();
        if (parent instanceof AbsListView) {
            return ((AbsListView) parent).getPositionForView(view);
        }
        return super.getIndexInParent(styleable);
    }
}
