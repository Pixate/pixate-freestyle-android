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

import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;

/**
 * - transform: <transform>+
 */
@PXDocStyler(hide=true)
public class PXTransformStyler extends PXStylerBase {

    private static PXTransformStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXTransformStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXTransformStyler getInstance() {
        if (instance == null) {
            instance = new PXTransformStyler(new PXStylerInvocation() {
                public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                    // TODO -- no transform property in Android.
                }
            });
        }

        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXTransformStyler.class) {

            if (declarationHandlers == null) {

                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>(1);

                declarationHandlers.put("transform", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setTransform(declaration.getAffineTransformValue());
                    }
                });
            }

            return declarationHandlers;
        }
    }

}
