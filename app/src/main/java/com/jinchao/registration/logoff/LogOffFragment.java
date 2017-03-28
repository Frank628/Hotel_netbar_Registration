package com.jinchao.registration.logoff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;
import com.jinchao.registration.adapter.baseadapter.CommonAdapter;
import com.jinchao.registration.adapter.baseadapter.ViewHolder;
import com.jinchao.registration.checkin.SelectSeatPopWindow;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.dbtable.PersonTable;
import com.jinchao.registration.dbtable.SeatTable;
import com.jinchao.registration.jsonbean.ADEOperationResult;
import com.jinchao.registration.jsonbean.AvailableRoomResult;
import com.jinchao.registration.jsonbean.UnAvailableRoomResult;
import com.jinchao.registration.utils.CommonUtils;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.utils.cashfilter.CashierInputFilter;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.jinchao.registration.R.id.edt_price;
import static com.jinchao.registration.R.id.tv_code;

/**
 * Created by user on 2017/3/19.
 */
@ContentView(R.layout.fragment_logoff)
public class LogOffFragment extends BaseFragment {
    @ViewInject(R.id.tv_code)TextView tv_code;
    @ViewInject(R.id.tv_starttime)TextView tv_starttime;
    @ViewInject(R.id.root)LinearLayout root;
    @ViewInject(R.id.lv)ListView lv;
    @ViewInject(R.id.edt_price)EditText edt_price;
    @ViewInject(R.id.tv_nodata)private TextView tv_nodata;
    DbManager db;
    UnAvailableRoomResult.UnAvailableRoomOne CurrentunAvailableRoomOne;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputFilter[] filter ={new CashierInputFilter()};
        edt_price.setFilters(filter);
    }
    @Event(value = R.id.btn_logoff)
    private void unregist(View view){
        unregist();
    }
    @Event(value = R.id.tv_code)
    private void selectSeatCode(View view){
        SelectSeatUnRegistPopWindow selectSeatPopWindow=new SelectSeatUnRegistPopWindow(getActivity(), new SelectSeatUnRegistPopWindow.OnEnsureClickListener() {
            @Override
            public void OnEnSureClick(UnAvailableRoomResult.UnAvailableRoomOne str) {
                tv_code.setText(str.roomName);
                tv_starttime.setText(str.start_time.trim());
                CurrentunAvailableRoomOne=str;
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
    private void deletePerson(){

    }
    private void unregist(){
        if (TextUtils.isEmpty(tv_code.getText().toString().trim())){
            Toast.makeText(getActivity(),getResources().getString(R.string.pleasesroom_value),Toast.LENGTH_SHORT).show();
            return;
        }
        String price =edt_price.getText().toString().trim();
        if (TextUtils.isEmpty(price)){
            Toast.makeText(getActivity(),"请输入最终收款金额",Toast.LENGTH_SHORT).show();
            return;
        }
        showProcessDialog("数据提交中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","cancelOrder");
        params.addBodyParameter("h_id", MyInforManager.getUserID(getActivity()));
        params.addBodyParameter("r_id", CurrentunAvailableRoomOne.r_id);
        params.addBodyParameter("order_id", CurrentunAvailableRoomOne.order_id);
        params.addBodyParameter("tot_price",price);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processUnregist(result);
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
    private void processUnregist(String json){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                Toast.makeText(getActivity(),"注销成功",Toast.LENGTH_SHORT).show();
                unregistSucc();
                db.delete(PersonTable.class, WhereBuilder.b("pk_guid","=",CurrentunAvailableRoomOne.order_id));
                db.delete(SeatTable.class,WhereBuilder.b("guid","=",CurrentunAvailableRoomOne.order_id));

            }else{
                Toast.makeText(getActivity(),adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void unregistSucc(){
        tv_code.setText("");
        CurrentunAvailableRoomOne=null;
        tv_starttime.setText("");
        edt_price.setText("");
        lv.setAdapter(null);
    }
}
