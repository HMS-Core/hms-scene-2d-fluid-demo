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

import android.content.res.AssetManager;

import android.opengl.GLES20;
import android.util.Log;

import com.huawei.scenekit.fluiddemo.util.FileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: ProgramUtil
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class ProgramUtil {
    /**
     * BLEND_ZERO
     */
    public static final int BLEND_ZERO = GLES20.GL_ZERO;

    /**
     * BLEND_ONE
     */
    public static final int BLEND_ONE = GLES20.GL_ONE;

    /**
     * BLEND_SRC_ALPHA
     */
    public static final int BLEND_SRC_ALPHA = GLES20.GL_SRC_ALPHA;

    /**
     * BLEND_ONE_MINUS_SRC_ALPHA
     */
    public static final int BLEND_ONE_MINUS_SRC_ALPHA = GLES20.GL_ONE_MINUS_SRC_ALPHA;

    /**
     * BLEND_DST_ALPHA
     */
    public static final int BLEND_DST_ALPHA = GLES20.GL_DST_ALPHA;

    /**
     * BLEND_ONE_MINUS_DST_ALPHA
     */
    public static final int BLEND_ONE_MINUS_DST_ALPHA = GLES20.GL_ONE_MINUS_DST_ALPHA;

    /**
     * BYTE
     */
    public static final int BYTE = GLES20.GL_BYTE;

    /**
     * UNSIGNED BYTE
     */
    public static final int UNSIGNED_BYTE = GLES20.GL_UNSIGNED_BYTE;

    /**
     * SHORT
     */
    public static final int SHORT = GLES20.GL_SHORT;

    /**
     * UNSIGNED SHORT
     */
    public static final int UNSIGNED_SHORT = GLES20.GL_UNSIGNED_SHORT;

    /**
     * FLOAT
     */
    public static final int FLOAT = GLES20.GL_FLOAT;

    /**
     * shader map
     */
    public static final Map<Shader, ProgramData> SHADERS =
        new HashMap<Shader, ProgramData>(20); // 20: initialCapacity, current use IN initShaders() is 6

    private static final String TAG = "ProgramManager";
    private static final String SHADER_PATH = "shaders/glsl";

    private static class ProgramData {
        String vertexShaderName;
        String fragmentShaderName;
        int glVertexShader;
        int glFragmentShader;
        int glProgram;

        ProgramData(String vsName, String fsName) {
            vertexShaderName = vsName;
            fragmentShaderName = fsName;
            glVertexShader = -1;
            glFragmentShader = -1;
            glProgram = -1;
        }
    }

    /**
     * Shader enum type
     */
    public enum Shader {
        NODE,
        WATER_NODE,
        TEXTURE,
        SCREEN,
        HBLUR,
        VBLUR,
        DEBUG
    }

    private static void initShaders() {
        SHADERS.put(Shader.NODE, new ProgramData("Particle.vert", "Particle.frag"));
        SHADERS.put(Shader.WATER_NODE, new ProgramData("WaterParticle.vert", "Particle.frag"));
        SHADERS.put(Shader.TEXTURE, new ProgramData("Texture.vert", "Texture.frag"));
        SHADERS.put(Shader.SCREEN, new ProgramData("Screen.vert", "Screen.frag"));
        SHADERS.put(Shader.HBLUR, new ProgramData("HBlur.vert", "Blur.frag"));
        SHADERS.put(Shader.VBLUR, new ProgramData("VBlur.vert", "Blur.frag"));
    }

    private static int createShader(int shaderType, String shaderName, String shaderSource) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);

        int[] params = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
        if (params[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderName + ":" + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    private static void loadShader(Shader shaderType, AssetManager assetManager) {
        ProgramData data = SHADERS.get(shaderType);
        if (data == null) {
            Log.e(TAG, "Invalid shader type()" + shaderType + "in loadShader");
            return;
        }
        String vertexShader = FileManager.loadShader(assetManager, SHADER_PATH + "/" + data.vertexShaderName);
        String fragmentShader = FileManager.loadShader(assetManager, SHADER_PATH + "/" + data.fragmentShaderName);
        data.glVertexShader = createShader(GLES20.GL_VERTEX_SHADER, data.vertexShaderName, vertexShader);
        data.glFragmentShader = createShader(GLES20.GL_FRAGMENT_SHADER, data.fragmentShaderName, fragmentShader);

        data.glProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(data.glProgram, data.glVertexShader);
        GLES20.glAttachShader(data.glProgram, data.glFragmentShader);
        GLES20.glLinkProgram(data.glProgram);

        int[] status = new int[1];
        GLES20.glGetProgramiv(data.glProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e(TAG, "Could not link shaders " + data.vertexShaderName + " and " + data.fragmentShaderName);
            Log.e(TAG, "GL log: " + GLES20.glGetShaderInfoLog(data.glProgram));
            data.glProgram = 0;
        }
    }

    /**
     * load all shaders
     *
     * @param assetManager the asset manager
     */
    public static void loadAllShaders(AssetManager assetManager) {
        initShaders();
        for (Map.Entry<Shader, ProgramData> entry : SHADERS.entrySet()) {
            loadShader(entry.getKey(), assetManager);
        }
    }

    /**
     * get program
     *
     * @param shader the shader
     * @return glProgram
     */
    public static int getProgram(Shader shader) {
        return SHADERS.get(shader).glProgram;
    }
}