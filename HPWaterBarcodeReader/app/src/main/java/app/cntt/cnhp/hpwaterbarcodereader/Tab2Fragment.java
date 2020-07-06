package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import model.DiemDungMatMang;
import model.ThongTinTieuThu;
import utils.CommonText;
import utils.DBManager;
import utils.ReadJson;
import utils.Server;
import utils.SharedPref;

public class Tab2Fragment extends Fragment
{
    Connection conn;
    private DiemDungDaDocAdapter adapter;
    ListView lstpro;
    String ms_tuyen = "";
    String ms_tk="";
    String ms_bd = "";
    TextView tvtiledadoc;
    TextView tvtilechuadoc;
    TextView tongtieuthu;
    TextView tvdathu;
    int tongtieuthudadoc = 0;
    int tongdathu = 0;
    EditText edtSearchName;
    Button btnSearchName;
    Button btnDocSo;
    String keysearch  = "";
    Spinner spidstimkiem;
    int positionloaitk = 0;
    SharedPref config;
    CommonText common = new CommonText();
    ArrayList<ThongTinTieuThu> listdddd = new ArrayList<>();
    ArrayList<ThongTinTieuThu> listdddd_ct = new ArrayList<>();
    ArrayList<ThongTinTieuThu> listdddd_dt = new ArrayList<>();
    ArrayList<ThongTinTieuThu> listddttbang0 = new ArrayList<>();
    int countUsePointNotRead = 0;
    int getCountUsePointWereRead = 0;

    private View mRootView;
    DBManager dbManager;

    private RadioGroup radioGroup;
    private RadioButton radioButton_All;
    private RadioButton radioButton_DaThu;
    private RadioButton radioButton_ChuaThu;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab2_fragment, container, false);
        initView();
        return mRootView;
    }

    private void initView(){

        config = new SharedPref(getActivity());

        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk = config.getString("ms_tk", "");
        ms_bd = config.getString("ms_bd", "");

        lstpro = (ListView) mRootView.findViewById(R.id.lstpro2);
        tvtilechuadoc = (TextView) mRootView.findViewById(R.id.tvtilechuadoc2);
        tvtiledadoc = (TextView) mRootView.findViewById(R.id.tvtiledadoc2);
        tongtieuthu = (TextView)  mRootView.findViewById(R.id.tongtieuthu);
        tvdathu = (TextView) mRootView.findViewById(R.id.tvdathu);
        edtSearchName = (EditText) mRootView.findViewById(R.id.edtsearchname);
        btnSearchName = (Button) mRootView.findViewById(R.id.btnsearchname);
        btnDocSo = (Button) mRootView.findViewById(R.id.btndocso);
        spidstimkiem = (Spinner)mRootView.findViewById(R.id.spidstimkiem) ;
        dbManager = new DBManager(getActivity());
        radioGroup = (RadioGroup) mRootView.findViewById(R.id.radioGroup);
        radioButton_All = (RadioButton) mRootView.findViewById(R.id.radioButton_All) ;
        radioButton_DaThu = (RadioButton) mRootView.findViewById(R.id.radioButton_DaThu);
        radioButton_ChuaThu = (RadioButton) mRootView.findViewById(R.id.radioButton_ChuaThu);
        ArrayList<String> listTT1 = new ArrayList<String>();




        listTT1.add("Danh bạ");
        listTT1.add("Tên");
        listTT1.add("Địa chỉ");
        listTT1.add("Seri ĐH");
        listTT1.add("SĐT");


        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, listTT1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spidstimkiem.setAdapter(dataAdapter1);


        spidstimkiem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionloaitk = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });



        getthongtindiemdung();
        getCountUsePointNotRead();
        getCountUsePointWereRead();



        btnDocSo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // new IntentIntegrator(getActivity()).setCaptureActivity(ScannerActivity.class).initiateScan();
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setBeepEnabled(false);
                integrator.forSupportFragment(Tab2Fragment.this).setCaptureActivity(ScannerActivity.class).initiateScan();

            }


        });

        btnSearchName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keysearch = edtSearchName.getText().toString();
                searchUsePointWereRead(keysearch, positionloaitk);
            }


        });



        lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<ThongTinTieuThu>  arrtt = new ArrayList<>();
                if (isConnected()) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    switch (selectedId){
                        case R.id.radioButton_All:
                            arrtt  = listdddd;
                            break;
                        case R.id.radioButton_ChuaThu:
                            arrtt = listdddd_ct;
                            break;
                        case R.id.radioButton_DaThu:
                            arrtt = listdddd_dt;
                            break;
                        case R.id.radioButton_TTBang0:
                            arrtt = listddttbang0;
                            break;
                    }

                    ThongTinTieuThu tttt = arrtt.get(i);
                    Intent intent = new Intent(getActivity(), ThongTinChiTiet.class);
                    intent.putExtra("arrlist", arrtt);
                    intent.putExtra("ttct", tttt);
                    intent.putExtra("ms_tuyen", ms_tuyen);
                    intent.putExtra("ms_tk", ms_tk);
                    startActivity(intent);

                }else {
                    Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
                }
            }


        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //getthongtindiemdung();
                switch (checkedId){
                    case R.id.radioButton_All:
                        addToListView(listdddd);
                        break;
                    case R.id.radioButton_ChuaThu:
                         addToListView(listdddd_ct);
                        break;
                    case R.id.radioButton_DaThu:
                        addToListView(listdddd_dt);
                        break;
                    case R.id.radioButton_TTBang0:
                        addToListView(listddttbang0);
                        break;
                }
            }
        });

        /*this.radioButton_All.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addToListView(listdddd);
            }
        });


        // Khi radio button "Male" có thay đổi.
        this.radioButton_ChuaThu.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //doOnGameCharacterChanged(buttonView,isChecked);
                addToListView(listdddd_ct);
            }
        });

        this.radioButton_DaThu.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //doOnGameCharacterChanged(buttonView,isChecked);
                addToListView(listdddd_dt);
            }
        });*/


    }

    public void searchUsePointWereRead(String keysearch, int conditionfield){
        String field= "";
        String keywrord = "&keyWord="+keysearch;
        if(conditionfield == 0){
            field = "&conditionField=contact";
        }else if (conditionfield ==1){
            field = "&conditionField=customer";
        }else if (conditionfield ==2){
            field = "&conditionField=address";
        }else if(conditionfield ==3 ){
            field = "&conditionField=meterserial";

        }else if(conditionfield ==4 ){
            field = "&conditionField=mobile";
        }
        if(!"".equals(keysearch)){
            //SearchUsePointWereRead?ms_tuyen=5115&ms_tk=247&keyWord=%22%22&conditionField=customer'
            if(isConnected()) {
                String url = common.URL_API + "/SearchUsePointWereRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk + keywrord + field;
                Log.d("SearchUsePointWereRead", url);
                new HttpAsyncTaskSearchUsePointWereRead().execute(url);
            }else {
                Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
            }
        }else {
            getthongtindiemdung();
        }

    }

    public void getthongtindiemdung(){

        if(isConnected()) {
            radioGroup.check(R.id.radioButton_All);
            String url = common.URL_API+"/GetUsePointWereRead?ms_tuyen="+ms_tuyen+ "&ms_tk="+ms_tk;
            new HttpAsyncTaskGetUsePointWereRead().execute(url);


        }else {
            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan được hủy bỏ", Toast.LENGTH_LONG).show();
            } else {
                Log.d("showResultDialogue:",result.getContents());
                showResultDialogue(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String ms_db = "";


    public void showResultDialogue( final String resultscan) {

        ms_db = common.strimBarcode(resultscan);
        if (isConnected()) {
            String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + ms_db + "&ms_tk=" + ms_tk+"&ms_tuyen="+ms_tuyen;
            new HttpAsyncTaskGetCustomerInfo().execute(url);

        } else {

            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTaskGetUsePointWereRead extends AsyncTask<String, JSONObject, Void> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Vui lòng đợi trong giây lát..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            Log.d("GetUsePointWereRead", url);
            JSONArray jsonArrayTuyen;
            String ngay_doc_moi_str = "";
            String dien_thoai ="";
            listdddd = new ArrayList<>();
            listdddd_ct = new ArrayList<>();
            listdddd_dt = new ArrayList<>();
            listddttbang0= new ArrayList<>();
            try {
                jsonArrayTuyen = ReadJson.readJSonArrayFromURL(url);
                tongtieuthudadoc = 0;
                tongdathu = 0;
                for (int i = 0; i < jsonArrayTuyen.length(); i++) {
                    ThongTinTieuThu tttt;
                    JSONObject objTTTT = jsonArrayTuyen.getJSONObject(i);
                    String ms_mnoi = common.cat2kitucuoi(objTTTT.getString("ms_mnoi"));
                    String nguoi_thue = objTTTT.getString("nguoi_thue");
                    String dia_chi = objTTTT.getString("dia_chi");
                    String  chi_so_cu = common.cat2kitucuoi(objTTTT.getString("chi_so_cu"));
                    String chi_so_moi = common.cat2kitucuoi(objTTTT.getString("chi_so_moi"));
                    String so_tthu = common.cat2kitucuoi(objTTTT.getString("so_tthu"));
                    String ngay_doc_cu_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_cu"));
                    if(null != objTTTT.getString("ngay_doc_moi") ) {
                        ngay_doc_moi_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_moi"));
                    }
                    String stt_lo_trinh = objTTTT.getString("stt_lo_trinh");
                    if(null!= objTTTT.getString("dien_thoai")|| "".equals(objTTTT.getString("dien_thoai"))) {
                        dien_thoai = objTTTT.getString("dien_thoai");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date ngay_doc_cu = null;
                    java.util.Date ngay_doc_moi = null;
                    try {
                        ngay_doc_cu = format1.parse(ngay_doc_cu_str);
                        ngay_doc_moi = format1.parse(ngay_doc_moi_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String ms_dh = common.cat2kitucuoi(objTTTT.getString("ms_dh"));
                    String so_seri = objTTTT.getString("so_seri");
                    String url_image = objTTTT.getString("url_image");

                    //String strms_ttrang_docdh = common.cat2kitucuoi(objTTTT.getString("ms_ttrang_docdh"));
                    //int ms_ttrang_docdh = (int) Double.parseDouble((strms_ttrang_docdh == "") ? "0" : strms_ttrang_docdh);

                    String strms_ttrang_tt = common.GetDataToValue(objTTTT.getString("TTHopDong"),"");
                    int ms_ttrang_tt = (int) Double.parseDouble((strms_ttrang_tt == "") ? "0" : strms_ttrang_tt);


                    String strms_hd = common.GetDataToValue(objTTTT.getString("so_hd"),"");
                    long ms_hd = (long) Double.parseDouble((strms_hd == "") ? "0" : strms_hd);
                    tttt = new ThongTinTieuThu(ms_mnoi, nguoi_thue, dia_chi, chi_so_cu, chi_so_moi,so_tthu,  ngay_doc_cu, ngay_doc_moi, stt_lo_trinh, dien_thoai,ms_dh, so_seri, url_image, "", ms_ttrang_tt, ms_hd);

                    if(ms_ttrang_tt ==3){
                        tongdathu +=1;
                        listdddd_dt.add(tttt);

                    }else {
                        listdddd_ct.add(tttt);
                    }
                    if(!"null".equals(so_tthu)) {
                        tongtieuthudadoc = tongtieuthudadoc + Integer.parseInt(so_tthu);
                    }
                    if(Integer.parseInt(so_tthu)==0){
                        listddttbang0.add(tttt);
                    }
                    // (String ms_moinoi, String nguoi_thue, String dia_chi, String chi_so_cu, String chi_so_moi, String so_tthu, Date ngay_doc_cu, Date ngay_doc_moi, String stt_lo_trinh) {

                    listdddd.add(tttt);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter = new DiemDungDaDocAdapter(getActivity(), listdddd, dbManager, Integer.parseInt(ms_tuyen));
            adapter.notifyDataSetChanged();
            lstpro.setAdapter(adapter);

            lstpro.invalidateViews();
            lstpro.refreshDrawableState();
            tongtieuthu.setText("Tổng: "+String.valueOf(tongtieuthudadoc)+ "(m3)");
            tvdathu.setText("ĐT: "+String.valueOf(tongdathu));
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    private class HttpAsyncTaskSearchUsePointWereRead extends AsyncTask<String, JSONObject, Void> {

        ArrayList<ThongTinTieuThu> listdddds = new ArrayList<>();
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            Log.d("SearchUsePointWereRead1", url);
            JSONArray jsonArrayTuyen;
            String ngay_doc_moi_str = "";
            String dien_thoai ="";

            try {
                jsonArrayTuyen = ReadJson.readJSonArrayFromURL(url);
                for (int i = 0; i < jsonArrayTuyen.length(); i++) {
                    ThongTinTieuThu tttt;
                    JSONObject objTTTT = jsonArrayTuyen.getJSONObject(i);
                    String ms_mnoi = common.cat2kitucuoi(objTTTT.getString("ms_mnoi"));
                    String nguoi_thue = objTTTT.getString("nguoi_thue");
                    String dia_chi = objTTTT.getString("dia_chi");
                    String  chi_so_cu = common.cat2kitucuoi(objTTTT.getString("chi_so_cu"));
                    String chi_so_moi = common.cat2kitucuoi(objTTTT.getString("chi_so_moi"));
                    String so_tthu = common.cat2kitucuoi(objTTTT.getString("so_tthu"));
                    String ngay_doc_cu_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_cu"));
                    if(null != objTTTT.getString("ngay_doc_moi") ) {
                        ngay_doc_moi_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_moi"));
                    }
                    String stt_lo_trinh = objTTTT.getString("stt_lo_trinh");
                    if(null!= objTTTT.getString("dien_thoai")|| "".equals(objTTTT.getString("dien_thoai"))) {
                        dien_thoai = objTTTT.getString("dien_thoai");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date ngay_doc_cu = null;
                    java.util.Date ngay_doc_moi = null;
                    try {
                        ngay_doc_cu = format1.parse(ngay_doc_cu_str);
                        ngay_doc_moi = format1.parse(ngay_doc_moi_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String ms_dh = common.cat2kitucuoi(objTTTT.getString("ms_dh"));
                    String so_seri = objTTTT.getString("so_seri");
                    String url_image = objTTTT.getString("url_image");

                    //String strms_ttrang_docdh = common.cat2kitucuoi(objTTTT.getString("ms_ttrang_docdh"));
                    //int ms_ttrang_docdh = (int) Double.parseDouble((strms_ttrang_docdh == "") ? "0" : strms_ttrang_docdh);

                    String strms_ttrang_tt = common.GetDataToValue(objTTTT.getString("TTHopDong"),"");
                    int ms_ttrang_tt = (int) Double.parseDouble((strms_ttrang_tt == "") ? "0" : strms_ttrang_tt);


                    String strms_hd = common.GetDataToValue(objTTTT.getString("so_hd"),"");
                    long ms_hd = (long) Double.parseDouble((strms_hd == "") ? "0" : strms_hd);
                    // (String ms_moinoi, String nguoi_thue, String dia_chi, String chi_so_cu, String chi_so_moi, String so_tthu, Date ngay_doc_cu, Date ngay_doc_moi, String stt_lo_trinh) {
                    tttt = new ThongTinTieuThu(ms_mnoi, nguoi_thue, dia_chi, chi_so_cu, chi_so_moi,so_tthu,  ngay_doc_cu, ngay_doc_moi, stt_lo_trinh, dien_thoai, ms_dh, so_seri, url_image, "", ms_ttrang_tt, ms_hd);
                    //listdddd.add(tttt);

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    switch (selectedId){
                        case R.id.radioButton_All:
                            listdddds.add(tttt);
                            break;
                        case R.id.radioButton_ChuaThu:
                            if(ms_ttrang_tt !=3){
                                listdddds.add(tttt);
                            }
                            break;
                        case R.id.radioButton_DaThu:
                            if(ms_ttrang_tt ==3){
                                listdddds.add(tttt);
                            }
                            break;
                        case R.id.radioButton_TTBang0:
                            if(Integer.parseInt(so_tthu)==0){
                                listdddds.add(tttt);
                            }
                            break;
                    }


                    Log.d("listdddd.size", String.valueOf(listdddd.size()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            int selectedId = radioGroup.getCheckedRadioButtonId();
            switch (selectedId){
                case R.id.radioButton_All:
                    listdddd = listdddds;
                    break;
                case R.id.radioButton_ChuaThu:
                    listdddd_ct = listdddds;
                    break;
                case R.id.radioButton_DaThu:
                    listdddd_dt = listdddds;
                    break;
                case R.id.radioButton_TTBang0:
                    listddttbang0 = listdddds;
                    break;
            }
            adapter = new DiemDungDaDocAdapter(getActivity(), listdddds, dbManager, Integer.parseInt(ms_tuyen));
            lstpro.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lstpro.invalidateViews();
            lstpro.refreshDrawableState();

        }
    }

    private void getCountUsePointNotRead(){

        if(isConnected()) {
            String url = common.URL_API + "/GetCountUsePointNotRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk;
            Log.d("GetCountUsePointNotRead", url);
            new HttpAsyncTaskGetCountUsePointNotRead().execute(url);
        }else {
            Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }
    }


    private class HttpAsyncTaskGetCountUsePointNotRead extends AsyncTask<String, JSONObject, Void> {

        String str = "0";

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            try {
                String str= ReadJson.readStringFromURL(url);
                if(null!=str|| !"".equals(str)){
                    countUsePointNotRead = Integer.parseInt(str);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("countUsePointNotRead", String.valueOf(countUsePointNotRead));
            tvtilechuadoc.setText("CĐ: "+ String.valueOf(countUsePointNotRead));
            super.onPostExecute(result);
        }
    }
    private void getCountUsePointWereRead(){

        if(isConnected()) {
            String url = common.URL_API + "/GetCountUsePointWereRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk;
            Log.d("GetCountUsePointWereRea", url);
            new HttpAsyncTaskGetCountUsePointWereRead().execute(url);
        }else {
            Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }
    }


    private class HttpAsyncTaskGetCountUsePointWereRead extends AsyncTask<String, JSONObject, Void> {

        String str = "0";

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];

            try {
                String str= ReadJson.readStringFromURL(url);
                if(null!=str|| !"".equals(str)){
                    getCountUsePointWereRead = Integer.parseInt(str);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("getCountUsePointWereRea", String.valueOf(getCountUsePointWereRead));
            tvtiledadoc.setText("ĐĐ: "+ String.valueOf(getCountUsePointWereRead));
            super.onPostExecute(result);
        }
    }

    private class HttpAsyncTaskGetCustomerInfo extends AsyncTask<String, JSONObject, Void> {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            try {
                jsonArray = ReadJson.readJSonArrayFromURL(url);
                if (jsonArray.length()>0) {
                    jsonObject = (JSONObject)jsonArray.get(0);


                    String ms_dh = common.cat2kitucuoi(jsonObject.getString("ms_dh"));
                    Log.d("showResultDialogue", ms_db);
                    Intent intent = new Intent(getActivity(), NhapSoActivity.class);
                    intent.putExtra("ms_tuyen", ms_tuyen);
                    intent.putExtra("ms_tk", ms_tk);
                    intent.putExtra("ms_bd", ms_bd);
                    intent.putExtra("ms_db", ms_db);
                    intent.putExtra("ms_dh", ms_dh);
                    // intent.putExtra("tongsodiemdung",String.valueOf(arrTD.size()) );
                    startActivity(intent);

                } else {

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (jsonArray.length()>0) {

            }else {
                Toast.makeText(getActivity(), "Mã danh bạ "+ ms_db+ " không tồn tại!", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);

        }
    }

    private void addToListView(ArrayList<ThongTinTieuThu> arr){

        adapter = new DiemDungDaDocAdapter(getActivity(), arr, dbManager, Integer.parseInt(ms_tuyen));
        adapter.notifyDataSetChanged();
        lstpro.invalidateViews();
        lstpro.setAdapter(adapter);

        lstpro.refreshDrawableState();
    }

}
