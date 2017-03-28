package com.jinchao.registration.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jinchao.registration.Base.BaseActivity;
import com.jinchao.registration.R;
import com.jinchao.registration.SysApplication;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.jsonbean.ADEOperationResult;
import com.jinchao.registration.main.LoginActivity;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.NavigationLayout;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by user on 2017/3/24.
 */
@ContentView(R.layout.activity_editpwd)
public class EditPwdActivity extends BaseActivity {
    @ViewInject(R.id.edt_pwd)EditText edt_pwd;
    @ViewInject(R.id.edt_pwd2)EditText edt_pwd2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationLayout navigationLayout =(NavigationLayout) findViewById(R.id.navgation_top);
        navigationLayout.setCenterText("密码修改");
        navigationLayout.setLeftTextOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Event(value = R.id.btn_submit)
    private void editpwd(View view){
        String pwd=edt_pwd.getText().toString().trim();
        String pwd2=edt_pwd2.getText().toString().trim();
        if (!pwd.equals(pwd2)){
            Toast.makeText(this,"两次输入密码不一致！",Toast.LENGTH_SHORT).show();
            return;
        }
        search(pwd);
    }
    private void search(String pwd){
        showProcessDialog("修改中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","updatePwd");
        params.addBodyParameter("h_id", MyInforManager.getUserID(this));
        params.addBodyParameter("login_name",MyInforManager.getUserName(this));
        params.addBodyParameter("password", pwd);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("una",result);
                hideProcessDialog();
                processData(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(EditPwdActivity.this,"服务器请求超时",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                hideProcessDialog();
            }
        });

    }
    private void processData(String json){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                Toast.makeText(EditPwdActivity.this,"修改成功！",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(EditPwdActivity.this, LoginActivity.class);
                startActivity(intent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SysApplication.getInstance().killExActivities();
                    }
                },2000);


            }else{
                Toast.makeText(EditPwdActivity.this,adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
