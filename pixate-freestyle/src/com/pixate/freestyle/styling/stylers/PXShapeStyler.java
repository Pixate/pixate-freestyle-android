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

import android.graphics.RectF;

import com.pixate.freestyle.annotations.PXDocProperty;
import com.pixate.freestyle.annotations.PXDocStyler;
import com.pixate.freestyle.cg.shapes.PXArrowRectangle;
import com.pixate.freestyle.cg.shapes.PXArrowRectangle.PXArrowRectangleDirection;
import com.pixate.freestyle.cg.shapes.PXEllipse;
import com.pixate.freestyle.cg.shapes.PXRectangle;
import com.pixate.freestyle.styling.PXDeclaration;

/**
 * - shape: ellipse | rectangle | arrow-button-left | arrow-button-right
 */
@PXDocStyler(properties = { @PXDocProperty(name = "shape", syntax = "ellipse | rectangle | arrow-button-left | arrow-button-right") })
public class PXShapeStyler extends PXStylerBase {

    private static PXShapeStyler instance;
    private static Map<String, PXDeclarationHandler> declarationHandlers;

    public PXShapeStyler(PXStylerInvocation invocation) {
        super(invocation);
    }

    public synchronized static PXShapeStyler getInstance() {
        if (instance == null) {
            instance = new PXShapeStyler(null);
        }
        return instance;
    }

    @Override
    public Map<String, PXDeclarationHandler> getDeclarationHandlers() {
        synchronized (PXShapeStyler.class) {

            if (declarationHandlers == null) {

                declarationHandlers = new HashMap<String, PXStylerBase.PXDeclarationHandler>(1);

                declarationHandlers.put("shape", new PXDeclarationHandler() {

                    public void process(PXDeclaration declaration, PXStylerContext stylerContext) {
                        String stringValue = declaration.getStringValue();

                        if ("ellipse".equals(stringValue)) {
                            stylerContext.setShape(new PXEllipse());

                        } else if ("arrow-button-left".equals(stringValue)) {
                            stylerContext.setShape(new PXArrowRectangle(PXArrowRectangleDirection.LEFT));

                        } else if ("arrow-button-right".equals(stringValue)) {
                            stylerContext.setShape(new PXArrowRectangle(PXArrowRectangleDirection.RIGHT));

                        } else {
                            stylerContext.setShape(new PXRectangle(new RectF()));
                        }
                    }
                });
            }

            return declarationHandlers;
        }
    }

}
