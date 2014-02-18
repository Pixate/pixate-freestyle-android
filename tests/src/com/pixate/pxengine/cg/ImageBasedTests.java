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
package com.pixate.pxengine.cg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.test.AndroidTestCase;

/**
 * Images tests base class.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class ImageBasedTests extends AndroidTestCase {
    private static final String FAILING_BITMAPS_PATH = "/pixateTests/FailingBitmaps/";

    /**
     * Compare two bitmaps, pixel by pixel. In case the compared result is not
     * as expected, we will write those images as png's under
     * /mnt/sdcard/pixateTests/FailingBitmaps/
     * 
     * @param result
     * @param expected
     */
    public static void assertImages(String name, Bitmap result, Bitmap expected) throws Exception {
        // Since we would like to write out the images when we assert, we do a
        // check before the assertion ofeach condition.
        if (result.getHeight() != expected.getHeight()) {
            saveAndAssert("Expected the same bitmap height", name, result, expected);
        }
        if (result.getWidth() != expected.getWidth()) {
            saveAndAssert("Expected the same bitmap width", name, result, expected);
        }

        // pixel-by-pixel comparison.
        // note that the Bitmap#sameAs() does not really compare well the data,
        // although it claims to do so.
        boolean same = true;
        outer: for (int x = 0; x < result.getWidth(); x++) {
            for (int y = 0; y < result.getHeight(); y++) {
                if (result.getPixel(x, y) != expected.getPixel(x, y)) {
                    same = false;
                    break outer;
                }
            }
        }
        if (!same) {
            saveAndAssert("Expected the same bitmap data", name, result, expected);
        }
    }

    /**
     * Write the bitmaps as PNGs to /mnt/sdcard/pixateTests/FailingBitmaps/ and
     * assert.
     * 
     * @param message
     * @param name
     * @param result
     * @param expected
     */
    private static void saveAndAssert(String message, String name, Bitmap result, Bitmap expected)
            throws Exception {
        // leave the resulted file name as is, in case we would like to update
        // the 'golden' images.
        writeBitmap(result, name);
        // mark the expected output with "-expected".
        writeBitmap(expected, name + "-expected");
        assertTrue(message, false);
    }

    /**
     * Writes a bitmap to /mnt/shell/emulated/0/pixateTests/FailingBitmaps/
     * 
     * @param bitmap
     * @param imageId
     * @throws IOException
     */
    private static void writeBitmap(Bitmap bitmap, String imageId) throws IOException {
        String path = Environment.getExternalStorageDirectory().getPath() + FAILING_BITMAPS_PATH;
        File outputDir = new File(path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File file = new File(path, imageId + ".png");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
    }

}
