package com.jinchao.registration.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinchao.registration.R;

import com.jinchao.registration.informanagement.OrderUndoFragment;
import com.jinchao.registration.informanagement.StatisticsFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;

import java.util.List;

/**
 * Created by user on 2017/3/21.
 */

public class InfoManageIndicatorAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
    private FragmentManager fragmentManager;
    LayoutInflater inflate;
    String[] tabs=new String[]{"订单撤销","统计"};
    public InfoManageIndicatorAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }
    public void initAdpater(Context context){
        inflate = LayoutInflater.from(context.getApplicationContext());
    }


    @Override
    public int getCount() {
        return tabs.length;
    }


    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = inflate.inflate(R.layout.tab_top, container, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(tabs[position]);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        if (position==0){
            return OrderUndoFragment.newInstance();
        }else{
            return StatisticsFragment.newInstance();
        }
    }



    @Override
    public int getItemPosition(Object object) {
        //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
        // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
        return PagerAdapter.POSITION_NONE;
    }
}
