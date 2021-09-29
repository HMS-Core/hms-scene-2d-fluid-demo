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

package com.huawei.scenekit.fluiddemo.shader;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.huawei.scenekit.fluiddemo.util.FileManager;

/**
 * Description: Texture
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Texture {
    private static final int DEFAULT_WRAP = GLES20.GL_CLAMP_TO_EDGE;

    private int[] textureId = new int[1];

    // Default texture.
    public Texture() {
        GLES20.glGenTextures(1, textureId, 0);
    }

    // Load the texture in the assets directory.
    public Texture(Context context, String assetName) {
        GLES20.glGenTextures(1, textureId, 0);
        Bitmap bitmap = FileManager.loadBitmap(context.getAssets(), assetName);
        loadTexture(bitmap, DEFAULT_WRAP, DEFAULT_WRAP);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /**
     * Loads the image to the texture.
     *
     * @param bitmap the bitmap
     * @param wrapS the wrap src
     * @param wrapT the wrap dst
     */
    private final void loadTexture(Bitmap bitmap, int wrapS, int wrapT) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapS);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    // Return the texture ID.
    public int getTextureId() {
        return textureId[0];
    }
}
