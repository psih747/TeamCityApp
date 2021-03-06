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

package com.github.vase4kin.teamcityapp.base.list.adapter;

import com.github.vase4kin.teamcityapp.base.list.view.BaseDataModel;

/**
 * Load more for data model
 *
 * @param <T> - Base data model
 */
public interface ModelLoadMore<T extends BaseDataModel> extends ViewLoadMore<T> {

    /**
     * Is load more
     *
     * @param position - data model position
     */
    boolean isLoadMore(int position);
}
