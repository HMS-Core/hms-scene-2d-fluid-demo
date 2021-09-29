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

import android.util.Log;

import com.huawei.hms.scene.engine.iphysics.utils.ParticleSystemInfo;
import com.huawei.hms.scene.sdk.fluid.ParticleSystem;
import com.huawei.hms.scene.sdk.fluid.SceneKitFluid;
import com.huawei.hms.scene.sdk.fluid.World;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Description: WorldManager
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class WorldManager {
    private Lock lock = new ReentrantLock();
    private World world = null;
    private ParticleSystem particleSystem = null;

    public WorldManager() {
    }


    /**
     * Obtain the thread lock for operating the world.
     *
     * @return world
     */
    public World acquire() {
        lock.lock();
        return world;
    }

    /**
     * Release the lock.
     */
    public void release() {
        lock.unlock();
    }

    /**
     * Initialize the world.
     */
    public void init() {
        // Create a world.
        world = SceneKitFluid.getInstance().createWorld(0, 0);
        if (world == null) {
            Log.e("WorldManager", "init: world is null");
            return;
        }
        // Create a particle system.
        ParticleSystemInfo info = new ParticleSystemInfo();
        info.setRadius(Config.NODE_RADIUS);
        info.setViscosity(0.f);
        particleSystem = world.createParticleSystem(info);
    }

    // Obtain the particle system in the physical world.
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    /**
     * Delete the world.
     */
    public void deleteWorld() {
        if (world != null) {
            SceneKitFluid.getInstance().destroyWorld(world);
            world = null;
            particleSystem = null;
        }
    }
}
