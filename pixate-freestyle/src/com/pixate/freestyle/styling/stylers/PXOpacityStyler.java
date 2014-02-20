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

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.styling.PXDeclaration;

/**
 * - opacity: <number>
 */
@PXDocStyler(properties = { @PXDocProperty(name = "opacity", syntax = "<number>") })
public class PXOpacityStyler extends PXStylerBase {

    private static PXOpacityStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXOpacityStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXOpacityStyler getInstance() {
        if (instance == null) {
            instance = new PXOpacityStyler(new PXStylerInvocation() {
                public void invoke(Object view, PXStyler styler, PXStylerContext context) {
                    if (view instanceof View) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            setAlphaHC((View) view, context.getOpacity());
                        } else {
                            setAlphaG((View) view, context.getOpacity());
                        }
                    }
                }
            });
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXOpacityStyler.class) {

            if (declarationHandlers == null) {
                declarationHandlers = new HashMap<String, PXDeclarationHandler>(1);

                declarationHandlers.put("opacity", new PXDeclarationHandler() {
                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        stylerContext.setOpacity(declaration.getFloatValue(stylerContext
                                .getDisplayMetrics()));
                    }
                });
            }

            return declarationHandlers;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void setAlphaHC(View view, float value) {
        view.setAlpha(value);
    }

    // Gingerbread
    private static void setAlphaG(View view, float value) {
        Drawable d = view.getBackground();
        if (d != null) {
            d.setAlpha(Float.valueOf(value * 255f).intValue());
        }
    }

}
