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
package com.pixate.freestyle.cg.paints;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Xfermode;

import com.pixate.freestyle.util.ObjectUtil;

/**
 * A {@link PXPaint} group.
 */
public class PXPaintGroup extends BasePXPaint {

    private List<PXPaint> paints;

    public PXPaintGroup() {
    }

    public PXPaintGroup(PXPaint... paints) {
        for (PXPaint paint : paints) {
            addPaint(paint);
        }
    }

    public PXPaintGroup(List<PXPaint> paints) {
        for (PXPaint paint : paints) {
            addPaint(paint);
        }
    }

    /**
     * Adds a paint to the group.
     * 
     * @param paint A {@link PXPaint}
     */
    public void addPaint(PXPaint paint) {
        if (paint != null) {
            if (paints == null) {
                paints = new ArrayList<PXPaint>(3);
            }
            paint.setBleningMode(blendingMode);
            paints.add(paint);
        }
    }

    /**
     * Removes a paint from the group.
     * 
     * @param paint A {@link PXPaint}
     */
    public void removePaint(PXPaint paint) {
        if (paints != null) {
            paints.remove(paint);
        }
    }

    /**
     * Returns the {@link PXPaint} list reference.
     * 
     * @return A {@link PXPaint} list.
     */
    public List<PXPaint> getPaints() {
        return paints;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.cg.PXPaint#applyFillToPath(android.graphics
     * . Path, android.graphics.Paint, android.graphics.Canvas)
     */
    public void applyFillToPath(Path path, Paint paint, Canvas context) {
        if (paints != null) {
            for (PXPaint p : paints) {
                p.applyFillToPath(path, paint, context);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.pixate.freestyle.pxengine.cg.BasePXPaint#setBleningMode(android.graphics
     * .Xfermode)
     */
    @Override
    public void setBleningMode(Xfermode mode) {
        super.setBleningMode(mode);
        if (paints != null) {
            for (PXPaint p : paints) {
                p.setBleningMode(mode);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#darkenByPercent(float)
     */
    public PXPaint darkenByPercent(float percent) {
        PXPaintGroup group = new PXPaintGroup();
        if (paints != null) {
            for (PXPaint paint : paints) {
                group.addPaint(paint.darkenByPercent(percent));
            }
        }
        return group;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#lightenByPercent(float)
     */
    public PXPaint lightenByPercent(float percent) {
        PXPaintGroup group = new PXPaintGroup();
        if (paints != null) {
            for (PXPaint paint : paints) {
                group.addPaint(paint.lightenByPercent(percent));
            }
        }
        return group;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isOpaque()
     */
    public boolean isOpaque() {
        if (paints != null) {
            for (PXPaint paint : paints) {
                if (!paint.isOpaque()) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.pixate.freestyle.cg.paints.PXPaint#isAsynchronous()
     */
    @Override
    public boolean isAsynchronous() {
        if (paints != null) {
            for (PXPaint paint : paints) {
                if (paint.isAsynchronous()) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof PXPaintGroup) {
            return ObjectUtil.areEqual(paints, ((PXPaintGroup) other).paints);
        }
        return false;
    }
}
