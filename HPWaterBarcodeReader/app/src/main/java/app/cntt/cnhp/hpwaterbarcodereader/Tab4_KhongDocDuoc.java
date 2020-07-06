package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.DiemDungKhongDoc;
import model.DongHoNoi;
import model.ThongTinTieuThu;
import model.TinhtrangDocDongHo;
import utils.CommonText;
import utils.DBManager;
import utils.ReadJson;
import utils.SharedPref;

public class Tab4_KhongDocDuoc extends Fragment {

    private View mRootView;

    private AdapterDiemDungKhongDoc adapter;
    public ArrayList<DiemDungKhongDoc> customlistViewValueArray = new ArrayList<DiemDungKhongDoc>();
    ListView lstpro1;
    DBManager dbManager ;
    String ms_tuyen = "";
    String ms_tk="";
    String ms_bd = "";
   // Button btnxoabangtam;
    CommonText common = new CommonText();
    SharedPref config;
    ArrayList<ThongTinTieuThu> listddkdd = new ArrayList<>();
    ArrayList<TinhtrangDocDongHo> listTinhTrang;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab4__khong_doc_duoc, container, false);
        initView();
        return mRootView;
    }

    private void initView(){
        config = new SharedPref(getActivity());
        lstpro1 = (ListView) mRootView.findViewById(R.id.lstkhongdocduoc);
        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk = config.getString("ms_tk", "");
        GetAllMeterStatus();


    }

    public boolean isConnected() {
        ConnectivityManager connMgr = null ;
        NetworkInfo networkInfo = null;
        if (getActivity() != null) {
            connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void getThongTinDiemDung() {

        if (isConnected()) {

            String url = common.URL_API + "/GetUsePointCouldNotRead?ms_tuyen=" + ms_tuyen + "&ms_tk=" + ms_tk;
            new HttpAsyncTaskGetUsePointCouldNotRead().execute(url);

        } else {

            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }



    private class HttpAsyncTaskGetUsePointCouldNotRead extends AsyncTask<String, JSONObject, Void> {

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
            listddkdd = new ArrayList<>();

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
                    String so_tthu = common.cat2kitucuoi(objTTTT.getString("so_tthu"));
                    String ngay_doc_cu_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_cu"));

                    // if(null != objTTTT.getString("ngay_doc_moi") ) {
                    //    String ngay_doc_moi_str = common.cat10kitucuoi(objTTTT.getString("ngay_doc_moi"));
                    //}
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
                    Log.d("ms_dh", String.valueOf(ms_dh));
                    String so_seri = objTTTT.getString("so_seri");
                    String url_image = objTTTT.getString("url_image");
                    String ms_ttrang = common.cat2kitucuoi(objTTTT.getString("ms_ttrang"));
                    for(int j = 0; j< listTinhTrang.size(); j++){
                        TinhtrangDocDongHo tt= listTinhTrang.get(j);
                        if(ms_ttrang.equals(String.valueOf(tt.getId()))){
                            ms_ttrang = tt.getName();
                        }
                    }
                    tttt = new ThongTinTieuThu(ms_mnoi, nguoi_thue, dia_chi, chi_so_cu, chi_so_moi, so_tthu, ngay_doc_cu, null, stt_lo_trinh, dien_thoai, ms_dh, so_seri, url_image, ms_ttrang, 0, 0 );
                    listddkdd.add(tttt);
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
            adapter = new AdapterDiemDungKhongDoc(getActivity(), listddkdd);
            lstpro1.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lstpro1.invalidateViews();
            lstpro1.refreshDrawableState();
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    public void GetAllMeterStatus() {
        if (isConnected()) {
            String url = common.URL_API + "/GetAllMeterStatus";
            new HttpAsyncTaskGetAllMeterStatus().execute(url);
        } else {
            Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    private class HttpAsyncTaskGetAllMeterStatus extends AsyncTask<String, JSONObject, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            JSONArray jsonArrayTinhTrang;

            try {
                listTinhTrang = new ArrayList<>();
                jsonArrayTinhTrang = ReadJson.readJSonArrayFromURL(url);
                Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
                for (int i = 0; i < jsonArrayTinhTrang.length(); i++) {
                    TinhtrangDocDongHo tinhtrangDocDongHo;
                    JSONObject objTinhTrang = jsonArrayTinhTrang.getJSONObject(i);
                    int ms_ttrang = objTinhTrang.getInt("ms_ttrang");
                    String mo_ta = objTinhTrang.getString("mo_ta");

                    tinhtrangDocDongHo = new TinhtrangDocDongHo(ms_ttrang, mo_ta);
                    listTinhTrang.add(tinhtrangDocDongHo);
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
            getThongTinDiemDung();
        }
    }


}
