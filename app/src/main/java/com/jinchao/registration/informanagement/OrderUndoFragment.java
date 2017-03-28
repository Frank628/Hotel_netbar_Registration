package com.jinchao.registration.informanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;

import com.jinchao.registration.adapter.baseadapter.CommonAdapter;
import com.jinchao.registration.adapter.baseadapter.ViewHolder;
import com.jinchao.registration.checkin.CheckInDialogFragment;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.DbConfig;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.dbtable.PersonTable;
import com.jinchao.registration.dbtable.SeatTable;
import com.jinchao.registration.jsonbean.ADEOperationResult;
import com.jinchao.registration.jsonbean.UnAvailableRoomResult;
import com.jinchao.registration.logoff.SelectSeatUnRegistPopWindow;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.LoadingView;
import com.shizhefei.fragment.LazyFragment;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jinchao.registration.R.id.edt_phone;
import static com.jinchao.registration.R.id.tv_nodata;
import static com.jinchao.registration.R.id.tv_starttime;

/**
 * Created by user on 2017/3/21.
 */
public class OrderUndoFragment extends LazyFragment {
    private TextView tv_code;
    private TextView tv_starttime;
    private LinearLayout root;
    private ListView lv;
    private Button btn_undo;
    private TextView tv_orderid;
    private TextView tv_nodata;
    private String currentOrderid="", currentRoomID="";
    DbManager db;
    public static OrderUndoFragment newInstance(){
        OrderUndoFragment orderUndoFragment=new OrderUndoFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(KEY, "");
//        orderUndoFragment.setArguments(bundle);
        return orderUndoFragment;
    }


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_orderundo);
        db= DbConfig.getDbManager();
        root=(LinearLayout) findViewById(R.id.root);
        lv=(ListView) findViewById(R.id.lv);
        tv_orderid=(TextView) findViewById(R.id.tv_orderid);
        tv_starttime=(TextView) findViewById(R.id.tv_starttime);
        tv_code=(TextView) findViewById(R.id.tv_code);
        btn_undo=(Button) findViewById(R.id.btn_undo);
        tv_nodata=(TextView) findViewById(R.id.tv_nodata);
        tv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSeatCode();
            }
        });
        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoOrder();
            }
        });
    }
    private void selectSeatCode(){
        SelectSeatUnRegistPopWindow selectSeatPopWindow=new SelectSeatUnRegistPopWindow(getActivity(), new SelectSeatUnRegistPopWindow.OnEnsureClickListener() {
            @Override
            public void OnEnSureClick(UnAvailableRoomResult.UnAvailableRoomOne str) {
                tv_code.setText(str.roomName);
                tv_starttime.setText(str.start_time.trim());
                currentOrderid=str.order_id;
                currentRoomID=str.r_id;
                tv_orderid.setText("订单号："+currentOrderid);
                processList(str);
            }
        });
        selectSeatPopWindow.showAtLocation(root, Gravity.NO_GRAVITY,0,0);
    }
    private void processList(UnAvailableRoomResult.UnAvailableRoomOne unAvailableRoomOne){
        if (unAvailableRoomOne.peopleData==null){
            tv_nodata.setVisibility(View.VISIBLE);
            return;
        }
        if (unAvailableRoomOne.peopleData.size()==0){
            tv_nodata.setVisibility(View.VISIBLE);
            return;
        }
        tv_nodata.setVisibility(View.GONE);
        lv.setAdapter(new CommonAdapter<UnAvailableRoomResult.PersonInRoom>(getActivity(),unAvailableRoomOne.peopleData,R.layout.item_already_regist) {
            @Override
            public void convert(ViewHolder helper, UnAvailableRoomResult.PersonInRoom item, int position) {
                helper.setText(R.id.tv_idcard,item.sfz+"   "+item.sname);
                helper.getView(R.id.ib_delete).setVisibility(View.GONE);
            }
        });
    }

    private void undoOrder(){
        if (TextUtils.isEmpty(currentOrderid)){
            Toast.makeText(getActivity(),"请先选择需要撤销的订单",Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","delOrder");
        params.addBodyParameter("h_id", MyInforManager.getUserID(getActivity()));
        params.addBodyParameter("r_id", currentRoomID);
        params.addBodyParameter("order_id", currentOrderid);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("aa",result);
                processUndo(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
               Toast.makeText(getActivity(),"网络请求超时...",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) { }
            @Override
            public void onFinished() {
                hideProcessDialog();
            }
        });
    }

    private void processUndo(String json){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                Toast.makeText(getActivity(),"撤销成功",Toast.LENGTH_SHORT).show();
                undoSucc();
                db.delete(PersonTable.class, WhereBuilder.b("pk_guid","=",currentOrderid));
                db.delete(SeatTable.class,WhereBuilder.b("guid","=",currentOrderid));

            }else{
                Toast.makeText(getActivity(),adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void undoSucc(){
        tv_code.setText("");
        currentOrderid="";
        currentRoomID="";
        tv_orderid.setText("");
        tv_starttime.setText("");
        lv.setAdapter(null);
    }



}
