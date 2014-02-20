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
 * - color: <color>
 */
@PXDocStyler(properties = { @PXDocProperty(name = "color", syntax = "<color>") })
public class PXColorStyler extends PXStylerBase {

    private static PXColorStyler instance;

    public static PXColorStyler getInstance() {
        if (instance == null) {
            instance = new PXColorStyler(null);
        }
        return instance;
    }

    public PXColorStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    private static Map<String, PXDeclarationHandler> declarationHandlers;

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXColorStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXDeclarationHandler>(1);
                declarationHandlers.put("color", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setPropertyValue(declaration.getColorValue(), "color");
                    }
                });
            }

            return declarationHandlers;
        }
    }

}
