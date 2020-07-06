package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.ThongTinTieuThu;
import utils.CommonText;
import utils.DBManager;
import utils.ReadJson;
import utils.SharedPref;

public class Tab1Fragment extends Fragment implements DecoratedBarcodeView.TorchListener {

    private CustomAdapter adapter;
    ListView lstpro;
    String ms_tuyen = "";
    String ms_tk = "";
    String ms_bd = "";
    TextView tvtiledadoc;
    TextView tvtilechuadoc;
    EditText edtSearchName;
    Button btnSearchName;
    Button btnDocSo;
    String keysearch = "";
    Spinner spidstimkiem;
    int positionloaitk = 0;
    private Context context;
    SharedPref config;
    CommonText common = new CommonText();
    private View mRootView;
    int countUsePointNotRead = 0;
    int getCountUsePointWereRead = 0;
    ArrayList<ThongTinTieuThu> listddcd = new ArrayList<>();
    DBManager dbManager ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_report, container, false);

        initView();
        return mRootView;
    }

    private void initView() {
        config = new SharedPref(getActivity());

        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk = config.getString("ms_tk", "");
        ms_bd = config.getString("ms_bd", "");

        lstpro = (ListView) mRootView.findViewById(R.id.lstpro);
        tvtilechuadoc = (TextView) mRootView.findViewById(R.id.tvtilechuadoc);
        tvtiledadoc = (TextView) mRootView.findViewById(R.id.tvtiledadoc);
        edtSearchName = (EditText) mRootView.findViewById(R.id.edtsearchname);
        btnSearchName = (Button) mRootView.findViewById(R.id.btnsearchname);
        btnDocSo = (Button) mRootView.findViewById(R.id.btndocso);
        spidstimkiem = (Spinner) mRootView.findViewById(R.id.spidstimkiem);
        dbManager = new DBManager(getActivity());


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

            }
        });


        getThongTinDiemDung();
        getCountUsePointNotRead();
        getCountUsePointWereRead();



        btnDocSo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setBeepEnabled(false);
                integrator.forSupportFragment(Tab1Fragment.this).setCaptureActivity(ScannerActivity.class).initiateScan();

            }


        });

        btnSearchName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keysearch = edtSearchName.getText().toString();
                searchUsePointNotRead(keysearch, positionloaitk);
            }


        });


        lstpro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isConnected()) {
                    ThongTinTieuThu tttt = listddcd.get(i);
                    if(dbManager.getDiemDungMatMang(tttt.getMs_moinoi(), Integer.parseInt(ms_tuyen)) == null) {
                        Intent intent = new Intent(getActivity(), NhapSoBangTay.class);
                        intent.putExtra("arrlist", listddcd);
                        intent.putExtra("ttct", tttt);
                        intent.putExtra("ms_tuyen", ms_tuyen);
                        intent.putExtra("ms_tk", ms_tk);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getActivity(), "Truyền về trong mục mất kết nối", Toast.LENGTH_LONG).show();
                    }
                }else {
                   Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    public void searchUsePointNotRead(String keysearch, int conditionfield) {
        String field = "";
        String keywrord = "&keyWord=" + keysearch;
        if (conditionfield == 0) {
            field = "&conditionField=contact";
        } else if (conditionfield == 1) {
            field = "&conditionField=customer";
        } else if (conditionfield == 2) {
            field = "&conditionField=address";
        } else if (conditionfield == 3) {
            field = "&conditionField=meterserial";
        } else if (conditionfield == 4) {
            field = "&conditionField=mobile";
        }
        if (!"".equals(keysearch)) {
            if(isConnected()) {
                //SearchUsePointWereRead?ms_tuyen=5115&ms_tk=247&keyWord=%22%22&conditionField=customer'
                String url = common.URL_API + "/SearchUsePointNotRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk + keywrord + field;
                Log.d("SearchUsePointNotRead", url);
                new HttpAsyncTaskSearchUsePointNotRead().execute(url);
            }else  {
                Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
            }
        } else {
            getThongTinDiemDung();
        }

    }


    public void getThongTinDiemDung() {

        if (isConnected()) {

            String url = common.URL_API + "/GetUsePointNotRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk;
            new HttpAsyncTaskGetUsePointNotRead().execute(url);

        } else {

            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }



    private class HttpAsyncTaskGetUsePointNotRead extends AsyncTask<String, JSONObject, Void> {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Nạp dữ liệu...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            JSONArray jsonArrayTuyen;
            String dien_thoai = "";
            listddcd = new ArrayList<>();

            try {
                jsonArrayTuyen = ReadJson.readJSonArrayFromURL(url);
                for (int i = 0; i < jsonArrayTuyen.length(); i++) {


                    ThongTinTieuThu tttt;
                    JSONObject objTTTT = jsonArrayTuyen.getJSONObject(i);
                    String ms_mnoi = common.cat2kitucuoi(objTTTT.getString("ms_mnoi"));
                    String nguoi_thue = objTTTT.getString("nguoi_thue");
                    String dia_chi = objTTTT.getString("dia_chi");
                    String chi_so_cu = common.cat2kitucuoi(objTTTT.getString("chi_so_cu"));
                    String ngay_doc_cu_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_cu"));


                    String stt_lo_trinh = objTTTT.getString("stt_lo_trinh");
                    if (null != objTTTT.getString("dien_thoai") || "".equals(objTTTT.getString("dien_thoai"))) {
                        dien_thoai = objTTTT.getString("dien_thoai");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date ngay_doc_cu = null;
                    Date ngay_doc_moi = null;
                    try {
                        ngay_doc_cu = format1.parse(ngay_doc_cu_str);
                        // ngay_doc_moi = sdf.parse(ngay_doc_moi_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    String ms_dh = common.cat2kitucuoi(objTTTT.getString("ms_dh"));
                   // Log.d("ms_dh", String.valueOf(ms_dh));
                    String so_seri = objTTTT.getString("so_seri");
                    String url_image = objTTTT.getString("url_image");
                    String ms_ttrang = objTTTT.getString("ms_ttrang");


                    tttt = new ThongTinTieuThu(ms_mnoi, nguoi_thue, dia_chi, chi_so_cu, null, null, ngay_doc_cu, null, stt_lo_trinh, dien_thoai, ms_dh, so_seri, url_image, ms_ttrang, 0,0 );
                    listddcd.add(tttt);
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
            adapter = new CustomAdapter(getActivity(), listddcd, dbManager, Integer.parseInt(ms_tuyen));
            adapter.notifyDataSetChanged();
            lstpro.setAdapter(adapter);
            lstpro.invalidateViews();
            lstpro.refreshDrawableState();
            progressDialog.hide();
            progressDialog.dismiss();
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
                Log.d("showResultDialogue:", result.getContents());
                showResultDialogue(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String ms_db = "";


    public void showResultDialogue(final String resultscan) {


        ms_db = common.strimBarcode(resultscan);
        if (isConnected()) {

            String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + ms_db + "&ms_tk=" + ms_tk+"&ms_tuyen="+ms_tuyen;
            Log.d("GetCustomerInfo", url);
            new HttpAsyncTaskGetCustomerInfo().execute(url);

        } else {

            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTaskSearchUsePointNotRead extends AsyncTask<String, JSONObject, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            JSONArray jsonArrayTuyen;
            String dien_thoai = "";
            listddcd = new ArrayList<>();
            try {
                jsonArrayTuyen = ReadJson.readJSonArrayFromURL(url);
                for (int i = 0; i < jsonArrayTuyen.length(); i++) {


                    ThongTinTieuThu tttt;
                    JSONObject objTTTT = jsonArrayTuyen.getJSONObject(i);
                    String ms_mnoi = common.cat2kitucuoi(objTTTT.getString("ms_mnoi"));
                    String nguoi_thue = objTTTT.getString("nguoi_thue");
                    String dia_chi = objTTTT.getString("dia_chi");
                    String chi_so_cu = common.cat2kitucuoi(objTTTT.getString("chi_so_cu"));
                    String chi_so_moi = common.cat2kitucuoi(objTTTT.getString("chi_so_moi"));
                    //Log.d("Tab1_chi_so_moi", chi_so_moi);
                    String so_tthu = "--";
                    String ngay_doc_cu_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_cu"));
                    String stt_lo_trinh = objTTTT.getString("stt_lo_trinh");
                    if (null != objTTTT.getString("dien_thoai") || "".equals(objTTTT.getString("dien_thoai"))) {
                        dien_thoai = objTTTT.getString("dien_thoai");
                    }
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date ngay_doc_cu = null;
                    Date ngay_doc_moi = null;
                    try {
                        ngay_doc_cu = format1.parse(ngay_doc_cu_str);
                        // ngay_doc_moi = sdf.parse(ngay_doc_moi_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String ms_dh = common.cat2kitucuoi(objTTTT.getString("ms_dh"));
                    String so_seri = objTTTT.getString("so_seri");
                    String url_image = objTTTT.getString("url_image");
                    String ms_ttrang = objTTTT.getString("ms_ttrang");
                    tttt = new ThongTinTieuThu(ms_mnoi, nguoi_thue, dia_chi, chi_so_cu, null, null, ngay_doc_cu, null, stt_lo_trinh, dien_thoai,ms_dh,so_seri,url_image, ms_ttrang, 0, 0 );
                    listddcd.add(tttt);
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
            adapter = new CustomAdapter(getActivity(), listddcd, dbManager, Integer.parseInt(ms_tuyen));
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
            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
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
            //Log.d("countUsePointNotRead", String.valueOf(countUsePointNotRead));
            tvtilechuadoc.setText("Chưa đọc: "+ String.valueOf(countUsePointNotRead));
            super.onPostExecute(result);
        }
    }
    private void getCountUsePointWereRead(){
        if(isConnected()) {

            String url = common.URL_API + "/GetCountUsePointWereRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk;
            Log.d("GetCountUsePointWereRea", url);
            new HttpAsyncTaskGetCountUsePointWereRead().execute(url);
        }else {
            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
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
           // Log.d("getCountUsePointWereRea", String.valueOf(getCountUsePointWereRead));
            tvtiledadoc.setText("Đã đọc: "+ String.valueOf(getCountUsePointWereRead));
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
                    //Log.d("showResultDialogue", ms_db);
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



}
