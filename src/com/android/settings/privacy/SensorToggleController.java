/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.privacy;

import android.content.Context;

import androidx.preference.PreferenceScreen;

import com.android.settings.core.TogglePreferenceController;
import com.android.settings.utils.SensorPrivacyManagerHelper;

import java.util.concurrent.Executor;

/**
 * Base class for sensor toggle controllers
 */
public abstract class SensorToggleController extends TogglePreferenceController {

    protected final SensorPrivacyManagerHelper mSensorPrivacyManagerHelper;
    private final Executor mCallbackExecutor;

    public SensorToggleController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mSensorPrivacyManagerHelper = SensorPrivacyManagerHelper.getInstance(context);
        mCallbackExecutor = context.getMainExecutor();
    }

    /**
     * The sensor id, defined in SensorPrivacyManagerHelper, which an implementing class controls
     */
    public abstract int getSensor();

    @Override
    public boolean isChecked() {
        return !mSensorPrivacyManagerHelper.isSensorBlocked(getSensor());
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        mSensorPrivacyManagerHelper.setSensorBlocked(getSensor(), !isChecked);
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSensorPrivacyManagerHelper.addSensorBlockedListener(
                getSensor(),
                (sensor, blocked) -> updateState(screen.findPreference(mPreferenceKey)),
                mCallbackExecutor);
    }
}