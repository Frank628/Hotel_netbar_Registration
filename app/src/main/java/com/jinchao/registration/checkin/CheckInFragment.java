package com.jinchao.registration.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caihua.cloud.common.entity.PersonInfo;
import com.jinchao.registration.Base.BaseDialogFragment;
import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;
import com.jinchao.registration.adapter.baseadapter.CommonAdapter;
import com.jinchao.registration.adapter.baseadapter.ViewHolder;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.DbConfig;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.dbtable.PersonTable;
import com.jinchao.registration.dbtable.SeatTable;
import com.jinchao.registration.jsonbean.ADEOperationResult;
import com.jinchao.registration.jsonbean.AvailableRoomResult;
import com.jinchao.registration.utils.CommonUtils;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.IDCardView;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by user on 2017/3/19.
 */

@ContentView(R.layout.fragment_checkin)
public class CheckInFragment extends BaseFragment {
    @ViewInject(R.id.tv_code)TextView tv_code;
    @ViewInject(R.id.root)LinearLayout root;
    @ViewInject(R.id.lv)ListView lv;
    DbManager db;
    SeatTable currentSeatTable;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db= DbConfig.getDbManager();
    }
    @Event(value = R.id.tv_code)
    private void selectSeatCode(View view){
        SelectSeatPopWindow selectSeatPopWindow=new SelectSeatPopWindow(getActivity(), new SelectSeatPopWindow.OnEnsureClickListener() {
            @Override
            public void OnEnSureClick(AvailableRoomResult.AvailableRoomOne str) {
                tv_code.setText(str.r_name);
                currentSeatTable=new SeatTable(str.r_id,str.r_name,CommonUtils.GenerateGUID(), MyInforManager.getCurrentAccountType(getActivity()));
            }
        });
        selectSeatPopWindow.showAtLocation(root, Gravity.NO_GRAVITY,0,0);
    }
    @Event(value = R.id.addguest)
    private void addGuest(View view){
        String code=tv_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)||code.equals("请选择")){
            Toast.makeText(getActivity(),getResources().getString(R.string.pleasesroom_value),Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
        CheckInDialogFragment checkInDialogFragment=CheckInDialogFragment.newInstance(currentSeatTable);
        checkInDialogFragment.show(getChildFragmentManager(),"CheckIn");
    }

    @Event(value = R.id.btn_save)
    private void save(View view){
        tv_code.setText("");
        currentSeatTable=null;
        lv.setAdapter(null);
        Toast.makeText(getActivity(),"登记结束",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<Fragment> fragments=getChildFragmentManager().getFragments();
        if (fragments==null)return;
        for (int i=0;i<fragments.size();i++){
            if (((BaseDialogFragment)fragments.get(i))!=null) {
                ((BaseDialogFragment) fragments.get(i)).onNewIntent(intent);
            }
        }
    }

    @Override
    public void onSubFragmentCallBack() {
        super.onSubFragmentCallBack();
        Toast.makeText(getActivity(),"登记成功！",Toast.LENGTH_SHORT).show();
        initSeatPersons();
    }
    private void initSeatPersons(){
        try {
            List<PersonTable> list=currentSeatTable.getPersonsInSeat(db);
            if (list!=null){
                lv.setAdapter(new CommonAdapter<PersonTable>(getActivity(),list,R.layout.item_already_regist) {
                    @Override
                    public void convert(ViewHolder helper,final PersonTable item, int position) {
                        helper.setText(R.id.tv_idcard,item.getIdcard()+"   "+item.getName());
                        helper.getView(R.id.ib_delete).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deletePerson(item);
                            }
                        });
                    }
                });
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    private void deletePerson(final PersonTable personTable){
        showProcessDialog("人员撤销中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","delPeople");
        params.addBodyParameter("h_id", MyInforManager.getUserID(getActivity()));
        params.addBodyParameter("sfz", personTable.getIdcard());
        params.addBodyParameter("order_id", currentSeatTable.getGuid());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("delete",result);
                hideProcessDialog();
                processOrder(result,personTable);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(),"服务器请求超时...",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(CancelledException cex) { }
            @Override
            public void onFinished() {
                hideProcessDialog();
            }
        });
    }
    private void processOrder(String json, PersonTable personTable){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                Toast.makeText(getActivity(),"人员撤销成功.",Toast.LENGTH_SHORT).show();
                db.delete(PersonTable.class, WhereBuilder.b("idcard","=",personTable.getIdcard()).and("pk_guid","=",personTable.getPk_guid()));
                initSeatPersons();
            }else{
                Toast.makeText(getActivity(),adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
