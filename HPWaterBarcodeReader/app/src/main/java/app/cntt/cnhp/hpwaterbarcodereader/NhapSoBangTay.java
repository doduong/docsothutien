package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import api.BarcodeReaderApiManager;
import model.BarcodeResponse;
import model.DiemDungMatMang;
import model.DongHoNoi;
import model.ThongTinTieuThu;
import model.TinhtrangDocDongHo;
import model.request.CustomerInfoChange;
import model.request.MeterResetHis;
import model.request.MeterUpdateRequest;
import model.request.UpdatePhoneNumber;
import model.request.UpdateUserPwd;
import model.response.ThongTinDaoSo;
import model.response.ThongTinDongHo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.CommonFuntions;
import utils.CommonText;
import utils.DBManager;
import utils.ReadJson;
import utils.SharedPref;
import utils.TinhTienFunction;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class NhapSoBangTay extends Activity {
    TextView tvTenTuyen;
    TextView tvCBTT;
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
    EditText edtkieuthanhtoan;
    Button btnLuu;
    Button btntruoc;
    Button btnsau;
    Button btnLuuSDT;
    EditText edtSDT;
    DBManager dbManager;
    Spinner spinkhongdocduoc;
    CheckBox chkkhongdocduoc;
    CheckBox chkdockotinhtien;
    Integer khongdocduocmatmang = 0;

    //bien toan cuc
    String ms_tk = "";
    String ms_tuyendoc = "";
    String ms_bd;
    Integer ms_db;
    boolean chkcachdoc;
    SharedPref config;
    int makhongdocduoc = 1;
    ThongTinTieuThu ttkh;
    Integer luongtieuthu = null;
    Integer chisomoi = null;
    Integer chisocu = null;
    Integer ms_dh = null;
    Integer so_tthu_cu = 0;
    Integer He_So = 0;
    Integer Kha_Nang_DH = 0;
    CommonText common = new CommonText();
    CommonFuntions cmfuntion;
    ArrayList<TinhtrangDocDongHo> listTinhTrang;
    //end

    // khái báo biến image upload
    private ImageButton btnPhoto;
    ImageView imgPhoto;
    Bitmap selectedBitmap;
    String ba1;

    //end

    //Thu tien
    Button btnThuTien;
    TinhTienFunction fnTinhTien;
    //End Thu tien

    private Uri photoUri;
    private final static int TAKE_PHOTO = 100;
    private final static String PHOTO_URI = "photoUri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhap_so_bang_tay);

        Intent intent = getIntent();
        ttkh = (ThongTinTieuThu) intent.getSerializableExtra("ttct");
        final ArrayList<ThongTinTieuThu> arrTTCT = (ArrayList<ThongTinTieuThu>) intent.getSerializableExtra("arrlist");
        ms_db = Integer.parseInt(ttkh.getMs_moinoi());
        ms_dh = Integer.valueOf(ttkh.getMs_dh());
        config = new SharedPref(this);
        ms_tk = config.getString("ms_tk", "");
        ms_bd = config.getString("ms_bd", "");
        ms_tuyendoc = config.getString("ms_tuyen", "");
        cmfuntion = new CommonFuntions();
        fnTinhTien = new TinhTienFunction();



        khaibaobien();
        getThongTinKhachHang(ms_db);
        GetAllMeterStatus();
        getKhaNangDongHo(ms_dh);
        GetTopNInvoice(ms_db);
        GetPurposeByMoinoi(ms_db);

        //Chức năng upload iamge
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        btnPhoto = (ImageButton) findViewById(R.id.btncamera);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra Camera trong thiết bị
                if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    // Mở camera mặc định
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //phien ban vsmart
                    photoUri = getContentResolver().insert(EXTERNAL_CONTENT_URI, new ContentValues());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    //End Vsmart

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, TAKE_PHOTO);
                    }

                } else {
                    Toast.makeText(getApplication(), "Camera không được hỗ trợ", Toast.LENGTH_LONG).show();
                }


            }
        });
        //phien ban vsmart
        if (savedInstanceState != null) {
            photoUri = (Uri) savedInstanceState.get(PHOTO_URI);
        }
        //End Vsmart

        btnLuuSDT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (isConnected()) {

                    if (!"".equals(edtSDT.getText().toString())) {
                        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
                        b.setTitle("Thông báo");
                        b.setMessage("Bạn muốn cập nhật số điện thoại mới: " + edtSDT.getText().toString());
                        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                updatePhoneNumber(ms_db, edtSDT.getText().toString());
                                //Log update phone


                            }
                        });
                        b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
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

        spinkhongdocduoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                TinhtrangDocDongHo ttd = (TinhtrangDocDongHo) spinkhongdocduoc.getSelectedItem();
                makhongdocduoc = ttd.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
        btnsau.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {

                    ms_db = Integer.parseInt(ttkh.getMs_moinoi());
                    chkdockotinhtien.setChecked(false);
                    for (int i = 0; i < arrTTCT.size() - 1; i++) {
                        if (ttkh.getStt_lo_trinh().equals(arrTTCT.get(i).getStt_lo_trinh())) {
                            ttkh = arrTTCT.get(i + 1);
                            break;
                        }
                    }
                    getKhaNangDongHo(Integer.parseInt(ttkh.getMs_dh()));
                    getThongTinKhachHang(Integer.parseInt(ttkh.getMs_moinoi()));
                    GetAllMeterStatus();
                    GetTopNInvoice(Integer.parseInt(ttkh.getMs_moinoi()));
                    GetPurposeByMoinoi(Integer.parseInt(ttkh.getMs_moinoi()));
                    //fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()),Integer.parseInt(ms_tk));
                    ms_db = Integer.parseInt(ttkh.getMs_moinoi());
                    btnLuu.setBackgroundResource(R.drawable.button_style);
                    btnLuu.setEnabled(true);
                    btnThuTien.setBackgroundColor(Color.GRAY);
                    btnThuTien.setEnabled(false);

                } else {
                    Toast.makeText(getApplicationContext(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
                }
            }


        });

        btntruoc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {

                    ms_db = Integer.parseInt(ttkh.getMs_moinoi());
                    chkdockotinhtien.setChecked(false);
                    for (int i = 1; i < arrTTCT.size(); i++) {
                        if (ttkh.getStt_lo_trinh().equals(arrTTCT.get(i).getStt_lo_trinh())) {
                            ttkh = arrTTCT.get(i - 1);
                            break;
                        }
                    }
                    getKhaNangDongHo(Integer.parseInt(ttkh.getMs_dh()));

                    getThongTinKhachHang(Integer.parseInt(ttkh.getMs_moinoi()));
                    GetAllMeterStatus();
                    GetTopNInvoice(Integer.parseInt(ttkh.getMs_moinoi()));
                    GetPurposeByMoinoi(Integer.parseInt(ttkh.getMs_moinoi()));
                    ms_db = Integer.parseInt(ttkh.getMs_moinoi());
                    btnLuu.setBackgroundResource(R.drawable.button_style);
                    btnLuu.setEnabled(true);
                    btnThuTien.setBackgroundColor(Color.GRAY);
                    btnThuTien.setEnabled(false);
                } else {

                    Toast.makeText(getApplicationContext(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();


                }
            }


        });

    }

    //phien ban vsmart
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PHOTO_URI, photoUri);
    }
    //end vsmart

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TabActivity.class);
        intent.putExtra("ms_tuyen", ms_tuyendoc);
        intent.putExtra("ms_tk", ms_tk);
        intent.putExtra("ms_bd", ms_bd);
        startActivity(intent);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if ((requestCode == TAKE_PHOTO || requestCode == 200) && resultCode == Activity.RESULT_OK) {
            /*if (null != data) {
                if (null != data.getExtras()) {
                    selectedBitmap = (Bitmap) data.getExtras().get("data");
                    imgPhoto.setImageBitmap(selectedBitmap);
                    selectedBitmap =getResizedBitmap( selectedBitmap, 600);




                }
            }*/

            //Phiên bản vsmart
            try {
            InputStream stream = getContentResolver().openInputStream(photoUri);
            selectedBitmap = getResizedBitmap(BitmapFactory.decodeStream(stream), 450);
            imgPhoto.setImageBitmap(selectedBitmap);
            getContentResolver().delete(photoUri, null, null);
            stream.close();
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //End phien ban Vsmart

        }

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
        //return Bitmap.createBitmap(image, 0,0, width, height);
    }

    public void writeBitmapToMemory(File file, Bitmap bitmap) {
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();


        }

    }

    // end funtion image upoad

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
        txtTT3T = (EditText) findViewById(R.id.txtTT3T);
        txtTTTB = (EditText) findViewById(R.id.txtTBTT);
        edtmdsd = (EditText) findViewById(R.id.edtmdsdkh);
        btnLuu = (Button) findViewById(R.id.btnLuuChiSoMoibt);
        btntruoc = (Button) findViewById(R.id.btntruoc);
        btnLuuSDT = (Button) findViewById(R.id.btnLuuSDT);
        btnsau = (Button) findViewById(R.id.btnsau);
        edtghichu = (EditText) findViewById(R.id.edtghichu);
        //  btnPrint = (Button) findViewById(R.id.btnPrint);
        chkkhongdocduoc = (CheckBox) findViewById(R.id.chkcachdoc);
        chkdockotinhtien = (CheckBox) findViewById(R.id.chkdockotinhtien);
        spinkhongdocduoc = (Spinner) findViewById(R.id.spinkhongdocduoc);
        edtSDT = (EditText) findViewById(R.id.edtSDT);
        edtSomoi.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        edttieuthu.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        edtSDT.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        dbManager = new DBManager(this);

        //Thu tien
        btnThuTien = (Button) findViewById(R.id.btnThuTien);
        btnThuTien.setBackgroundColor(Color.GRAY);
        btnThuTien.setEnabled(false);
        edtkieuthanhtoan = (EditText) findViewById(R.id.edtkieuthanhtoan);
        //End Thu tien

        //Nhà máy Minh Đức
        //btnThuTien.setText("Xem Hóa Đơn");
        //End kết thúc

        chkkhongdocduoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkkhongdocduoc.isChecked()) {
                    khongdocduocmatmang = 1;
                    edtSomoi.setText("");
                    edttieuthu.setText("");
                    edtSomoi.setEnabled(false);
                    edttieuthu.setEnabled(false);
                    spinkhongdocduoc.setEnabled(true);

                } else {
                    khongdocduocmatmang = 0;
                    //spinkhongdocduoc.setSelection(7);
                    spinkhongdocduoc.setSelection(0);
                    spinkhongdocduoc.setEnabled(false);
                    edtSomoi.setEnabled(true);
                    edttieuthu.setEnabled(true);
                }
            }
        });


        btnLuu.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                //if(checkGetCustomerInfoWereRead(ms_db, Integer.parseInt(ms_tk),  Integer.parseInt(ms_tuyendoc))) {

                //check giao diện trước
                if (coNguyenNhanVaAnh()) {
                    if (chkdockotinhtien.isChecked() == true) {
                        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
                        b.setTitle("Thông báo");
                        b.setMessage("Bạn chắc chắn cập nhật điểm dùng KHÔNG TÍNH TIỀN!");
                        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                xulyLuuDuLieu();
                            }
                        });
                        b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        b.create().show();

                    } else {
                        xulyLuuDuLieu();
                    }

                } else {
                    canh_bao_khong_co_nguyen_nhan_va_anh();
                }
                //}else {
                //    Toast.makeText(NhapSoBangTay.this, "Điểm dùng đã được đọc số!", Toast.LENGTH_LONG).show();
                //}

            }


        });


        edtSomoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!"".equals(edtSomoi.getText().toString()) && !"".equals(edtSocu.getText().toString())) {
                    luongtieuthu = tinhLuongTieuThu(Integer.parseInt(edtSomoi.getText().toString()), Integer.parseInt(edtSocu.getText().toString()));
                } else {
                    edttieuthu.setText("");
                }
            }
        });

        //Thu tienbt
        btnThuTien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected()) {
                    //Log.d("SHD1_TT", ""+fnTinhTien.So_Hoa_Don);
                    //if (fnTinhTien.laySoHoaDonByMnoiTky(ms_db, Integer.parseInt(ms_tk)) != 0) {
                    if (fnTinhTien.So_Hoa_Don != 0) {
                        Log.d("SHD1", "" + fnTinhTien.So_Hoa_Don);
                        Intent intent = new Intent(NhapSoBangTay.this, ThuTienKhachHang.class);
                        intent.putExtra("ms_mnoi", ms_db.toString());
                        intent.putExtra("ms_tk", ms_tk.toString());
                        startActivity(intent);
                    } else {
                        //fnTinhTien.LayThamSoThanhToan(ms_db, Integer.parseInt(ms_tk));
                        //Log.d("SHD2", ""+fnTinhTien.So_Hoa_Don);
                        //if (fnTinhTien.laySoHoaDonByMnoiTky(Integer.parseInt(ttkh.getMs_moinoi()), Integer.parseInt(ms_tk)) != 0) {
                        /*if (fnTinhTien.So_Hoa_Don != 0) {
                            Log.d("SHD2", ""+fnTinhTien.So_Hoa_Don);
                            Intent intent = new Intent(NhapSoBangTay.this, ThuTienKhachHang.class);
                            intent.putExtra("ms_mnoi", ms_db.toString());
                            intent.putExtra("ms_tk", ms_tk.toString());
                            startActivity(intent);
                        } else {*/
                        Toast.makeText(NhapSoBangTay.this, "Điểm dùng chưa có hóa đơn!", Toast.LENGTH_LONG).show();
                        //}
                    }
                } else {
                    Toast.makeText(NhapSoBangTay.this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean checkGetCustomerInfoWereRead(Integer madb, Integer ms_tk, Integer ms_tuyendoc) {


        String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + madb + "&ms_tk=" + ms_tk + "&ms_tuyen=" + ms_tuyendoc;
        Log.d("checkGetCustomerInfo", url);

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
                Log.d("so_tthu_check", "" + so_tthu);
                if ("null".equals(chi_so_moi) && "null".equals(so_tthu) && "null".equals(ngay_doc_moi)) {
                    return true;
                }
            } else {
                Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
                intent.putExtra("ms_tuyen", ms_tuyendoc);
                intent.putExtra("ms_tk", ms_tk);
                intent.putExtra("ms_bd", ms_bd);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void canh_bao_khong_co_nguyen_nhan_va_anh() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
        b.setTitle("Cảnh báo");
        b.setMessage("Bạn phải chụp ảnh trước!");
        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.create().show();

    }

    private void xulyLuuDuLieu() {
        //check có phải không đọc được
        if (chkkhongdocduoc.isChecked() == false) {
            //check da nhap chi so moi
            if (!"".equals(edtSomoi.getText().toString())) {
                chisomoi = Integer.parseInt(edtSomoi.getText().toString());
                chisocu = Integer.parseInt(edtSocu.getText().toString());
                luongtieuthu = tinhLuongTieuThu(chisomoi, chisocu);
                //15042019_CanhBaoTieuThu
                int tieuthucanhbao = 0;
                if (chisomoi >= chisocu) {
                    tieuthucanhbao = (chisomoi - chisocu) * He_So + so_tthu_cu;
                } else {
                    tieuthucanhbao = (chisomoi + Kha_Nang_DH - chisocu + 1) * He_So + so_tthu_cu;
                }
                if (tieuthucanhbao != luongtieuthu || chisomoi > Kha_Nang_DH) {

                    String msg = "";
                    if (chisomoi >= chisocu) {
                        msg = "Lượng tiêu thụ: " + tieuthucanhbao + "=(" + chisomoi + " - " + chisocu + ")* " + He_So + " + " + so_tthu_cu + ")" + ". Lượng tiêu thụ hiển thị: " + luongtieuthu + " => Sai. Vui lòng kiểm tra lại kết nối, nhập lại chỉ số mới và tính lại số tiêu thụ!";
                    } else {
                        msg = "Lượng tiêu thụ: " + tieuthucanhbao + "=(" + chisomoi + "+" + Kha_Nang_DH + " - " + chisocu + ")* " + He_So + " + " + so_tthu_cu + ")" + ". Lượng tiêu thụ hiển thị: " + luongtieuthu + " => Sai. Vui lòng kiểm tra kết nối, nhập lại chỉ số mới và tính lại số tiêu thụ!";
                    }
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NhapSoBangTay.this);
                    alertDialogBuilder.setTitle("Cảnh báo");
                    alertDialogBuilder.setMessage(msg);
                    alertDialogBuilder.setIcon(R.mipmap.ic_alert);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("ĐỒNG Ý",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Toast.makeText(NhapSoBangTay.this, "Nhập lại chỉ số mới và số tiêu thụ!", Toast.LENGTH_LONG).show();

                                        }
                                    })
                            .setNegativeButton("HỦY",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.WHITE);
                    nbutton.setBackgroundResource(R.drawable.button_style);
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.WHITE);
                    pbutton.setBackgroundResource(R.drawable.button_style);


                } else {
                    if (null != luongtieuthu && !"".equals(edttieuthu.getText().toString())) {
                        Log.d("edttieuthu", edttieuthu.getText().toString());
                        if ((chisomoi > chisocu) || ((luongtieuthu !=0) && (chisomoi.equals(chisocu)))) {

                            if (!canhbaochenhlech(luongtieuthu)) {
                                cap_nhat_co_anh_co_nguyen_nhan_co_chi_so_moi();
                            } else {
                                cap_nhat_canh_bao_chi_so_moi_vuot_nguong();
                            }
                        } else if (luongtieuthu == 0) {
                            canh_bao_cap_nhat_luong_tieu_thu_bang_khong();

                        } else {

                            cap_nhat_luong_tieu_thu_nho_hon_khong_dh_quay_vong(luongtieuthu, ms_dh, ms_db, chisocu, chisomoi);
                        }

                    } else {
                        edttieuthu.setEnabled(false);
                        Toast.makeText(NhapSoBangTay.this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
                    }
                    ////15042019_CanhBaoTieuThu_End
                }
            } else {
                canh_bao_chi_so_moi_de_trong();
            }
        } else {

            if (getIndexKhongQuetDuocMV(spinkhongdocduoc) == spinkhongdocduoc.getSelectedItemPosition() || 7 == spinkhongdocduoc.getSelectedItemPosition()) {
                // Log.d("nguyen nhan", getIndexKhongQuetDuocMV(spinkhongdocduoc))
                Toast.makeText(NhapSoBangTay.this, "Bạn phải chọn nguyên nhân khác!", Toast.LENGTH_LONG).show();
            } else {
                cap_nhat_dd_khong_doc_duoc();
            }
        }
    }

    public void cap_nhat_dd_khong_doc_duoc() {
        if (isConnected()) {
            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
            b.setTitle("Thông báo");
            b.setMessage("Bạn có muốn cập nhật điểm dùng không đọc được!");
            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    capnhatDiemDungKhongDocDuoc();


                }
            });
            b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            b.create().show();


        } else {

            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
            b.setTitle("Cảnh báo");
            b.setMessage("Mất kết nối internet! Cập nhật điểm dùng vào bảng tạm");
            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    capnhatDiemDungMatMang(luongtieuthu);


                }
            });
            b.create().show();


        }

    }

    private void updatePhoneNumber(Integer ms_mnoi, String sdt) {

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
                    //ghi log sua sđt

                    cmfuntion.insertCustomerInfoChangeHistory(ms_bd, ms_db, ttkh.getDien_thoai());
                    if (null != edtSDT.getText()) {
                        ttkh.setDien_thoai(edtSDT.getText().toString());
                    }
                    //end
                    Toast.makeText(NhapSoBangTay.this, "Cập nhật số điện thoại thành công!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(NhapSoBangTay.this, "Lỗi cập nhật số điện thoại", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("LOGIN", "update user pwd err : " + t.getMessage());

            }
        });


    }

    public void cap_nhat_co_anh_co_nguyen_nhan_co_chi_so_moi() {
        if (isConnected()) {

            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
            b.setTitle("Thông báo");
            b.setMessage("Bạn muốn cập nhật chỉ số mới: " + String.valueOf(chisomoi) + ", Lượng tiêu thụ: " + luongtieuthu);
            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    capnhatsomoiDBTam(luongtieuthu);
                    updateChiSoMoi(luongtieuthu, false);
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


        } else {
            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
            b.setTitle("Cảnh báo");
            b.setMessage("Mất kết nối internet! Cập nhật điểm dùng vào bảng tạm");
            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    capnhatDiemDungMatMang(luongtieuthu);


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
    }

    public void cap_nhat_canh_bao_chi_so_moi_vuot_nguong() {

        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
        b.setTitle("Cảnh báo");
        b.setMessage("Lượng tiêu thụ vượt quá ngưỡng cảnh báo. Bạn có muốn tiếp tục");
        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                cap_nhat_co_anh_co_nguyen_nhan_co_chi_so_moi();
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

    public void canh_bao_cap_nhat_luong_tieu_thu_bang_khong() {
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
        b.setTitle("Cảnh báo");
        b.setMessage("Lượng tiêu thụ bằng 0. Bạn chắc chắn muốn nhập?");
        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!canhbaochenhlech(luongtieuthu)) {
                    cap_nhat_co_anh_co_nguyen_nhan_co_chi_so_moi();
                } else {
                    cap_nhat_canh_bao_chi_so_moi_vuot_nguong();
                }

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

    public void cap_nhat_luong_tieu_thu_nho_hon_khong_dh_quay_vong(final Integer luongtieuthu, final Integer ms_dh, final Integer ms_mnoi, final Integer chisocu, final Integer chisomoi) {
        //updateChiSoMoi(luongtieuthu, false);

        if (isConnected()) {
            edttieuthu.setText(String.valueOf(luongtieuthu));
            LayoutInflater li = LayoutInflater.from(NhapSoBangTay.this);
            View promptsView = li.inflate(R.layout.alert_so_tieu_thu, null);
            android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
            b.setView(promptsView);
            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);
            b.setTitle("Cảnh báo");
            b.setIcon(R.mipmap.ic_alert);
            b.setMessage("CSM nhỏ hơn CSC - Đồng hồ quay vòng! Có phải bạn muốn lưu chỉ số mới: " + chisomoi + ". Lượng tiêu thụ: " + luongtieuthu + ". Nhập '1234' để xác nhận đồng hồ quay vòng");
            b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if ("1234".equals(userInput.getText().toString())) {
                        if (!canhbaochenhlech(luongtieuthu)) {
                            cap_nhat_co_anh_co_nguyen_nhan_co_chi_so_moi();
                        } else {
                            cap_nhat_canh_bao_chi_so_moi_vuot_nguong();
                        }

                        BarcodeReaderApiManager.getInstance().waterApi().GetWaterMeterResetNumber(ms_dh).enqueue(new Callback<ThongTinDaoSo>() {
                            @Override
                            public void onResponse(Call<ThongTinDaoSo> call, retrofit2.Response<ThongTinDaoSo> response) {
                                Integer so_lan_dao_so = null;
                                if (response.code() == 404) {
                                    so_lan_dao_so = 1;
                                } else {

                                    so_lan_dao_so = response.body().getSo_lan_dao_so();
                                    if (so_lan_dao_so == null) {
                                        so_lan_dao_so = 1;

                                    } else {
                                        so_lan_dao_so = so_lan_dao_so + 1;
                                    }
                                }
                                //Insert lịch sử đảo số
                                //Integer ms_dh, Integer ms_mnoi , Integer chisodh, Integer solandaoso
                                insertMeterResetHis(ms_dh, ms_mnoi, chisomoi, so_lan_dao_so);


                            }

                            @Override
                            public void onFailure(Call<ThongTinDaoSo> call, Throwable t) {
                                Log.i("insertMeterResetHis", "update user pwd err : " + t.getMessage());
                            }
                        });

                    } else {
                        Toast.makeText(NhapSoBangTay.this, "Chưa nhập mã xác nhận! Không cập nhật chỉ số quay vòng", Toast.LENGTH_LONG).show();
                    }
                }
            });
            b.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                @Override

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            //b.create().show();
            AlertDialog alert = b.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.WHITE);
            nbutton.setBackgroundResource(R.drawable.button_style);
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            //pbutton.setBackgroundColor(Color.RED);
            pbutton.setTextColor(Color.WHITE);
            pbutton.setBackgroundResource(R.drawable.button_style);

        } else {
            Toast.makeText(getApplicationContext(), "Không có kết nối internet! Không cập nhật chỉ số quay vòng", Toast.LENGTH_LONG).show();
        }
    }

    public void canh_bao_chi_so_moi_de_trong() {
        //Log.d("btnLuu:", "không checkbok- khong nhap chi so moi");
        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
        b.setTitle("Cảnh báo");
        b.setMessage("Bạn phải nhập chỉ số mới!");
        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                edtSomoi.requestFocus();

            }
        });
        b.create().show();
    }

    private void insertMeterResetHis(Integer ms_dh, Integer ms_mnoi, Integer chisodh, Integer solandaoso) {


        MeterResetHis request = new MeterResetHis(ms_dh, ms_mnoi, chisodh, solandaoso);
        BarcodeReaderApiManager.getInstance().waterApi().insertMeterResetHistory(request).enqueue(new Callback<BarcodeResponse>() {

            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NhapSoBangTay.this, "Cập nhật lịch sử đồng hồ thành công!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(NhapSoBangTay.this, "Cập nhật lịch sử đồng hồ ko thành công!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                Log.i("MeterResetHis", "update user pwd err : " + t.getMessage());

            }
        });
    }


    /*
     *  ms_mnoi
        ms_tk
        chiSoMoi
        soTieuThu
        readNewDate - định dạng ngày tháng truyền xuống
        hasAmountFlag
        ms_ttrang
        Base64Image
    * */
    public void updateChiSoMoi(final Integer luongtieuthu, final boolean khongdocduoc) {
        if (isConnected()) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                    byte[] ba = bao.toByteArray();
                    ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

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
                    int ms_mnoi = Integer.parseInt(edtdanhba.getText().toString());
                    int ms_tk1 = Integer.parseInt(ms_tk);
                    String ghi_chu = edtghichu.getText().toString();
                    int ms_ttrang = makhongdocduoc;
                    MeterUpdateRequest request = new MeterUpdateRequest(ms_mnoi, ms_tk1, chisomoi, luongtieuthu1, ms_ttrang, hasAmountFlag, ba1, ghi_chu);
                    //if (checkGetCustomerInfoWereRead(ms_db, ms_tk1, Integer.parseInt(ms_tuyendoc))) {
                    if (isConnected()) {
                        updateMeter(ms_mnoi, request);
                    } else {
                        Toast.makeText(NhapSoBangTay.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
                    }
                    //} else {
                    //    Toast.makeText(getApplicationContext(), "Điểm dùng đã được đọc số!", Toast.LENGTH_SHORT).show();
                    //}

                }
            }.execute();

        } else {
            Toast.makeText(getApplicationContext(), "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    public int flag = 0;

    private void updateMeter(int id, MeterUpdateRequest request) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        flag = 0;
        final ProgressDialog progressDialog = new ProgressDialog(NhapSoBangTay.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Tính tiền....");
        progressDialog.show();

        try {
            boolean rp = BarcodeReaderApiManager.getInstance().waterApi().updateMeter(id, request).execute().isSuccessful();
            if (rp) {
                if (chkdockotinhtien.isChecked() == false && chkkhongdocduoc.isChecked() == false) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (fnTinhTien.laySoHoaDonByMnoiTky(ms_db, Integer.parseInt(ms_tk)) == 0) {
                                fnTinhTien.LayThamSoThanhToan(ms_db, Integer.parseInt(ms_tk));
                                flag = 1;
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (fnTinhTien.CheckDeleteHD) {
                                progressDialog.dismiss();
                                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(NhapSoBangTay.this);
                                b.setTitle("Thông báo");
                                b.setMessage("Tính Tiền Lỗi! Bạn có muốn tính tiền lại? ");
                                b.setPositiveButton("TÍNH TIỀN LẠI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        if (isConnected()) {
                                            if (fnTinhTien.laySoHoaDonByMnoiTky(ms_db, Integer.parseInt(ms_tk)) == 0) {
                                                fnTinhTien.LayThamSoThanhToan(ms_db, Integer.parseInt(ms_tk));
                                                Toast.makeText(getApplicationContext(), "Cập nhật CSM và Xuất Hóa Đơn Thành Công!", Toast.LENGTH_SHORT).show();
                                            }

                                            btnThuTien.setBackgroundResource(R.drawable.button_style);
                                            btnThuTien.setEnabled(true);
                                            btnLuu.setBackgroundColor(Color.GRAY);
                                            btnLuu.setEnabled(false);
                                        } else {
                                            Toast.makeText(NhapSoBangTay.this, "Không có kết nối Intenet!", Toast.LENGTH_LONG).show();
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
                            } else {

                                if (flag == 1) {
                                    Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();
                                }
                                btnThuTien.setBackgroundResource(R.drawable.button_style);
                                btnThuTien.setEnabled(true);
                                btnLuu.setBackgroundColor(Color.GRAY);
                                btnLuu.setEnabled(false);
                                progressDialog.dismiss();
                            }

                        }
                    }.execute();

                } else if (chkdockotinhtien.isChecked() == true && chkkhongdocduoc.isChecked() == false) {
                    Toast.makeText(getApplicationContext(), "Cập nhật thành công điểm dùng KHÔNG TÍNH TIỀN!", Toast.LENGTH_SHORT).show();
                    btnLuu.setBackgroundColor(Color.GRAY);
                    btnLuu.setEnabled(false);
                    progressDialog.dismiss();
                } else if (chkkhongdocduoc.isChecked() == true) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Cập nhật thành công điểm dùng KHÔNG ĐỌC ĐƯỢC!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
                    startActivity(intent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().waterApi().updateMeter(id, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(getApplicationContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    //Fix_07052019
                    if(chkdockotinhtien.isChecked()==false&&chkkhongdocduoc.isChecked()==false) {

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {

                                fnTinhTien.LayThamSoThanhToan(ms_db, Integer.parseInt(ms_tk));
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();

                                btnThuTien.setBackgroundResource(R.drawable.button_style);
                                btnThuTien.setEnabled(true);
                                btnLuu.setBackgroundColor(Color.GRAY);
                                btnLuu.setEnabled(false);
                                progressDialog.dismiss();

                            }
                        }.execute();
                        /*fnTinhTien.LayThamSoThanhToan(ms_db, Integer.parseInt(ms_tk));
                        Toast.makeText(getApplicationContext(), "Cập nhật CSM và xuất hóa đơn thành công!", Toast.LENGTH_SHORT).show();

                        btnThuTien.setBackgroundResource(R.drawable.button_style);
                        btnThuTien.setEnabled(true);
                        btnLuu.setBackgroundColor(Color.GRAY);
                        btnLuu.setEnabled(false);*/
                    /*}else if(chkdockotinhtien.isChecked()==true && chkkhongdocduoc.isChecked()==false){
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công điểm dùng KHÔNG TÍNH TIỀN!", Toast.LENGTH_SHORT).show();
                        btnLuu.setBackgroundColor(Color.GRAY);
                        btnLuu.setEnabled(false);
                        progressDialog.dismiss();
                    }else if (chkkhongdocduoc.isChecked()==true){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công điểm dùng KHÔNG ĐỌC ĐƯỢC!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
                        startActivity(intent);
                    }
                    //End_Fix_07052019

                } else {

                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                Log.i("Meter", "update meter err : " + t.getMessage());

            }
        });*/


    }


    public void getThongTinKhachHang(Integer madb) {

        if (isConnected()) {
            String url = common.URL_API + "/GetCustomerInfo?ms_mnoi=" + madb + "&ms_tk=" + ms_tk + "&ms_tuyen=" + ms_tuyendoc;
            Log.d("GetCustomerInfo", url);
            new HttpAsyncTaskGetCustomerInfo().execute(url);
        } else {
            Toast.makeText(this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();

        }
    }


    public void showErrorDialogue(final Integer resultscan) {
        Toast.makeText(this, "Mã danh bạ không tồn tại!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chuadoc) {
            Intent intent = new Intent(NhapSoBangTay.this, Login.class);
            intent.putExtra("ms_tuyen", ms_tuyendoc);
            intent.putExtra("ms_tk", ms_tk);
            intent.putExtra("ms_bd", ms_bd);
            startActivity(intent);

        } else if (item.getItemId() == R.id.danhbadiemdung) {
            Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
            intent.putExtra("ms_tuyen", ms_tuyendoc);
            intent.putExtra("ms_tk", ms_tk);
            intent.putExtra("ms_bd", ms_bd);
            //  intent.putExtra("tongsodiemdung",String.valueOf(tongsoluong) );
            startActivity(intent);

        } else if (item.getItemId() == R.id.tuyendoc) {
            Intent intent = new Intent(NhapSoBangTay.this, TuyenDoc.class);
            intent.putExtra("ms_tuyen", ms_tuyendoc);
            intent.putExtra("ms_tk", ms_tk);
            String ms_bd1 = config.getString("app_username", "");
            intent.putExtra("ms_bd", ms_bd1);
            startActivity(intent);

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public byte[] ConverttoArrayByte(ImageView img) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
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
                //Toast.makeText(getBaseContext(), "Cập nhật bảng tạm thành công!", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(NhapSoBangTay.this);
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


    public boolean coNguyenNhanVaAnh() {
        TinhtrangDocDongHo ttd = (TinhtrangDocDongHo) spinkhongdocduoc.getSelectedItem();

        if (imgPhoto.getDrawable() == null) {
            //ttd.getId() == 1 ||
            //Log.d("savedFileDestination", savedFileDestination.getName());
            return false;
        }
        return true;

    }


    public void capnhatDiemDungMatMang(Integer luongtieuthu) {
        String textnguyennhan = "";
        String dien_thoai = "";
        String so_tthu = null;
        if (luongtieuthu != null) {
            so_tthu = String.valueOf(luongtieuthu);
        }
        String chi_so_moi = null;
        if (!"".equals(edtdanhba.getText().toString())) {
            int ms_moinoi = Integer.parseInt(edtdanhba.getText().toString());

            String tenkh = edttenkh.getText().toString();
            String diachi = edtdiachi.getText().toString();
            int ms_tk1 = Integer.parseInt(ms_tk);
            int ms_tt = makhongdocduoc;
            String chi_so_cu = edtSocu.getText().toString();
            //String textNguyenNhan = spinkhongdocduoc.getTe
            if (!"".equals(edtSomoi.getText().toString())) {
                chi_so_moi = edtSomoi.getText().toString();
            }
            String ngay_doc_cu = edtngaydoccu.getText().toString();
            String ngay_doc_moi = edtngaydocmoi.getText().toString();
            //int so_tthu = chi_so_moi - chi_so_cu;
            int co_chi_so_moi = 0;
            if (chkkhongdocduoc.isChecked()) {
                co_chi_so_moi = 0;
            } else {
                co_chi_so_moi = 1;
            }
            int ms_tuyen = Integer.parseInt(ms_tuyendoc);
            byte[] anh = null;
            if (null != imgPhoto.getDrawable()) {
                anh = ConverttoArrayByte(imgPhoto);
            }
            String tt3t = txtTT3T.getText().toString();
            String tttb = txtTTTB.getText().toString();
            String seri_dh = edtsodongho1.getText().toString();
            String mdsd = edtmdsd.getText().toString();
            textnguyennhan = spinkhongdocduoc.getSelectedItem().toString();
            dien_thoai = edtSDT.getText().toString();
            int ma_nn = makhongdocduoc;
            String ghi_chu = edtghichu.getText().toString();

            DiemDungMatMang ddmm = new DiemDungMatMang(ms_moinoi, tenkh, diachi, ms_tk1, ms_tt, chi_so_cu, chi_so_moi,
                    ngay_doc_cu, ngay_doc_moi, so_tthu, ms_tuyen, co_chi_so_moi, tt3t, tttb, seri_dh, mdsd, ma_nn, anh, textnguyennhan, dien_thoai, khongdocduocmatmang, ghi_chu);

            int ketqua = dbManager.ThemDiemDungMatMang(ddmm);

            if (ketqua == 1) {
                Toast.makeText(getBaseContext(), "Cập nhật điểm dùng mất mạng! Truyền về trong Tab MẤT KẾT NỐI!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getBaseContext(), "Lỗi cập nhật bảng tạm!", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getBaseContext(), "Danh bạ điểm dùng không được để trống", Toast.LENGTH_LONG).show();
        }

    }

    public void capnhatDiemDungKhongDocDuoc() {
        updateChiSoMoi(luongtieuthu, true);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public boolean canhbaochenhlech(Integer luongtieuthu) {
        int tyledanhgia = config.getInt("tyledanhgia", 0);
        int TLDG = config.getInt("TLDG", 0);
        int CLTD = config.getInt("CLTD", 0);
        int trungbinh = 0;
        if ("".equals(txtTTTB.getText().toString()) || "null".equals(txtTTTB.getText().toString())) {
            trungbinh = 0;
        } else {
            trungbinh = Integer.parseInt(txtTTTB.getText().toString());
        }
        int giatrituyetdoi = Math.abs(luongtieuthu - trungbinh);
        if (tyledanhgia == 1) {
            if (giatrituyetdoi > (TLDG * trungbinh / 100)) {
                return true;
            }
        } else {
            if (giatrituyetdoi > CLTD) {
                return true;
            }
        }

        return false;
    }

    public void GetAllMeterStatus() {
        if (isConnected()) {
            String url = common.URL_API + "/GetAllMeterStatus";
            new HttpAsyncTaskGetAllMeterStatus().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    public void GetTopNInvoice(Integer ms_mnoi) {
        if (isConnected()) {
            String url = common.URL_API + "/GetTopNInvoice?ms_mnoi=" + ms_mnoi + "&topNo=3";
            new HttpAsyncTaskGetTopNInvoice().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    public void GetPurposeByMoinoi(Integer ms_mnoi) {
        if (isConnected()) {
            String url = common.URL_API + "/GetPurposeByMoinoi?ms_mnoi=" + ms_mnoi;
            Log.d("GetPurposeByMoinoi", url);
            new HttpAsyncTaskGetPurposeByMoinoi().execute(url);
        } else {
            Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
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
        String dien_thoai = "";
        String so_seri = "";
        String so_tieu_thu = "";
        Integer ms_dh = null;
        Boolean co_chi_so = false;
        String url_image = null;


        JSONObject jsonKH;
        JSONArray jsonArray = new JSONArray();
        ProgressDialog progressDialog = new ProgressDialog(NhapSoBangTay.this);

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
                    so_tieu_thu = jsonKH.getString("so_tieu_thu");
                    mo_ta_dong = jsonKH.getString("mo_ta_dong");
                    ngay_doc_cu = jsonKH.getString("ngay_doc_cu");
                    dien_thoai = jsonKH.getString("dien_thoai");
                    so_seri = jsonKH.getString("so_seri");

                    ms_dh = jsonKH.getInt("ms_dh");
                    co_chi_so = jsonKH.getBoolean("co_chi_so");
                    so_tthu_cu = jsonKH.getInt("so_tthu_cu");


//                    ms_ttrang = jsonKH.getInt("ms_ttrang");
                    url_image = jsonKH.getString("url_image");

                } else {

                    Intent intent = new Intent(NhapSoBangTay.this, TabActivity.class);
                    intent.putExtra("ms_tuyen", ms_tuyendoc);
                    intent.putExtra("ms_tk", ms_tk);
                    intent.putExtra("ms_bd", ms_bd);

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
                    edtsodongho1.setText(so_seri);
                    // edtmdsd.setText(mo_ta_dong);
                    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    //Date ngaydoccu = (Date) sdf.parse();
                    //imgPhoto.setImageBitmap(null);

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = format1.parse(common.cat10kitucuoi(ngay_doc_cu));
                    String ngaydoccu = format2.format(date);

                    edtngaydoccu.setText(ngaydoccu);
                    edtngaydocmoi.setText(format2.format(new Date()).toString());
                    edtSDT.setText(dien_thoai);

                    //Sưa so btntruoc_sau
                    if ("null".equals(chi_so_moi) && "null".equals(so_tieu_thu)) {
                        edtSomoi.setText("");
                        edttieuthu.setText("");
                        edtSomoi.setEnabled(true);
                        edttieuthu.setEnabled(true);
                    } else {
                        edtSomoi.setText(common.cat2kitucuoi(chi_so_moi));
                        edttieuthu.setText(common.cat2kitucuoi(so_tieu_thu));
                        edtSomoi.setEnabled(false);
                        edttieuthu.setEnabled(false);
                        btnLuu.setBackgroundColor(Color.GRAY);
                        btnLuu.setEnabled(false);
                        //docsokhongtinhtien
                        //Log.d("nhapsobt", "SHD:" + fnTinhTien.)
                        if (fnTinhTien.laySoHoaDonByMnoiTky(ms_db, Integer.parseInt(ms_tk)) != 0) {
                            Log.d("nhapsobt", "SHD:" + fnTinhTien.So_Hoa_Don);
                            btnThuTien.setBackgroundResource(R.drawable.button_style);
                            btnThuTien.setEnabled(true);
                        } else {
                            btnThuTien.setBackgroundColor(Color.GRAY);
                            btnThuTien.setEnabled(false);
                            chkdockotinhtien.setChecked(true);
                        }
                        //end
                        //btnThuTien.setBackgroundResource(R.drawable.button_style);
                        //btnThuTien.setEnabled(true);
                    }
                    if ("null".equals(url_image) || "".equals(url_image)) {
                        imgPhoto.setImageDrawable(null);
                    } else {
                        imgPhoto.setImageBitmap(common.getBitmapFromURL(url_image));
                    }
                } else {
                    showErrorDialogue(ms_db);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
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
                // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
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

            ArrayAdapter<TinhtrangDocDongHo> dataAdapter = new ArrayAdapter<TinhtrangDocDongHo>(NhapSoBangTay.this, android.R.layout.simple_spinner_item, listTinhTrang);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinkhongdocduoc.setAdapter(dataAdapter);
            spinkhongdocduoc.setSelection(getIndexKhongQuetDuocMV(spinkhongdocduoc));
            Log.i("spinner index", String.valueOf(getIndexKhongQuetDuocMV(spinkhongdocduoc)));
            spinkhongdocduoc.setEnabled(false);
        }
    }

    private class HttpAsyncTaskGetTopNInvoice extends AsyncTask<String, JSONObject, Void> {

        JSONArray jsonArrayTT3T = new JSONArray();
        String strTemp = "";
        int[] stt3t = new int[3];
        int stttong = 0;
        int ms_kieu_tt = 0;

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
                        if (i == 0) {
                            String strms_kieu_tt = common.GetDataToValue(objTinhTrang.getString("ms_kieu_tt"), "");
                            ms_kieu_tt = (int) Double.parseDouble((strms_kieu_tt == "") ? "0" : strms_kieu_tt);
                        }
                        stt3t[i] = stt;
                        strTemp += String.valueOf(stt) + "-";

                    }
                    if (strTemp.length() > 0) {
                        strTemp = strTemp.substring(0, strTemp.length() - 1);
                    }

                    for (int i = 0; i < stt3t.length; i++) {
                        stttong += stt3t[i];
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


    private void getKhaNangDongHo(Integer ms_dh) {
        BarcodeReaderApiManager.getInstance().waterApi().getWaterMeterInfo(ms_dh).enqueue(new Callback<ThongTinDongHo>() {
            @Override
            public void onResponse(Call<ThongTinDongHo> call, retrofit2.Response<ThongTinDongHo> response) {

                Integer he_so = response.body().getHe_so();
                He_So = he_so;
                Integer kha_nang_dh = response.body().getKha_nang_dh();
                Kha_Nang_DH = kha_nang_dh;
            }

            @Override
            public void onFailure(Call<ThongTinDongHo> call, Throwable t) {

            }
        });
    }

    private Integer tinhLuongTieuThu(final Integer chisomoi, final Integer chisocu) {


        if (chisomoi < chisocu) {
            luongtieuthu = (chisomoi + Kha_Nang_DH - chisocu + 1) * He_So + so_tthu_cu;
        } else {
            luongtieuthu = (chisomoi - chisocu) * He_So + so_tthu_cu;
        }
        edttieuthu.setText(String.valueOf(luongtieuthu));
        return luongtieuthu;


    }

    private int getIndexKhongQuetDuocMV(Spinner spinner) {
        String myString = "Đọc bình thường";
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 7;
    }

    private int getIndexDocBinhThuong(Spinner spinner) {
        String myString = "Đọc bình thường";
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }


    private class HttpAsyncTaskGetPurposeByMoinoi extends AsyncTask<String, JSONObject, Void> {

        String mdsd = "";

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            JSONArray jsonArrMDSD;
            String temp = "";
            int countmdkd = 0;

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

    //-- HÀM TÍNH TIỀN-----
    /*Hàm tính lại định mức*/


}

//-- HAM XU LY ANH ------
// function image upload
    /*public void deleteFileImage() {

        File dir = new File(Environment.getExternalStorageDirectory() + "/cnhp/uploadFiles/");
        if (dir.isDirectory()) {
            if (null != dir.list()) {
                String[] children = dir.list();
                if (dir.list().length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();

                    }
                }
            }
        }

    }*/
