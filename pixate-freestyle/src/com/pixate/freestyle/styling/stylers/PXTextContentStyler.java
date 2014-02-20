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
 * - text: <string>
 */
@PXDocStyler(properties={@PXDocProperty(name="text", syntax="<string>")})
public class PXTextContentStyler extends PXStylerBase {

    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXTextContentStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXTextContentStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>(1);

                declarationHandlers.put("text", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setText(declaration.getStringValue());
                    }
                });

            }

            return declarationHandlers;
        }
    }

}
