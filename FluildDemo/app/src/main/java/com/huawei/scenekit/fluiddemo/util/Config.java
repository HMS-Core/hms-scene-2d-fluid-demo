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

import android.opengl.Matrix;

import com.huawei.hms.scene.engine.iphysics.utils.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Description: Config
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Config {
    /**
     * EGL_CONTEXT_VERSION
     */
    public static final int EGL_CONTEXT_VERSION = 2;

    /**
     * Rendering frame rate.
     */
    public static final float TIME_INTERVAL = 1 / 60f;

    /**
     * VELOCITY_LOOP
     */
    public static final int VELOCITY_LOOP = 6;

    /**
     * POSITION_LOOP
     */
    public static final int POSITION_LOOP = 2;

    /**
     * PARTICLE_LOOP
     */
    public static final int PARTICLE_LOOP = 5;

    /**
     * Particle radius.
     */
    public static final float NODE_RADIUS = 0.05f;

    /**
     * Max node count.
     */
    public static final int MAX_NODE_COUNT = 5000;

    /**
     * MAX_NODE_GROUP_COUNT
     */
    public static final int MAX_NODE_GROUP_COUNT = 256;

    /**
     * DEFAULT_WORLD_HEIGHT
     */
    public static final float DEFAULT_WORLD_HEIGHT = 3.0f;

    /**
     * WORLD_WIDTH
     */
    public static float WORLD_WIDTH = DEFAULT_WORLD_HEIGHT;

    /**
     * WORLD_HEIGHT
     */
    public static float WORLD_HEIGHT = DEFAULT_WORLD_HEIGHT;

    /**
     * FB_SIZE
     */
    public static final int FB_SIZE = 256;

    /**
     * THICKNESS
     */
    public static final float THICKNESS = 1.0f;

    /**
     * CLEAR_COLOR
     */
    public static final int CLEAR_COLOR = android.graphics.Color.argb(0, 255, 255, 255);

    /**
     * color
     */
    public static final Color DEFAULT_COLOR = new Color((short) 30, (short) 144, (short) 255, (short) 220);

    /**
     * WATER_ALPHA
     */
    public static final float WATER_ALPHA = 0.7f;

    /**
     * OTHER_ALPHA
     */
    public static final float OTHER_ALPHA = 0.8f;

    /**
     * BLUR_TEXTURE_NAME
     */
    public static final String BLUR_TEXTURE_NAME = "textures/blur.png";

    /**
     * DEFAULT_TEXTURE_NAME
     */
    public static final String DEFAULT_TEXTURE_NAME = "textures/canvas.jpg";

    /**
     * MATRIX4
     */
    public static float[] MATRIX4 = new float[16];

    /**
     * Coordinates of the quadrilateral on the screen.
     */
    public static final float[] QUAD_VERTEX = {
        -1.0f, -1.0f, 0.0f, // Position coordinate 0.
        0.0f, 0.0f,         // Texture coordinate 0.
        -1.0f, 1.0f, 0.0f,  // Position coordinate 1.
        0.0f, 1.0f,         // Texture coordinate 1.
        1.0f, 1.0f, 0.0f,   // Position coordinate 2.
        1.0f, 1.0f,         // Texture coordinate 2.
        1.0f, -1.0f, 0.0f,  // Position coordinate 3.
        1.0f, 0.0f          // Texture coordinate 3.
    };

    /**
     * Coordinate buffer of the quadrilateral on the screen.
     */
    public static final FloatBuffer QUAD_VERTEX_BUFFER;

    /**
     * QUAD_VERTEX_STRIDE
     */
    public static final int QUAD_VERTEX_STRIDE = QUAD_VERTEX.length / 4 * 4;

    static {
        QUAD_VERTEX_BUFFER = ByteBuffer.allocateDirect(QUAD_VERTEX.length * 4).order(
            ByteOrder.nativeOrder()).asFloatBuffer();
        QUAD_VERTEX_BUFFER.put(QUAD_VERTEX).position(0);
        Matrix.setIdentityM(MATRIX4, 0);
    }

    /**
     * setWorldWidth
     *
     * @param worldWidthValue the screen world width value
     */
    public static void setWorldWidth(float worldWidthValue) {
        WORLD_WIDTH = worldWidthValue;
    }

    /**
     * setWorldHeight
     *
     * @param worldHeightValue the screen world height value
     */
    public static void setWorldHeight(float worldHeightValue) {
        WORLD_HEIGHT = worldHeightValue;
    }
}
