/*
 * Copyright 2021 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.scenekit.fluiddemo.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Description: FileManager
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public final class FileManager {
    /**
     * load shader
     *
     * @param am asset manager
     * @param fileName file name
     * @return content
     */
    public static String loadShader(AssetManager am, String fileName) {
        String content = null;
        InputStream inStream = null;
        IOException processException = null;

        try {
            inStream = am.open(fileName);

            byte[] buffer = new byte[inStream.available()];
            int readCount = inStream.read(buffer);
            if (readCount == -1) {
                Log.e("fileManager", "loadShader: file is empty");
            }

            content = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            processException = e;
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    if (processException != null) {
                        Log.e("fileManager", "loadShader: error reading input stream");
                    } else {
                        Log.e("fileManager", "loadShader: error closing input stream");
                    }
                    e.printStackTrace();
                }
            }
        }

        return content;
    }

    /**
     * load bitmap
     *
     * @param am asset manager
     * @param fileName file name
     * @return Bitmap
     */
    public static Bitmap loadBitmap(AssetManager am, String fileName) {
        Bitmap image;
        InputStream inStream = null;

        try {
            inStream = am.open(fileName);
            image = BitmapFactory.decodeStream(inStream);
        } catch (IOException e) {
            e.printStackTrace();
            image = null;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    Log.e("fileManager", "loadBitmap: error closing input stream");
                    e.printStackTrace();
                }
            }
        }

        return image;
    }
}
