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

package com.huawei.scenekit.fluiddemo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.huawei.scenekit.fluiddemo.render.Render;
import com.huawei.scenekit.fluiddemo.util.Config;
import com.huawei.scenekit.fluiddemo.util.SensorManager;

/**
 * Description: DemoActivity
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class DemoActivity extends Activity {
    private GLSurfaceView mainView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Initialize the gravity sensor.
        sensorManager = new SensorManager(this);

        // Initialize the renderer.
        Render.getInstance().init(this);

        // Initialize mainView.
        initMainView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.onResume();
        mainView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.onPause();
        mainView.onPause();
    }

    // Initialize mainView.
    private void initMainView() {
        mainView = findViewById(R.id.world);
        if (mainView == null) {
            Log.e("DemoActivity", "initMainView: mainView is null");
            return;
        }
        mainView.setEGLContextClientVersion(Config.EGL_CONTEXT_VERSION);
        mainView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mainView.setPreserveEGLContextOnPause(true);
        mainView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mainView.setRenderer(Render.getInstance());

        // Start rendering.
        Render.getInstance().start();

        Button add = findViewById(R.id.addwater);
        if (add != null) {
            add.setOnClickListener(view -> Render.getInstance().addWater());
        }
        Button del = findViewById(R.id.delwater);
        if (del != null) {
            del.setOnClickListener(view -> Render.getInstance().deleteWater());
        }
    }
}
