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

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.huawei.hms.scene.engine.iphysics.utils.CircleShape;
import com.huawei.hms.scene.engine.iphysics.utils.Color;
import com.huawei.hms.scene.engine.iphysics.utils.ParticleGroupInfo;
import com.huawei.hms.scene.engine.iphysics.utils.PolygonShape;
import com.huawei.hms.scene.engine.iphysics.utils.Vector2;
import com.huawei.hms.scene.sdk.fluid.Body;
import com.huawei.hms.scene.sdk.fluid.ParticleSystem;
import com.huawei.hms.scene.sdk.fluid.World;
import com.huawei.scenekit.fluiddemo.shader.ProgramUtil;
import com.huawei.scenekit.fluiddemo.util.Config;
import com.huawei.scenekit.fluiddemo.util.WorldManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description: Render
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class Render implements GLSurfaceView.Renderer {
    private static Render instance = new Render();

    private static final int DIAL_KEY_NUM = 10;
    private static final int WATER = 1 << 0;
    private static final int VISCOUS = 1 << 1;
    private static final int STRESSFUL = 1 << 2;
    private static final int MIX_COLOR = 1 << 3;
    private static final int REPULSIVE = 1 << 4;
    private static final int TENSILE = 1 << 5;
    private static final int POWER = 1 << 6;
    private static final int WALL = 1 << 7;
    private static final int BARRIER = 1 << 8;
    private static final int ZOMBIE = 1 << 9;

    private WorldManager worldManager;
    private Activity activity = null;
    private Body border = null;
    private Body[] circleDialKeyBody = {null};
    private NodeRender nodeRender;
    private CanvasRender canvasRender;
    private boolean update = false;

    private Render() {
        worldManager = new WorldManager();
        nodeRender = new NodeRender();
        canvasRender = new CanvasRender();
    }

    public static Render getInstance() {
        return instance;
    }

    /**
     * init activity
     *
     * @param activity the activity
     */
    public void init(Activity activity) {
        this.activity = activity;
        // Rebuild the world.
        resetWorld();
        // Rebuild borders.
        resetBorder();
        // Rebuid particles.
        resetNodes();
    }

    @Override
    protected void finalize() {
        deleteAll();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Load shaders.
        ProgramUtil.loadAllShaders(activity.getAssets());

        canvasRender.onSurfaceCreated(activity);

        nodeRender.onSurfaceCreated(activity);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the viewport.
        GLES20.glViewport(0, 0, width, height);
        // Adjust the view size.
        changeViewSize(width, height);
        // Rebuild the bounding box.
        resetBorder();
        // Adjust NodeRender.
        nodeRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // Simulate particles.
        simulate();

        // Draw all particles.
        draw();
    }

    /**
     * Stop simulation.
     */
    public void pause() {
        update = false;
    }

    /**
     * Start simulation.
     */
    public void start() {
        update = true;
    }

    /**
     * Obtains the world management class.
     *
     * @return WorldManager
     */
    public WorldManager getManager() {
        return worldManager;
    }

    /**
     * Increase the water volume.
     */
    public void addWater() {
        worldManager.acquire();
        try {
            ParticleGroupInfo info = new ParticleGroupInfo(
                ParticleGroupInfo.ParticleFlag.WATER | ParticleGroupInfo.ParticleFlag.MIX_COLOR);

            // Add particles to an area.
            CircleShape shape = new CircleShape();
            shape.setRadius(0.6f);
            shape.setPosition(new Vector2(0, Config.WORLD_HEIGHT));
            info.setShape(shape);

            Color color = new Color(
                30, 144, 255, 220);
            info.setColor(color);

            ParticleSystem system = worldManager.getParticleSystem();
            system.addParticles(info);
        } finally {
            worldManager.release();
        }
    }

    /**
     * Decrease the water volume.
     */
    public void deleteWater() {
        worldManager.acquire();
        try {
            ParticleSystem system = worldManager.getParticleSystem();
            system.deleteParticles(100);
        } finally {
            worldManager.release();
        }
    }

    // Cyclically perform simulation.
    private void simulate() {
        if (!update) {
            return;
        }

        World world = worldManager.acquire();
        try {
            world.singleStep(Config.TIME_INTERVAL);
        } finally {
            worldManager.release();
        }
    }

    // Cyclically perform rendering.
    private void draw() {
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Draw a background.
        canvasRender.draw();

        // Draw particles.
        nodeRender.draw();
    }

    private void deleteAll() {
        World world = worldManager.acquire();
        try {
            if (border != null) {
                world.destroyBody(border);
                border = null;
            }
            for (int i = 0; i < DIAL_KEY_NUM; i++) {
                if (circleDialKeyBody[i] != null) {
                    world.destroyBody(circleDialKeyBody[i]);
                    circleDialKeyBody[i] = null;
                }
            }
            worldManager.deleteWorld();
        } finally {
            worldManager.release();
        }
    }

    // Reset the world.
    private void resetWorld() {
        worldManager.acquire();
        try {
            worldManager.deleteWorld();
            border = null;
            worldManager.init();
        } finally {
            worldManager.release();
        }
    }

    // Reset borders.
    private void resetBorder() {
        World world = worldManager.acquire();
        try {
            // Clear body information.
            if (border != null) {
                world.destroyBody(border);
            }

            border = world.createBody(World.BodyType.STATIC_BODY);
            if (border == null) {
                Log.e("Render", "resetBorder: border is null");
                return;
            }
            PolygonShape borderShape = new PolygonShape(
                0.f, 0.f, new Vector2(0.f, 0.f), 0.f);

            float width = Config.WORLD_WIDTH;
            float height = Config.WORLD_HEIGHT;
            float thick = Config.THICKNESS;

            // Create the top border.
            borderShape.setBox(width, thick, new Vector2(width / 2, height + thick), 0.f);
            border.addPolygonShape(borderShape);
            // Create the bottom border.
            borderShape.setBox(width, thick, new Vector2(width / 2, -thick), 0.f);
            border.addPolygonShape(borderShape);
            // Create the left border.
            borderShape.setBox(thick, height, new Vector2(-thick, height / 2), 0.f);
            border.addPolygonShape(borderShape);
            // Create the right border.
            borderShape.setBox(thick, height, new Vector2(width + thick, height / 2), 0.f);
            border.addPolygonShape(borderShape);
        } finally {
            worldManager.release();
        }
    }

    // Create dial keys.
    private void resetDialKey() {
        World world = worldManager.acquire();
        circleDialKeyBody = new Body[DIAL_KEY_NUM];
        int ori = activity.getResources().getConfiguration().orientation;

        try {
            int dialKeyCount = 0;
            final int keys = 3;
            for (int i = 0; i < keys; i++) {
                for (int j = 0; j < keys; j++) {
                    if (circleDialKeyBody[dialKeyCount] != null) {
                        world.destroyBody(circleDialKeyBody[dialKeyCount]);
                    }

                    circleDialKeyBody[dialKeyCount] = world.createBody(World.BodyType.STATIC_BODY);

                    CircleShape circleShape = new CircleShape();
                    if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                        circleShape.setPosition(new Vector2(Config.WORLD_HEIGHT * 0.75f + 0.8f + 0.8f * i,
                            (Config.DEFAULT_WORLD_HEIGHT / 4) * (1 + j)));
                    } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                        circleShape.setPosition(new Vector2((Config.WORLD_WIDTH / 4) * (1 + i),
                            (Config.DEFAULT_WORLD_HEIGHT / 5) * (2 + j)));
                    } else {
                        circleShape.setPosition(new Vector2(0, 0));
                    }
                    circleShape.setRadius(0.2f);
                    circleDialKeyBody[dialKeyCount].addCircleShape(circleShape);
                    ++dialKeyCount;
                }
            }

            {
                CircleShape circleShape = new CircleShape();
                if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                    circleShape.setPosition(new Vector2(Config.WORLD_HEIGHT * 0.75f + 0.8f + 0.8f * 2 + 0.8f,
                        (Config.DEFAULT_WORLD_HEIGHT / 4) * (1 + 1)));
                } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                    circleShape.setPosition(new Vector2(Config.WORLD_WIDTH / 4 * 2,
                        (Config.DEFAULT_WORLD_HEIGHT / 5)));
                } else {
                    circleShape.setPosition(new Vector2(0, 0));
                }
                circleShape.setRadius(0.2f);
                circleDialKeyBody[dialKeyCount] = world.createBody(World.BodyType.STATIC_BODY);
                circleDialKeyBody[dialKeyCount].addCircleShape(circleShape);
            }
        } finally {
            worldManager.release();
        }
    }

    private void resetNodes() {
        worldManager.acquire();
        try {
                ParticleGroupInfo groupDef = new ParticleGroupInfo(WATER | MIX_COLOR);
                groupDef.setColor(Config.DEFAULT_COLOR);

                // Set the shape of a particle group.
                PolygonShape shape = new PolygonShape(0.f, 0.f, new Vector2(0.f, 0.f), 0);
                shape.setBox(Config.DEFAULT_WORLD_HEIGHT * 0.6f, Config.DEFAULT_WORLD_HEIGHT * 0.6f,
                    new Vector2(Config.DEFAULT_WORLD_HEIGHT/ 2, Config.DEFAULT_WORLD_HEIGHT/ 2), 0);
                groupDef.setShape(shape);

                ParticleSystem system = worldManager.getParticleSystem();
                system.addParticles(groupDef);
        } finally {
            worldManager.release();
        }
    }

    // Adjust the view size.
    private void changeViewSize(int width, int height) {
        if (height > width) {
            // portrait
            Config.setWorldWidth(Config.DEFAULT_WORLD_HEIGHT);
            Config.setWorldHeight(height * Config.DEFAULT_WORLD_HEIGHT / width);
        } else {
            // landspace
            Config.setWorldHeight(Config.DEFAULT_WORLD_HEIGHT);
            Config.setWorldWidth(width * Config.DEFAULT_WORLD_HEIGHT / height);
        }
    }
}
