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

import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.styling.PXDeclaration;

public class PXInsetStyler extends PXStylerBase {

    private static Map<String, Map<String, PXDeclarationHandler>> declarationHandlerMaps;

    private String shortcutName;
    private String topName;
    private String rightName;
    private String bottomName;
    private String leftName;

    private PXOffsets insets;

    public PXInsetStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public PXInsetStyler(String baseName, PXStylerInvocation invocation) {
        super(invocation);
        setBaseName(baseName);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXInsetStyler.class) {

            if (declarationHandlerMaps == null) {
                declarationHandlerMaps = new HashMap<String, Map<String, PXStylerBase.PXDeclarationHandler>>();
            }

            // See if we have a handler dictionary for our prefix
            Map<String, PXDeclarationHandler> result = declarationHandlerMaps.get(shortcutName);

            if (result == null) {
                result = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                result.put(shortcutName, new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        insets = declaration.getInsetsValue(stylerContext.getDisplayMetrics());
                    }
                });

                result.put(topName, new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        PXInsetStyler.this.insets =
                                new PXOffsets(value, insets.getRight(), insets.getBottom(), insets.getLeft());
                    }
                });

                result.put(rightName, new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        PXInsetStyler.this.insets =
                                new PXOffsets(insets.getTop(), value, insets.getBottom(), insets.getLeft());
                    }
                });

                result.put(bottomName, new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        PXInsetStyler.this.insets =
                                new PXOffsets(insets.getTop(), insets.getRight(), value, insets.getLeft());
                    }
                });

                result.put(leftName, new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        PXInsetStyler.this.insets =
                                new PXOffsets(insets.getTop(), insets.getRight(), insets.getBottom(), value);
                    }
                });

                declarationHandlerMaps.put(shortcutName, result);
            }

            return result;
        }
    }

    // Getters

    public PXOffsets getInsets() {
        return insets;
    }

    // Private

    private void setBaseName(String baseName) {
        shortcutName = baseName + "-inset";
        topName = baseName + "-top-inset";
        rightName = baseName + "-right-inset";
        bottomName = baseName + "-bottom-inset";
        bottomName = baseName + "-left-inset";
    }

}
