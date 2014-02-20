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
import java.util.List;
import java.util.Map;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.cg.math.PXOffsets;
import com.pixate.freestyle.cg.paints.PXPaint;
import com.pixate.freestyle.cg.shapes.PXBoxModel;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.infos.PXBorderInfo;
import com.pixate.freestyle.styling.infos.PXBorderInfo.PXBorderStyle;
import com.pixate.freestyle.util.Size;

@PXDocStyler(properties = {
        @PXDocProperty(name = "border", syntax = "<width> || <border-style> || <paint>"),
        @PXDocProperty(hide = true, name = "border-top", syntax = "<width> || <border-style> || <paint>"),
        @PXDocProperty(hide = true, name = "border-right", syntax = "<width> || <border-style> || <paint>"),
        @PXDocProperty(hide = true, name = "border-bottom", syntax = "<width> || <border-style> || <paint>"),
        @PXDocProperty(hide = true, name = "border-left", syntax = "<width> || <border-style> || <paint>"),
        @PXDocProperty(name = "border-radius", syntax = "<size>{1,4}"),
        @PXDocProperty(name = "border-top-left-radius", syntax = "<length>"),
        @PXDocProperty(name = "border-top-right-radius", syntax = "<length>"),
        @PXDocProperty(name = "border-bottom-right-radius", syntax = "<length>"),
        @PXDocProperty(name = "border-bottom-left-radius", syntax = "<length>"),
        @PXDocProperty(name = "border-width", syntax = "<length>{1,4}"),
        @PXDocProperty(hide = true, name = "border-top-width", syntax = "<length>"),
        @PXDocProperty(hide = true, name = "border-right-width", syntax = "<length>"),
        @PXDocProperty(hide = true, name = "border-bottom-width", syntax = "<length>"),
        @PXDocProperty(hide = true, name = "border-left-width", syntax = "<length>"),
        @PXDocProperty(name = "border-color", syntax = "<paint>{1,4}"),
        @PXDocProperty(hide = true, name = "border-top-color", syntax = "<paint>"),
        @PXDocProperty(hide = true, name = "border-right-color", syntax = "<paint>"),
        @PXDocProperty(hide = true, name = "border-bottom-color", syntax = "<paint>"),
        @PXDocProperty(hide = true, name = "border-left-color", syntax = "<paint>"),
        @PXDocProperty(name = "border-style", syntax = "<border-style>{1,4}"),
        @PXDocProperty(hide = true, name = "border-top-style", syntax = "<border-style>"),
        @PXDocProperty(hide = true, name = "border-right-style", syntax = "<border-style>"),
        @PXDocProperty(hide = true, name = "border-bottom-style", syntax = "<border-style>"),
        @PXDocProperty(hide = true, name = "border-left-style", syntax = "<border-style>"), })
public class PXBorderStyler extends PXStylerBase {
    // @formatter:off
    // Comments copied from iOS:
    /*
     *  - border: <width> || <border-style> || <paint>
     *  - border-top: <width> || <border-style> || <paint>
     *  - border-right: <width> || <border-style> || <paint>
     *  - border-bottom: <width> || <border-style> || <paint>
     *  - border-left: <width> || <border-style> || <paint>
     *  - border-radius: <size>{1,4}
     *  - border-top-left-radius: <length>
     *  - border-top-right-radius: <length>
     *  - border-bottom-right-radius: <length>
     *  - border-bottom-left-radius: <length>
     *  - border-width: <length>{1,4}
     *  - border-top-width: <length>
     *  - border-right-width: <length>
     *  - border-bottom-width: <length>
     *  - border-left-width: <length>
     *  - border-color: <paint>{1,4}
     *  - border-top-color: <paint>
     *  - border-right-color: <paint>
     *  - border-bottom-color: <paint>
     *  - border-left-color: <paint>
     *  - border-style: <border-style>{1,4}
     *  - border-top-style: <border-style>
     *  - border-right-style: <border-style>
     *  - border-bottom-style: <border-style>
     *  - border-left-style: <border-style>
     */
    // @formatter:on

    private static PXBorderStyler instance;

    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXBorderStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXBorderStyler getInstance() {
        if (instance == null) {
            instance = new PXBorderStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXBorderStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("border", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXBorderInfo settings = declaration.getBorderValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderWidth(settings.getWidth());
                        boxModel.setBorderStyle(settings.getStyle());
                        boxModel.setBorderPaint(settings.getPaint());
                    }
                });

                declarationHandlers.put("border-top", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXBorderInfo settings = declaration.getBorderValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderTopWidth(settings.getWidth());
                        boxModel.setBorderTopStyle(settings.getStyle());
                        boxModel.setBorderTopPaint(settings.getPaint());
                    }
                });

                declarationHandlers.put("border-right", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXBorderInfo settings = declaration.getBorderValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderRightWidth(settings.getWidth());
                        boxModel.setBorderRightStyle(settings.getStyle());
                        boxModel.setBorderRightPaint(settings.getPaint());
                    }
                });

                declarationHandlers.put("border-bottom", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXBorderInfo settings = declaration.getBorderValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderBottomWidth(settings.getWidth());
                        boxModel.setBorderBottomStyle(settings.getStyle());
                        boxModel.setBorderBottomPaint(settings.getPaint());
                    }
                });

                declarationHandlers.put("border-left", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXBorderInfo settings = declaration.getBorderValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderLeftWidth(settings.getWidth());
                        boxModel.setBorderLeftStyle(settings.getStyle());
                        boxModel.setBorderLeftPaint(settings.getPaint());
                    }
                });

                declarationHandlers.put("border-radius", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<Size> radii = declaration.getBorderRadiiList(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setRadiusTopLeft(radii.get(0));
                        boxModel.setRadiusTopRight(radii.get(1));
                        boxModel.setRadiusBottomRight(radii.get(2));
                        boxModel.setRadiusBottomLeft(radii.get(3));
                    }
                });

                declarationHandlers.put("border-top-left-radius", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setRadiusTopLeft(
                                declaration.getSizeValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-top-right-radius", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setRadiusTopRight(
                                declaration.getSizeValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-bottom-right-radius", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setRadiusBottomRight(
                                declaration.getSizeValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-bottom-left-radius", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setRadiusBottomLeft(
                                declaration.getSizeValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        PXOffsets widths = declaration.getOffsetsValue(stylerContext
                                .getDisplayMetrics());
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderTopWidth(widths.getTop());
                        boxModel.setBorderRightWidth(widths.getRight());
                        boxModel.setBorderBottomWidth(widths.getBottom());
                        boxModel.setBorderLeftWidth(widths.getLeft());
                    }
                });

                declarationHandlers.put("border-top-width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderTopWidth(
                                declaration.getFloatValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-right-width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderRightWidth(
                                declaration.getFloatValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-bottom-width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderBottomWidth(
                                declaration.getFloatValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-left-width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderLeftWidth(
                                declaration.getFloatValue(stylerContext.getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("border-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXPaint> paints = declaration.getPaintList();
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderTopPaint(paints.get(0));
                        boxModel.setBorderRightPaint(paints.get(1));
                        boxModel.setBorderBottomPaint(paints.get(2));
                        boxModel.setBorderLeftPaint(paints.get(3));
                    }
                });

                declarationHandlers.put("border-top-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderTopPaint(declaration.getPaintValue());
                    }
                });

                declarationHandlers.put("border-right-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel()
                                .setBorderRightPaint(declaration.getPaintValue());
                    }
                });

                declarationHandlers.put("border-bottom-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderBottomPaint(
                                declaration.getPaintValue());
                    }
                });

                declarationHandlers.put("border-left-color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderLeftPaint(declaration.getPaintValue());
                    }
                });

                declarationHandlers.put("border-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        List<PXBorderStyle> styles = declaration.getBorderStyleList();
                        PXBoxModel boxModel = stylerContext.getBoxModel();
                        boxModel.setBorderTopStyle(styles.get(0));
                        boxModel.setBorderRightStyle(styles.get(1));
                        boxModel.setBorderBottomStyle(styles.get(2));
                        boxModel.setBorderLeftStyle(styles.get(3));
                    }
                });

                declarationHandlers.put("border-top-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderTopStyle(
                                declaration.getBorderStyleValue());
                    }
                });

                declarationHandlers.put("border-right-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderRightStyle(
                                declaration.getBorderStyleValue());
                    }
                });

                declarationHandlers.put("border-bottom-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderBottomStyle(
                                declaration.getBorderStyleValue());
                    }
                });

                declarationHandlers.put("border-left-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.getBoxModel().setBorderLeftStyle(
                                declaration.getBorderStyleValue());
                    }
                });

            }

            return declarationHandlers;
        }
    }

}
