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

package com.github.vase4kin.teamcityapp.artifact.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.vase4kin.teamcityapp.R;
import com.github.vase4kin.teamcityapp.TeamCityApplication;
import com.github.vase4kin.teamcityapp.api.TeamCityService;
import com.github.vase4kin.teamcityapp.artifact.api.File;
import com.github.vase4kin.teamcityapp.artifact.api.Files;
import com.github.vase4kin.teamcityapp.base.extractor.BundleExtractorValues;
import com.github.vase4kin.teamcityapp.build_details.view.BuildDetailsActivity;
import com.github.vase4kin.teamcityapp.buildlist.api.Build;
import com.github.vase4kin.teamcityapp.dagger.components.AppComponent;
import com.github.vase4kin.teamcityapp.dagger.components.RestApiComponent;
import com.github.vase4kin.teamcityapp.dagger.modules.AppModule;
import com.github.vase4kin.teamcityapp.dagger.modules.FakeTeamCityServiceImpl;
import com.github.vase4kin.teamcityapp.dagger.modules.Mocks;
import com.github.vase4kin.teamcityapp.dagger.modules.RestApiModule;
import com.github.vase4kin.teamcityapp.helper.CustomActivityTestRule;
import com.github.vase4kin.teamcityapp.helper.TestUtils;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.cosenonjaviste.daggermock.DaggerMockRule;
import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.github.vase4kin.teamcityapp.helper.RecyclerViewMatcher.withRecyclerView;
import static com.github.vase4kin.teamcityapp.helper.TestUtils.hasItemsCount;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ArtifactListFragment}
 */
@RunWith(AndroidJUnit4.class)
public class ArtifactListFragmentTest {

    private static final String BUILD_TYPE_NAME = "name";

    @Rule
    public DaggerMockRule<AppComponent> mAppComponentDaggerRule = new DaggerMockRule<>(AppComponent.class, new AppModule((TeamCityApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()))
            .set(new DaggerMockRule.ComponentSetter<AppComponent>() {
                @Override
                public void setComponent(AppComponent appComponent) {
                    TeamCityApplication app = (TeamCityApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                    app.setAppInjector(appComponent);
                }
            });

    @Rule
    public DaggerMockRule<RestApiComponent> mRestComponentDaggerRule = new DaggerMockRule<>(RestApiComponent.class, new RestApiModule(Mocks.URL))
            .addComponentDependency(AppComponent.class, new AppModule((TeamCityApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()))
            .set(new DaggerMockRule.ComponentSetter<RestApiComponent>() {
                @Override
                public void setComponent(RestApiComponent restApiComponent) {
                    TeamCityApplication app = (TeamCityApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
                    app.setRestApiInjector(restApiComponent);
                }
            });

    @Rule
    public CustomActivityTestRule<BuildDetailsActivity> mActivityRule = new CustomActivityTestRule<>(BuildDetailsActivity.class);

    @Spy
    private TeamCityService mTeamCityService = new FakeTeamCityServiceImpl();

    @Spy
    private Build mBuild = Mocks.successBuild();

    @BeforeClass
    public static void disableOnboarding() {
        TestUtils.disableOnboarding();
    }

    @Test
    public void testUserCanSeeArtifacts() {
        // Prepare mocks
        when(mTeamCityService.build(anyString())).thenReturn(Observable.just(mBuild));

        // Prepare intent
        // <! ---------------------------------------------------------------------- !>
        // Passing build object to activity, had to create it for real, Can't pass mock object as serializable in bundle :(
        // <! ---------------------------------------------------------------------- !>
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(BundleExtractorValues.BUILD, Mocks.successBuild());
        b.putString(BundleExtractorValues.NAME, BUILD_TYPE_NAME);
        intent.putExtras(b);

        // Start activity
        mActivityRule.launchActivity(intent);

        // Checking artifact tab title
        onView(withText("Artifacts"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        // Checking artifacts
        onView(withId(R.id.artifact_recycler_view)).check(hasItemsCount(3));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(0, R.id.itemTitle)).check(matches(withText("res")));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(1, R.id.itemTitle)).check(matches(withText("AndroidManifest.xml")));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(1, R.id.itemSubTitle)).check(matches(withText("7.59KB")));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(2, R.id.itemTitle)).check(matches(withText("index.html")));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(2, R.id.itemSubTitle)).check(matches(withText("681KB")));
    }

    @Test
    public void testUserCanSeeArtifactsEmptyMessageIfArtifactsAreEmpty() throws Exception {
        // Prepare mocks
        when(mTeamCityService.build(anyString())).thenReturn(Observable.just(mBuild));
        when(mTeamCityService.listArtifacts(anyString(), anyString())).thenReturn(Observable.just(new Files(Collections.<File>emptyList())));

        // Prepare intent
        // <! ---------------------------------------------------------------------- !>
        // Passing build object to activity, had to create it for real, Can't pass mock object as serializable in bundle :(
        // <! ---------------------------------------------------------------------- !>
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(BundleExtractorValues.BUILD, Mocks.successBuild());
        b.putString(BundleExtractorValues.NAME, BUILD_TYPE_NAME);
        intent.putExtras(b);

        // Start activity
        mActivityRule.launchActivity(intent);

        // Checking artifact tab title
        onView(withText("Artifacts"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        //Checking message
        onView(withText(R.string.empty_list_message_artifacts)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserCanSeeArtifactsErrorMessageIfSmthBadHappens() throws Exception {
        // Prepare mocks
        when(mTeamCityService.build(anyString())).thenReturn(Observable.just(mBuild));
        when(mTeamCityService.listArtifacts(anyString(), anyString())).thenReturn(Observable.<Files>error(new RuntimeException("Fake error happened!")));

        // Prepare intent
        // <! ---------------------------------------------------------------------- !>
        // Passing build object to activity, had to create it for real, Can't pass mock object as serializable in bundle :(
        // <! ---------------------------------------------------------------------- !>
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(BundleExtractorValues.BUILD, Mocks.successBuild());
        b.putString(BundleExtractorValues.NAME, BUILD_TYPE_NAME);
        intent.putExtras(b);

        // Start activity
        mActivityRule.launchActivity(intent);

        // Checking artifact tab title
        onView(withText("Artifacts"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        //Checking error
        onView(withText(R.string.error_view_error_text)).check(matches(isDisplayed()));
    }

    @Ignore
    @Test
    public void testUserCanOpenArtifactWithChildren() throws Exception {
        // Prepare mocks
        when(mTeamCityService.build(anyString())).thenReturn(Observable.just(mBuild));
        File folderOne = new File("res", new File.Children("/guestAuth/app/rest/builds/id:92912/artifacts/children/TCity.apk!/res"), "/guestAuth/app/rest/builds/id:92912/artifacts/metadata/TCity.apk!/res");
        File folderTwo = new File("res_level_deeper1", new File.Children("/guestAuth/app/rest/builds/id:92912/artifacts/children/TCity.apk!/res"), "/guestAuth/app/rest/builds/id:92912/artifacts/metadata/TCity.apk!/res");
        File folderThree = new File("res_level_deeper2", new File.Children("/guestAuth/app/rest/builds/id:92912/artifacts/children/TCity.apk!/res"), "/guestAuth/app/rest/builds/id:92912/artifacts/metadata/TCity.apk!/res");
        List<File> deeperArtifacts = new ArrayList<>();
        deeperArtifacts.add(folderTwo);
        deeperArtifacts.add(folderThree);
        when(mTeamCityService.listArtifacts(anyString(), anyString()))
                .thenReturn(Observable.just(new Files(Collections.singletonList(folderOne))))
                .thenReturn(Observable.just(new Files(deeperArtifacts)));

        // Prepare intent
        // <! ---------------------------------------------------------------------- !>
        // Passing build object to activity, had to create it for real, Can't pass mock object as serializable in bundle :(
        // <! ---------------------------------------------------------------------- !>
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putSerializable(BundleExtractorValues.BUILD, Mocks.successBuild());
        b.putString(BundleExtractorValues.NAME, BUILD_TYPE_NAME);
        intent.putExtras(b);

        // Start activity
        mActivityRule.launchActivity(intent);

        // Checking artifact tab title
        onView(withText("Artifacts"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        // Checking first level artifacts
        onView(withId(R.id.artifact_recycler_view)).check(hasItemsCount(1));
        onView(withRecyclerView(R.id.artifact_recycler_view).atPositionOnView(0, R.id.itemTitle))
                .check(matches(withText("res")))
                .perform(click());

        // In case of the same recycler view ids
        onView(withText("res_level_deeper1")).check(matches(isDisplayed()));
        onView(withText("res_level_deeper2")).check(matches(isDisplayed()));
    }

    @SuppressWarnings("EmptyMethod")
    @Ignore
    @Test
    public void testUserCanOpenHtmlFileInBrowser() throws Exception {
    }

    @SuppressWarnings("EmptyMethod")
    @Ignore
    @Test
    public void testUserCanDownloadArtifact() throws Exception {
    }
}