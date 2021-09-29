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

import android.opengl.GLES20;
import android.util.Log;

import com.huawei.scenekit.fluiddemo.shader.Material;
import com.huawei.scenekit.fluiddemo.shader.Texture;
import com.huawei.scenekit.fluiddemo.shader.Program;
import com.huawei.scenekit.fluiddemo.shader.ProgramUtil;
import com.huawei.scenekit.fluiddemo.util.Config;

/**
 * Description: BlurRender
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class BlurRender {
    private static final String TAG = "BlurRender";
    private static final String BLUR_TEXTURE_NAME = "texture";
    private static final int FB_SIZE = 128;

    private Surface blurSurface;
    private Material hBlurMaterial;
    private Material vBlurMaterial;

    public BlurRender() {
    }

    /**
     * draw particles
     *
     * @param inputTexture input texture
     * @param outputSurface output surface
     */
    public void draw(Texture inputTexture, Surface outputSurface) {
        if (blurSurface == null || hBlurMaterial == null || vBlurMaterial == null) {
            Log.e(TAG, "draw: null pointer exception");
            return;
        }
        blurSurface.beginRender(0);
        hBlurMaterial.startRender();

        Config.QUAD_VERTEX_BUFFER.rewind();

        hBlurMaterial.setVertexBuffer("position", Config.QUAD_VERTEX_BUFFER, 0, Config.QUAD_VERTEX_STRIDE);
        hBlurMaterial.setVertexBuffer("uv", Config.QUAD_VERTEX_BUFFER, 3, Config.QUAD_VERTEX_STRIDE);
        hBlurMaterial.updateUniformTexture(BLUR_TEXTURE_NAME, 0, inputTexture.getTextureId());
        hBlurMaterial.updateUniform("blurBufferSize", 1.0f / FB_SIZE);;
        hBlurMaterial.draw(Material.DrawType.TRIANGLE_FAN, 0, 4);
        hBlurMaterial.endRender();
        blurSurface.endRender();

        GLES20.glFlush();
        outputSurface.beginRender(0);
        vBlurMaterial.startRender();
        vBlurMaterial.setVertexBuffer("position", Config.QUAD_VERTEX_BUFFER, 0, Config.QUAD_VERTEX_STRIDE);
        vBlurMaterial.setVertexBuffer("uv", Config.QUAD_VERTEX_BUFFER, 3, Config.QUAD_VERTEX_STRIDE);
        vBlurMaterial.updateUniformTexture(BLUR_TEXTURE_NAME, 0, blurSurface.getTexture().getTextureId());
        vBlurMaterial.updateUniform("blurBufferSize", 1.0f / FB_SIZE);
        vBlurMaterial.draw(Material.DrawType.TRIANGLE_FAN, 0, 4);
        vBlurMaterial.endRender();
        outputSurface.endRender();
    }

    /**
     * create material
     */
    public void createMaterial() {
        hBlurMaterial = new Material(new Program(ProgramUtil.Shader.HBLUR));
        vBlurMaterial = new Material(new Program(ProgramUtil.Shader.VBLUR));
        hBlurMaterial.addAttribute("position", 3, ProgramUtil.FLOAT, 4, false);
        hBlurMaterial.addAttribute("uv", 2, ProgramUtil.FLOAT, 4, false);
        vBlurMaterial.addAttribute("position", 3, ProgramUtil.FLOAT, 4, false);
        vBlurMaterial.addAttribute("uv", 2, ProgramUtil.FLOAT, 4, false);

        blurSurface = new Surface(FB_SIZE, FB_SIZE);
    }
}
