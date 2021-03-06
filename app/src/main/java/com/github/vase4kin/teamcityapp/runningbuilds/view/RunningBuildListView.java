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

package com.github.vase4kin.teamcityapp.runningbuilds.view;

import com.github.vase4kin.teamcityapp.buildlist.view.BuildListView;

/**
 * Custom view interactions for {@link com.github.vase4kin.teamcityapp.buildlist.view.BuildListActivity}
 */
public interface RunningBuildListView extends BuildListView {

    /**
     * Update tool bar title with value
     *
     * @param count - value to update
     */
    void updateTitle(int count);
}
