/*
 * Copyright 2016 Andrey Tolpeev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.vase4kin.teamcityapp.runbuild.tracker;

import com.github.vase4kin.teamcityapp.navigation.tracker.ViewTracker;

/**
 * Run build tracking
 */
public interface RunBuildTracker extends ViewTracker {

    /**
     * Run build content name
     */
    String CONTENT_NAME_RUN_BUILD = "Run build";

    /**
     * Title for run build success event
     */
    String EVENT_RUN_BUILD_SUCCESS = "Run build success";

    /**
     * Title for run build failed event
     */
    String EVENT_RUN_BUILD_FAILED = "Run build failed";

    /**
     * Title for run build failed forbidden event
     */
    String EVENT_RUN_BUILD_FAILED_FORBIDDEN = "Run build failed forbidden";

    /**
     * Track user ran build success
     */
    void trackUserRunBuildSuccess();

    /**
     * Track user ran build failed
     */
    void trackUserRunBuildFailed();

    /**
     * Track user ran build failed forbidden
     */
    void trackUserRunBuildFailedForbidden();
}