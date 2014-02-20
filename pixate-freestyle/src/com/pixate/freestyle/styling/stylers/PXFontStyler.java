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
import java.util.Locale;
import java.util.Map;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;

// @formatter:off
/**
 *  - font-family: <string>
 *  - font-size: <length>
 *  - font-style: normal | italic | oblique
 *  - font-weight: normal | bold | black | heavy | extra-bold | ultra-bold | semi-bold | demi-bold | medium | light | extra-thin | ultra-thin | thin | 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900
 *  - font-stretch: normal | ultra-condensed | extra-condensed | condensed | semi-condensed | semi-expanded | expanded | extra-expanded | ultra-expanded
 */
// @formatter:on
@PXDocStyler(properties = {
        @PXDocProperty(name = "font-family", syntax = "<string>"),
        @PXDocProperty(name = "font-size", syntax = "<length>"),
        @PXDocProperty(name = "font-style", syntax = "normal | italic"),
        @PXDocProperty(name = "font-weight", syntax = "normal | bold"),
        @PXDocProperty(hide=true, name = "font-stretch", syntax = "normal | ultra-condensed | extra-condensed | condensed | semi-condensed | semi-expanded | expanded | extra-expanded | ultra-expanded"), })
public class PXFontStyler extends PXStylerBase {

    private Map<String, PXDeclarationHandler> declarationHandlers;

    public PXFontStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXFontStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("font-family", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFontName(declaration.getStringValue());
                    }
                });

                declarationHandlers.put("font-size", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFontSize(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });

                declarationHandlers.put("font-style", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFontStyle(declaration.getStringValue());
                    }
                });

                declarationHandlers.put("font-weight", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFontWeight(declaration.getStringValue().toLowerCase(
                                Locale.US));
                    }
                });

                // Note: this will go unused in Android
                declarationHandlers.put("font-stretch", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFontStretch(declaration.getStringValue().toLowerCase(
                                Locale.US));
                    }
                });

            }

            return declarationHandlers;
        }
    }

}
