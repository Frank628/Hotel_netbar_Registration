package com.jinchao.registration.checkin;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caihua.cloud.common.entity.PersonInfo;
import com.caihua.cloud.common.link.Link;
import com.caihua.cloud.common.link.LinkFactory;
import com.caihua.cloud.common.reader.IDReader;
import com.jinchao.registration.Base.BaseDialogFragment;
import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.R;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.DbConfig;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.dbtable.PersonTable;
import com.jinchao.registration.dbtable.SeatTable;
import com.jinchao.registration.jsonbean.ADEOperationResult;
import com.jinchao.registration.utils.Base64Coder;
import com.jinchao.registration.utils.CommonUtils;
import com.jinchao.registration.utils.GlobalPref;
import com.jinchao.registration.utils.GsonTools;
import com.jinchao.registration.webservice.CompareAsyncTask;
import com.jinchao.registration.webservice.CompareResult;
import com.jinchao.registration.widget.IDCardView;
import com.jinchao.registration.widget.LoadingView;
import com.jinchao.registration.widget.ValidateEidtText;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by user on 2017/3/19.
 */
@ContentView(R.layout.dialog_checkin)
public class CheckInDialogFragment extends BaseDialogFragment {
    @ViewInject(R.id.idcard)IDCardView idCardView;
    @ViewInject(R.id.tv_facematch)TextView tv_facematch;
    @ViewInject(R.id.edt_phone)ValidateEidtText edt_phone;
    @ViewInject(R.id.root)LinearLayout root;
    public IDReader idReader;
    public Link link = null;
    private SeatTable seat;
    public  PersonInfo person;
    private DbManager db;
    private FacePop facePop;
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    public byte[] imgA,imgB;
    public File photofile;
    private static final int REQUEST_CODE_CAMERA=3;
    public static CheckInDialogFragment newInstance(SeatTable seat){
        CheckInDialogFragment checkInDialogFragment=new CheckInDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("seat", seat);
        checkInDialogFragment.setArguments(bundle);
        return checkInDialogFragment;
    }



    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( dm );
        getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height );
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) idCardView.getLayoutParams();
        params.width= CommonUtils.getWindowWidth(getActivity())-CommonUtils.dip2px(getActivity(),35);
        params.height=(CommonUtils.getWindowWidth(getActivity())-CommonUtils.dip2px(getActivity(),35))*377/600;
        idCardView.setLayoutParams(params);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        seat=getArguments().getSerializable("seat")==null?null:(SeatTable)getArguments().getSerializable("seat") ;
        db= DbConfig.getDbManager();
        idReader = new IDReader(getActivity());
        idReader.setUseSpecificServer(!GlobalPref.getUseAuto());
        idReader.addSpecificServer(GlobalPref.getAddress(), GlobalPref.getPort());
        idReader.setListener(new IDReader.IDReaderListener() {
            @Override
            public void onReadCardSuccess(final PersonInfo personInfo) {
                try {
                    link.disconnect();
                } catch (Exception e) {
                    Log.e("readcard", e.getMessage());
                } finally {
                    link = null;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        person=personInfo;
                        showCard(personInfo);
                    }
                });
            }

            @Override
            public void onReadCardFailed(final String s) {
                try {
                    link.disconnect();
                } catch (Exception e) {
                    Log.e("readcard", e.getMessage());
                } finally {
                    link = null;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError(s);
                    }
                });
            }
        });
    }
    @Event(value = R.id.tv_facematch)
    private void faceMatch(View view){
        photofile = CommonUtils.getCompareTempImage();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);

    }
    private void showfacepop(){
        facePop =new FacePop(getActivity(), BitmapFactory.decodeByteArray(imgA, 0, imgA.length),photofile.getAbsolutePath(), new FacePop.OnCompareClickListener() {
            @Override
            public void onClick() {
                if (imgB != null && imgA != null) {
                    new MyCompareAsyncTask(imgA, imgB).execute();
                } else if (imgA == null) {
                    Toast.makeText(getActivity(),"请读取身份证",Toast.LENGTH_SHORT).show();
                } else if (imgB == null) {
                    Toast.makeText(getActivity(),"请拍照",Toast.LENGTH_SHORT).show();
                }
            }
        });
        facePop.showPopupWindow(root,0,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode ==getActivity().RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA :
                    setPhoto(photofile);
                    showfacepop();
                    break;
            }
        }

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (link != null)
            return;
        Tag nfcTag = null;
        try {
            nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        } catch (Exception e) {

        }
        if (nfcTag != null) {
            link = LinkFactory.createExNFCLink(nfcTag);
            try {
                link.connect();
            } catch (Exception e) {
                showError(e.getMessage());
                try {
                    link.disconnect();
                } catch (Exception ex) {
                } finally {
                    link = null;
                }
                return;
            }
            idReader.setLink(link);
            person=null;
            showCard(null);
            idCardView.reading();
            idReader.startReadCard();
        }
    }
    @Event(value = R.id.btn_save)
    private void save(View view){
        if (person==null){
            Toast.makeText(getActivity(),"请先读取身份证！",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            seat.setStarttime(CommonUtils.getCurrentDate());
            SeatTable seatTable=db.selector(SeatTable.class).where("guid","=",seat.getGuid()).findFirst();
            if (seatTable==null){
                registOrder();
            }else{
                registPerson();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void showCard(PersonInfo personInfo){
        if (personInfo==null){
            tv_facematch.setVisibility(View.GONE);
            idCardView.clearIDCard();
            return;
        }tv_facematch.setVisibility(View.VISIBLE);
        imgA=personInfo.getPhoto();
        idCardView.setIDCard(personInfo.getName().trim(),personInfo.getSex().trim(),personInfo.getNation().trim(),
                personInfo.getBirthday().trim().substring(0,4),personInfo.getBirthday().trim().substring(4,6),personInfo.getBirthday().trim().substring(6,8),
                personInfo.getAddress(),personInfo.getIdNumber().trim(), BitmapFactory.decodeByteArray(personInfo.getPhoto(), 0, personInfo.getPhoto().length));

    }
    private void showError(String error){
        tv_facematch.setVisibility(View.GONE);
        idCardView.showError(error);
    }
    private void registOrder(){
        showProcessDialog("订单生成中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","saveOrder");
        params.addBodyParameter("h_id", MyInforManager.getUserID(getActivity()));
        params.addBodyParameter("order_id", seat.getGuid());
        params.addBodyParameter("r_id", seat.getSeatid());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("order",result);
                hideProcessDialog();
                processOrder(result);
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
    private void processOrder(String json){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                registPerson();
                db.save(seat);
            }else{
                Toast.makeText(getActivity(),adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void registPerson(){
        showProcessDialog("人员登记中...");
        RequestParams params=new RequestParams(Constants.URL+"HostelService.aspx");
        params.addBodyParameter("type","savePeople");
        params.addBodyParameter("sname", person.getName());
        params.addBodyParameter("sex", person.getSex());
        params.addBodyParameter("mingzu", person.getNation());
        params.addBodyParameter("birthday", person.getBirthday());
        params.addBodyParameter("sfz", person.getIdNumber());
        params.addBodyParameter("hujidz",person.getAddress());
        params.addBodyParameter("gzdx_type", "0");
        params.addBodyParameter("rz_status", "0");
        params.addBodyParameter("order_id", seat.getGuid());
        params.addBodyParameter("tetelphone", edt_phone.getText().toString().trim());
        params.addBodyParameter("dpname", MyInforManager.getDpName(getActivity()));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("person",result);
                hideProcessDialog();
                processPerson(result);
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
    private void processPerson(String json){
        try {
            ADEOperationResult adeOperationResult= GsonTools.changeGsonToBean(json,ADEOperationResult.class);
            if (adeOperationResult.code==0){
                PersonTable personTable =db.selector(PersonTable.class).where("idcard","=",person.getIdNumber().trim()).and("pk_guid","=",seat.getGuid()).findFirst();
                if (personTable==null){
                    db.save(new PersonTable(seat.getGuid(),person.getIdNumber(),person.getName(),person.getSex(),person.getNation(),person.getBirthday(),
                            person.getAddress(),"",edt_phone.getText().toString().trim()));
                }
                ((BaseFragment)getParentFragment()).onSubFragmentCallBack();
                CheckInDialogFragment.this.dismiss();
            }else{
                Toast.makeText(getActivity(),adeOperationResult.msg,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class MyCompareAsyncTask extends CompareAsyncTask {
        public MyCompareAsyncTask(byte[] imgA, byte[] imgB) {
            super(imgA, imgB);
        }
        @Override
        protected void onPreExecute() {}
        @Override
        protected void onPostExecute(CompareResult result) {
            if (result == null) {
                if (facePop!=null){
                    facePop.setResult("人脸对比失败");
                }
                return;
            }
            StringBuilder builder = new StringBuilder();
            double score = result.getScore();
            builder.append("相似度：" + (score / 100) + "%,");
            builder.append(score >= 7000 ? "可以判断为同一人" : "可以判断不为同一个人");
            if (facePop!=null){
                facePop.setResult(builder.toString());
            }
        }
    }



    public void setPhoto(File photofile) {
        imgB = null;
        if (photofile != null && photofile.exists() && photofile.length() > 10) {
            byte[] img = CommonUtils.getByte(photofile);// 获得源图片
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);// 将原图片转换成bitmap，方便后面转换
            imgB = CommonUtils.Bitmap2Bytes(bitmap);// 得到有损图
        } else {
            Toast.makeText(getActivity(), "拍摄照片失败", Toast.LENGTH_SHORT).show();
        }
    }
}
