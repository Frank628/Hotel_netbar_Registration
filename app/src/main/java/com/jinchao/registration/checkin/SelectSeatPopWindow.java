package com.jinchao.registration.checkin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jinchao.registration.R;
import com.jinchao.registration.adapter.baseadapter.CommonAdapter;
import com.jinchao.registration.adapter.baseadapter.ViewHolder;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.DbConfig;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.dbtable.SeatTable;
import com.jinchao.registration.jsonbean.AvailableRoomResult;
import com.jinchao.registration.logoff.SelectSeatUnRegistPopWindow;
import com.jinchao.registration.main.LoginActivity;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.LoadingView;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/3/19.
 */

public class SelectSeatPopWindow extends PopupWindow{
    public interface OnEnsureClickListener{
        void  OnEnSureClick(AvailableRoomResult.AvailableRoomOne str);
    }
    private GridView gv;
    private LoadingView loadingView;
    private View mMenuView;
    private ViewFlipper viewfipper;
    private OnEnsureClickListener onEnsureClickListener;
    private Context context;
    DbManager db;
    public SelectSeatPopWindow(final Activity context, final OnEnsureClickListener onEnsureClickListener) {
        super();
        this.context=context;
        this.onEnsureClickListener = onEnsureClickListener;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView=inflater.inflate(R.layout.pop_select_seat, null);
        viewfipper = new ViewFlipper(context);
        db= DbConfig.getDbManager();
        viewfipper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        gv =(GridView) mMenuView.findViewById(R.id.gv);
        loadingView=(LoadingView) mMenuView.findViewById(R.id.loadingview);
        mMenuView.findViewById(R.id.ib_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectSeatPopWindow.this.dismiss();
            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AvailableRoomResult.AvailableRoomOne code=(AvailableRoomResult.AvailableRoomOne)(((GridView)adapterView).getItemAtPosition(i));
//                if (!code.r_enable.equals("0")){
//                    new MaterialDialog.Builder(context)
//                            .title("提示")
//                            .content(context.getResources().getString(R.string.zhanyongtishi_value))
//                            .positiveText("确认")
//                            .show();
//                    return;
//                }
                onEnsureClickListener.OnEnSureClick(code);
                SelectSeatPopWindow.this.dismiss();
            }
        });
        getSeat();
        viewfipper.addView(mMenuView);
        viewfipper.setFlipInterval(6000000);
        this.setContentView(viewfipper);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }
    private void processData(String json){
        try {
            AvailableRoomResult availableRoomResult= GsonTools.changeGsonToBean(json,AvailableRoomResult.class);
            if (availableRoomResult.code==0){
                if (availableRoomResult.data==null){
                    loadingView.empty(context.getString(R.string.nounavailableroom_value));
                    return;
                }
                if (availableRoomResult.data.size()==0){
                    loadingView.empty(context.getString(R.string.nounavailableroom_value));
                    return;
                }
                loadingView.loadComplete();
                CommonAdapter<AvailableRoomResult.AvailableRoomOne> adapter=new CommonAdapter<AvailableRoomResult.AvailableRoomOne>(context,availableRoomResult.data,R.layout.item_seatcode) {
                    @Override
                    public void convert(ViewHolder helper, AvailableRoomResult.AvailableRoomOne item, int position) {
                        helper.setText(R.id.textview,item.r_name);
                        if (item.r_enable.equals("0")){
                            ((TextView)helper.getView(R.id.textview)).setTextColor(Color.parseColor("#666666"));
                        }else{
                            ((TextView)helper.getView(R.id.textview)).setTextColor(Color.parseColor("#d81e06"));
                        }
                    }
                };
                gv.setAdapter(adapter);
            }else{
                loadingView.reload(availableRoomResult.msg,new LoadingView.OnReloadClickListener() {
                    @Override
                    public void onReload() {
                        getSeat();
                    }
                });
            }


        } catch (Exception e) {
            loadingView.reload("服务器返回数据格式错误",new LoadingView.OnReloadClickListener() {
                @Override
                public void onReload() {
                    getSeat();
                }
            });
            e.printStackTrace();
        }

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        viewfipper.startFlipping();
    }
    private void getSeat(){
        loadingView.loading();
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","getRoom");
        params.addBodyParameter("h_id", MyInforManager.getUserID(context));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                loadingView.reload(new LoadingView.OnReloadClickListener() {
                    @Override
                    public void onReload() {
                        getSeat();
                    }
                });
            }
            @Override
            public void onCancelled(CancelledException cex) { loadingView.loadComplete();}
            @Override
            public void onFinished() {

            }
        });
    }
}
