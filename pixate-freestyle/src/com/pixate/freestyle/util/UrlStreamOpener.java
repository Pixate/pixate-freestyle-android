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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Base64;
import android.util.Base64InputStream;

import com.pixate.freestyle.PixateFreestyle;

public class UrlStreamOpener {

    // URL schemes
    public static final String FILE_SCHEME = "file://";
    public static final String DOCUMENTS_SCHEME = "documents://";
    public static final String BUNDLE_SCHEME = "bundle://";
    public static final String TMP_SCHEME = "tmp://";
    public static final String DATA_SCHEME = "data:";

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private static final String[] RESOURCE_TYPES = { "drawable", "raw" };

    private static final String TAG = UrlStreamOpener.class.getSimpleName();

    public static InputStream open(Uri uri) {
        return open(uri.toString());
    }

    public static InputStream open(String uri) {
        if (uri.startsWith(FILE_SCHEME)) {
            // Be sure it's absolute (file:/// instead of file://)
            if (uri.startsWith("file:///")) {
                return openFileScheme(uri);
            } else {
                return openFileScheme(uri.replace("file:", "file:/"));
            }
        } else if (uri.startsWith(DOCUMENTS_SCHEME)) {
            return openDocumentScheme(uri);
        } else if (uri.startsWith(BUNDLE_SCHEME)) {
            return openBundleScheme(uri);
        } else if (uri.startsWith(TMP_SCHEME)) {
            return openTempScheme(uri);
        } else if (uri.startsWith(DATA_SCHEME)) {
            return openDataScheme(uri);
        } else {
            return openImplicit(uri);
        }
    }

    private static InputStream openFileScheme(String urlString) {
        try {
            URI uri = URI.create(urlString);
            return new FileInputStream(new File(uri));
        } catch (FileNotFoundException e) {
            PXLog.e(TAG, e, "Unable to load file at this url: " + urlString);
            return null;
        }
    }

    private static InputStream openDocumentScheme(String urlString) {
        return openDocumentScheme(urlString, false);
    }

    private static InputStream openDocumentScheme(String urlString, boolean suppressErrorLog) {
        try {
            return PixateFreestyle.getAppContext().openFileInput(
                    urlString.substring(DOCUMENTS_SCHEME.length()));
        } catch (Exception e) {
            if (!suppressErrorLog) {
                PXLog.e(TAG, e, "Unable to load the document at this url: " + urlString);
            }
            return null;
        }
    }

    private static InputStream openBundleScheme(String urlString) {
        return openBundleScheme(urlString, false);
    }

    private static InputStream openBundleScheme(String urlString, boolean suppressErrorLog) {
        // bundle:// -- can be either a resource
        // or an asset.

        InputStream stream = null;
        Context context = PixateFreestyle.getAppContext();
        Resources resources = context.getResources();

        String resourceName = urlString.substring(BUNDLE_SCHEME.length());
        String base = resourceName;

        int lastDot = resourceName.lastIndexOf(".");

        if (lastDot >= 0) {
            base = resourceName.substring(0, lastDot);
        }

        int resId = findResourceId(resources, context.getPackageName(), base);

        if (resId != 0) {
            try {
                stream = resources.openRawResource(resId);
            } catch (Resources.NotFoundException e) {
                // Don't suppress this error since it's remarkable
                // that a resource cannot be opened even when we know it
                // exists (because we have a non-zero ID for it.)
                PXLog.e(TAG, e, "Unable to open asset/resource " + resourceName);
                stream = null;
            }

        } else {
            // Was not a resource, maybe an asset.
            AssetManager assets = resources.getAssets();

            try {
                stream = assets.open(resourceName);
            } catch (IOException e) {
                if (!suppressErrorLog) {
                    PXLog.e(TAG, e, "Unable to open asset/resource " + resourceName);
                }
                stream = null;
            }
        }

        return stream;
    }

    private static InputStream openTempScheme(String urlString) {
        Context context = PixateFreestyle.getAppContext();
        String fileName = urlString.substring(TMP_SCHEME.length());

        try {
            return new FileInputStream(context.getCacheDir().getAbsolutePath() + File.separator
                    + fileName);
        } catch (FileNotFoundException e) {
            PXLog.e(TAG, e, "Unable to open temp file " + fileName);
            return null;
        }

    }

    private static InputStream openDataScheme(String urlString) {
        String cleanString = WHITESPACE.matcher(urlString).replaceAll("");
        return openDataUriStream(cleanString);
    }

    private static InputStream openImplicit(String urlString) {
        InputStream stream = openDocumentScheme("documents://" + urlString, true);

        if (stream == null) {
            stream = openBundleScheme("bundle://" + urlString, true);
        }

        if (stream == null) {
            // last try, open as a regular URL.
            // Note that this is a networking call, and cannot be made from the
            // main thread.
            try {
                URL url = new URL(urlString);
                stream = url.openStream();
            } catch (Exception e) {
                PXLog.w(TAG, e, "Failed to open '%s' as URL", urlString);
            }
        }
        if (stream == null) {
            PXLog.w(TAG, "Neither a document nor a bundle entry with url '%s' could be opened.",
                    urlString);
        }

        return stream;
    }

    private static int findResourceId(Resources resources, String packageName, String resourceName) {
        int result = 0;

        for (int i = 0; i < RESOURCE_TYPES.length; i++) {
            result = resources.getIdentifier(resourceName, RESOURCE_TYPES[i], packageName);
            if (result != 0) {
                break;
            }
        }

        if (result == 0) {
            // Try built-in (system) resources
            for (int i = 0; i < RESOURCE_TYPES.length; i++) {
                result = Resources.getSystem().getIdentifier(resourceName, RESOURCE_TYPES[i],
                        "android");
                if (result != 0) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * @see http
     *      ://svn.apache.org/viewvc/xmlgraphics/commons/trunk/src/java/org/
     *      apache /xmlgraphics/util/uri/DataURIResolver.java
     */
    private static InputStream openDataUriStream(String uri) {
        int commaPos = uri.indexOf(',');
        if (commaPos < 0) {
            PXLog.w(TAG, "Data uri is malformed: " + uri);
            return null;
        }

        String header = uri.substring(0, commaPos);
        String data = uri.substring(commaPos + 1);
        if (header.endsWith(";base64")) {
            byte[] bytes = data.getBytes();
            ByteArrayInputStream encodedStream = new ByteArrayInputStream(bytes);
            return new Base64InputStream(encodedStream, Base64.DEFAULT);
        } else {
            String encoding = "UTF-8";
            final int charsetpos = header.indexOf(";charset=");
            if (charsetpos > 0) {
                encoding = header.substring(charsetpos + 9);
            }
            try {
                return new ByteArrayInputStream(URLDecoder.decode(data, encoding)
                        .getBytes(encoding));
            } catch (Exception e) {
                PXLog.e(TAG, e, "Unable to decode data uri contents: " + uri);
            }
        }
        return null;
    }

}
