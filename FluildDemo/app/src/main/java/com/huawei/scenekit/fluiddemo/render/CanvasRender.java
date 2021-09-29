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

import android.content.Context;

import com.huawei.scenekit.fluiddemo.shader.Material;
import com.huawei.scenekit.fluiddemo.shader.Texture;
import com.huawei.scenekit.fluiddemo.shader.Program;
import com.huawei.scenekit.fluiddemo.shader.ProgramUtil;
import com.huawei.scenekit.fluiddemo.util.Config;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Description: CanvasRender
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class CanvasRender {
    private Material canvasMaterial;
    private float[] uvTransform = new float[16];
    private final FloatBuffer positionBuffer;
    private final FloatBuffer coordBuffer;
    private Texture canvasTexture;

    public CanvasRender() {
        positionBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        float[] data = new float[] {
                0, 0, 1, 0, 0, 1, 1, 1
        };
        coordBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        coordBuffer.put(data);
    }

    /**
     * onSurfaceCreated override
     *
     * @param context the context
     */
    public void onSurfaceCreated(Context context) {
        canvasTexture = new Texture(context, Config.DEFAULT_TEXTURE_NAME);

        canvasMaterial = new Material(new Program(ProgramUtil.Shader.TEXTURE));
        canvasMaterial.addAttribute("position", 2, ProgramUtil.FLOAT, 4, false);
        canvasMaterial.addAttribute("uv", 2, ProgramUtil.FLOAT, 4, false);
        canvasMaterial.setBlendFactor(ProgramUtil.BLEND_ONE, ProgramUtil.BLEND_ONE_MINUS_SRC_ALPHA);
        canvasMaterial.addSamplerTexture("texture", canvasTexture);
    }

    /**
     * draw particles
     */
    public void draw() {
        setVolume(-1, -1, 1, 1);
        uvTransform = Arrays.copyOf(Config.MATRIX4, uvTransform.length);

        coordBuffer.rewind();
        positionBuffer.rewind();
        canvasMaterial.startRender();
        canvasMaterial.setVertexBuffer("position", positionBuffer, 0, 0);
        canvasMaterial.setVertexBuffer("uv", coordBuffer, 0, 0);
        canvasMaterial.updateUniformTexture("texture", 0, canvasTexture.getTextureId());
        canvasMaterial.updateUniform("mvp", Config.MATRIX4);
        canvasMaterial.updateUniform("uvTransform", uvTransform);
        canvasMaterial.updateUniform("alphaFactor", 1.0f);
        canvasMaterial.draw(Material.DrawType.TRIANGLE_STRIP, 0, 4);
        canvasMaterial.endRender();
    }

    /**
     * set Volume
     *
     * @param left the left
     * @param bottom the bottom
     * @param right the right
     * @param top the top
     */
    public void setVolume(float left, float bottom, float right, float top) {
        float[] data = new float[] {
                left, bottom, right, bottom, left, top, right, top
        };
        positionBuffer.position(0);
        positionBuffer.put(data);
    }
}
