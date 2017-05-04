package com.jinchao.registration.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jinchao.registration.Base.BaseActivity;
import com.jinchao.registration.Base.BaseLoginActivity;
import com.jinchao.registration.MyApplication;
import com.jinchao.registration.R;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.jsonbean.LoginResult;
import com.jinchao.registration.jsonbean.VersionResult;
import com.jinchao.registration.service.DownLoadService;
import com.jinchao.registration.utils.CommonUtils;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.MaterialTextField;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.security.AccessController.getContext;


/**
 * Created by user on 2017/3/17.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseLoginActivity{
    @ViewInject(R.id.edt_user)EditText edt_user;
    @ViewInject(R.id.edt_password)EditText edt_password;
    @ViewInject(R.id.mt_user)MaterialTextField mt_user;
    @ViewInject(R.id.mt_pwd)MaterialTextField mt_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mt_user.toggle();mt_pwd.toggle();
        checkUpdate();
        if (!TextUtils.isEmpty(MyInforManager.getUserName(this))){
            mt_user.toggle();
            edt_user.setText(MyInforManager.getUserName(this));
            edt_user.setSelection(MyInforManager.getUserName(this).length());
        }
        if (!TextUtils.isEmpty(MyInforManager.getPassword(this))){
            mt_pwd.toggle();
            edt_password.setText(MyInforManager.getPassword(this));
            edt_password.setSelection(MyInforManager.getPassword(this).length());
        }

    }
    @Event(value = R.id.btn_login)
    private void login(View view){
        String user=edt_user.getText().toString().trim();
        if (TextUtils.isEmpty(user)){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }
        String password=edt_password.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        Login(user,password);
    }

    private void Login(String name,String password){
        showProcessDialog("登录中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","login");
        params.addBodyParameter("loginName",name);
        params.addBodyParameter("password",password);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("LOGIN",result);
                processData(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {
                hideProcessDialog();
            }
        });
    }
    private void processData(String json){
        Log.i("login",json);
        try {
            final LoginResult loginResult = GsonTools.changeGsonToBean(json,LoginResult.class);
            if (loginResult.code==0){
                if (!loginResult.data.srv_enable.trim().equals("1")){
                    new MaterialDialog.Builder(this)
                            .title("提示")
                            .content("您的账号服务截至时间为"+loginResult.data.srv_endtime+",请立即续费！")
                            .positiveText("确认")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    LoginActivity.this.finish();
                                }
                            })
                            .show();
                    return;
                }
                if(isLeftOneMonth(loginResult.data.srv_endtime)){
                    new MaterialDialog.Builder(this)
                            .title("提示")
                            .content("您的账号服务截至时间为"+loginResult.data.srv_endtime+",请及时续费，以免影响正常使用！")
                            .positiveText("确认")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (loginResult.data.hrs_type.equals("1")){
                                        ((MyApplication)getApplication()).setAccountType("en");
                                    }else{
                                        ((MyApplication)getApplication()).setAccountType("zh");
                                    }
                                    MyInforManager.setUserName(LoginActivity.this,loginResult.data.login_name);
                                    MyInforManager.setPassword(LoginActivity.this,edt_password.getText().toString().trim());
                                    MyInforManager.setUserID(LoginActivity.this,loginResult.data.id);
                                    MyInforManager.setCurrentAccountType(LoginActivity.this,loginResult.data.hrs_type);
                                    MyInforManager.setDpName(LoginActivity.this,loginResult.data.dp_name);
                                    MyInforManager.setHrsName(LoginActivity.this,loginResult.data.hrs_name);
                                    MyInforManager.setSrvTime(LoginActivity.this,loginResult.data.srv_endtime);
                                    Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                }
                            })
                            .show();
                    return;
                }
                if (loginResult.data.hrs_type.equals("1")){
                    ((MyApplication)getApplication()).setAccountType("en");
                }else{
                    ((MyApplication)getApplication()).setAccountType("zh");
                }
                MyInforManager.setUserName(this,loginResult.data.login_name);
                MyInforManager.setPassword(this,edt_password.getText().toString().trim());
                MyInforManager.setUserID(this,loginResult.data.id);
                MyInforManager.setCurrentAccountType(this,loginResult.data.hrs_type);
                MyInforManager.setDpName(this,loginResult.data.dp_name);
                MyInforManager.setHrsName(this,loginResult.data.hrs_name);
                MyInforManager.setSrvTime(this,loginResult.data.srv_endtime);
                Intent intent =new Intent(this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }else{
                Toast.makeText(this,loginResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkUpdate(){
        RequestParams params=new RequestParams(Constants.URL+"VersionInfor.aspx");
        params.addBodyParameter("type","4");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("update",result);
                processUpdate(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            @Override
            public void onCancelled(CancelledException cex) {}
            @Override
            public void onFinished() {
                hideProcessDialog();
            }
        });
    }
    private void processUpdate(String json){
        try {
            final VersionResult versionResult =GsonTools.changeGsonToBean(json,VersionResult.class);
            if (versionResult.versionNum.trim().equals("")){
                return;
            }
            String[] str =versionResult.versionNum.trim().split("\\.");
            String versionStr=str[0]+str[1]+str[2];
            int new_version =Integer.parseInt(versionStr);
            if (new_version> CommonUtils.getVersionCode(LoginActivity.this)) {
                new MaterialDialog.Builder(LoginActivity.this)
                        .title("提示")
                        .content("发现新版本，请及时更新！")
                        .positiveText("立即更新")
                        .negativeText("稍后再说")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent serviceDownload=new Intent(LoginActivity.this,DownLoadService.class);
                                if (CommonUtils.isServiceRunning(LoginActivity.this, "com.jinchao.registration.service.DownLoadService")) {
                                    LoginActivity.this.stopService(serviceDownload);
                                }
                                serviceDownload.putExtra("url", versionResult.desFile);
                                LoginActivity.this.startService(serviceDownload);
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isLeftOneMonth(String lastDF){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try{
            Date currentDate = new Date();
            Date lastDate = format.parse(lastDF);
            int days=differentDaysByMillisecond(currentDate,lastDate);
            System.out.println("两个日期的差距：" + days);
            if (days<30)
                return true;
            else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static int differentDaysByMillisecond(Date date1,Date date2){
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
}
