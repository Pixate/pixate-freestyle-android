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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {

    private static final String UTF8 = "UTF-8";
    public static final String BOM = "\uFEFF";

    public static String read(String filePath) throws IOException {
        return read(filePath, UTF8);
    }

    public static String read(String filePath, String charsetName) throws IOException {
        return read(new FileInputStream(filePath), charsetName);
    }

    public static String read(InputStream inputStream) throws IOException {
        return read(inputStream, UTF8);
    }

    public static String read(InputStream inputStream, String charsetName) throws IOException {
        char[] readBuffer = new char[1024];
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(inputStream, charsetName);
            StringBuilder builder = new StringBuilder();
            int read = -1;
            while ((read = reader.read(readBuffer)) != -1) {
                builder.append(readBuffer, 0, read);
            }
            String s = builder.toString();

            if (s.startsWith(BOM)) {
                s = s.substring(1);
            }

            return s;

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
