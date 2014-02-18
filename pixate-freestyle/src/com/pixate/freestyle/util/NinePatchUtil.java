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
package com.pixate.freestyle.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;

import com.pixate.freestyle.cg.math.PXOffsets;

/**
 * A utility class that create {@link NinePatchDrawable} instances that wrap a
 * given {@link Bitmap}. The utility works with {@link PXOffsets} that defines
 * the cap offsets of the bitmap, just like in iOS.
 */
public class NinePatchUtil {

    public static NinePatchDrawable createNinePatch(Resources res, Bitmap bitmap, PXOffsets insets,
            String srcName) {

        byte[] chunk = createNinePatchChunk(insets);
        NinePatchDrawable drawable = new NinePatchDrawable(res, new NinePatch(bitmap, chunk,
                srcName));
        return drawable;
    }

    public static byte[] createNinePatchChunk(PXOffsets insets) {
        int top = (int) insets.getTop();
        int left = (int) insets.getLeft();
        int bottom = (int) insets.getBottom();
        int right = (int) insets.getRight();
        ByteBuffer buffer = ByteBuffer.allocate(56).order(ByteOrder.nativeOrder());

        // translated
        buffer.put((byte) 0x01);
        // divx array size
        buffer.put((byte) 0x02);
        // divy array size
        buffer.put((byte) 0x02);
        // color array size
        buffer.put((byte) 0x02);

        // skip 8 bytes
        buffer.putInt(0);
        buffer.putInt(0);

        // zero padding
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);
        buffer.putInt(0);

        // skip 4 bytes
        buffer.putInt(0);

        // write divx values
        buffer.putInt(left);
        buffer.putInt(right);
        // write divy values
        buffer.putInt(top);
        buffer.putInt(bottom);
        // write color values
        buffer.putInt(0x00000001); // no color
        buffer.putInt(0x00000001); // no color

        return buffer.array();
    }

}
