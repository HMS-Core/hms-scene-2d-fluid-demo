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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huawei.hms.scene.common.base.error.exception.ModuleException;
import com.huawei.hms.scene.common.base.error.exception.StateException;
import com.huawei.hms.scene.sdk.fluid.SceneKitFluid;

/**
 * Description: MainActivity
 *
 * @author HUAWEI
 * @since 2021-06-29
 */
public class MainActivity extends Activity {
    private static final int REQ_CODE_UPDATE = 10001;
    private boolean initialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Start Button click event
     *
     * @param view the view
     */
    public void onBtnStartDemoClicked(View view) {
        if (!initialized) {
            initializeSceneKitFluid();
        }
        startActivity(new Intent(this, DemoActivity.class));
    }

    private void initializeSceneKitFluid() {
        SceneKitFluid.getInstance().initialize(this, new SceneKitFluid.OnInitEventListener() {
            @Override
            public void onUpdateNeeded(Intent intent) {
                startActivityForResult(intent, REQ_CODE_UPDATE);
            }

            @Override
            public void onInitialized() {
                Toast.makeText(MainActivity.this, "SceneKit fluid initialized",
                    Toast.LENGTH_SHORT).show();
                initialized = true;
            }

            @Override
            public void onException(Exception exception) {
                Toast.makeText(MainActivity.this, "failed to initilize SceneKit fluid: " +
                    exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_CODE_UPDATE) {
            return;
        }
        if (resultCode == -1) {
            try {
                SceneKitFluid.getInstance().initializeSync(this);
                Toast.makeText(MainActivity.this, "SceneKit fluid initialized", Toast.LENGTH_SHORT).show();
                initialized = true;
            } catch (StateException | ModuleException e) {
                Toast.makeText(MainActivity.this, "failed to initilize SceneKit fluid: " +
                    e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "failed to update SceneKit apk", Toast.LENGTH_SHORT).show();
        }
    }
}
