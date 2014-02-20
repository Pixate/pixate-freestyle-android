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

/**
 * -android-fading-edge-length: length <br>
 * -android-vertical-fading-edge: enabled | disabled <br>
 * -android-horizontal-fading-edge: enabled | disabled
 */
// Support fading-edge styles for Views that scroll and support it (like
// ListView).
@PXDocStyler(properties = { @PXDocProperty(name = "android-fading-edge-length", syntax = "<length>"),
        @PXDocProperty(name = "android-vertical-fading-edge", syntax = "enabled | disabled"),
        @PXDocProperty(name = "android-horizontal-fading-edge", syntax = "enabled | disabled"), })
public class PXFadingEdgeStyler extends PXStylerBase {
    private static String ENABLED = "enabled";
    private static PXFadingEdgeStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXFadingEdgeStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXFadingEdgeStyler getInstance() {
        if (instance == null) {
            instance = new PXFadingEdgeStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXFadingEdgeStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("android-fading-edge-length", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setFadingEdgeLength((int) declaration
                                .getFloatValue(stylerContext.getDisplayMetrics()));
                    }
                });
                declarationHandlers.put("android-horizontal-fading-edge",
                        new PXDeclarationHandler() {
                            public void process(PXDeclaration declaration,
                                    PXStylerContext stylerContext) {
                                stylerContext.setHorizontalFadingEdgeEnabled(ENABLED
                                        .equals(declaration.getStringValue()));
                            }
                        });
                declarationHandlers.put("android-vertical-fading-edge", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setVerticalFadingEdgeEnabled(ENABLED.equals(declaration
                                .getStringValue()));
                    }
                });
            }

            return declarationHandlers;
        }
    }
}
