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

package com.github.vase4kin.teamcityapp.root.view;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.github.vase4kin.teamcityapp.R;
import com.github.vase4kin.teamcityapp.drawer.view.DrawerViewImpl;
import com.github.vase4kin.teamcityapp.onboarding.OnboardingManager;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * impl of {@link RootDrawerView}
 */
public class RootDrawerViewImpl extends DrawerViewImpl implements RootDrawerView {

    private static final int TIME_NAVIGATION_DRAWER_PROMPT = 500;

    public RootDrawerViewImpl(AppCompatActivity activity, int drawerSelection, boolean isBackArrowEnabled) {
        super(activity, drawerSelection, isBackArrowEnabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDrawerSelection(int selection) {
        mDrawerResult.setSelection(selection, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showAppRateDialog(final OnAppRateListener listener) {
        AppRate.with(mActivity)
                .setInstallDays(R.integer.install_days)
                .setLaunchTimes(R.integer.launch_times)
                .setRemindInterval(R.integer.remind_interval)
                .setShowLaterButton(true)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                listener.onNegativeButtonClick();
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                listener.onNeutralButtonClick();
                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                listener.onPositiveButtonClick();
                                break;
                            default:
                                // Do nothing
                                break;
                        }
                    }
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(mActivity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showNavigationDrawerPrompt(final OnboardingManager.OnPromptShownListener listener) {
        // Creating prompt
        int color = ContextCompat.getColor(mActivity, mDefaultColor);
        final MaterialTapTargetPrompt.Builder navigationDrawerPrompt = new MaterialTapTargetPrompt.Builder(mActivity)
                .setPrimaryText(R.string.title_onboarding_navigation_drawer)
                .setSecondaryText(R.string.text_onboarding_navigation_drawer)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setIcon(R.drawable.ic_menu_black_24dp)
                .setIconDrawableTintList(ColorStateList.valueOf(color))
                .setBackgroundColour(color)
                .setCaptureTouchEventOutsidePrompt(true)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                        listener.onPromptShown();
                    }

                    @Override
                    public void onHidePromptComplete() {
                    }
                });
        // Show prompt
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                navigationDrawerPrompt.setTarget(mToolbar.getChildAt(1));
                navigationDrawerPrompt.show();
            }
        }, TIME_NAVIGATION_DRAWER_PROMPT);
    }
}
