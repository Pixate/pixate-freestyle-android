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
package com.pixate.freestyle.styling.stylers;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.widget.ListView;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.adapters.PXStyleAdapter;
import com.pixate.freestyle.util.PXDrawableUtil;

//@formatter:off
/**
 * - divider: <paint>
 * - divider-height: <length>
 */
//@formatter:on
@PXDocStyler(properties = { @PXDocProperty(name = "divider", syntax = "<paint>"),
        @PXDocProperty(name = "divider-height", syntax = "<length>"), })
public class PXDividerStyler extends PXStylerBase {

    private static PXDividerStyler instance;
    private static Map<String, PXDeclarationHandler> handlers;

    public static PXDividerStyler getInstance() {
        if (instance == null) {
            instance = new PXDividerStyler(null);
        }
        return instance;
    }

    public PXDividerStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        if (handlers == null) {
            handlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

            handlers.put("divider", new PXDeclarationHandler() {
                public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                    stylerContext.setDividerFill(declaration.getPaintValue());
                }
            });

            handlers.put("divider-height", new PXDeclarationHandler() {
                public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                    float height = declaration.getFloatValue(stylerContext.getDisplayMetrics());
                    ListView listView = (ListView) stylerContext.getStyleable();
                    listView.setDividerHeight((int) height);
                }

            });
        }
        return handlers;
    }

    /*
     * Apply styles after everything is loaded to the context. We'll need to get
     * the accurate divider height when creating the drawable.
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#applyStylesWithContext
     * (com.pixate.freestyle.styling.stylers.PXStylerContext)
     */
    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        ListView listView = (ListView) stylerContext.getStyleable();
        // create the divider drawable and set it to the list
        int width = listView.getWidth();
        if (width <= 0) {
            View parent = (View) PXStyleAdapter.getStyleAdapter(listView).getParent(listView);
            if (parent != null) {
                width = parent.getWidth();
            }
        }
        listView.setDivider(PXDrawableUtil.createDrawable(width, listView.getDividerHeight(),
                stylerContext.getDividerFill()));
    }
}
