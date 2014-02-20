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

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.util.Size;

// @formatter:off
/**
 *  - position: <size>
 *  - top: <length>
 *  - left: <length>
 *  - size: <size>
 *  - width: <length>
 *  - height: <length>
 */
// @formatter:on
@PXDocStyler(properties = { @PXDocProperty(name = "position", syntax = "<size>"),
        @PXDocProperty(name = "top", syntax = "<length>"),
        @PXDocProperty(name = "left", syntax = "<length>"),
        @PXDocProperty(name = "size", syntax = "<size>"),
        @PXDocProperty(name = "width", syntax = "<length>"),
        @PXDocProperty(name = "height", syntax = "<length>"), })
public class PXLayoutStyler extends PXStylerBase {

    private static PXLayoutStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXLayoutStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXLayoutStyler getInstance() {
        if (instance == null) {
            instance = new PXLayoutStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXLayoutStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("top", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setTop(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("left", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setLeft(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("width", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setWidth(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("height", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setHeight(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("size", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        Size sizeValue = declaration.getSizeValue(stylerContext.getDisplayMetrics());
                        stylerContext.setWidth(sizeValue.width);
                        stylerContext.setHeight(sizeValue.height);
                    }
                });

                declarationHandlers.put("position", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        Size sizeValue = declaration.getSizeValue(stylerContext.getDisplayMetrics());
                        stylerContext.setLeft(sizeValue.width);
                        stylerContext.setTop(sizeValue.height);
                    }
                });
            }

            return declarationHandlers;
        }
    }

}
