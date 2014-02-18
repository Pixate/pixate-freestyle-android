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
package com.pixate.freestyle.styling.animation;

import com.pixate.freestyle.parsing.PXSourceWriter;
import com.pixate.freestyle.styling.PXDeclaration;
import com.pixate.freestyle.styling.PXDeclarationContainer;

public class PXKeyframeBlock extends PXDeclarationContainer {

    private float offset;

    public PXKeyframeBlock(float offset) {
        this.offset = offset;
    }

    public float getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        PXSourceWriter writer = new PXSourceWriter();

        writer.increaseIndent();
        writer.printIndent();

        writer.print(String.format("%f ", offset));

        writer.println("{");
        writer.increaseIndent();

        if (declarations != null) {
            for (PXDeclaration declaration : declarations) {
                writer.printIndent();
                writer.println(declaration.toString());
            }
        }
        writer.decreaseIndent();
        writer.printIndent();
        writer.print("}");

        writer.decreaseIndent();

        return writer.toString();
    }
}
