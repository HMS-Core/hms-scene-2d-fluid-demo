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

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: Program
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Program {
    private static class ShaderPara {
        String name; // Attribute name.
        int type; // Attribute type.
        int location; // Attribute sequence.

        private ShaderPara(String nameValue, int typeValue, int locationValue) {
            location = locationValue;
        }

    }

    private int program = 0;
    private Map<String, ShaderPara> attributes;
    private Map<String, ShaderPara> uniforms;

    public Program(ProgramUtil.Shader shader) {
        program = ProgramUtil.getProgram(shader);
        if (program > 0) {
            initAttributes();
            initUniforms();
        }
    }

    /**
     * if compiled
     *
     * @return true or false
     */
    public boolean isCompiled() {
        return program > 0;
    }

    /**
     * get attribute name
     *
     * @param name attribute name
     * @return attribute location
     */
    public int getAttributeLocation(String name) {
        ShaderPara para = attributes.get(name);
        if (para != null) {
            return para.location;
        }
        return -1;
    }

    /**
     * get uniform location
     *
     * @param name uniform name
     * @return uniform location
     */
    public int getUniformLocation(String name) {
        ShaderPara para = uniforms.get(name);
        if (para != null) {
            return para.location;
        }
        return -1;
    }

    /**
     * use program
     */
    public void useProgram() {
        int[] para = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, para, 0);
        if (para[0] != program) {
            GLES20.glUseProgram(program);
        }
    }

    private void initAttributes() {
        int[] para = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_ATTRIBUTES, para, 0);
        int attributeCount = para[0];
        attributes = new HashMap<>(attributeCount);

        IntBuffer sizeBuffer = IntBuffer.allocate(1);
        IntBuffer typeBuffer = IntBuffer.allocate(1);
        for (int i = 0; i < attributeCount; i++) {
            String name = GLES20.glGetActiveAttrib(program, i, sizeBuffer, typeBuffer);
            int location = GLES20.glGetAttribLocation(program, name);
            attributes.put(name, new ShaderPara(name, typeBuffer.get(0), location));
        }
    }

    private void initUniforms() {
        int[] para = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_ACTIVE_UNIFORMS, para, 0);
        int uniformCount = para[0];
        uniforms = new HashMap<>(uniformCount);

        IntBuffer sizeBuffer = IntBuffer.allocate(1);
        IntBuffer typeBuffer = IntBuffer.allocate(1);
        for (int i = 0; i < uniformCount; i++) {
            String name = GLES20.glGetActiveUniform(program, i, sizeBuffer, typeBuffer);
            int location = GLES20.glGetUniformLocation(program, name);
            uniforms.put(name, new ShaderPara(name, typeBuffer.get(0), location));
        }
    }

}