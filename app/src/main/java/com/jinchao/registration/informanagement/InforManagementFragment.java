package com.jinchao.registration.informanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;
import com.jinchao.registration.adapter.InfoManageIndicatorAdapter;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by user on 2017/3/19.
 */
@ContentView(R.layout.fragment_infomanagement)
public class InforManagementFragment extends BaseFragment {
    private IndicatorViewPager indicatorViewPager;
    @ViewInject(R.id.moretab_indicator)ScrollIndicatorView scrollIndicatorView;
    @ViewInject(R.id.moretab_viewPager)ViewPager viewPager;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        float unSelectSize = 15;
        float selectSize = unSelectSize * 1.3f;
        scrollIndicatorView.setOnTransitionListener(new OnTransitionTextListener().setColor(0xFF2196F3, Color.GRAY).setSize(selectSize, unSelectSize));
        scrollIndicatorView.setScrollBar(new ColorBar(getActivity(), 0xFF2196F3, 4));
        viewPager.setOffscreenPageLimit(2);
        indicatorViewPager = new IndicatorViewPager(scrollIndicatorView, viewPager);
        InfoManageIndicatorAdapter adapter= new InfoManageIndicatorAdapter(getChildFragmentManager());
        adapter.initAdpater(getActivity());
        indicatorViewPager.setAdapter(adapter);
    }
}
