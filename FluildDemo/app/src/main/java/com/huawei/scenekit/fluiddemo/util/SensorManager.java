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

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.Surface;

import com.huawei.hms.scene.sdk.fluid.World;
import com.huawei.scenekit.fluiddemo.render.Render;

/**
 * Description: SensorManager
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class SensorManager implements SensorEventListener {
    private int rotation;
    private android.hardware.SensorManager sensorManager;

    public SensorManager(Activity activity) {
        rotation = activity.getWindowManager().getDefaultDisplay().getOrientation();
        if (activity.getSystemService(Activity.SENSOR_SERVICE) instanceof
            android.hardware.SensorManager) {
            sensorManager =
                (android.hardware.SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {
            float gravityX = 0;
            float gravityY = 0;
            float xAxis = sensorEvent.values[0];
            float yAxis = sensorEvent.values[1];
            float gravity = 9.8f;
            switch (rotation) {
                case Surface.ROTATION_0:
                    gravityX = -gravity * xAxis;
                    gravityY = -gravity * yAxis;
                    break;
                case Surface.ROTATION_90:
                    gravityX = gravity * yAxis;
                    gravityY = -gravity * xAxis;
                    break;
                case Surface.ROTATION_180:
                    gravityX = gravity * xAxis;
                    gravityY = gravity * yAxis;
                    break;
                case Surface.ROTATION_270:
                    gravityX = -gravity * yAxis;
                    gravityY = gravity * xAxis;
                    break;
                default:
                    break;
            }

            // Set the gravity.
            World world = Render.getInstance().getManager().acquire();
            try {
                world.setGravity(gravityX, gravityY);
            } finally {
                Render.getInstance().getManager().release();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int index) {
    }

    /**
     * onResume
     */
    public void onResume() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                android.hardware.SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * onPause
     */
    public void onPause() {
        sensorManager.unregisterListener(this);
    }
}
