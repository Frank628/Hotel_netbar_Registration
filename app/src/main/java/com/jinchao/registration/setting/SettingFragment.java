package com.jinchao.registration.setting;

import android.content.Intent;
import android.view.View;

import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;
import com.jinchao.registration.nfcregister.NFCActiveActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

/**
 * Created by user on 2017/3/19.
 */
@ContentView(R.layout.fragment_setting)
public class SettingFragment extends BaseFragment {

    @Event(value = R.id.rl_jihuo)
    private void nfcActive(View view){
        Intent intent=new Intent(getActivity(), NFCActiveActivity.class);
        startActivity(intent);
    }
    @Event(value = R.id.rl_edtpwd)
    private void edtpwd(View view){
        Intent intent=new Intent(getActivity(), EditPwdActivity.class);
        startActivity(intent);
    }
    @Event(value = R.id.rl_aboutus)
    private void about(View view){
        Intent intent=new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }
}
