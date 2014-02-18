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
/**
 * Copyright (c) 2012-2013 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.shadow;

import android.graphics.Canvas;
import android.graphics.Path;

/**
 * The PXShadowPaint interface specifies the properties and methods required for
 * a class to be used for shadowing of a contour
 */
public interface PXShadowPaint {
    /**
     * Apply an outer shadow to the specified path
     * 
     * @param path A path used to generate a shadow
     * @param context The context into which to render the shadow
     */
    void applyOutsetToPath(Path path, Canvas context);

    /**
     * Apply an inner shadow to the specified path
     * 
     * @param path A path used to generate a shadow
     * @param context The context into which to render the shadow
     */
    void applyInsetToPath(Path path, Canvas context);

}
