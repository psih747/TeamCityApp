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

package com.github.vase4kin.teamcityapp.changes.view;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.vase4kin.teamcityapp.R;
import com.github.vase4kin.teamcityapp.base.list.view.BaseListViewImpl;
import com.github.vase4kin.teamcityapp.changes.api.Changes;
import com.github.vase4kin.teamcityapp.changes.data.ChangesDataModel;
import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

/**
 * Impl of {@link ChangesView}
 */
public class ChangesViewImpl extends BaseListViewImpl<ChangesDataModel, ChangesAdapter> implements ChangesView {

    private MugenCallbacks mLoadMoreCallbacks;

    public ChangesViewImpl(View view,
                           Activity activity,
                           @StringRes int emptyMessage,
                           ChangesAdapter adapter) {
        super(view, activity, emptyMessage, adapter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoadMoreListener(MugenCallbacks loadMoreCallbacks) {
        this.mLoadMoreCallbacks = loadMoreCallbacks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showData(ChangesDataModel dataModel) {
        Mugen.with(mRecyclerView, mLoadMoreCallbacks).start();
        mAdapter.setOnChangeClickListener(this);
        mAdapter.setDataModel(dataModel);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLoadMore() {
        mAdapter.addLoadMore();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLoadMore() {
        mAdapter.removeLoadMore();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMoreBuilds(ChangesDataModel dataModel) {
        mAdapter.addMoreBuilds(dataModel);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showRetryLoadMoreSnackBar() {
        Snackbar snackBar = Snackbar.make(
                mRecyclerView,
                R.string.load_more_retry_snack_bar_text,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.download_artifact_retry_snack_bar_retry_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLoadMoreCallbacks.onLoadMore();
                    }
                });
        TextView textView = (TextView) snackBar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(Changes.Change change) {
        String content = change.getUsername() + " on " + change.getDate();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                .title(change.getComment())
                .content(content)
                .positiveText(R.string.dialog_ok_title);
        if (change.getFiles().getFiles().isEmpty()) {
            builder.items(new String[]{mActivity.getString(R.string.empty_list_files)});
        } else {
            builder.items(change.getFiles().getFiles());
        }
        MaterialDialog dialog = builder.build();
        dialog.getTitleView().setEllipsize(TextUtils.TruncateAt.END);
        dialog.getTitleView().setMaxLines(2);

        dialog.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int recyclerViewId() {
        return R.id.changes_recycler_view;
    }
}
