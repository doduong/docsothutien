package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import api.BarcodeReaderApiManager;
import model.BarcodeResponse;
import model.SoTieuThu;
import model.ThongTinTieuThu;
import model.TinhtrangDocDongHo;
import model.request.UpdatePhoneNumber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.CommonFuntions;
import utils.CommonText;
import utils.ReadJson;
import utils.SharedPref;
import utils.TinhTienFunction;

public class ThongTinChiTiet extends Activity {

    TextView tvTenTuyen;
    EditText edtdanhba;
    EditText edttenkh;
    EditText edtdiachi;
    EditText edtSocu;
    EditText edtSomoi;
    EditText edttieuthu;
    EditText edtngaydoccu;
    EditText edtngaydocmoi;
    EditText edtmdsd;
    EditText edtSDT;
    EditText edtsodongho1;
    EditText txtTT3T;
    EditText txtTTTB;
    EditText txtghichugcs;
    EditText edtkieuthanhtoan;
    CheckBox chkdockotinhtien;
    Button btnLuuSDT;
    String ms_tk = "";
    Button btntruoc;
    Button btnsau;
    String ms_tuyen = "";
    ThongTinTieuThu ttkh;
    SharedPref config;
    CommonText common = new CommonText();
    ImageView imgPhoto;
    Spinner spinkhongdocduoc;
    ArrayList<TinhtrangDocDongHo> listTinhTrang;

    CommonFuntions commonFuntions;
    String savesdttemp = "";
    String ms_bd;

    //Thu tien
    Button btnThuTien;
    TinhTienFunction fnTinhTien;
    Date Ngay_Doc_Moi = null;
    Date To_Day = null;
    //End Thu tien

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String z = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_chi_tiet);
        tvTenTuyen = (TextView) findViewById(R.id.tentuyen);
        edtdanhba = (EditText) findViewById(R.id.edtdanhba);
        edttenkh = (EditText) findViewById(R.id.edttennguoithue);
        edtdiachi = (EditText) findViewById(R.id.edtDiaChi);
        edtSocu = (EditText) findViewById(R.id.edtSocu);
        edtSomoi = (EditText) findViewById(R.id.edtSomoi);
        edttieuthu = (EditText) findViewById(R.id.edttieuthu);
        edtngaydoccu = (EditText) findViewById(R.id.edtngaydoccu);
        edtngaydocmoi = (EditText) findViewById(R.id.edtngaydocmoi);
        edtsodongho1 = (EditText) findViewById(R.id.edtsodongho);
        txtTT3T = (EditText) findViewById(R.id.txtTT3T);
        txtTTTB = (EditText) findViewById(R.id.txtTBTT);
        btntruoc = (Button) findViewById(R.id.btntruoc);
        btnsau = (Button) findViewById(R.id.btnsau);
        btnLuuSDT = (Button) findViewById(R.id.btnLuuSDT);
        edtmdsd = (EditText) findViewById(R.id.edtmdsdkh);
        edtSDT = (EditText) findViewById(R.id.edtSDT);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        spinkhongdocduoc = (Spinner) findViewById(R.id.spinkhongdocduoc);
        txtghichugcs = (EditText) findViewById(R.id.txtghichugcs);
        edtkieuthanhtoan = (EditText) findViewById(R.id.edtkieuthanhtoan);
        chkdockotinhtien = (CheckBox) findViewById(R.id.chkdockotinhtien);
        //Thu tien
        btnThuTien = (Button) findViewById(R.id.btnThuTien);
        fnTinhTien = new TinhTienFunction();
        //End Thu tien

        //Nhà máy Minh Đức
        //btnThuTien.setText("Xem Hóa Đơn");
        //End Nhà máy MĐ
        config = new SharedPref(this);
        commonFuntions = new CommonFuntions();
        Intent intent = getIntent();
        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk = config.getString("ms_tk", "");
        ms_bd = config.getString("ms_bd", "");
        ttkh = (ThongTinTieuThu) intent.getSerializableExtra("ttct");
        final ArrayList<ThongTinTieuThu> arrTTCT = (ArrayList<ThongTinTieuThu>) intent.getSerializableExtra("arrlist");


        GetAllMeterStatus();
        GetPurposeByMoinoi(ttkh.getMs_moinoi());
        GetTopNInvoice(ttkh.getMs_moinoi());
        getthongtinkhachhang(ttkh.getMs_moinoi());

        chkdockotinhtien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkdockotinhtien.isChecked()) {
                    btnThuTien.setBackgroundColor(Color.GRAY);
                    btnThuTien.setEnabled(false);
                } else {
                    if("".equals(txtghichugcs.getText().toString())) {
                        btnThuTien.setBackgroundResource(R.drawable.button_style);
                        btnThuTien.setEnabled(true);
                    }else {
                        chkdockotinhtien.setChecked(true);
                    }
                }
            }
        });


        btnsau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chkdockotinhtien.setChecked(false);
                for (int i = 0; i < arrTTCT.size() - 1; i++) {
                    if (ttkh.getStt_lo_trinh().equals(arrTTCT.get(i).getStt_lo_trinh())) {
                        Log.d("arrTTCT", arrTTCT.get(i).getMs_moinoi());
                        ttkh = arrTTCT.get(i + 1);
                        break;
                    }


                }
                GetPurposeByMoinoi(ttkh.getMs_moinoi());
                GetTopNInvoice(ttkh.getMs_moinoi());
                getthongtinkhachhang(ttkh.getMs_moinoi());
                fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk));


            }


        });

        btntruoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chkdockotinhtien.setChecked(false);
                for (int i = 1; i < arrTTCT.size(); i++) {
                    if (ttkh.getStt_lo_trinh() == arrTTCT.get(i).getStt_lo_trinh()) {
                        ttkh = arrTTCT.get(i - 1);
                        break;
                    }

                }
                GetPurposeByMoinoi(ttkh.getMs_moinoi());
                GetTopNInvoice(ttkh.getMs_moinoi());
                getthongtinkhachhang(ttkh.getMs_moinoi());
                //Fix22/05/2019
                fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk));

            }


        });

        //Thu tien
        btnThuTien.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isConnected()) {

                    if (fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk)) != 0) {
                        Intent intent = new Intent(ThongTinChiTiet.this, ThuTienKhachHang.class);
                        intent.putExtra("ms_mnoi", ttkh.getMs_moinoi());
                        intent.putExtra("ms_tk", ms_tk);
                        startActivity(intent);
                    } else {
                        if (duocPhepTinhTien(To_Day, Ngay_Doc_Moi)) {
                            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ThongTinChiTiet.this);
                            b.setTitle("Thông báo");
                            b.setMessage("Bạn chắc chắn muốn tính lại tiền điểm dùng trên?");
                            b.setPositiveButton("TÍNH TIỀN LẠI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    if (isConnected()) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        final ProgressDialog progressDialog = new ProgressDialog(ThongTinChiTiet.this);
                                        progressDialog.setCancelable(false);
                                        progressDialog.setTitle("Tính tiền....");
                                        progressDialog.show();
                                        if(fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk)) == 0) {
                                            fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk));
                                            if (fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk)) != 0) {
                                                Intent intent = new Intent(ThongTinChiTiet.this, ThuTienKhachHang.class);
                                                intent.putExtra("ms_mnoi", ttkh.getMs_moinoi());
                                                intent.putExtra("ms_tk", ms_tk);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(ThongTinChiTiet.this, "Điểm dùng chưa có hóa đơn!", Toast.LENGTH_LONG).show();
                                            }
                                        }else {
                                            Toast.makeText(ThongTinChiTiet.this, "Điểm dùng đã được tính tiền", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(ThongTinChiTiet.this, ThuTienKhachHang.class);
                                            intent.putExtra("ms_mnoi", ttkh.getMs_moinoi());
                                            intent.putExtra("ms_tk", ms_tk);
                                            startActivity(intent);
                                        }

                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(ThongTinChiTiet.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
                                    }


                                }
                            });
                            b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    return;
                                }
                            });
                            b.create().show();

                        /*fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk));
                        //Toast.makeText(NhapSoActivity.this, "Điểm dùng đã được tính tiền", Toast.LENGTH_LONG).show();
                        if (fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk)) != 0) {
                            Intent intent = new Intent(ThongTinChiTiet.this, ThuTienKhachHang.class);
                            intent.putExtra("ms_mnoi", ttkh.getMs_moinoi());
                            intent.putExtra("ms_tk", ms_tk);
                            startActivity(intent);
                        } else {*/
                            //   Toast.makeText(ThongTinChiTiet.this, "Điểm dùng chưa có hóa đơn!", Toast.LENGTH_LONG).show();
                            //}

                        } else {
                            Toast.makeText(ThongTinChiTiet.this, "Bạn chỉ được tính lại tiền trong ngày đọc!", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    Toast.makeText(ThongTinChiTiet.this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //End Thu tien

        btnLuuSDT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (isConnected()) {

                    if (!"".equals(edtSDT.getText().toString())) {
                        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ThongTinChiTiet.this);
                        b.setTitle("Thông báo");
                        b.setMessage("Bạn muốn cập nhật số điện thoại mới: " + edtSDT.getText().toString());
                        b.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                updatePhoneNumber(Integer.parseInt(edtdanhba.getText().toString()), edtSDT.getText().toString());

                                //Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_LONG).show();


                            }
                        });
                        b.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        b.create().show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Số điện thoại không được để trống", Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Không có kết nối internet", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean duocPhepTinhTien(Date today, Date ngay_doc_moi) {
        boolean result = false;
        if (today.compareTo(ngay_doc_moi) > 0) {
            result = false;
        } else if (today.compareTo(ngay_doc_moi) == 0) {
            result = true;
        }

        return result;

    }

    private void updatePhoneNumber(final Integer ms_mnoi, String sdt) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Đang cập nhật...");
        progressDialog.show();

        UpdatePhoneNumber request = new UpdatePhoneNumber(ms_mnoi, sdt);
        BarcodeReaderApiManager.getInstance().waterApi().updatePhoneNumber("" + ms_mnoi, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    commonFuntions.insertCustomerInfoChangeHistory(ms_bd, ms_mnoi, savesdttemp);
                    if (null != edtSDT.getText()) {
                        savesdttemp = edtSDT.getText().toString();
                    }
                    Toast.makeText(ThongTinChiTiet.this, "Cập nhật số điện thoại thành công!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(ThongTinChiTiet.this, "Lỗi cập nhật số điện thoại", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("LOGIN", "update user pwd err : " + t.getMessage());

            }
        });


    }

    public void getthongtinkhachhang(String madb) {
        String ms_tk1 = ms_tk;
        if (isConnected()) {
            String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + madb + "&ms_tk=" + ms_tk1 + "&ms_tuyen=" + ms_tuyen;
            Log.d("GetCustomerInfo", url);
            new HttpAsyncTaskGetCustomerInfo().execute(url);
        } else {
            Toast.makeText(this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();

        }
    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    public void GetPurposeByMoinoi(String ms_mnoi) {
        if (isConnected()) {
            String url = common.URL_API + "/GetPurposeByMoinoi?ms_mnoi=" + ms_mnoi;
            new HttpAsyncTaskGetPurposeByMoinoi().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncTaskGetPurposeByMoinoi extends AsyncTask<String, JSONObject, Void> {
        String mdsd = "";
        JSONArray jsonArrMDSD;
        String temp = "";
        int countmdkd = 0;


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];

            try {
                jsonArrMDSD = ReadJson.readJSonArrayFromURL(url);
                if (jsonArrMDSD.length() > 1) {
                    for (int i = 0; i < jsonArrMDSD.length(); i++) {
                        JSONObject objMDSD = jsonArrMDSD.getJSONObject(i);
                        String mo_ta_dong = objMDSD.getString("mo_ta_dong");
                        if (i == jsonArrMDSD.length() - 1) {
                            temp += mo_ta_dong;
                        } else {
                            temp += mo_ta_dong + " + ";
                        }
                        countmdkd += 1;
                    }
                } else if (jsonArrMDSD.length() == 1) {
                    JSONObject objMDSD = jsonArrMDSD.getJSONObject(0);
                    {
                        String mo_ta_dong = objMDSD.getString("mo_ta_dong");
                        String mo_ta_dong_vuot = objMDSD.getString("mo_ta_dong_vuot");
                        if (!"null".equals(mo_ta_dong_vuot) && !"null".equals(mo_ta_dong)) {
                            temp = mo_ta_dong + " + " + mo_ta_dong_vuot;
                            countmdkd = 2;
                        } else {
                            temp = mo_ta_dong;
                            countmdkd = 1;
                        }
                    }
                }
                mdsd = "(" + countmdkd + ")" + temp;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            edtmdsd.setText(mdsd);
            super.onPostExecute(result);
        }
    }

    public void GetTopNInvoice(String ms_mnoi) {
        if (isConnected()) {
            String url = common.URL_API + "/GetTopNInvoice?ms_mnoi=" + ms_mnoi + "&topNo=3";
            new HttpAsyncTaskGetTopNInvoice().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    private class HttpAsyncTaskGetTopNInvoice extends AsyncTask<String, JSONObject, Void> {

        String strTemp = "";
        int[] stt3t = new int[3];
        int stttong = 0;
        int ms_kieu_tt = 0;
        JSONArray jsonArrayTT3T = new JSONArray();

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];

            try {
                jsonArrayTT3T = ReadJson.readJSonArrayFromURL(url);
                if (jsonArrayTT3T.length() > 0) {
                    for (int i = 0; i < jsonArrayTT3T.length(); i++) {
                        JSONObject objTinhTrang = jsonArrayTT3T.getJSONObject(i);
                        int stt = objTinhTrang.getInt("so_tieu_thu");
                        if (i == 1) {
                            String strms_kieu_tt = common.GetDataToValue(objTinhTrang.getString("ms_kieu_tt"), "");
                            ms_kieu_tt = (int) Double.parseDouble((strms_kieu_tt == "") ? "0" : strms_kieu_tt);
                        }
                        stt3t[i] = stt;
                        strTemp = (strTemp + String.valueOf(stt) + "-");

                    }
                    if (strTemp.length() > 0) {
                        strTemp = strTemp.substring(0, strTemp.length() - 1);
                    }

                    for (int i = 0; i < stt3t.length; i++) {
                        stttong += stt3t[i];
                    }
                }
                // Log.d("strTemp", strTemp);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            txtTT3T.setText(strTemp);
            if (jsonArrayTT3T.length() > 0) {
                txtTTTB.setText(String.valueOf(stttong / jsonArrayTT3T.length()));
                if (ms_kieu_tt == 1) {
                    edtkieuthanhtoan.setText("Bằng tiền mặt");
                } else if (ms_kieu_tt == 2) {
                    edtkieuthanhtoan.setText("Chuyển khoản");
                } else if (ms_kieu_tt == 3) {
                    edtkieuthanhtoan.setText("Hỗn hợp");
                } else if (ms_kieu_tt == 4) {
                    edtkieuthanhtoan.setText("Thu tại quầy");
                } else {
                    edtkieuthanhtoan.setText("");
                }
            }
            super.onPostExecute(result);
        }
    }

    private class HttpAsyncTaskGetCustomerInfo extends AsyncTask<String, JSONObject, Void> {

        String ms_tuyen = "";
        String mo_ta_tuyen = "";
        String ms_mnoi = "";
        String nguoi_thue = "";
        String dia_chi_mnoi = "";
        String chi_so_cu = "";
        String chi_so_moi = "";
        String mo_ta_dong = "";
        String ngay_doc_cu = "";
        String ngay_doc_moi = "";
        String dien_thoai = "";
        String so_tieu_thu = "";
        String so_seri = "";
        String url_image = "";
        String ms_ttrang = "";
        String ghi_chu = "";
        JSONObject jsonKH;
        JSONArray jsonArray = new JSONArray();

        ProgressDialog progressDialog = new ProgressDialog(ThongTinChiTiet.this);

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Nạp dữ liệu...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];


            try {
                jsonArray = ReadJson.readJSonArrayFromURL(url);
                if (jsonArray.length() > 0) {
                    jsonKH = jsonArray.getJSONObject(0);

                    ms_tuyen = jsonKH.getString("ms_tuyen");
                    mo_ta_tuyen = jsonKH.getString("mo_ta_tuyen");
                    ms_mnoi = jsonKH.getString("ms_mnoi");
                    nguoi_thue = jsonKH.getString("nguoi_thue");
                    dia_chi_mnoi = jsonKH.getString("dia_chi_mnoi");
                    chi_so_cu = jsonKH.getString("chi_so_cu");
                    chi_so_moi = jsonKH.getString("chi_so_moi");
                    mo_ta_dong = jsonKH.getString("mo_ta_dong");
                    ngay_doc_cu = jsonKH.getString("ngay_doc_cu");
                    ngay_doc_moi = jsonKH.getString("ngay_doc_moi");
                    dien_thoai = common.GetDataToValue(jsonKH.getString("dien_thoai"), "");
                    savesdttemp = dien_thoai;
                    so_tieu_thu = jsonKH.getString("so_tieu_thu");
                    so_seri = jsonKH.getString("so_seri");

                    url_image = jsonKH.getString("url_image");
                    if ("null".equals(jsonKH.getString("ms_ttrang"))) {
                        ms_ttrang = jsonKH.getString("ms_ttrang");
                    } else {
                        ms_ttrang = common.cat2kitucuoi(jsonKH.getString("ms_ttrang"));
                    }
                    ghi_chu = common.GetDataToValue(jsonKH.getString("ghi_chu"), "");
                    Log.d("ghi_chu", ghi_chu);

                    if (null != ngay_doc_moi && !"".equals(ngay_doc_moi)) {
                        //String strngay_doc_moi = "2019-05-09T15:33:00";
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Ngay_Doc_Moi = format1.parse(common.cat10kitucuoi(ngay_doc_moi));
                            //String today_test = "2019-06-04 10:58:00";
                            To_Day = format1.parse(format1.format(new Date()));
                            //today = format1.parse(common.cat10kitucuoi(today_test));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


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
            try {
                if (jsonArray.length() > 0) {
                    tvTenTuyen.setText("Tuyến: " + common.cat2kitucuoi(ms_tuyen) + " - " + mo_ta_tuyen);
                    edtdanhba.setText(common.cat2kitucuoi(ms_mnoi));
                    edttenkh.setText(String.valueOf(nguoi_thue));
                    edtdiachi.setText(String.valueOf(dia_chi_mnoi));
                    edtSocu.setText(common.cat2kitucuoi(chi_so_cu));
                    edtSomoi.setText(common.cat2kitucuoi(chi_so_moi));
                    edtSDT.setText(dien_thoai);
                    edttieuthu.setText(common.cat2kitucuoi(so_tieu_thu));
                    edtsodongho1.setText(so_seri);
                    // edtmdsd.setText(mo_ta_dong);
                    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    //Date ngaydoccu = (Date) sdf.parse();

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = null;
                    Date date1 = null;
                    //String ngaydocmoi = "";

                    date = format1.parse(common.cat10kitucuoi(ngay_doc_cu));
                    if (!"".equals(ngay_doc_moi) && !"null".equals(ngay_doc_moi)) {
                        date1 = format1.parse(common.cat10kitucuoi(ngay_doc_moi));
                        ngay_doc_moi = format2.format(date1);
                    }

                    String ngaydoccu = format2.format(date);

                    edtngaydoccu.setText(ngaydoccu);
                    edtngaydocmoi.setText(ngay_doc_moi);
                    if (!"null".equals(url_image)) {
                        imgPhoto.setImageBitmap(common.getBitmapFromURL(url_image));
                    } else {
                        imgPhoto.setImageBitmap(null);
                    }
                    if ("null".equals(ms_ttrang) || "1".equals(ms_ttrang) || "".equals(ms_ttrang)) {
                        spinkhongdocduoc.setSelection(getIndexByString(spinkhongdocduoc, listTinhTrang.get(0).getName()));
                    } else {
                        for (int i = 0; i < listTinhTrang.size(); i++) {
                            if (listTinhTrang.get(i).getId() == Integer.parseInt(ms_ttrang)) {
                                spinkhongdocduoc.setSelection(getIndexByString(spinkhongdocduoc, listTinhTrang.get(i).getName()));
                            }
                        }
                    }
                    txtghichugcs.setText(ghi_chu);


                    if (fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(edtdanhba.getText().toString()), Integer.parseInt(ms_tk)) != 0) {
                        btnThuTien.setBackgroundResource(R.drawable.button_style);
                        btnThuTien.setEnabled(true);
                    } else {
                        btnThuTien.setBackgroundColor(Color.GRAY);
                        btnThuTien.setEnabled(false);
                        chkdockotinhtien.setChecked(true);
                    }
                } else {
                    showErrorDialogue(ms_mnoi);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    private int getIndexByString(Spinner spinner, String string) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void showErrorDialogue(final String resultscan) {
        Toast.makeText(this, "Mã danh bạ không tồn tại!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ThongTinChiTiet.this, TabActivity.class);
        startActivity(intent);
    }

    public void GetAllMeterStatus() {
        if (isConnected()) {
            String url = common.URL_API + "/GetAllMeterStatus";
            new HttpAsyncTaskGetAllMeterStatus().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
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
                //Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
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

            ArrayAdapter<TinhtrangDocDongHo> dataAdapter = new ArrayAdapter<TinhtrangDocDongHo>(ThongTinChiTiet.this, android.R.layout.simple_spinner_item, listTinhTrang);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinkhongdocduoc.setAdapter(dataAdapter);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                myOwnBackPress();
            }
        }, 500);
    }

    private void myOwnBackPress() {
        if (!isFinishing()) {
            //super.onBackPressed();
            Intent intent = new Intent(ThongTinChiTiet.this, TabActivity.class);
            startActivity(intent);
        }
    }


}
