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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.paints.PXPaintGroup;
import com.pixate.freestyle.styling.PXDeclaration;

// @formatter:off
/**
 *  - background-color: <paint>
 *  - background-image: <url>
 *  - background-size: <size>  (default is 32x32)
 *  - background-inset: <inset>
 *  - background-inset-top: <length>
 *  - background-inset-right: <length>
 *  - background-inset-bottom: <length>
 *  - background-inset-left: <length>
 *  - background-padding: <padding>
 *  - background-padding-top: <length>
 *  - background-padding-right: <length>
 *  - background-padding-bottom: <length>
 *  - background-padding-left: <length>
 */
// @formatter:on
@PXDocStyler(properties = { @PXDocProperty(name = "background-color", syntax = "<paint>"),
        @PXDocProperty(name = "background-image", syntax = "<url>"),
        @PXDocProperty(name = "background-size", syntax = "<size>"),
        @PXDocProperty(name = "background-inset", syntax = "<inset>"),
        @PXDocProperty(name = "background-inset-top", syntax = "<length>"),
        @PXDocProperty(name = "background-inset-right", syntax = "<length>"),
        @PXDocProperty(name = "background-inset-bottom", syntax = "<length>"),
        @PXDocProperty(name = "background-inset-left", syntax = "<length>"),
        @PXDocProperty(name = "background-padding", syntax = "<padding>"),
        @PXDocProperty(name = "background-padding-top", syntax = "<length>"),
        @PXDocProperty(name = "background-padding-right", syntax = "<length>"),
        @PXDocProperty(name = "background-padding-bottom", syntax = "<length>"),
        @PXDocProperty(name = "background-padding-left", syntax = "<length>"), })
public class PXFillStyler extends PXStylerBase {

    private static Map<String, PXDeclarationHandler> declarationHandlers;
    private static PXFillStyler instance;

    public PXFillStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXFillStyler getInstance() {
        if (instance == null) {
            instance = new PXFillStyler(null);
        }

        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXFillStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("background-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFill(declaration.getPaintValue());
                    }
                });

                declarationHandlers.put("background-size", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setImageSize(declaration.getSizeValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("background-inset", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setInsets(declaration.getInsetsValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("background-inset-top", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setInsets(new PXOffsets(value, insets.getRight(), insets
                                .getBottom(), insets.getLeft()));
                    }
                });

                declarationHandlers.put("background-inset-right", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setInsets(new PXOffsets(insets.getTop(), value, insets
                                .getBottom(), insets.getLeft()));
                    }
                });

                declarationHandlers.put("background-inset-bottom", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setInsets(new PXOffsets(insets.getTop(), insets.getRight(),
                                value, insets.getLeft()));
                    }
                });

                declarationHandlers.put("background-inset-left", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets insets = stylerContext.getInsets();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setInsets(new PXOffsets(insets.getTop(), insets.getRight(),
                                insets.getBottom(), value));
                    }
                });

                declarationHandlers.put("background-image", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXPaint paint = declaration.getPaintValue();

                        if (paint instanceof PXPaintGroup) {
                            PXPaintGroup group = (PXPaintGroup) paint;
                            List<PXPaint> paints = group.getPaints();
                            Collections.reverse(paints);
                            stylerContext.setImageFill(new PXPaintGroup(paints));

                        } else {
                            stylerContext.setImageFill(paint);
                        }
                    }
                });

                declarationHandlers.put("background-padding", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setPadding(declaration.getOffsetsValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("background-top-padding", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets padding = stylerContext.getPadding();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setPadding(new PXOffsets(value, padding.getRight(), padding
                                .getBottom(), padding.getLeft()));
                    }
                });

                declarationHandlers.put("background-right-padding", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets padding = stylerContext.getPadding();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setPadding(new PXOffsets(padding.getTop(), value, padding
                                .getBottom(), padding.getLeft()));
                    }
                });

                declarationHandlers.put("background-bottom-padding", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets padding = stylerContext.getPadding();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setPadding(new PXOffsets(padding.getTop(),
                                padding.getRight(), value, padding.getLeft()));
                    }
                });

                declarationHandlers.put("background-left-padding", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets padding = stylerContext.getPadding();
                        float value = declaration.getFloatValue(stylerContext.getDisplayMetrics());

                        stylerContext.setPadding(new PXOffsets(padding.getTop(),
                                padding.getRight(), padding.getBottom(), value));
                    }
                });
            }

            return declarationHandlers;
        }
    }
}
