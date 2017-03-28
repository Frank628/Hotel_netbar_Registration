package com.jinchao.registration.informanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinchao.registration.R;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.jsonbean.StatisticsResult;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.widget.LoadingView;
import com.shizhefei.fragment.LazyFragment;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Calendar;

/**
 * Created by user on 2017/3/21.
 */
public class StatisticsFragment extends LazyFragment {
    private int mYear,mMonth,mDay;
    TextView tv_endtime;
    TextView tv_starttime;
    TextView tv_pcount;
    TextView tv_cash;
    Button btn_submit;
    public static StatisticsFragment newInstance(){
        StatisticsFragment statisticsFragment=new StatisticsFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(KEY, "");
//        orderUndoFragment.setArguments(bundle);
        return statisticsFragment;
    }


    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_statistics);
        btn_submit=(Button) findViewById(R.id.btn_submit);
        tv_starttime=(TextView) findViewById(R.id.tv_starttime);
        tv_endtime=(TextView) findViewById(R.id.tv_endtime);
        tv_pcount=(TextView) findViewById(R.id.tv_pcount);
        tv_cash=(TextView) findViewById(R.id.tv_cash);
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        tv_starttime.setText(mYear+"-"+((mMonth+1)>9?(mMonth+1):("0"+(mMonth+1)))+"-"+(mDay>9?mDay:("0"+mDay)));
        tv_endtime.setText(mYear+"-"+((mMonth+1)>9?(mMonth+1):("0"+(mMonth+1)))+"-"+(mDay>9?mDay:("0"+mDay)));
        tv_starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tv_starttime.setText(year+"-"+((month+1)>9?(month+1):("0"+(month+1)))+"-"+(day>9?day:("0"+day)));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        tv_endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tv_endtime.setText(year+"-"+((month+1)>9?(month+1):("0"+(month+1)))+"-"+(day>9?day:("0"+day)));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    private void search(){
        showProcessDialog("查询中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","getcount");
        params.addBodyParameter("h_id", MyInforManager.getUserID(getActivity()));
        params.addBodyParameter("starttime",tv_starttime.getText().toString().trim());
        params.addBodyParameter("endtime", tv_endtime.getText().toString().trim());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("una",result);
                hideProcessDialog();
                processData(result);
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(),"服务器请求超时",Toast.LENGTH_SHORT).show();
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
            StatisticsResult statisticsResult= GsonTools.changeGsonToBean(json,StatisticsResult.class);
            if (statisticsResult.code==0){
                Toast.makeText(getActivity(),"查询成功",Toast.LENGTH_SHORT).show();
                tv_pcount.setText("人次："+statisticsResult.data.person);
                tv_cash.setText("总收入："+statisticsResult.data.price);
            }else{
                Toast.makeText(getActivity(),statisticsResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
