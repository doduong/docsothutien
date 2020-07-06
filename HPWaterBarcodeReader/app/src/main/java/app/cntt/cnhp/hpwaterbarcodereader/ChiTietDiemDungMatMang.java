package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.ChineseCalendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import api.BarcodeReaderApiManager;
import model.BarcodeResponse;
import model.DiemDungMatMang;
import model.DongHoNoi;
import model.SoTieuThu;
import model.ThongTinTieuThu;
import model.TinhtrangDocDongHo;
import model.request.MeterUpdateRequest;
import model.request.UpdatePhoneNumber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.CommonFuntions;
import utils.CommonText;
import utils.DBManager;
import utils.ReadJson;
import utils.SharedPref;
import utils.TinhTienFunction;

public class ChiTietDiemDungMatMang extends Activity {

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
    EditText edtsodongho1;
    EditText txtTT3T;
    EditText txtTTTB;
    EditText edtghichu;
    EditText edtnguyennhan;

    TextView txtSocu;
    Button btnNhapSo;
    Button btnLuu;
    //Button btnLuuSDT;
    //EditText edtSDT;
    String ms_tk = "";
    String ms_tuyendoc = "";
    String ms_bd;
    String ms_db;
    Button btntruoc;
    Button btnsau;
    Button btnTruyen;
    Button btnXoa;
    DBManager dbManager;
    Spinner spinnguyennhan;
    CheckBox chkkhongdocduoc;
    CheckBox chkcoanh;
    CheckBox chkdockotinhtien;
    boolean chkcachdoc;
    SharedPref config;
    Integer khongdocduoc = null;
    Integer ms_ttrangdoc = null;
    ArrayAdapter<TinhtrangDocDongHo> dataAdapter;
    ArrayList<TinhtrangDocDongHo> listTinhTrang;
    CommonText common = new CommonText();
    DiemDungMatMang ddmm;
    ArrayList<SoTieuThu> arrstt = new ArrayList<SoTieuThu>();

    // khái báo biến image upload
    ImageView imgPhoto;
    Bitmap selectedBitmap = null;
    String ba1 = null;
    TinhTienFunction fnTinhTien;
    //end
    CommonFuntions cmfuntion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_diem_dung_mat_mang);

        Intent intent = getIntent();
        ms_bd = intent.getStringExtra("ms_bd");
        ms_tk = intent.getStringExtra("ms_tk");
        ms_tuyendoc = intent.getStringExtra("ms_tuyen");
        config = new SharedPref(this);
        cmfuntion = new CommonFuntions();
        fnTinhTien = new TinhTienFunction();

        ddmm = (DiemDungMatMang) intent.getSerializableExtra("ddmm");
        final ArrayList<ThongTinTieuThu> arrTTCT = (ArrayList<ThongTinTieuThu>) intent.getSerializableExtra("arrlist");
        ms_db = String.valueOf(ddmm.getMs_mnoi());


        khaibaobien();
        getThongTinKhachHang();

    }

    public void khaibaobien() {

        tvTenTuyen = (TextView) findViewById(R.id.tentuyenbt);
        edtdanhba = (EditText) findViewById(R.id.edtdanhbabt);
        edttenkh = (EditText) findViewById(R.id.edttennguoithuebt);
        edtdiachi = (EditText) findViewById(R.id.edtDiaChibt);
        edtSocu = (EditText) findViewById(R.id.edtSocubt);
        edtSomoi = (EditText) findViewById(R.id.edtSomoibt);
        edttieuthu = (EditText) findViewById(R.id.edttieuthubt);
        edtngaydoccu = (EditText) findViewById(R.id.edtngaydoccubt);
        edtngaydocmoi = (EditText) findViewById(R.id.edtngaydocmoibt);
        edtsodongho1 = (EditText) findViewById(R.id.edtsodonghobt);
        edtnguyennhan = (EditText) findViewById(R.id.edtnguyennhan);
        /*edtSDT = (EditText) findViewById(R.id.edtSDT);
        btnLuuSDT = (Button) findViewById(R.id.btnLuuSDT);*/
        edtghichu = (EditText) findViewById(R.id.edtghichu);

        txtTT3T = (EditText) findViewById(R.id.txtTT3T);
        txtTTTB = (EditText) findViewById(R.id.txtTBTT);
        edtmdsd = (EditText) findViewById(R.id.edtmdsdkh);
        btnLuu = (Button) findViewById(R.id.btnLuuChiSoMoibt);
        btntruoc = (Button) findViewById(R.id.btntruoc);
        btnsau = (Button) findViewById(R.id.btnsau);
        //btnPrint = (Button) findViewById(R.id.btnPrint);
        chkkhongdocduoc = (CheckBox) findViewById(R.id.chkkhongdocduoc);
        spinnguyennhan = (Spinner) findViewById(R.id.spinkhongdocduoc);
        edtSomoi.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        edttieuthu.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        dbManager = new DBManager(this);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        chkcoanh = (CheckBox) findViewById(R.id.chkcoanh);
        chkdockotinhtien = (CheckBox) findViewById(R.id.chkdockotinhtien) ;

        btnTruyen = (Button) findViewById(R.id.btnTruyen);
        btnXoa = (Button) findViewById(R.id.btnXoa);
        btnTruyen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {


                    if(checkGetCustomerInfoWereRead(ddmm.getMs_mnoi())) {
                        if(chkdockotinhtien.isChecked()==true) {
                            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ChiTietDiemDungMatMang.this);
                            b.setTitle("Thông báo");
                            b.setMessage("Bạn chắc chắn cập nhật điểm dùng KHÔNG TÍNH TIỀN!");
                            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    xulidulieu();
                                }
                            });
                            b.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            b.create().show();

                        }else {
                            xulidulieu();
                        }

                    }else {
                        //Toast.makeText(getApplicationContext(), "Điểm dùng đã được đọc số. Vui lòng chọn 'Xóa'!", Toast.LENGTH_LONG).show();
                        int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());
                        if (ketqua == 1) {
                            Toast.makeText(getBaseContext(), "Điểm dùng đã được đọc số, Xóa dữ liệu bảng tạm thành công!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                            intent.putExtra("ms_tuyen", ms_tuyendoc);
                            intent.putExtra("ms_tk", ms_tk);
                            intent.putExtra("ms_bd", ms_bd);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "Điểm dùng đã được đọc số. Vui lòng chọn 'Xóa'!", Toast.LENGTH_LONG).show();

                        }


                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Không có kết nối internet!", Toast.LENGTH_LONG).show();

                }
            }


        });

        btnXoa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ChiTietDiemDungMatMang.this);
                b.setTitle("Thông báo");
                b.setMessage("Bạn chắc chắn muốn xóa dữ liệu danh bạ " + edtdanhba.getText().toString());
                b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());

                        if (ketqua == 1) {
                            Toast.makeText(getBaseContext(), "Xóa dữ liệu thành công!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getBaseContext(), "Lỗi trong quá trình xóa dữ liệu!", Toast.LENGTH_LONG).show();

                        }
                        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                        intent.putExtra("ms_tuyen", ms_tuyendoc);
                        intent.putExtra("ms_tk", ms_tk);
                        intent.putExtra("ms_bd", ms_bd);
                        startActivity(intent);


                    }
                });
                b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.create().show();
            }
        });

    }




    boolean updateChiSoMoi = false;

    public boolean updateChiSoMoi(final Integer luongtieuthu, final boolean khongdocduoc) {

        if (isConnected()) {

            if (null != selectedBitmap) {
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                byte[] ba = bao.toByteArray();
                ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            } else {
                ba1 = null;
            }

            Integer chisomoi = null;
            Integer luongtieuthu1 = luongtieuthu;
            boolean hasAmountFlag = true;
            if (!khongdocduoc) {
                chisomoi = Integer.parseInt(edtSomoi.getText().toString().trim());
                hasAmountFlag = true;

            } else {
                hasAmountFlag = false;
                chisomoi = null;
                luongtieuthu1 = null;


                //ba1 = null;
            }
            int chisocu = Integer.parseInt(edtSocu.getText().toString());
            int ms_mnoi = Integer.parseInt(edtdanhba.getText().toString());
            int ms_tk1 = Integer.parseInt(ms_tk);
            String readNewDate = edtngaydocmoi.getText().toString();
            String ghi_chu = edtghichu.getText().toString();
            int ms_ttrang = ms_ttrangdoc;
            MeterUpdateRequest request = new MeterUpdateRequest(ms_mnoi, ms_tk1, chisomoi, luongtieuthu1, ms_ttrang, hasAmountFlag, ba1, ghi_chu);
            if(isConnected()) {
                updateMeter(ms_mnoi, request);
            }else {
                Toast.makeText(ChiTietDiemDungMatMang.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
            }


            /*new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (null != selectedBitmap) {
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        byte[] ba = bao.toByteArray();
                        ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                    } else {
                        ba1 = null;
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    Integer chisomoi = null;
                    Integer luongtieuthu1 = luongtieuthu;
                    boolean hasAmountFlag = true;
                    if (!khongdocduoc) {
                        chisomoi = Integer.parseInt(edtSomoi.getText().toString().trim());
                        hasAmountFlag = true;

                    } else {
                        hasAmountFlag = false;
                        chisomoi = null;
                        luongtieuthu1 = null;


                        //ba1 = null;
                    }
                    int chisocu = Integer.parseInt(edtSocu.getText().toString());
                    int ms_mnoi = Integer.parseInt(edtdanhba.getText().toString());
                    int ms_tk1 = Integer.parseInt(ms_tk);
                    String readNewDate = edtngaydocmoi.getText().toString();
                    String ghi_chu = edtghichu.getText().toString();
                    int ms_ttrang = ms_ttrangdoc;
                    MeterUpdateRequest request = new MeterUpdateRequest(ms_mnoi, ms_tk1, chisomoi, luongtieuthu1, ms_ttrang, hasAmountFlag, ba1, ghi_chu);
                    if(isConnected()) {
                        updateMeter(ms_mnoi, request);
                    }else {
                        Toast.makeText(ChiTietDiemDungMatMang.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
                    }


                }
            }.execute();
            */

        } else {

            Toast.makeText(getApplicationContext(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();

        }
        return updateChiSoMoi;
    }

    public void xulidulieu(){
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ChiTietDiemDungMatMang.this);
        b.setTitle("Thông báo");
        b.setMessage("Bạn chắc chắn muốn truyền dữ liệu danh bạ " + edtdanhba.getText().toString() + " về server?");
        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                if (khongdocduoc == 0) {
                    updateChiSoMoi(Integer.parseInt(edttieuthu.getText().toString()), false);
                    capnhatsomoiDBTam(Integer.parseInt(edttieuthu.getText().toString()));
                } else {
                    updateChiSoMoi(null, true);
                }

                /*Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                intent.putExtra("ms_tuyen", ms_tuyendoc);
                intent.putExtra("ms_tk", ms_tk);
                intent.putExtra("ms_bd", ms_bd);
                startActivity(intent);*/


            }
        });
        b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.create().show();
    }

    public void capnhatsomoiDBTam(int luongtieuthu) {

        int ms_mnoi = Integer.parseInt(edtdanhba.getText().toString());
        String ten_kh = edttenkh.getText().toString();
        String dia_chi = edtdiachi.getText().toString();
        int ms_tk1 = Integer.parseInt(ms_tk);
        int ms_ttrang = 1;
        int chi_so_cu = Integer.parseInt(edtSocu.getText().toString());
        int chi_so_moi = Integer.parseInt(edtSomoi.getText().toString());
        String ngay_doc_cu = edtngaydoccu.getText().toString();
        String ngay_doc_moi = edtngaydocmoi.getText().toString();
        int ms_tuyen = Integer.parseInt(ms_tuyendoc);
        DongHoNoi dh = new DongHoNoi(ms_mnoi, ms_tuyen, ten_kh, dia_chi, ms_tk1, ms_ttrang, chi_so_cu, chi_so_moi, ngay_doc_cu, ngay_doc_moi, luongtieuthu);
        try {
            int ketqua = dbManager.nhapso(dh);
            if (ketqua == 1) {
                Toast.makeText(getBaseContext(), "Cập nhật bảng tạm thành công!", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(ChiTietDiemDungMatMang.this);
                b.setTitle("Xác nhận");
                b.setMessage("Lỗi trong quá trình lưu vào bảng tạm");
                b.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        edtSomoi.clearFocus();
                    }
                });
                b.create().show();

            }

        } catch (SQLiteException e) {
            Toast.makeText(getBaseContext(), "Lỗi!", Toast.LENGTH_LONG).show();
        }

        Log.d("SQLite", "Insert ok");

    }


    private boolean updateMeter(int id, MeterUpdateRequest request) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().waterApi().updateMeter(id, request).execute().isSuccessful();
            if(rp){

                Toast.makeText(getApplicationContext(), "Cập nhật CSM thành công! Vui lòng tính tiền trong phần đã đọc!", Toast.LENGTH_SHORT).show();
                int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());
                if (ketqua == 1) {
                    //Toast.makeText(getBaseContext(), "Xóa dữ liệu bảng tạm thành công!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getBaseContext(), "Lỗi XÓA dữ liệu bảng tạm!", Toast.LENGTH_LONG).show();

                }

                Intent intent = new Intent(ChiTietDiemDungMatMang.this, TabActivity.class);
                startActivity(intent);
                updateChiSoMoi = true;
                /*if(chkdockotinhtien.isChecked()==false&&chkkhongdocduoc.isChecked()==false) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {

                            fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ms_db), Integer.parseInt(ms_tk));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if(fnTinhTien.CheckDeleteHD){

                                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ChiTietDiemDungMatMang.this);
                                b.setTitle("Thông báo");
                                b.setMessage("Tính Tiền Lỗi! Bạn có muốn tính tiền lại? ");
                                b.setPositiveButton("TÍNH TIỀN LẠI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if(isConnected()){
                                            fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ms_db), Integer.parseInt(ms_tk));

                                            Toast.makeText(getApplicationContext(), "Cập nhật CSM và Xuất Hóa Đơn Thành Công!", Toast.LENGTH_SHORT).show();
                                            int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());
                                            if (ketqua == 1) {
                                                //Toast.makeText(getBaseContext(), "Xóa dữ liệu bảng tạm thành công!", Toast.LENGTH_LONG).show();

                                            } else {
                                                Toast.makeText(getBaseContext(), "Lỗi XÓA dữ liệu bảng tạm!", Toast.LENGTH_LONG).show();

                                            }

                                            Intent intent = new Intent(ChiTietDiemDungMatMang.this, TabActivity.class);
                                            startActivity(intent);
                                            updateChiSoMoi = true;
                                        }else {
                                            Toast.makeText(ChiTietDiemDungMatMang.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
                                        }
                                        progressDialog.dismiss();



                                    }
                                });
                                b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                b.create().show();
                            }else {
                                Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                                int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());
                                if (ketqua == 1) {
                                    //Toast.makeText(getBaseContext(), "Xóa dữ liệu bảng tạm thành công!", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getBaseContext(), "Lỗi XÓA dữ liệu bảng tạm!", Toast.LENGTH_LONG).show();

                                }
                                progressDialog.dismiss();

                                Intent intent = new Intent(ChiTietDiemDungMatMang.this, TabActivity.class);
                                startActivity(intent);
                                updateChiSoMoi = true;

                            }

                            }
                    }.execute();

                    //fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ms_db), Integer.parseInt(ms_tk));
                    //Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                }*/

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().waterApi().updateMeter(id, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    if(chkdockotinhtien.isChecked()==false) {

                        fnTinhTien.LayThamSoThanhToan(Integer.parseInt(ms_db), Integer.parseInt(ms_tk));
                        Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                    }
                    int ketqua = dbManager.DeleteDiemDungMatMang(edtdanhba.getText().toString());
                    if (ketqua == 1) {
                        Toast.makeText(getBaseContext(), "Xóa dữ liệu bảng tạm thành công!", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getBaseContext(), "Lỗi XÓA dữ liệu bảng tạm!", Toast.LENGTH_LONG).show();

                    }

                    Intent intent = new Intent(ChiTietDiemDungMatMang.this, TabActivity.class);
                    startActivity(intent);
                    updateChiSoMoi = true;

                } else {

                }

            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                Log.i("Meter", "update meter err : " + t.getMessage());

            }
        });
        */
        return updateChiSoMoi;
    }

    public void getThongTinKhachHang() {

        //GetAllMeterStatus();
        tvTenTuyen.setText("Tuyến: " + String.valueOf(ms_tuyendoc) + " - " + config.getString("ten_tuyen", ""));
        int ms_mnoi = ddmm.getMs_mnoi();
        edtdanhba.setText(String.valueOf(ms_mnoi));
        edttenkh.setText(String.valueOf(ddmm.getTenkh()));
        edtdiachi.setText(String.valueOf(ddmm.getDiachi()));
        edtSocu.setText(String.valueOf(ddmm.getChi_so_cu()));
        if (null == ddmm.getChi_so_moi()) {
            edtSomoi.setText("");
        } else {
            edtSomoi.setText(String.valueOf(ddmm.getChi_so_moi()));
        }
        edttieuthu.setText(String.valueOf(ddmm.getSo_tthu()));

        edtmdsd.setText(ddmm.getMdsd());
        //edtSDT.setText(ddmm.getDien_thoai());
        edtsodongho1.setText(ddmm.getSeri_dh());
        txtTT3T.setText(ddmm.getTieu_thu_3t());
        txtTTTB.setText(ddmm.getTieu_thu_tb());

        if (ddmm.getMa_nguyen_nhan() == 1) {
            edtnguyennhan.setText("Đọc bình thường");
        } else {
            edtnguyennhan.setText(ddmm.getTextNguyenNhan());
        }
        khongdocduoc = ddmm.getKhongdocduoc();
        ms_ttrangdoc = ddmm.getMs_ttrang();
        edtghichu.setText(ddmm.getGhi_chu());
        edtngaydoccu.setText(String.valueOf(ddmm.getNgay_doc_cu()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        edtngaydocmoi.setText(sdf.format(new Date()));

        if (ddmm.getCo_chi_so_moi() == 0) {
            chkkhongdocduoc.setChecked(true);
            edttieuthu.setText("");
            edttieuthu.setText("");
        }

        if (null == ddmm.getAnh() || "".equals(ddmm.getAnh())) {
            chkcoanh.setChecked(false);
            selectedBitmap = null;
        } else {
            chkcoanh.setChecked(true);
            byte[] hinhAnh = ddmm.getAnh();
            selectedBitmap = BitmapFactory.decodeByteArray(hinhAnh, 0, hinhAnh.length);
            imgPhoto.setImageBitmap(selectedBitmap);
        }


    }



    public void GetAllMeterStatus() {
        if (isConnected()) {
            String url = common.URL_API + "/GetAllMeterStatus";
            new HttpAsyncTaskGetAllMeterStatus().execute(url);
        } else {
            Toast.makeText(ChiTietDiemDungMatMang.this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
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

            dataAdapter = new ArrayAdapter<TinhtrangDocDongHo>(ChiTietDiemDungMatMang.this, android.R.layout.simple_spinner_item, listTinhTrang);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnguyennhan.setAdapter(dataAdapter);
        }
    }

    public boolean checkGetCustomerInfoWereRead(Integer madb) {

        if (isConnected()) {
            String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + madb + "&ms_tk=" + ms_tk + "&ms_tuyen=" + ms_tuyendoc;
            Log.d("GetCustomerInfo", url);

            String ms_tuyen = "";
            String chi_so_moi = null;
            String so_tthu = null;
            String ngay_doc_moi = null;
            JSONObject jsonKH;
            JSONArray jsonArray = new JSONArray();

            try {
                jsonArray = ReadJson.readJSonArrayFromURL(url);
                if (jsonArray.length() > 0) {
                    jsonKH = jsonArray.getJSONObject(0);
                    chi_so_moi = jsonKH.getString("chi_so_moi");
                    so_tthu = jsonKH.getString("so_tieu_thu");
                    ngay_doc_moi = jsonKH.getString("ngay_doc_moi");

                    if ("null".equals(chi_so_moi) && "null".equals(so_tthu) && "null".equals(ngay_doc_moi)) {
                        return true;
                    }
                } else {
                    Intent intent = new Intent(ChiTietDiemDungMatMang.this, TabActivity.class);
                    intent.putExtra("ms_tuyen", ms_tuyendoc);
                    intent.putExtra("ms_tk", ms_tk);
                    intent.putExtra("ms_bd", ms_bd);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();

        }

        return false;
    }



}
