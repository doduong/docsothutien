package app.cntt.cnhp.hpwaterbarcodereader;


import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.ThongTinThanhToanTuyen;
import utils.CommonText;
import utils.ReadJson;
import utils.SharedPref;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab6_ThongKe extends Fragment {


    EditText edttonghoadon;
    EditText edtSoHDCT;
    EditText edtHDDT;
    EditText edtTongTien;
    EditText edtTongTienCT;
    EditText edtTongTienDT;
    EditText edtThuTienMat;
    EditText edtThuTienMattrongngay;

    EditText edtThuQuay;
    EditText edtThuChuyenKhoan;

    String ms_tuyen = "";
    String ms_tk="";
    SharedPref config;
    Locale localeEN = null;
    NumberFormat en = null;

    ListView lstpro;
    CommonText common = new CommonText();
    ArrayList<ThongTinThanhToanTuyen> lstThanhToanTuyen;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    Date today;

    {
        try {
            today = format1.parse(format1.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private ThongKeThanhToanAdapter adapter;





    private View mRootView;
    public Tab6_ThongKe() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab6__thong_ke_frangment, container, false);
        initView();
        return mRootView;
    }

    private void initView() {
        lstThanhToanTuyen = new ArrayList<>();
        config = new SharedPref(getActivity());
        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk = config.getString("ms_tk", "");
        lstpro = (ListView) mRootView.findViewById(R.id.lstpro);

        edttonghoadon = (EditText) mRootView.findViewById(R.id.edttonghoadon);
        edtSoHDCT = (EditText) mRootView.findViewById(R.id.edtSoHDCT);
        edtHDDT = (EditText) mRootView.findViewById(R.id.edtHDDT);
        edtTongTien = (EditText) mRootView.findViewById(R.id.edtTongTien);
        edtTongTienCT = (EditText) mRootView.findViewById(R.id.edtTongTienCT);
        edtTongTienDT = (EditText) mRootView.findViewById(R.id.edtTongTienDT);

        edtThuTienMat = (EditText) mRootView.findViewById(R.id.edtThuTienMat);
        edtThuTienMattrongngay = (EditText) mRootView.findViewById(R.id.edtThuTienMattrongngay);
        edtThuQuay = (EditText) mRootView.findViewById(R.id.edtThuQuay);
        edtThuChuyenKhoan = (EditText) mRootView.findViewById(R.id.edtThuChuyenKhoan);

        localeEN = new Locale("en", "EN");
        en = NumberFormat.getInstance(localeEN);
        viewPaymentReport();

    }

    public void viewPaymentReport(){

        if(isConnected()) {

            String url = common.URL_API+"/ViewPaymentReport?ms_tuyen="+ms_tuyen+ "&ms_tky="+ms_tk;
            new HttpAsyncTaskViewPaymentResultDetail().execute(url);

        }else {
            Toast.makeText(getActivity(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    private class HttpAsyncTaskViewPaymentResultDetail extends AsyncTask<String, JSONObject, Void> {

        Integer ms_mnoi;
        String ten_kh;
        long Tong_tien;
        int ms_ttrang;

        long tongHD = 0;
        long tongHDCT =0;
        long tongHDDT = 0;
        long TTien = 0;
        long TTienCT = 0;
        long TTienDT = 0;
        long TTienDTTienMat = 0;
        long TTienDTChuyenKhoan = 0;
        long TTienDTThuQuay = 0;
        long TTtrongngay= 0;
        String ngay_thu = "";

        JSONArray jsonArrayHoaDon;
        JSONObject jsonHoaDon;
        ThongTinThanhToanTuyen tttuyen;
        Date ngay_doc_moi;


        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Vui lòng đợi trong giây lát..");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];


            try {
                jsonArrayHoaDon = ReadJson.readJSonArrayFromURL(url);
                for (int i = 0; i < jsonArrayHoaDon.length(); i++) {

                    jsonHoaDon = jsonArrayHoaDon.getJSONObject(i);
                    ms_mnoi = Integer.parseInt(common.cat2kitucuoi(jsonHoaDon.getString("ms_mnoi")));
                    ten_kh = jsonHoaDon.getString("ten_kh");
                    Tong_tien = (long) Double.parseDouble(jsonHoaDon.getString("Tong_Tien"));
                    String strms_ttrang = common.GetDataToValue(jsonHoaDon.getString("TTHopDong"),"");
                    int ms_ttrang_tt = (int) Double.parseDouble((strms_ttrang == "") ? "0" : strms_ttrang);
                    String  strms_kieu_tt = common.GetDataToValue(jsonHoaDon.getString("ms_kieu_tt"),"");
                    int ms_kieu_tt = (int) Double.parseDouble((strms_kieu_tt == "") ? "0" : strms_kieu_tt);
                    String  strstt_lo_trinh = common.GetDataToValue(jsonHoaDon.getString("stt_lo_trinh"),"");
                    int stt_lo_trinh = (int) Double.parseDouble((strstt_lo_trinh == "") ? "0" : strstt_lo_trinh);
                    tttuyen = new ThongTinThanhToanTuyen(ms_mnoi, ten_kh, Tong_tien, ms_ttrang_tt, ms_kieu_tt, stt_lo_trinh);
                    ngay_thu = common.GetDataToValue(jsonHoaDon.getString("den_ngay"), "");
                    if(!"".equals(ngay_thu)){
                        ngay_doc_moi = format1.parse(common.cat10kitucuoi(ngay_thu));
                    }



                    TTien = TTien+ (long)tttuyen.getTong_tien();
                    tongHD += 1;

                    if(tttuyen.getMs_ttrang()==1||tttuyen.getMs_ttrang()==0){
                        tongHDCT+=1;
                        TTienCT = TTienCT + (long)tttuyen.getTong_tien();

                    }else if(tttuyen.getMs_ttrang()==3){
                        tongHDDT +=1;
                        TTienDT = TTienDT + (long)tttuyen.getTong_tien();
                        if(tttuyen.getMs_kieu_tt()==1){
                            TTienDTTienMat = TTienDTTienMat + (long)tttuyen.getTong_tien();
                            if(tienmattrongngay(today, ngay_doc_moi)){
                                TTtrongngay = TTtrongngay + (long)tttuyen.getTong_tien();
                            }
                        }else if (tttuyen.getMs_kieu_tt()==4){
                            TTienDTThuQuay = TTienDTThuQuay + (long)tttuyen.getTong_tien();
                        }else if(tttuyen.getMs_kieu_tt()==2){
                            TTienDTChuyenKhoan = TTienDTChuyenKhoan + (long) tttuyen.getTong_tien();
                        }

                    }
                    lstThanhToanTuyen.add(tttuyen);
                    //Log.d("lstThanhToanTuyen size", "" + lstThanhToanTuyen.size());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (lstThanhToanTuyen.size() > 0) {
                    adapter = new ThongKeThanhToanAdapter(getActivity(), lstThanhToanTuyen);
                    edttonghoadon.setText(String.valueOf(tongHD));
                    edtSoHDCT.setText(String.valueOf(tongHDCT));
                    edtHDDT.setText(String.valueOf(tongHDDT));
                    //edtTongTien.setText(String.valueOf(TTien));
                    String strTTien = en.format(TTien);
                    edtTongTien.setText(strTTien);

                    String strTTienCT = en.format(TTienCT);
                    edtTongTienCT.setText(strTTienCT);

                    String strTTienDT = en.format(TTienDT);
                    edtTongTienDT.setText(strTTienDT);

                    String strTTienDTTienMat = en.format(TTienDTTienMat);
                    edtThuTienMat.setText(strTTienDTTienMat);

                    String strTTTrongNgay = en.format(TTtrongngay);
                    edtThuTienMattrongngay.setText(strTTTrongNgay);

                    String strTTienDTThuQuay = en.format(TTienDTThuQuay);
                    edtThuQuay.setText(strTTienDTThuQuay);

                    String strTTienDTChuyenKhoan = en.format(TTienDTChuyenKhoan);
                    edtThuChuyenKhoan.setText(strTTienDTChuyenKhoan);

                    lstpro.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    lstpro.invalidateViews();
                    lstpro.refreshDrawableState();
                    progressDialog.hide();
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
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

    public boolean tienmattrongngay(Date today, Date ngay_doc_moi) {
        boolean result = false;
        if (today.compareTo(ngay_doc_moi) > 0) {
            result = false;
        } else if (today.compareTo(ngay_doc_moi) == 0) {
            result = true;
        }

        return result;

    }


}
