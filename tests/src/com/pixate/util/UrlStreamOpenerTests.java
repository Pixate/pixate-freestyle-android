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
package com.pixate.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Base64;
import android.util.Base64OutputStream;

import com.pixate.PixateFreestyle;

public class UrlStreamOpenerTests extends AndroidTestCase {

    private static final String RAW_TEST_FILE = "url_test_file";
    private static final String IMAGE_RES = "ic_launcher.png";
    private static final String IMAGE_ASSET = "urlOpener/asset_test.png";
    private static final String DOCUMENT_FILE = "doc_test";
    private static final String TMP_FILE = "tmp_test";

    private String testFileContents;
    private String documentFileUri;
    private File tempFile;
    private Bitmap assetBitmap;
    private String assetBitmapBase64;

    public UrlStreamOpenerTests() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Context context = this.getContext();
        PixateFreestyle.init(context.getApplicationContext());

        // Grab the bitmap placed in the assets. We can use it to compare
        // results later.
        InputStream is = context.getAssets().open(IMAGE_ASSET);
        assetBitmap = BitmapFactory.decodeStream(is);
        is.close();

        Resources resources = context.getResources();

        int rawFileId = resources.getIdentifier(RAW_TEST_FILE, "raw", this.getContext().getPackageName());

        testFileContents = readStream(resources.openRawResource(rawFileId));

        // Create a document file.
        OutputStreamWriter writer =
                new OutputStreamWriter(getContext().openFileOutput(DOCUMENT_FILE, Context.MODE_PRIVATE));
        try {
            writer.write(testFileContents);
        } finally {
            writer.close();
        }

        // Learn the document file's file:// uri so we can test that scheme.
        documentFileUri = new File(context.getFilesDir(), DOCUMENT_FILE).toURI().toString();

        // Clean it up to make it look like someone would type it in css
        // (file:// instead of just file:/)
        if (documentFileUri.startsWith("file:/") && !documentFileUri.startsWith("file://")) {
            documentFileUri = documentFileUri.replace("file:", "file://");
        }

        // Create a temp file.
        tempFile = new File(context.getCacheDir(), TMP_FILE);
        writer = new OutputStreamWriter(new FileOutputStream(tempFile));
        try {
            writer.write(testFileContents);
        } finally {
            writer.close();
        }

        // Get a base64 of the test asset image bytes so we can do a data: call
        // and compare results.
        is = context.getAssets().open(IMAGE_ASSET);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Base64OutputStream bos = new Base64OutputStream(output, Base64.DEFAULT);

        try {
            byte[] buffer = new byte[2048];
            int count = is.read(buffer);

            while (count > 0) {
                bos.write(buffer, 0, count);
                count = is.read(buffer);
            }

            assetBitmapBase64 = output.toString();

        } finally {
            is.close();
            bos.close();
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        // Remove files created for the tests.

        // The document file.
        try {
            getContext().deleteFile(DOCUMENT_FILE);
        } catch (Exception e) {
            // No-op
        }

        // The temp file.
        try {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        } catch (Exception e) {
            // no-op
        }

    }

    public void testResourceBitmap() {
        Bitmap bitmap =
                new BitmapDrawable(getContext().getResources(), UrlStreamOpener.open("bundle://" + IMAGE_RES)).getBitmap();
        assertNotNull("Bitmap not created", bitmap);
        assertTrue("Returned bitmap too small to be valid", bitmap.getHeight() > 10 && bitmap.getWidth() > 10);
    }

    public void testResourceText() throws Exception {
        assertEquals(testFileContents, readStream(UrlStreamOpener.open("bundle://" + RAW_TEST_FILE)));
    }

    public void testAssetBitmap() throws Exception {
        InputStream is = UrlStreamOpener.open("bundle://" + IMAGE_ASSET);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            boolean result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                result = checkSameBitmapHC(assetBitmap, bitmap);
            } else {
                result = checkSameBitmapGB(assetBitmap, bitmap);
            }
            assertTrue("Returned bitmap different than canonical bitmap", result);
        } finally {
            is.close();
        }
    }

    public void testDocument() throws Exception {
        assertEquals(testFileContents, readStream(UrlStreamOpener.open("documents://" + DOCUMENT_FILE)));
    }

    public void testFile() throws Exception {
        assertEquals(testFileContents, readStream(UrlStreamOpener.open(documentFileUri)));
    }

    public void testTmp() throws Exception {
        assertEquals(testFileContents, readStream(UrlStreamOpener.open("tmp://" + TMP_FILE)));
    }

    public void testData() throws Exception {
        InputStream is = UrlStreamOpener.open("data:image/png;base64," + assetBitmapBase64);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            boolean result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                result = checkSameBitmapHC(assetBitmap, bitmap);
            } else {
                result = checkSameBitmapGB(assetBitmap, bitmap);
            }
            assertTrue("Returned bitmap different than canonical bitmap", result);
        } finally {
            is.close();
        }
    }

    public void testImplicitDocument() throws Exception {
        // Document is checked first. This matches the test
        // document file name, so it should work.
        assertEquals(testFileContents, readStream(UrlStreamOpener.open(DOCUMENT_FILE)));
    }

    public void testImplicitBundleAsset() throws Exception {
        // Bundle is checked after documents. This is a valid
        // file name in assets (a bundle source), so it should be found.
        InputStream is = UrlStreamOpener.open(IMAGE_ASSET);
        assertNotNull(is);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            boolean result;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                result = checkSameBitmapHC(assetBitmap, bitmap);
            } else {
                result = checkSameBitmapGB(assetBitmap, bitmap);
            }
            assertTrue("Returned bitmap different than canonical bitmap", result);
        } finally {
            is.close();
        }
    }

    // Private

    // Also closes the stream.
    private String readStream(InputStream is) throws Exception {
        if (is == null) {
            return null;
        }
        char[] buffer = new char[2048];
        StringBuilder sb = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is, "utf-8");

        int count = reader.read(buffer);
        while (count > 0) {
            sb.append(buffer, 0, count);
            count = reader.read(buffer);
        }

        is.close();
        return sb.toString();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private boolean checkSameBitmapHC(Bitmap b1, Bitmap b2) {
        if (b1 == null || b2 == null) {
            return false;
        }
        return b1.sameAs(b2);
    }

    private boolean checkSameBitmapGB(Bitmap b1, Bitmap b2) {
        if (b1 != null && b1 == b2) {
            return true;
        }
        if (b1 == null || b2 == null) {
            return false;
        }
        // Poor man's comparison for Gingerbread.
        return b1.getHeight() == b2.getHeight()
                && b1.getWidth() == b2.getWidth()
                && b1.getRowBytes() == b2.getRowBytes()
                && b1.getPixel(0, 0) == b2.getPixel(0, 0)
                && b1.getPixel(b1.getWidth() - 1, b1.getHeight() - 1) == b2.getPixel(b2.getWidth() - 1,
                        b2.getHeight() - 1);
    }

}
