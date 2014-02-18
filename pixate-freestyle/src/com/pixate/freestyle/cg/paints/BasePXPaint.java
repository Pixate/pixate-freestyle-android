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
 * Copyright (c) 2012 Pixate, Inc. All rights reserved.
 */
package com.pixate.freestyle.cg.paints;

import android.graphics.Xfermode;

/**
 * Base {@link PXPaint} implementation.
 */
public abstract class BasePXPaint implements PXPaint {

    protected Xfermode blendingMode;

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.cg.PXPaint#setBleningMode(android.graphics.Xfermode )
     */
    public void setBleningMode(Xfermode mode) {
        blendingMode = mode;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.pxengine.cg.PXPaint#getBleningMode()
     */
    public Xfermode getBleningMode() {
        return blendingMode;
    }

}
