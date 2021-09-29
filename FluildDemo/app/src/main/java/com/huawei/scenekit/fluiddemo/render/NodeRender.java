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
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.huawei.hms.scene.sdk.fluid.ParticleGroup;
import com.huawei.hms.scene.sdk.fluid.ParticleSystem;
import com.huawei.scenekit.fluiddemo.shader.Texture;
import com.huawei.scenekit.fluiddemo.shader.Material;
import com.huawei.scenekit.fluiddemo.shader.Program;
import com.huawei.scenekit.fluiddemo.shader.ProgramUtil;
import com.huawei.scenekit.fluiddemo.util.Config;

/**
 * Description: NodeRender
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class NodeRender {
    private ByteBuffer colorBuffer;
    private ByteBuffer positionBuffer;
    private ByteBuffer weightBuffer;

    // Render a blur.
    private BlurRender blurRender;

    private Material waterNodeMaterial;
    private Material otherNodeMaterial;
    private Material waterScreenMaterial;
    private Material otherScreenMaterial;

    private final float[] textureTransform = new float[16];
    private final float[] worldTransform = new float[16];

    private final Surface[] renderSurface = new Surface[2];
    private ArrayList<ParticleGroup> noneWaterGroup = new ArrayList<>(Config.MAX_NODE_GROUP_COUNT);

    private int screenWidth = (int)Config.DEFAULT_WORLD_HEIGHT;
    private int screenHeight = (int)Config.DEFAULT_WORLD_HEIGHT;

    public NodeRender() {
        int positionSize = 2 * 4 * Config.MAX_NODE_COUNT;
        int colorSize = 4 * Config.MAX_NODE_COUNT;
        int weightSize = 4 * Config.MAX_NODE_COUNT;
        positionBuffer = ByteBuffer.allocateDirect(positionSize).order(ByteOrder.nativeOrder());
        colorBuffer = ByteBuffer.allocateDirect(colorSize).order(ByteOrder.nativeOrder());
        weightBuffer = ByteBuffer.allocateDirect(weightSize).order(ByteOrder.nativeOrder());

        blurRender = new BlurRender();
    }

    /**
     * onSurfaceChanged override
     *
     * @param width the surface width
     * @param height the surface height
     */
    public void onSurfaceChanged(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        int offset = 0;
        // portrait
        if (height > width) {
            float ratio = (float) width / height;
            Matrix.setIdentityM(textureTransform, offset);
            Matrix.scaleM(textureTransform, offset, 1 / ratio, 1, 1);

            Matrix.setIdentityM(worldTransform, offset);
            Matrix.translateM(worldTransform, offset, -ratio, -1, 0);
            Matrix.scaleM(worldTransform, offset, 2 * ratio / Config.WORLD_WIDTH, 2.f / Config.WORLD_HEIGHT, 1);
        } else {
            float ratio = (float) height / width;
            Matrix.setIdentityM(textureTransform, offset);
            Matrix.scaleM(textureTransform, offset, 1, 1 / ratio, 1);

            Matrix.setIdentityM(worldTransform, offset);
            Matrix.translateM(worldTransform, offset, -1, -ratio, 0);
            Matrix.scaleM(worldTransform, offset, 2f / Config.WORLD_WIDTH, 2 * ratio / Config.WORLD_HEIGHT, 1);
        }
    }

    /**
     * onSurfaceCreated override
     *
     * @param context the context
     */
    public void onSurfaceCreated(Context context) {
        // Create a rendering plane.
        createSurface();

        // Create a rendering material.
        createMaterial(context);
    }

    /**
     * draw particles
     */
    public void draw() {
        positionBuffer.rewind();
        colorBuffer.rewind();
        weightBuffer.rewind();
        noneWaterGroup.clear();

        Render.getInstance().getManager().acquire();
        ParticleSystem system = Render.getInstance().getManager().getParticleSystem();
        try {
            int worldParticleCount = system.getParticleCount();

            byte[] positionByte = new byte[positionBuffer.capacity()];
            byte[] colorByte = new byte[colorBuffer.capacity()];
            byte[] weightByte = new byte[weightBuffer.capacity()];
            system.copyPositionBuffer(worldParticleCount, positionByte);
            system.copyColorBuffer(worldParticleCount, colorByte);
            system.copyWeightBuffer(worldParticleCount, weightByte);
            positionBuffer.put(positionByte);
            colorBuffer.put(colorByte);
            weightBuffer.put(weightByte);

            GLES20.glClearColor(0, 0, 0, 0);
            // Draw water particles.
            drawWaterNodes();

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glViewport(0, 0, screenWidth, screenHeight);

            // Draw water particles on the screen.
            drawWaterNodesScreen();
        } finally {
            Render.getInstance().getManager().release();
        }
    }

    // Create a rendering plane.
    private void createSurface() {
        renderSurface[0] = new Surface(Config.FB_SIZE, Config.FB_SIZE);
        renderSurface[0].setClearColor(Config.CLEAR_COLOR);

        renderSurface[1] = new Surface(Config.FB_SIZE, Config.FB_SIZE);
        renderSurface[1].setClearColor(Config.CLEAR_COLOR);
    }

    // Create a rendering material.
    private void createMaterial(Context context) {
        // Set material attributes of water particles.
        waterNodeMaterial = new Material(new Program(ProgramUtil.Shader.WATER_NODE));
        waterNodeMaterial.addAttribute("position", 2, ProgramUtil.FLOAT, 4, false);
        waterNodeMaterial.addAttribute("color", 4, ProgramUtil.UNSIGNED_BYTE, 1, true);
        waterNodeMaterial.addAttribute("weight", 1, ProgramUtil.FLOAT, 1, false);
        waterNodeMaterial.setBlendFactor(ProgramUtil.BLEND_ONE, ProgramUtil.BLEND_ONE_MINUS_SRC_ALPHA);
        waterNodeMaterial.addSamplerTexture("texture", new Texture(context, Config.BLUR_TEXTURE_NAME));

        // Set material attributes of non-water particles.
        otherNodeMaterial = new Material(new Program(ProgramUtil.Shader.NODE));
        otherNodeMaterial.addAttribute("position", 2, ProgramUtil.FLOAT, 4, false);
        otherNodeMaterial.addAttribute("color", 4, ProgramUtil.UNSIGNED_BYTE, 1, true);
        otherNodeMaterial.setBlendFactor(ProgramUtil.BLEND_ONE, ProgramUtil.BLEND_ONE_MINUS_SRC_ALPHA);
        otherNodeMaterial.addSamplerTexture("texture", new Texture(context, Config.BLUR_TEXTURE_NAME));

        waterScreenMaterial = new Material(new Program(ProgramUtil.Shader.SCREEN));
        waterScreenMaterial.addAttribute("position", 3, ProgramUtil.FLOAT, 4, false);
        waterScreenMaterial.addAttribute("uv", 2, ProgramUtil.FLOAT, 4, false);
        waterScreenMaterial.addSamplerTexture("texture", renderSurface[0].getTexture());
        waterScreenMaterial.setBlendFactor(ProgramUtil.BLEND_SRC_ALPHA, ProgramUtil.BLEND_ONE_MINUS_SRC_ALPHA);

        otherScreenMaterial = new Material(new Program(ProgramUtil.Shader.SCREEN));
        otherScreenMaterial.addAttribute("position", 3, ProgramUtil.FLOAT, 4, false);
        otherScreenMaterial.addAttribute("uv", 2, ProgramUtil.FLOAT, 4, false);
        otherScreenMaterial.addSamplerTexture("texture", renderSurface[1].getTexture());
        otherScreenMaterial.setBlendFactor(ProgramUtil.BLEND_SRC_ALPHA, ProgramUtil.BLEND_ONE_MINUS_SRC_ALPHA);

        // Create a material for blur rendering.
        blurRender.createMaterial();
    }

    private void drawWaterNodes() {
        renderSurface[0].beginRender(GLES20.GL_COLOR_BUFFER_BIT);
        waterNodeMaterial.startRender();
        waterNodeMaterial.setVertexBuffer("position", positionBuffer, 0, 0);
        waterNodeMaterial.setVertexBuffer("color", colorBuffer, 0, 0);

        waterNodeMaterial.updateUniform("pointSize", 10.f);
        waterNodeMaterial.updateUniform("mvp", worldTransform);

        Render.getInstance().getManager().acquire();
        ParticleSystem system = Render.getInstance().getManager().getParticleSystem();
        try {
            for (ParticleGroup group : system.getParticleGroupList()) {
                // Draw a water particle group only.
                if (group.getGroupFlags() == 1) { // 1 GROUP_DYNAMIC
                    drawNodeGroup(group);
                } else {
                    noneWaterGroup.add(group);
                }
            }
        } finally {
            Render.getInstance().getManager().release();
        }

        waterNodeMaterial.endRender();

        renderSurface[0].endRender();

        blurRender.draw(renderSurface[0].getTexture(), renderSurface[0]);
    }

    /**
     * draw Water Particles
     */
    public void drawWaterNodesScreen() {
        Config.QUAD_VERTEX_BUFFER.rewind();

        waterScreenMaterial.startRender();
        waterScreenMaterial.setVertexBuffer("position", Config.QUAD_VERTEX_BUFFER, 0, Config.QUAD_VERTEX_STRIDE);
        waterScreenMaterial.setVertexBuffer("uv", Config.QUAD_VERTEX_BUFFER, 3, Config.QUAD_VERTEX_STRIDE);

        waterScreenMaterial.updateUniform("mvp", textureTransform);
        waterScreenMaterial.updateUniform("alphaThreshold", Config.WATER_ALPHA);
        waterScreenMaterial.draw(Material.DrawType.TRIANGLE_FAN, 0, 4);
        waterScreenMaterial.endRender();
    }

    private void drawNodeGroup(ParticleGroup group) {
        int nodeCount = group.getParticleCount();
        int instanceOffset = group.getParticleBufferIndex();
        waterNodeMaterial.draw(Material.DrawType.POINT, instanceOffset, nodeCount);
    }
}
