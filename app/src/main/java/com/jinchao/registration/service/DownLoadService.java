package com.jinchao.registration.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;


import com.jinchao.registration.config.Constants;
import com.jinchao.registration.utils.SharePrefUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class DownLoadService extends Service{
	private String url="";
	private ProgressDialog progressDialog;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		if (intent!=null) {
			url=intent.getStringExtra("url");
		}else{
			url= SharePrefUtil.getString(this, "url", "");
		}
		download(url);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags=START_REDELIVER_INTENT;
		return super.onStartCommand(intent, flags, startId);
	}
	private void download(String url){
		Log.i("aaaa",url);
		progressDialog = new ProgressDialog(getApplicationContext());
		progressDialog.setTitle("正在下载更新...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		progressDialog.show();
		RequestParams params =new RequestParams(url.trim());
		params.setSaveFilePath(Constants.DB_PATH+"hotel.apk");
		x.http().get(params, new Callback.ProgressCallback<File>() {
			@Override
			public void onSuccess(File result) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "hotel.apk")),
						"application/vnd.android.package-archive");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				DownLoadService.this.startActivity(intent);
			}
			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				progressDialog.dismiss();
			}
			@Override
			public void onCancelled(CancelledException cex) {
				progressDialog.dismiss();
			}

			@Override
			public void onFinished() {
				progressDialog.dismiss();
			}

			@Override
			public void onWaiting() {

			}
			@Override
			public void onStarted() {

			}
			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				Log.i("pro",(int) (current*100/total)+"");
				Log.i("pro",current+"/"+total);
				progressDialog.setProgress((int) (current*100/total));
			}
		});
	}
}
