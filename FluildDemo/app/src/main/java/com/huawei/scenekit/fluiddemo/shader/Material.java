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

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Material
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Material {
    private static final String TAG = "Material";

    /**
     * draw type
     */
    public static class DrawType {
        /**
         * point
         */
        public static final int POINT = GLES20.GL_POINTS;

        /**
         * line
         */
        public static final int LINE = GLES20.GL_LINES;

        /**
         * triangle
         */
        public static final int TRIANGLE = GLES20.GL_TRIANGLES;

        /**
         * triangle strip
         */
        public static final int TRIANGLE_STRIP = GLES20.GL_TRIANGLE_STRIP;

        /**
         * triangle fan
         */
        public static final int TRIANGLE_FAN = GLES20.GL_TRIANGLE_FAN;
    }

    /**
     * attribute
     */
    public static class Attribute {
        String name;
        int elementCount;
        int elementSize;
        int type;
        boolean normalized;
        int location;


        public Attribute(String attriName, int attriCount, int attriSize, int attriType, boolean isNormalized,
            int attriLocation) {
            elementCount = attriCount;
            type = attriType;
            normalized = isNormalized;
            location = attriLocation;
        }
    }


    private static class BlendPara {
        boolean enable = false;
        int srcFactor = ProgramUtil.BLEND_ONE;
        int dstFactor = ProgramUtil.BLEND_ZERO;
    }

    private Map<String, Attribute> attributes = new HashMap<>();
    private Map<String, Texture> textures = new HashMap<>();
    private Program program = null;
    private BlendPara blend = new BlendPara();

    public Material(Program program) {
        if (program != null && program.isCompiled()) {
            this.program = program;
        } else {
            String message = "Failed to create material, program " + program + " is  invalid!";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * add attribute
     *
     * @param name attribute name
     * @param elementCount element count
     * @param valueType value type
     * @param elementSize element size
     * @param isNormalized if normalize
     */
    public void addAttribute(String name, int elementCount, int valueType, int elementSize, boolean isNormalized) {
        if (attributes.containsKey(name)) {
            Log.w(TAG, "attribute " + name + " has been added.");
            return;
        }
        int location = program.getAttributeLocation(name);
        if (location < 0) {
            Log.e(TAG, "attribute" + name + " location is invalid");
            return;
        }

        Attribute attribute = new Attribute(name, elementCount, elementSize, valueType, isNormalized, location);
        attributes.put(name, attribute);
    }

    /**
     * add sampler texture
     *
     * @param name uniform name
     * @param texture the texture
     */
    public void addSamplerTexture(String name, Texture texture) {
        if (textures.containsKey(name)) {
            Log.w(TAG, "texture " + name + "has been add to material.");
            return;
        }
        textures.put(name, texture);
    }

    /**
     * update uniform texture
     *
     * @param name uniform name
     * @param index the index
     * @param textureId texture id
     */
    public void updateUniformTexture(String name, int index, int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(getUniformLocation(name), index);
    }

    /**
     * update uniform
     *
     * @param name uniform name
     * @param value set value
     */
    public void updateUniform(String name, float value) {
        GLES20.glUniform1f(getUniformLocation(name), value);
    }

    /**
     * update uniform
     *
     * @param name uniform name
     * @param value set value
     */
    public void updateUniform(String name, float[] value) {
        switch (value.length) {
            case 3:
                GLES20.glUniform3fv(getUniformLocation(name), 1, value, 0);
                break;
            case 4:
                GLES20.glUniform4fv(getUniformLocation(name), 1, value, 0);
                break;
            case 16:
                GLES20.glUniformMatrix4fv(getUniformLocation(name), 1, false, value, 0);
                break;
            default:
                break;
        }
    }

    /**
     * draw
     *
     * @param type mode type
     * @param offset array offset
     * @param count draw number
     */
    public void draw(int type, int offset, int count) {
        GLES20.glDrawArrays(type, offset, count);
    }

    /**
     * get uniform location
     *
     * @param name the name
     * @return uniform location
     */
    public int getUniformLocation(String name) {
        return program.getUniformLocation(name);
    }

    /**
     * set blend factor
     *
     * @param src blend src alpha
     * @param dst blend dst alpha
     */
    public void setBlendFactor(int src, int dst) {
        if (src == ProgramUtil.BLEND_ONE && dst == ProgramUtil.BLEND_ZERO) {
            blend.enable = false;
        } else {
            blend.enable = true;
            blend.srcFactor = src;
            blend.dstFactor = dst;
        }
    }

    /**
     * set vertex buffer
     *
     * @param attrName the attrName
     * @param buffer vertex buffer
     * @param offset buffer offset
     * @param stride the stride
     */
    public void setVertexBuffer(String attrName, Buffer buffer, int offset, int stride) {
        Attribute attr = attributes.get(attrName);
        buffer.position(offset);
        GLES20.glVertexAttribPointer(attr.location, attr.elementCount, attr.type, attr.normalized, stride, buffer);
    }

    /**
     * start render
     */
    public void startRender() {
        program.useProgram();

        // enable blend
        if (blend.enable) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(blend.srcFactor, blend.dstFactor);
        }

        // enable attributes
        for (Attribute attr : attributes.values()) {
            GLES20.glEnableVertexAttribArray(attr.location);
        }

        // enable uniform texture
        int index = 0;
        for (Map.Entry<String, Texture> texture : textures.entrySet()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getValue().getTextureId());
            GLES20.glUniform1i(getUniformLocation(texture.getKey()), index);
            index++;
        }
    }

    /**
     * end render
     */
    public void endRender() {
        // disable uniform texture
        for (int i = 0; i < textures.size(); i++) {
            int index = GLES20.GL_TEXTURE0 + i;
            GLES20.glActiveTexture(index);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        // disable attributes
        for (Attribute attr : attributes.values()) {
            GLES20.glDisableVertexAttribArray(attr.location);
        }

        if (blend.enable) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
}