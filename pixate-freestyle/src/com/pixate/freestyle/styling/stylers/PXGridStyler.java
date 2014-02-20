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

import android.widget.GridView;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.stylers.PXStylerContext.GridStyle;
import com.pixate.freestyle.styling.stylers.PXStylerContext.GridStyle.PXColumnStretchMode;

//@formatter:off
/**
* - column-width: <length> | auto
* - column-count: <number> | auto
* - column-stretch-mode: none | spacing | spacing-uniform | column-width
* - column-gap: <length> | normal
* - row-gap: <length> | normal
*/
//@formatter:on

/**
 * Column style information, principally for GridView and subclasses.
 * 
 * @author Bill Dawson
 */
@PXDocStyler(properties = {
        @PXDocProperty(name = "column-width", syntax = "<length> | auto"),
        @PXDocProperty(name = "column-count", syntax = "<number> | auto"),
        @PXDocProperty(name = "column-stretch-mode", syntax = "none | spacing | spacing-uniform | column-width"),
        @PXDocProperty(name = "column-gap", syntax = "<length> | normal"),
        @PXDocProperty(name = "row-gap", syntax = "<length> | normal"), })
public class PXGridStyler extends PXStylerBase {

    private static Map<String, PXDeclarationHandler> handlers;
    private static PXGridStyler instance;

    public synchronized static PXGridStyler getInstance() {
        if (instance == null) {
            instance = new PXGridStyler(null);
        }
        return instance;
    }

    public PXGridStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#getDeclarationHandlers
     * ()
     */
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXGridStyler.class) {
            if (handlers == null) {
                handlers = new HashMap<String, PXDeclarationHandler>();

                handlers.put("column-width", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setColumnWidth(declaration.getColumnWidth(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                handlers.put("column-count", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setColumnCount(declaration.getColumnCount());
                    }
                });

                handlers.put("column-stretch-mode", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXColumnStretchMode mode = declaration.getColumnStretchMode();
                        if (mode != null) {
                            stylerContext.setColumnStretchMode(mode.getAndroidValue());
                        }
                    }
                });

                handlers.put("column-gap", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setColumnGap(declaration.getColumnGap(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                handlers.put("row-gap", new PXDeclarationHandler() {
                    @Override
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setRowGap(declaration.getRowGap(stylerContext
                                .getDisplayMetrics()));
                    }
                });

            }
            return handlers;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.styling.stylers.PXStylerBase#applyStylesWithContext
     * (com.pixate.freestyle.styling.stylers.PXStylerContext)
     */
    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {

        GridStyle style = stylerContext.getGridStyle();
        if (style != null) {
            GridView view = (GridView) stylerContext.getStyleable();
            if (style.columnCount != Integer.MIN_VALUE) {
                view.setNumColumns(style.columnCount);
            }
            if (style.columnWidth != Integer.MIN_VALUE) {
                view.setColumnWidth(style.columnWidth);
            }
            if (style.columnGap != Integer.MIN_VALUE) {
                view.setHorizontalSpacing(style.columnGap);
            }
            if (style.rowGap != Integer.MIN_VALUE) {
                view.setVerticalSpacing(style.rowGap);
            }
            if (style.columnStretchMode != Integer.MIN_VALUE) {
                view.setStretchMode(style.columnStretchMode);
            }
        }

        super.applyStylesWithContext(stylerContext);
    }

}
