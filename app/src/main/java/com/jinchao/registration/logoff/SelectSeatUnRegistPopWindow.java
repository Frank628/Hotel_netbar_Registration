package com.jinchao.registration.logoff;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.jinchao.registration.R;
import com.jinchao.registration.adapter.baseadapter.CommonAdapter;
import com.jinchao.registration.adapter.baseadapter.ViewHolder;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.DbConfig;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.jsonbean.AvailableRoomResult;
import com.jinchao.registration.jsonbean.UnAvailableRoomResult;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.LoadingView;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by user on 2017/3/19.
 */

public class SelectSeatUnRegistPopWindow extends PopupWindow{
    public interface OnEnsureClickListener{
        void  OnEnSureClick(UnAvailableRoomResult.UnAvailableRoomOne str);
    }
    private GridView gv;
    private LoadingView loadingView;
    private View mMenuView;
    private ViewFlipper viewfipper;
    private OnEnsureClickListener onEnsureClickListener;
    private Context context;
    DbManager db;
    public SelectSeatUnRegistPopWindow(Activity context, final OnEnsureClickListener onEnsureClickListener) {
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
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UnAvailableRoomResult.UnAvailableRoomOne code=(UnAvailableRoomResult.UnAvailableRoomOne)(((GridView)adapterView).getItemAtPosition(i));
                onEnsureClickListener.OnEnSureClick(code);
                SelectSeatUnRegistPopWindow.this.dismiss();
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
            UnAvailableRoomResult unAvailableRoomResult= GsonTools.changeGsonToBean(json,UnAvailableRoomResult.class);
            if (unAvailableRoomResult.code==0){
                if (unAvailableRoomResult.data==null){
                    loadingView.empty(context.getResources().getString(R.string.nounavailableroom_value));
                    return;
                }
                if (unAvailableRoomResult.data.size()==0){
                    loadingView.empty(context.getResources().getString(R.string.nounavailableroom_value));
                    return;
                }
                loadingView.loadComplete();
                CommonAdapter<UnAvailableRoomResult.UnAvailableRoomOne> adapter=new CommonAdapter<UnAvailableRoomResult.UnAvailableRoomOne>(context,unAvailableRoomResult.data,R.layout.item_seatcode) {
                    @Override
                    public void convert(ViewHolder helper, UnAvailableRoomResult.UnAvailableRoomOne item, int position) {
                        helper.setText(R.id.textview,item.roomName);
                    }
                };
                gv.setAdapter(adapter);
            }else{
                loadingView.reload(unAvailableRoomResult.msg,new LoadingView.OnReloadClickListener() {
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
        params.addBodyParameter("type","getpeople");
        params.addBodyParameter("h_id", MyInforManager.getUserID(context));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("una",result);
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
            public void onCancelled(CancelledException cex) {
                loadingView.loadComplete();
            }
            @Override
            public void onFinished() {

            }
        });
    }
}
