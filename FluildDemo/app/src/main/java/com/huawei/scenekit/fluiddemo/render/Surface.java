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

package com.huawei.scenekit.fluiddemo.render;

import android.graphics.Color;
import android.opengl.GLES20;

import com.huawei.scenekit.fluiddemo.shader.Texture;

/**
 * Description: Surface
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Surface {
    private int[] frameBuffer = new int[1];
    private Texture texture;
    private int width;
    private int height;
    private int color = Color.TRANSPARENT;

    public Surface(int width, int height) {
        this.width = width;
        this.height = height;

        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        texture = new Texture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureId());
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, this.width, this.height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture.getTextureId(), 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * begin render
     *
     * @param clearMask gl clear mask
     */
    public void beginRender(int clearMask) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glViewport(0, 0, width, height);
        if (clearMask != 0) {
            GLES20.glClearColor(Color.red(color), Color.blue(color), Color.green(color), Color.alpha(color));
            GLES20.glClear(clearMask);
        }
    }

    /**
     * end render
     */
    public void endRender() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public Texture getTexture() {
        return texture;
    }

    /**
     * set clear color
     *
     * @param color the color
     */
    public void setClearColor(int color) {
        this.color = color;
    }
}
