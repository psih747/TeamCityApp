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

package com.github.vase4kin.teamcityapp.artifact.presenter;

import android.os.Bundle;

import com.github.vase4kin.teamcityapp.account.create.data.OnLoadingListener;
import com.github.vase4kin.teamcityapp.artifact.api.File;
import com.github.vase4kin.teamcityapp.artifact.data.ArtifactDataManager;
import com.github.vase4kin.teamcityapp.artifact.data.ArtifactDataModel;
import com.github.vase4kin.teamcityapp.artifact.extractor.ArtifactValueExtractor;
import com.github.vase4kin.teamcityapp.artifact.permissions.OnPermissionsResultListener;
import com.github.vase4kin.teamcityapp.artifact.permissions.PermissionManager;
import com.github.vase4kin.teamcityapp.artifact.router.ArtifactRouter;
import com.github.vase4kin.teamcityapp.artifact.view.ArtifactView;
import com.github.vase4kin.teamcityapp.artifact.view.OnPermissionsDialogListener;
import com.github.vase4kin.teamcityapp.base.tracker.ViewTracker;
import com.github.vase4kin.teamcityapp.buildlist.api.Build;
import com.github.vase4kin.teamcityapp.overview.data.BuildDetails;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ArtifactPresenterImplTest {

    @Captor
    private ArgumentCaptor<OnPermissionsDialogListener> mOnPermissionsDialogListenerArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnLoadingListener<java.io.File>> mArgumentCaptor;

    @Captor
    private ArgumentCaptor<OnPermissionsResultListener> mOnPermissionsResultListenerArgumentCaptor;

    @Mock
    private File.Content mContent;

    @Mock
    private java.io.File mIoFile;

    @Mock
    private File mFile;

    @Mock
    private OnLoadingListener<List<File>> mLoadingListener;

    @Mock
    private Bundle mBundle;

    @Mock
    private Build mBuild;

    @Mock
    private BuildDetails mBuildDetails;

    @Mock
    private ArtifactView mView;

    @Mock
    private ArtifactDataManager mDataManager;

    @Mock
    private ArtifactRouter mRouter;

    @Mock
    private ArtifactValueExtractor mValueExtractor;

    @Mock
    private PermissionManager mPermissionManager;

    @Mock
    private ViewTracker mTracker;

    private ArtifactPresenterImpl mPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new ArtifactPresenterImpl(mView, mDataManager, mTracker, mValueExtractor, mRouter, mPermissionManager);
    }

    @Test
    public void testCreateModel() throws Exception {
        List<File> files = new ArrayList<>();
        files.add(mFile);
        ArtifactDataModel dataModel = mPresenter.createModel(files);
        assertThat(dataModel.getFile(0), is(mFile));
        assertThat(dataModel.getItemCount(), is(1));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testLoadData() throws Exception {
        when(mValueExtractor.getUrl()).thenReturn("url");
        mPresenter.loadData(mLoadingListener, false);
        verify(mValueExtractor).getUrl();
        verify(mDataManager).load(eq("url"), eq(mLoadingListener), eq(false));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testInitViews() throws Exception {
        mPresenter.initViews();
        verify(mView).setOnArtifactPresenterListener(eq(mPresenter));
    }

    @Test
    public void testOnClickIfHasChildren() throws Exception {
        when(mFile.hasChildren()).thenReturn(true);
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        mPresenter.onClick(mFile);
        verify(mView).showFullBottomSheet(eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnClickIfHasChildrenAndIsFolder() throws Exception {
        when(mFile.hasChildren()).thenReturn(true);
        when(mFile.isFolder()).thenReturn(true);
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        mPresenter.onClick(mFile);
        verify(mValueExtractor).getBuildDetails();
        verify(mRouter).openArtifactFile(eq(mBuildDetails), eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnClickIfBrowserUrl() throws Exception {
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.getHref()).thenReturn(".html");
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        mPresenter.onClick(mFile);
        verify(mView).showBrowserBottomSheet(eq(mFile));

        when(mFile.getHref()).thenReturn(".htm");
        mPresenter.onClick(mFile);
        verify(mView, times(2)).showBrowserBottomSheet(eq(mFile));

        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnClickIfHasNoChildrenUrl() throws Exception {
        when(mContent.getHref()).thenReturn("url");
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.getHref()).thenReturn("url");
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.getContent()).thenReturn(mContent);
        when(mFile.getName()).thenReturn("name");
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        when(mPermissionManager.isWriteStoragePermissionsGranted()).thenReturn(true);

        mPresenter.onClick(mFile);
        verify(mView).showDefaultBottomSheet(eq(mFile));

        mPresenter.mArtifactFile = mFile;
        mPresenter.downloadArtifactFile();
        verify(mView).showProgressDialog();
        verify(mPermissionManager).isWriteStoragePermissionsGranted();
        verify(mDataManager).downloadArtifact(eq("url"), eq("name"), mArgumentCaptor.capture());

        OnLoadingListener<java.io.File> onLoadingListener = mArgumentCaptor.getValue();
        onLoadingListener.onSuccess(mIoFile);
        verify(mView).dismissProgressDialog();
        verify(mRouter).startFileActivity(eq(mIoFile));

        onLoadingListener.onFail("error");
        verify(mView, times(2)).dismissProgressDialog();
        verify(mView).showRetryDownloadArtifactSnackBar(eq(mPresenter));

        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnLongClickIfFileHasChildrenAndNotAFolder() throws Exception {
        when(mFile.hasChildren()).thenReturn(true);
        when(mFile.isFolder()).thenReturn(false);
        mPresenter.onLongClick(mFile);
        verify(mView).showFullBottomSheet(eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mTracker);
    }

    @Test
    public void testOnLongClickIfFileIsAFolder() throws Exception {
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.isFolder()).thenReturn(true);
        mPresenter.onLongClick(mFile);
        verify(mView).showFolderBottomSheet(eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnLongClickIfFileHasBrowserUrl() throws Exception {
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.isFolder()).thenReturn(false);
        when(mFile.getHref()).thenReturn(".htm");
        mPresenter.onLongClick(mFile);
        verify(mView).showBrowserBottomSheet(eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnLongClick() throws Exception {
        when(mFile.hasChildren()).thenReturn(false);
        when(mFile.isFolder()).thenReturn(false);
        when(mFile.getHref()).thenReturn("url");
        mPresenter.onLongClick(mFile);
        verify(mView).showDefaultBottomSheet(eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnViewsDestroyed() throws Exception {
        mPresenter.onViewsDestroyed();
        verify(mRouter).unbindCustomsTabs();
    }

    @Test
    public void testOpenArtifactFile() throws Exception {
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        mPresenter.openArtifactFile(mFile);
        verify(mValueExtractor).getBuildDetails();
        verify(mRouter).openArtifactFile(eq(mBuildDetails), eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testStartBrowser() throws Exception {
        when(mValueExtractor.getBuildDetails()).thenReturn(mBuildDetails);
        mPresenter.startBrowser(mFile);
        verify(mValueExtractor).getBuildDetails();
        verify(mRouter).startBrowser(eq(mBuildDetails), eq(mFile));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testUnSubscribe() throws Exception {
        mPresenter.unSubscribe();
        verify(mDataManager).unsubscribe();
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testHandleOnStartIfRegistered() throws Exception {
        mPresenter.onStart();
        verify(mDataManager).registerEventBus();
        verify(mDataManager).setListener(eq(mPresenter));
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testHandleOnStop() throws Exception {
        mPresenter.OnStop();
        verify(mDataManager).unregisterEventBus();
        verify(mDataManager).setListener(null);
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testOnEventHappen() {
        mPresenter.onEventHappen();
        verify(mView).onArtifactTabChangeEvent();
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testHandleOnRequestPermissionsResult() throws Exception {
        when(mPermissionManager.isWriteStoragePermissionsGranted()).thenReturn(false);
        when(mPermissionManager.isNeedToShowInfoPermissionsDialog()).thenReturn(false);
        int requestCode = 12;
        String[] permissions = new String[]{"123"};
        int[] grantResults = new int[]{12, 123};
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
        verify(mPermissionManager).onRequestPermissionsResult(eq(requestCode), eq(permissions), eq(grantResults), mOnPermissionsResultListenerArgumentCaptor.capture());
        OnPermissionsResultListener listener = mOnPermissionsResultListenerArgumentCaptor.getValue();
        listener.onDenied();
        verify(mView).showPermissionsDeniedDialog();
        listener.onGranted();
        verify(mPermissionManager).isWriteStoragePermissionsGranted();
        verify(mPermissionManager).isNeedToShowInfoPermissionsDialog();
        verify(mPermissionManager).requestWriteStoragePermissions();
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testDownloadArtifactFileIfUserDoesNotHaveRequiredPermissionsAndNeedToShowTheDialogInfo() {
        when(mPermissionManager.isWriteStoragePermissionsGranted()).thenReturn(false);
        when(mPermissionManager.isNeedToShowInfoPermissionsDialog()).thenReturn(true);
        mPresenter.downloadArtifactFile(mFile);
        verify(mPermissionManager).isWriteStoragePermissionsGranted();
        verify(mPermissionManager).isNeedToShowInfoPermissionsDialog();
        verify(mView).showPermissionsInfoDialog(mOnPermissionsDialogListenerArgumentCaptor.capture());
        OnPermissionsDialogListener listener = mOnPermissionsDialogListenerArgumentCaptor.getValue();
        listener.onAllow();
        verify(mPermissionManager).requestWriteStoragePermissions();
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

    @Test
    public void testDownloadArtifactFileIfUserDoesNotHaveRequiredPermissionsAndNoNeedToShowTheDialogInfo() {
        when(mPermissionManager.isWriteStoragePermissionsGranted()).thenReturn(false);
        when(mPermissionManager.isNeedToShowInfoPermissionsDialog()).thenReturn(false);
        mPresenter.downloadArtifactFile(mFile);
        verify(mPermissionManager).isWriteStoragePermissionsGranted();
        verify(mPermissionManager).isNeedToShowInfoPermissionsDialog();
        verify(mPermissionManager).requestWriteStoragePermissions();
        verifyNoMoreInteractions(mView, mDataManager, mRouter, mValueExtractor, mPermissionManager, mTracker);
    }

}