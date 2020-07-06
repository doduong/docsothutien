package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.LocaleData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import api.BarcodeReaderApiManager;
import api.InvoiceApi;
import model.BarcodeResponse;
import model.request.BodyUpdateInvoiceSendSMSStatus;
import model.request.UpdateInvoice;
import model.response.GetMDSDByMoiNoi;
import model.response.InvoiceReceipt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.CommonText;
import utils.ConvertUtil;
import utils.ReadJson;
import utils.SharedPref;
import utils.TinhTienFunction;

public class ThuTienKhachHang extends Activity {

    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    Button btnKetNoiMayIn;
    Button btnInGiayBienNhan;
    Button btnInGiayBao;
    Button btnInLaiBN;
    Button btnGuiTinNhan;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    private final Charset UTF32 = Charset.forName("UTF32");
    String namePrinter = "B50";
    TinhTienFunction tinhTienFunction = new TinhTienFunction();
    SharedPref config;
    Integer Ms_Mnoi;
    Integer Ms_Tk;
    long So_Hoa_Don_TT;
    Integer Ms_TTrang_TT;

    //Thu tien
    TextView tentuyen;
    EditText edtdanhba;
    EditText edtsohd;
    EditText edttennguoithue;
    EditText edtDiaChi;
    EditText edtSocu;
    EditText edtSomoi;
    EditText edttieuthu;
    EditText txtloaidiemdung;
    EditText txttrangthaidiemdung;
    EditText edttiennuoc;
    EditText txtTienDVTN;
    EditText txtVAT5;
    EditText txtVAT10;
    EditText txtTongTien;
    CheckBox chktinhPTN;
    CommonText common = new CommonText();
    TinhTienFunction fnTinhTien;
    //End Thu tien

    String Ten_Kh = "";
    String Dia_Chi_Kh = "";
    String Dien_Thoai = "";
    Integer Ms_Mnoi_BN;
    Integer Ms_Tuyen;
    String Tu_Ngay = "";
    String Den_Ngay = "";
    Integer Chi_So_Cu;
    Integer Chi_So_Moi;
    Integer Luong_Tinh_Hd;
    long Tong_Nhan;
    long Tong_Tien;
    Integer ThanhVien_Id;
    String Ngay_Thu = "";
    String Thoi_Ky = "";
    Integer Ms_Loai_Kh = null;
    Integer Ms_Tql;
    String Ten_Tql = "";
    String ThanhVien_Name = "";
    long So_Hd;
    String Ten_BĐ;
    String Ten_TK;
    boolean findBTOk = false;
    boolean openBTOk = true;
    Date today;
    Date ngay_doc_moi;
    Integer tt_tin_nhan = 0;
    Integer ms_ttrang = 0;
    Locale localeEN = null;
    NumberFormat en = null;
    //ArrayList<String> arrDt = new ArrayList<>();

    public BluetoothDevice getMmDevice() {
        return mmDevice;
    }

    public void setMmDevice(BluetoothDevice mmDevice) {
        this.mmDevice = mmDevice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thu_tien_khach_hang);

        myLabel = (TextView) findViewById(R.id.label);
        btnKetNoiMayIn = (Button) findViewById(R.id.btnkenoimayin);
        btnInGiayBienNhan = (Button) findViewById(R.id.btnInGachNo);
        btnInGiayBao = (Button) findViewById(R.id.btnInKhongGachNo);
        btnInLaiBN = (Button) findViewById(R.id.btnInLaiBienNhan);
        btnGuiTinNhan = (Button) findViewById(R.id.btnGuiTinNhan);
        localeEN = new Locale("en", "EN");
        en = NumberFormat.getInstance(localeEN);
        //Thu tien
        tentuyen = (TextView) findViewById(R.id.tentuyen);
        edtdanhba = (EditText) findViewById(R.id.edtdanhba);
        edtsohd = (EditText) findViewById(R.id.edtsohd);
        edttennguoithue = (EditText) findViewById(R.id.edttennguoithue);
        edtDiaChi = (EditText) findViewById(R.id.edtDiaChi);
        edtSocu = (EditText) findViewById(R.id.edtSocu);
        edtSomoi = (EditText) findViewById(R.id.edtSomoi);
        edttieuthu = (EditText) findViewById(R.id.edttieuthu);
        txtloaidiemdung = (EditText) findViewById(R.id.txtloaidiemdung);
        txttrangthaidiemdung = (EditText) findViewById(R.id.txttrangthaidiemdung);
        edttiennuoc = (EditText) findViewById(R.id.edttiennuoc);
        txtTienDVTN = (EditText) findViewById(R.id.txtTienDVTN);
        txtVAT5 = (EditText) findViewById(R.id.txtVAT5);
        txtVAT10 = (EditText) findViewById(R.id.txtVAT10);
        txtTongTien = (EditText) findViewById(R.id.txtTongTien);
        chktinhPTN = (CheckBox) findViewById(R.id.chktinhPTN);
        fnTinhTien = new TinhTienFunction();
        //End Thu tien
        //Minh Đức
        //btnInGiayBienNhan.setVisibility(View.INVISIBLE);
        //btnInLaiBN.setVisibility(View.INVISIBLE);
        //End MĐ
        //findBT();
        //openBT();


        config = new SharedPref(this);
        Ten_BĐ = config.getString("ten_bd", "");
        Ten_TK = config.getString("ten_tk", "");
        Intent intent = getIntent();
        Ms_Mnoi = Integer.parseInt(intent.getStringExtra("ms_mnoi"));
        Ms_Tk = Integer.parseInt(intent.getStringExtra("ms_tk"));
        laySoHoaDonMsTTrangByMnoiTky(Ms_Mnoi, Ms_Tk);
        GetCustomerInfo4PrintReceipt();
        GetCustomerInvoice4PrintReceipt();
        GetCustomerInvoiceVAT4PrintReceipt();

        //String dt = Dien_Thoai;
        //boolean dt1 = Dien_Thoai.substring(0,1).equals("0");
        //String dt2 = Dien_Thoai.substring(0,3);
        /*arrDt.add("086"); arrDt.add("096");arrDt.add("097");arrDt.add("098");arrDt.add("032");
        arrDt.add("033");arrDt.add("034");arrDt.add("035");arrDt.add("036");arrDt.add("037");
        arrDt.add("038");arrDt.add("039");arrDt.add("089");arrDt.add("090");arrDt.add("093");
        arrDt.add("070");arrDt.add("079");arrDt.add("077");arrDt.add("076");arrDt.add("078");
        arrDt.add("088");arrDt.add("091");arrDt.add("094");arrDt.add("083");arrDt.add("084");
        arrDt.add("085");arrDt.add("081");arrDt.add("082");arrDt.add("092");arrDt.add("056");
        arrDt.add("058");arrDt.add("099");arrDt.add("059");*/
         /*String dau_so = "";
        if(Dien_Thoai.trim().length()>3){
             dau_so = Dien_Thoai.substring(0,3);
        }
       boolean choPhepInGiayBao = true;
        for(int i =0; i<arrDt.size(); i++){
            if(dau_so.equals(arrDt.get(i))){
                choPhepInGiayBao = false;
                break;
            }
        }*/
        if(Ms_Loai_Kh ==2){
            btnInGiayBao.setBackgroundColor(Color.GRAY);
            btnInGiayBao.setEnabled(false);
        }



        if(Dien_Thoai.trim().equals("")||!(Dien_Thoai.substring(0,1).trim().equals("0"))||Dien_Thoai.trim().equals("null")){
            btnGuiTinNhan.setBackgroundColor(Color.GRAY);
            btnGuiTinNhan.setEnabled(false);
            myLabel.setText("Điểm Dùng Không Nhắn Tin");
        }
        if(Dien_Thoai.length()>4){
            if(Dien_Thoai.substring(0,4).trim().equals("0225")){
                btnGuiTinNhan.setBackgroundColor(Color.GRAY);
                btnGuiTinNhan.setEnabled(false);
                myLabel.setText("Điểm Dùng Không Nhắn Tin");
            }
        }else {
            btnGuiTinNhan.setBackgroundColor(Color.GRAY);
            btnGuiTinNhan.setEnabled(false);
            myLabel.setText("Điểm Dùng Không Nhắn Tin");
        }




        if (invoiceGetSendSmsStatus(So_Hoa_Don_TT) != 0) {
            btnGuiTinNhan.setBackgroundColor(Color.GRAY);
            btnGuiTinNhan.setEnabled(false);
            btnInGiayBao.setBackgroundColor(Color.GRAY);
            btnInGiayBao.setEnabled(false);
        }
        if (Ms_TTrang_TT == 3) {
            btnGuiTinNhan.setBackgroundColor(Color.GRAY);
            btnGuiTinNhan.setEnabled(false);
            btnInGiayBienNhan.setBackgroundColor(Color.GRAY);
            btnInGiayBienNhan.setEnabled(false);
        }

        if (isConnected()) {

            if (So_Hoa_Don_TT != 0) {
                viewPaymentResultDetail(Ms_Mnoi, Ms_Tk);
            } else {
                //1.Hiển thị popup hỏi bạn có muốn tình không
                //2.Tính tiền xong hiển thị lại form
                Toast.makeText(this, "Không tồn tại số hóa đơn", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ThuTienKhachHang.this, "Không có kết nối internet", Toast.LENGTH_LONG).show();
        }

        btnKetNoiMayIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

       /* btnTatMayIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });*/
        btnGuiTinNhan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isConnected()) {
                    android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ThuTienKhachHang.this);
                    b.setTitle("Thông báo");
                    b.setMessage("Bạn có chắc chắn muốn gửi tin nhắn thông báo cước?");
                    b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (invoiceGetSendSmsStatus(So_Hoa_Don_TT) == 0) {
                                updateInvoiceSendSMSStatus(So_Hoa_Don_TT);
                                btnInGiayBao.setBackgroundColor(Color.GRAY);
                                btnInGiayBao.setEnabled(false);
                            } else {
                                Toast.makeText(ThuTienKhachHang.this, "Đã gửi tin nhắn cho khách hàng!", Toast.LENGTH_LONG).show();
                            }


                            dialog.cancel();


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
                    Toast.makeText(ThuTienKhachHang.this, "Không có kết nối Internet!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnInGiayBienNhan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //fnLapHoaDon();
                //Làm 2 việc
                //1. Cập nhật trạng thái đã thu tiền trong bảng hóa đơn
                if (isConnected()) {
                    if (duocPhepThuTien(today, ngay_doc_moi)) {

                        android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ThuTienKhachHang.this);
                        b.setTitle("Thông báo");
                        b.setMessage("Bạn có chắc chắn muốn IN BIÊN NHẬN?");
                        b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (mmDevice == null || mmOutputStream == null) {
                                    findBT();
                                    openBT();
                                }

                                if (!openBTOk) {
                                    findBT();
                                    openBT();
                                }
                                if (openBTOk) {

                                    laySoHoaDonMsTTrangByMnoiTky(Ms_Mnoi, Ms_Tk);
                                    if (So_Hoa_Don_TT != 0 && Ms_TTrang_TT != 3) {
                                        updateInvoicePayment(So_Hoa_Don_TT);

                                    } else {
                                        if (So_Hoa_Don_TT == 0) {
                                            Toast.makeText(ThuTienKhachHang.this, "Điểm dùng chưa được tính tiền", Toast.LENGTH_LONG).show();
                                        } else if (Ms_TTrang_TT == 3) {
                                            Toast.makeText(ThuTienKhachHang.this, "Điểm dùng đã in biên nhận!", Toast.LENGTH_LONG).show();
                                            btnInGiayBienNhan.setBackgroundColor(Color.GRAY);
                                            btnInGiayBienNhan.setEnabled(false);
                                        }
                                    }
                                } else {
                                    Toast.makeText(ThuTienKhachHang.this, "Lỗi kết nối máy in!", Toast.LENGTH_LONG).show();
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


                    } else {
                        Toast.makeText(ThuTienKhachHang.this, "Khách hàng chỉ được thanh toán trong ngày tính tiền!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ThuTienKhachHang.this, "Không có kết nối Internet!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnInLaiBN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mmDevice == null || mmOutputStream == null) {

                    findBT();
                    openBT();

                }

                if (!openBTOk) {
                    findBT();
                    openBT();
                }
                try {
                    if (openBTOk) {
                        laySoHoaDonMsTTrangByMnoiTky(Ms_Mnoi, Ms_Tk);
                        if (Ms_TTrang_TT == 3) {
                            sendData("inlai");
                            updateNumberPrintInvoice(So_Hoa_Don_TT);
                        } else if (Ms_TTrang_TT == 1) {
                            Toast.makeText(ThuTienKhachHang.this, "Điểm dùng chưa thanh toán. Bạn phải in giấy biên nhận!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ThuTienKhachHang.this, "Lỗi kết nối máy in!", Toast.LENGTH_LONG).show();
                    }
                    //response.raw().body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ThuTienKhachHang.this, "Lỗi kết nối máy in!", Toast.LENGTH_LONG).show();
                }

                //2. In giấy biên nhận


            }
        });

        btnInGiayBao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //fnLapHoaDon();
                //Làm 2 việc
                //1. Cập nhật trạng thái đã thu tiền trong bảng hóa đơn

                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(ThuTienKhachHang.this);
                b.setTitle("Thông báo");
                b.setMessage("Bạn có chắc chắn muốn IN GIẤY BÁO?");
                b.setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (mmDevice == null|| mmOutputStream==null) {

                            findBT();
                            openBT();

                        }

                        if(!openBTOk){
                            findBT();
                            openBT();
                        }

                        try {
                            if(openBTOk) {
                                laySoHoaDonMsTTrangByMnoiTky(Ms_Mnoi, Ms_Tk);
                                if (Ms_TTrang_TT == 3) {
                                    //sendData("inlai");
                                    Toast.makeText(ThuTienKhachHang.this, "Điểm dùng đã thanh toán, Vui lòng in giấy biên nhận!", Toast.LENGTH_LONG).show();
                                } else if (Ms_TTrang_TT == 1) {
                                    sendData("ingiaybao");
                                    updateInvoicePrintNotification(So_Hoa_Don_TT);
                                    btnGuiTinNhan.setBackgroundColor(Color.GRAY);
                                    btnGuiTinNhan.setEnabled(false);
                                }
                            }else {
                                Toast.makeText(ThuTienKhachHang.this, "Bạn chưa bật máy in", Toast.LENGTH_LONG).show();
                            }
                            //response.raw().body().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ThuTienKhachHang.this, "Bạn chưa kết nối máy in", Toast.LENGTH_LONG).show();
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


                //2. In giấy biên nhận


            }
        });
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
            Intent intent = new Intent(ThuTienKhachHang.this, TabActivity.class);
            startActivity(intent);
        }
    }

    public void laySoHoaDonMsTTrangByMnoiTky(Integer ms_mnoi, Integer ms_tk) {


        String url = common.URL_API + "/LaySoHoaDonByMnoiTky?ms_mnoi=" + ms_mnoi + "&ms_tk=" + ms_tk;
        Log.d("MsTTrangByMnoiTky", url);
        String str = "";
        JSONObject objLaySoHD = null;


        try {
            objLaySoHD = ReadJson.readJsonFromUrl(url);

            String strms_hd = common.GetDataToValue(objLaySoHD.getString("so_hd"), "");
            long ms_hd = (long) Double.parseDouble((strms_hd == "") ? "0" : strms_hd);

            String strms_ttrang_tt = common.cat2kitucuoi(objLaySoHD.getString("ms_ttrang"));
            int ms_ttrang_tt = (int) Double.parseDouble((strms_ttrang_tt == "") ? "0" : strms_ttrang_tt);

            if (ms_hd != 0) {
                // So_Hoa_Don = Integer.parseInt(common.cat2kitucuoi(str));
                So_Hoa_Don_TT = ms_hd;
                Ms_TTrang_TT = ms_ttrang_tt;
            }
            Log.d("HoaDonMsTTrangByMnoiTky", "" + So_Hoa_Don_TT);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateInvoiceSendSMSStatus(long so_hd) {


        BodyUpdateInvoiceSendSMSStatus request = new BodyUpdateInvoiceSendSMSStatus(so_hd, 1);
        BarcodeReaderApiManager.getInstance().invoiceApi().updateInvoiceSendSMSStatus(so_hd, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ThuTienKhachHang.this, "Cập nhật SMS thành công!", Toast.LENGTH_LONG).show();
                    btnGuiTinNhan.setBackgroundColor(Color.GRAY);
                    btnGuiTinNhan.setEnabled(false);
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });
        //Test
        //return  true;
    }

    private void updateInvoicePrintNotification(long so_hd) {


        BodyUpdateInvoiceSendSMSStatus request = new BodyUpdateInvoiceSendSMSStatus(so_hd, 2);
        BarcodeReaderApiManager.getInstance().invoiceApi().updateInvoiceSendSMSStatus(so_hd, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    btnGuiTinNhan.setBackgroundColor(Color.GRAY);
                    btnGuiTinNhan.setEnabled(false);
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });
        //Test
        //return  true;
    }


    private void updateNumberPrintInvoice(long so_hd) {

        try {
            BarcodeReaderApiManager.getInstance().invoiceApi().updateNumberPrintInvoice(so_hd).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().invoiceApi().updateNumberPrintInvoice(so_hd).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("NumberPrintInvoice", "OK");
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
            }
        });*/
    }


    private void updateInvoicePayment(final long so_hd) {
        final ProgressDialog progressDialog = new ProgressDialog(ThuTienKhachHang.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Đang in biên nhận...");
        progressDialog.show();

        BarcodeReaderApiManager.getInstance().invoiceApi().updateInvoicePayment(so_hd).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {

                    try {
                        if (!openBTOk) {
                            findBT();
                            openBT();
                        }
                        sendData("");
                        updateNumberPrintInvoice(so_hd);
                        btnGuiTinNhan.setBackgroundColor(Color.GRAY);
                        btnGuiTinNhan.setEnabled(false);
                        btnInGiayBienNhan.setBackgroundColor(Color.GRAY);
                        btnInGiayBienNhan.setEnabled(false);
                    } catch (IOException e) {
                        findBT();
                        openBT();
                        try {
                            sendData("");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    progressDialog.dismiss();
                } else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ThuTienKhachHang.this, "Lỗi cập nhật trạng thái thanh toán", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void viewPaymentResultDetail(Integer ms_mnoi, Integer ms_tk1) {
        if (isConnected()) {
            String url = common.URL_API + "/ViewPaymentResultDetail?ms_mnoi=" + ms_mnoi + "&ms_tky=" + ms_tk1;
            Log.d("ViewPaymentResultDetail", url);
            new HttpAsyncTaskViewPaymentResultDetail().execute(url);
        } else {
            Toast.makeText(this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();

        }
    }

    private class HttpAsyncTaskViewPaymentResultDetail extends AsyncTask<String, JSONObject, Void> {

        String tentuyen = "";
        Integer ms_mnoi;
        long so_hd;
        String ten_kh;
        String dia_chi_kh;
        Integer chi_so_cu;
        Integer chi_so_moi;
        Integer so_tieu_thu;
        String ten_loai_mn;
        String mo_ta;
        long tien_nuoc;
        long VAT5;
        long tien_DVTN;
        long VAT10;
        //Tong_tien;
        boolean tinh_ptn = false;
        String den_ngay = "";
        JSONObject jsonHoaDon;

        ProgressDialog progressDialog = new ProgressDialog(ThuTienKhachHang.this);

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Nạp dữ liệu...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];


            try {
                jsonHoaDon = ReadJson.readJsonFromUrl(url);


                ms_mnoi = Integer.parseInt(common.cat2kitucuoi(jsonHoaDon.getString("ms_mnoi")));
                so_hd = (long) Double.parseDouble(jsonHoaDon.getString("so_hd"));
                ten_kh = jsonHoaDon.getString("ten_kh");
                dia_chi_kh = jsonHoaDon.getString("dia_chi_kh");
                chi_so_cu = (int) Double.parseDouble(jsonHoaDon.getString("chi_so_cu"));
                chi_so_moi = (int) Double.parseDouble(jsonHoaDon.getString("chi_so_moi"));
                so_tieu_thu = (int) Double.parseDouble(jsonHoaDon.getString("so_tieu_thu"));
                ten_loai_mn = jsonHoaDon.getString("ten_loai_mn");
                mo_ta = jsonHoaDon.getString("mo_ta");
                tien_nuoc = (long) Double.parseDouble(jsonHoaDon.getString("tien_nuoc"));
                VAT5 = (long) Double.parseDouble(jsonHoaDon.getString("VAT5"));
                tien_DVTN = (long) Double.parseDouble(jsonHoaDon.getString("tien_DVTN"));
                VAT10 = (long) Double.parseDouble(jsonHoaDon.getString("VAT10"));
                Tong_Tien = (long) Double.parseDouble(jsonHoaDon.getString("Tong_tien"));

                den_ngay = common.GetDataToValue(jsonHoaDon.getString("den_ngay"), "");
                if (null != den_ngay && !"".equals(den_ngay)) {
                    //String strngay_doc_moi = "2019-05-09T15:33:00";
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        ngay_doc_moi = format1.parse(common.cat10kitucuoi(den_ngay));
                        //String today_test = "2019-07-03 09:59:00";
                        today = format1.parse(format1.format(new Date()));
                        //today = format1.parse(common.cat10kitucuoi(today_test));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (jsonHoaDon.getString("tinh_ptn").equals("true")) {
                    tinh_ptn = true;
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
                if (null != jsonHoaDon) {
                    edtdanhba.setText(ms_mnoi.toString());
                    edtsohd.setText(String.valueOf(so_hd));
                    edttennguoithue.setText(String.valueOf(ten_kh));
                    edtDiaChi.setText(dia_chi_kh);
                    edtSocu.setText(String.valueOf(chi_so_cu));
                    edtSomoi.setText(String.valueOf(chi_so_moi));
                    edttieuthu.setText(String.valueOf(so_tieu_thu));
                    txtloaidiemdung.setText(String.valueOf(ten_loai_mn));
                    txttrangthaidiemdung.setText(String.valueOf(mo_ta));
                    String strtien_nuoc = en.format(tien_nuoc);
                    edttiennuoc.setText(String.valueOf(strtien_nuoc));

                    String strtien_DVTN = en.format(tien_DVTN);
                    txtTienDVTN.setText(String.valueOf(strtien_DVTN));

                    String strVAT5 = en.format(VAT5);
                    txtVAT5.setText(String.valueOf(strVAT5));

                    String strVAT10 = en.format(VAT10);
                    txtVAT10.setText(String.valueOf(strVAT10));

                    String strTong_tien = en.format(Tong_Tien);
                    txtTongTien.setText(String.valueOf(strTong_tien));
                    chktinhPTN.setChecked(tinh_ptn);

                    if (so_tieu_thu != 0 && Tong_Tien == 0 && tien_nuoc == 0 && tien_DVTN == 0 && VAT5 == 0 && VAT10 == 0) {
                        fnTinhTien.deleteInvoice(so_hd);
                        Toast.makeText(ThuTienKhachHang.this, "Hóa đơn tính tiền lỗi", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ThuTienKhachHang.this, TabActivity.class);
                        startActivity(intent);
                    }


                } else {
                    showErrorDialogue(String.valueOf(ms_mnoi));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }

    public void showErrorDialogue(final String resultscan) {
        Toast.makeText(this, "Mã danh bạ không tồn tại!", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(ThuTienKhachHang.this, TabActivity.class);
        //startActivity(intent);
    }

    /*void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Đã tắt Bluetooth.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            if (mmOutputStream != null) {
                mmOutputStream.close();
                mmOutputStream = null;
            }

            if (mmInputStream != null) {
                mmInputStream.close();
                mmInputStream = null;
            }

            if (mmSocket != null) {
                mmSocket.close();
                mmSocket = null;
            }
            if (mmDevice != null)
                mmDevice = null;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                myLabel.setText("Máy không có bluetooth");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }


            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices

                    if (device.getName().equals(namePrinter)) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            myLabel.setText("Đã tìm thấy thiết bị Bluetooth");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean openBT() {

        // Standard SerialPortService ID
        openBTOk = true;


        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        try {


            try {
                mmSocket = null;
                //mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                mBluetoothAdapter.cancelDiscovery();
            } catch (Exception e) {
                Log.e("", "Error creating socket");

            }

            //try {
            if (null != mmSocket) {
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();
                beginListenForData();
                myLabel.setText("Bluetooth đã được mở");
                //} catch (IOException e) {
                //    openBTOk = false;
                //}
            } else {
                openBTOk = false;
            }


        } catch (IOException e) {
            try {
                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
                mmInputStream = mmSocket.getInputStream();

            }
            catch (Exception e2) {
                openBTOk = false;
            }

        }


        return openBTOk;
    }


    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        //final String data = new String(encodedBytes, "US-ASCII");
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendData(String loaigiayin) throws IOException {
        try {

            if (mmDevice == null || mmOutputStream == null) {

                findBT();
                openBT();

            }

            if (!openBTOk) {
                findBT();
                openBT();
            }

            // mmOutputStream.write(msg.getBytes("UNICODE"));
            GetCustomerInfo4PrintReceipt_InBienNhan();
            /*GetCustomerInfo4PrintReceipt();
            GetCustomerInvoice4PrintReceipt();
            GetCustomerInvoiceVAT4PrintReceipt();*/

            byte[] cc = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
            byte[] bb1 = new byte[]{0x1B, 0x21, 0x4};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
            //  print_bar_code("12345678");
            String msg = ConvertUtil.InsertBlank("CÔNG TY CP CẤP NƯỚC HP", 24, 0);
            msg += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));
            if ("".equals(loaigiayin)) {
                msg = ConvertUtil.InsertBlank("BIÊN NHẬN", 28, 0);
            } else if ("inlai".equals(loaigiayin)) {
                msg = ConvertUtil.InsertBlank("IN LẠI BIÊN NHẬN", 28, 0);
            } else if ("ingiaybao".equals(loaigiayin)) {
                msg = ConvertUtil.InsertBlank("GIẤY BÁO", 28, 0);
            }
            msg += "\n";
            msg += ConvertUtil.InsertBlank("THANH TOÁN TIỀN NƯỚC", 24, 0);
            mmOutputStream.write(bb3);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.InsertBlank("(Tháng: " + Ten_TK + ")", 32, 0);
            msg += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.ConvertToStringAndNewLine("Tên KH: " + Ten_Kh);
            msg += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));


            msg = ConvertUtil.ConvertToStringAndNewLine("Địa chỉ: " + Dia_Chi_Kh);
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = ConvertUtil.ConvertToStringAndNewLine("Số ĐT: " + Dien_Thoai);
            msg += "\n";
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = "DBĐD: " + Ms_Mnoi_BN + "   " + " Tuyến: " + Ms_Tuyen + "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));
            Date tungay = new Date();
            Date denngay = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            tungay = sdf1.parse(common.cat10kitucuoi(Tu_Ngay));
            denngay = sdf1.parse(common.cat10kitucuoi(Den_Ngay));
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            msg = "Từ : " + sdf2.format(tungay) + "   " + "CSC: " + Chi_So_Cu + "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = "Đến: " + sdf2.format(denngay) + "   " + "CSM: " + Chi_So_Moi + "\n";
            msg += "Tiêu thụ : " + Luong_Tinh_Hd + " M3" + "\n";
            msg += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));


            msg = "Chi tiết : " + "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg.getBytes(UTF32));

            //msg = ConvertUtil.InsertBlank("SL", 5, 0) + ConvertUtil.InsertBlank("Đ.Giá", 7, 0) + ConvertUtil.InsertBlank("T.Tiền", 10, 0) + ConvertUtil.InsertBlank("VAT", 10, 0);
            msg = "SL   " + " Đ.Giá  " + " T.Tiền   " + "VAT  " + "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = "";
            for (int i = 0; i < arrct1.size(); i++) {
                msg += (ConvertUtil.InsertBlank(String.valueOf(arrct1.get(i).getSo_don_vi()), 7, 0) + ConvertUtil.InsertBlank(String.valueOf(arrct1.get(i).getDon_gia()), 6, 0)
                        + ConvertUtil.InsertBlank(String.valueOf(arrct1.get(i).getTien_phai_thu()), 10, 0) + ConvertUtil.InsertBlank(String.valueOf(arrct1.get(i).getTien_VAT()), 9, 0) + "\n");
            }
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));

            msg = "DVTN: " + "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg.getBytes(UTF32));

            String msg1 = "";
            for (int j = 0; j < arrct2.size(); j++) {
                msg1 += (ConvertUtil.InsertBlank(String.valueOf(arrct2.get(j).getSo_don_vi()), 7, 0) + ConvertUtil.InsertBlank(String.valueOf(arrct2.get(j).getDon_gia()), 6, 0)
                        + ConvertUtil.InsertBlank(String.valueOf(arrct2.get(j).getTien_phai_thu()), 10, 0) + ConvertUtil.InsertBlank(String.valueOf(arrct2.get(j).getTien_VAT()), 9, 0) + "\n");
            }
            msg1 += "\n";
            mmOutputStream.write(bb1);
            mmOutputStream.write(msg1.getBytes(UTF32));

            if ("ingiaybao".equals(loaigiayin)) {
                msg = "Tổng: " + String.valueOf(txtTongTien.getText());
                msg += "\n";
                mmOutputStream.write(bb2);
                mmOutputStream.write(msg.getBytes(UTF32));
                String msg4 = common.WriteNum(Tong_Tien);
                msg4 += "\n";
                msg4 += "\n";
                mmOutputStream.write(bb1);
                mmOutputStream.write(msg4.getBytes(UTF32));
            } else {

                msg = "Tổng: " + String.valueOf(en.format(Tong_Nhan));
                msg += "\n";
                mmOutputStream.write(bb2);
                mmOutputStream.write(msg.getBytes(UTF32));
                String msg4 = common.WriteNum(Tong_Nhan);
                msg4 += "\n";
                msg4 += "\n";
                mmOutputStream.write(bb1);
                mmOutputStream.write(msg4.getBytes(UTF32));
            }

            if (!"".equals(Ngay_Thu)) {
                Date thungay = sdf1.parse(common.cat10kitucuoi(Ngay_Thu));
                mmOutputStream.write(cc);
                msg = "Thu ngày : " + sdf2.format(thungay);
                msg += "\n";
                mmOutputStream.write(cc);
                mmOutputStream.write(msg.getBytes(UTF32));
            }

            String msg3 = "Nhân viên: " + ConvertUtil.ConvertToStringAndNewLine(Ten_BĐ);
            msg3 += "\n";
            msg3 += ConvertUtil.getSpaces(32, "-");
            msg3 += "\n";


            msg3 += ConvertUtil.ConvertToStringAndNewLine("Khách hàng truy cập: www.hddt.capnuochaiphong.com.vn để lấy hóa đơn điện tử.");
            msg3 += "\n";
            msg3 += "\n";
            msg3 += "\n";
            msg3 += "\n";
            msg3 += "\n";
            mmOutputStream.write(cc);
            mmOutputStream.write(msg3.getBytes(UTF32));
            mmOutputStream.flush();
            // tell the user data were sent
            myLabel.setText("Đã in.");

        } catch (Exception e) {


            findBT();
            openBT();
            e.printStackTrace();
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


    public void GetCustomerInfo4PrintReceipt() {


        String url = common.URL_API + "/GetCustomerInfo4PrintReceipt?so_hd=" + So_Hoa_Don_TT;
        JSONArray jsonArrCustomerInfo;

        try {
            jsonArrCustomerInfo = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            for (int i = 0; i < jsonArrCustomerInfo.length(); i++) {
                JSONObject objCustomerInfo = jsonArrCustomerInfo.getJSONObject(i);
                Ten_Kh = objCustomerInfo.getString("ten_kh");
                Dia_Chi_Kh = objCustomerInfo.getString("dia_chi_kh");
                Dien_Thoai = objCustomerInfo.getString("dien_thoai");

                String strMs_Mnoi_BN = common.GetDataToValue(objCustomerInfo.getString("ms_mnoi"), "");
                Ms_Mnoi_BN = (int) Double.parseDouble((strMs_Mnoi_BN == "") ? "0" : strMs_Mnoi_BN);
                String strMs_Tuyen = objCustomerInfo.getString("ms_tuyen");
                Ms_Tuyen = (int) Double.parseDouble((strMs_Tuyen == "") ? "0" : strMs_Tuyen);
                Tu_Ngay = objCustomerInfo.getString("tu_ngay");
                Den_Ngay = objCustomerInfo.getString("den_ngay");

                String strChi_So_Cu = common.GetDataToValue(objCustomerInfo.getString("chi_so_cu"), "");
                Chi_So_Cu = (int) Double.parseDouble((strChi_So_Cu == "") ? "0" : strChi_So_Cu);

                String strChi_So_Moi = common.GetDataToValue(objCustomerInfo.getString("chi_so_moi"), "");
                Chi_So_Moi = (int) Double.parseDouble((strChi_So_Moi == "") ? "0" : strChi_So_Moi);

                String strLuong_Tinh_Hd = common.GetDataToValue(objCustomerInfo.getString("luong_tinh_hd"), "");
                Luong_Tinh_Hd = (int) Double.parseDouble((strLuong_Tinh_Hd == "") ? "0" : strLuong_Tinh_Hd);

                String strTong_Nhan = common.GetDataToValue(objCustomerInfo.getString("tong_nhan"), "");
                Tong_Nhan = (long) Double.parseDouble((strTong_Nhan == "") ? "0" : strTong_Nhan);

                Ngay_Thu = common.GetDataToValue(objCustomerInfo.getString("ngay_thu"), "");

                String strThanhVien_Id = common.GetDataToValue(objCustomerInfo.getString("thanhvien_id"), "");
                ThanhVien_Id = (int) Double.parseDouble((strThanhVien_Id == "") ? "0" : strThanhVien_Id);

                Thoi_Ky = common.GetDataToValue(objCustomerInfo.getString("thoi_ky"), "");

                String strMs_Tql = common.GetDataToValue(objCustomerInfo.getString("ms_tql"), "");
                Ms_Tql = (int) Double.parseDouble((strMs_Tql == "") ? "0" : strMs_Tql);


                Ten_Tql = common.GetDataToValue(objCustomerInfo.getString("ten_tql"), "");
                ThanhVien_Name = common.GetDataToValue(objCustomerInfo.getString("thanhvien_name"), "");

                String strSo_Hd = common.GetDataToValue(objCustomerInfo.getString("so_hd"), "");
                So_Hd = (long) Double.parseDouble((strSo_Hd == "") ? "0" : strSo_Hd);

                String strMs_Loai_Kh = common.GetDataToValue(objCustomerInfo.getString("ms_loai_kh"), "");
                Ms_Loai_Kh = (int) Double.parseDouble((strMs_Loai_Kh == "") ? "0" : strMs_Loai_Kh);


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GetCustomerInfo4PrintReceipt_InBienNhan() {


        String url = common.URL_API + "/GetCustomerInfo4PrintReceipt?so_hd=" + So_Hoa_Don_TT;
        JSONArray jsonArrCustomerInfo;

        try {
            jsonArrCustomerInfo = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            for (int i = 0; i < jsonArrCustomerInfo.length(); i++) {
                JSONObject objCustomerInfo = jsonArrCustomerInfo.getJSONObject(i);

                String strTong_Nhan = common.GetDataToValue(objCustomerInfo.getString("tong_nhan"), "");
                Tong_Nhan = (long) Double.parseDouble((strTong_Nhan == "") ? "0" : strTong_Nhan);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //GetCustomerInvoiceVAT4PrintReceipt?so_hd
    ArrayList<InvoiceReceipt> arrct1;
    ArrayList<InvoiceReceipt> arrct2;

    public void GetCustomerInvoice4PrintReceipt() {


        String url = common.URL_API + "/GetCustomerInvoice4PrintReceipt?so_hd=" + So_Hoa_Don_TT;
        Log.d("InvoiceVAT4PrintReceipt", url);
        //new HttpAsyncTaskGetMucDichSuDungByMoiNoi().execute(url).get();
        JSONArray jsonArrInvoice;

        try {
            jsonArrInvoice = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            arrct1 = new ArrayList<>();
            for (int i = 0; i < jsonArrInvoice.length(); i++) {
                InvoiceReceipt ct1;
                JSONObject objInvoice = jsonArrInvoice.getJSONObject(i);

                String strSo_don_vi = common.GetDataToValue(objInvoice.getString("so_don_vi"), "");
                double so_don_vi = Double.parseDouble((strSo_don_vi == "") ? "0" : strSo_don_vi);

                String strDon_gia = common.GetDataToValue(objInvoice.getString("don_gia"), "");
                long don_gia = (long) Double.parseDouble((strDon_gia == "") ? "0" : strDon_gia);

                String strTien_phai_thu = common.GetDataToValue(objInvoice.getString("tien_phai_thu"), "");
                long tien_phai_thu = (long) Double.parseDouble((strTien_phai_thu == "") ? "0" : strTien_phai_thu);

                String strVAT = common.GetDataToValue(objInvoice.getString("VAT"), "");
                long VAT = (long) Double.parseDouble((strVAT == "") ? "0" : strVAT);

                String strtien_VAT = common.GetDataToValue(objInvoice.getString("tien_VAT"), "");
                long tien_VAT = (long) Double.parseDouble((strtien_VAT == "") ? "0" : strtien_VAT);

                ct1 = new InvoiceReceipt(so_don_vi, don_gia, tien_phai_thu, VAT, tien_VAT);
                arrct1.add(ct1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void GetCustomerInvoiceVAT4PrintReceipt() {


        String url = common.URL_API + "/GetCustomerInvoiceVAT4PrintReceipt?so_hd=" + So_Hoa_Don_TT;
        Log.d("InvoiceVAT4PrintReceipt", url);
        //new HttpAsyncTaskGetMucDichSuDungByMoiNoi().execute(url).get();
        JSONArray jsonArrInvoice;

        try {
            jsonArrInvoice = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            arrct2 = new ArrayList<>();
            for (int i = 0; i < jsonArrInvoice.length(); i++) {
                InvoiceReceipt ct2;
                JSONObject objInvoice = jsonArrInvoice.getJSONObject(i);

                String strSo_don_vi = common.GetDataToValue(objInvoice.getString("so_don_vi"), "");
                double so_don_vi = Double.parseDouble((strSo_don_vi == "") ? "0" : strSo_don_vi);

                String strDon_gia = common.GetDataToValue(objInvoice.getString("don_gia"), "");
                long don_gia = (long) Double.parseDouble((strDon_gia == "") ? "0" : strDon_gia);

                String strTien_phai_thu = common.GetDataToValue(objInvoice.getString("tien_phai_thu"), "");
                long tien_phai_thu = (long) Double.parseDouble((strTien_phai_thu == "") ? "0" : strTien_phai_thu);

                String strVAT = common.GetDataToValue(objInvoice.getString("VAT"), "");
                long VAT = (long) Double.parseDouble((strVAT == "") ? "0" : strVAT);

                String strtien_VAT = common.GetDataToValue(objInvoice.getString("tien_VAT"), "");
                long tien_VAT = (long) Double.parseDouble((strtien_VAT == "") ? "0" : strtien_VAT);

                ct2 = new InvoiceReceipt(so_don_vi, don_gia, tien_phai_thu, VAT, tien_VAT);
                arrct2.add(ct2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean duocPhepThuTien(Date today, Date ngay_doc_moi) {
        boolean result = false;
        if (today.compareTo(ngay_doc_moi) > 0) {
            result = false;
        } else if (today.compareTo(ngay_doc_moi) == 0) {
            result = true;
        }

        return result;

    }

    public int invoiceGetSendSmsStatus(long so_hd) {


        String url = common.URL_API + "/InvoiceGetSendSmsStatus?so_hd=" + so_hd;
        Log.d("InvoiceGetSendSmsStatus", url);
        String str = "";
        JSONObject objLaySoHD = null;


        try {
            objLaySoHD = ReadJson.readJsonFromUrl(url);
            //{"tt_tin_nhan":0,"ms_ttrang":1.0}
            String strms_sms = common.GetDataToValue(objLaySoHD.getString("tt_tin_nhan"), "");
            tt_tin_nhan = Integer.parseInt((strms_sms == "") ? "0" : strms_sms);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tt_tin_nhan;
    }
}
