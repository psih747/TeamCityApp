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

package com.github.vase4kin.teamcityapp.login.tracker;

import com.github.vase4kin.teamcityapp.account.create.tracker.FirebaseCreateAccountTrackerImpl;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Tracker fabric impl of {@link LoginTracker}
 */
public class FirebaseLoginTrackerImpl extends FirebaseCreateAccountTrackerImpl implements LoginTracker {

    public FirebaseLoginTrackerImpl(FirebaseAnalytics firebaseAnalytics) {
        super(firebaseAnalytics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trackView() {
        mFirebaseAnalytics.logEvent(LoginTracker.SCREEN_NAME, null);
    }

}
