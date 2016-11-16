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

package com.github.vase4kin.teamcityapp.root.tracker;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.fabric.sdk.android.Fabric;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Fabric.class, Answers.class})
public class RootTrackerImplTest {

    @Mock
    private Answers mAnswers;

    private RootTrackerImpl mTracker;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Fabric.class);
        PowerMockito.mockStatic(Answers.class);
        when(Answers.getInstance()).thenReturn(mAnswers);
        mTracker = new RootTrackerImpl();
    }

    @After
    public void tearDown() throws Exception {
        verifyNoMoreInteractions(mAnswers);
    }

    @Test
    public void testTrackViewIfFabricIsNotInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(false);
        mTracker.trackView();
    }

    @Test
    public void testTrackViewIfFabricIsInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(true);
        mTracker.trackView();
        verify(mAnswers).logContentView(any(ContentViewEvent.class));
    }

    @Test
    public void testTrackUserRatedTheAppIfFabricIsNotInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(false);
        mTracker.trackUserRatedTheApp();
    }

    @Test
    public void testTrackTrackUserRatedIfFabricIsInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(true);
        mTracker.trackUserRatedTheApp();
        verify(mAnswers).logCustom(any(CustomEvent.class));
    }

    @Test
    public void testTrackUserDidNotRateTheAppIfFabricIsNotInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(false);
        mTracker.trackUserDidNotRateTheApp();
    }

    @Test
    public void testTrackUserDidNotRateTheAppIfFabricIsInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(true);
        mTracker.trackUserDidNotRateTheApp();
        verify(mAnswers).logCustom(any(CustomEvent.class));
    }

    @Test
    public void testTrackUserDecidedToRateTheAppLaterIfFabricIsNotInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(false);
        mTracker.trackUserDecidedToRateTheAppLater();
    }

    @Test
    public void testTrackUserDecidedToRateTheAppLaterIfFabricIsInitialized() throws Exception {
        when(Fabric.isInitialized()).thenReturn(true);
        mTracker.trackUserDecidedToRateTheAppLater();
        verify(mAnswers).logCustom(any(CustomEvent.class));
    }
}