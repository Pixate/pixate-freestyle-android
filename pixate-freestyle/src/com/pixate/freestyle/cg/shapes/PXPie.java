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
package com.pixate.freestyle.cg.shapes;

import android.graphics.Path;
import android.graphics.RectF;

import com.pixate.freestyle.util.ObjectPool;

/**
 * A PXArc subclass used to render pie graphs
 */
public class PXPie extends PXArc {

    @Override
    public Path newPath() {
        Path path = ObjectPool.pathPool.checkOut();
        float startingRadians = (float) Math.toRadians(this.startingAngle);
        float endingRadians = (float) Math.toRadians(this.endingAngle);

        path.moveTo(this.center.x, this.center.y);

        // Android arc is defined differently than the iOS arc.
        // TODO - Test this visually. Not so sure if the given angles are good,
        // or need a conversion.
        RectF oval = new RectF(center.x - radius, center.y + radius, center.x + radius, center.y
                - radius);
        // We keep those in degrees, not radians.
        path.addArc(oval, startingRadians, endingRadians);
        path.close();
        return path;
    }
}
