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

import android.view.View;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;

/**
 * - box-shadow: <shadow> ["," <shadow>]*
 */

/*
 * Support inner and outer shadows. Note that if no styler callback is supplied,
 * this styler will automatically apply any outer shadows to the view being
 * styled. It is important to know that if you provide a callback for you box
 * shadow styler, you are responsible for applying the outer shadows.
 */
@PXDocStyler(properties = { @PXDocProperty(name = "box-shadow", syntax = "<shadow> [\",\" <shadow>]*") })
public class PXBoxShadowStyler extends PXStylerBase {
    private static PXBoxShadowStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXBoxShadowStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXBoxShadowStyler getInstance() {
        if (instance == null) {
            instance = new PXBoxShadowStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXBoxShadowStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>();

                declarationHandlers.put("box-shadow", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setShadow(declaration.getShadowValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });
            }

            return declarationHandlers;
        }
    }

    @Override
    public void applyStylesWithContext(PXStylerContext stylerContext) {
        if (this.stylerInvocation != null) {
            super.applyStylesWithContext(stylerContext);

        } else {
            Object styleable = stylerContext.getStyleable();
            if (styleable instanceof View) {
                stylerContext.applyOuterShadow((View) styleable);
            }
        }
    }
}
