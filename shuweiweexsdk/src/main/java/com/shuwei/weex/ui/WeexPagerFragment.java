package com.shuwei.weex.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shuwei.weex.R;

/**
 * 使用weex加载的Fragment
 *
 * @author alguojian
 * @date 2018.06.08
 */
public class WeexPagerFragment extends Fragment {

    public static final String BUNDLE_URL = "bundleUrl";

    private WeexPagerViewModule mWeexPagerViewModule;

    public static WeexPagerFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(BUNDLE_URL, url);
        WeexPagerFragment fragment = new WeexPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.activity_weex_pager, container, false);

        FrameLayout flContent = inflate.findViewById(R.id.flContent);
        final LinearLayout errorView = inflate.findViewById(R.id.errorView);
        RelativeLayout loading = inflate.findViewById(R.id.loading);
        String bundleUrl = getArguments().getString(BUNDLE_URL);
        errorView.setVerticalGravity(View.GONE);
        mWeexPagerViewModule = new WeexPagerViewModule(getActivity());
        mWeexPagerViewModule.setView(flContent, errorView, loading).setUrl(bundleUrl);

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorView.setVerticalGravity(View.GONE);
                mWeexPagerViewModule.initData();
            }
        });
        mWeexPagerViewModule.initInternalWXData();

        return inflate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWeexPagerViewModule.destroyWXSdkInstance();
    }

}
