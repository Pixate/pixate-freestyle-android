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
package com.pixate.freestyle.cg.shadow;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Path;

public class PXShadowGroup extends ArrayList<PXShadowPaint> implements PXShadowPaint {

    private static final long serialVersionUID = -8931242820673829504L;

    public void applyInsetToPath(Path path, Canvas context) {
        for (PXShadowPaint shadow : this) {
            shadow.applyInsetToPath(path, context);
        }
    }

    public void applyOutsetToPath(Path path, Canvas context) {
        for (PXShadowPaint shadow : this) {
            shadow.applyOutsetToPath(path, context);
        }
        
    }
    
}
