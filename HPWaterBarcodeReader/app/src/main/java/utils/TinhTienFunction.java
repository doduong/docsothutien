package utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import api.BarcodeReaderApiManager;
import app.cntt.cnhp.hpwaterbarcodereader.NhapSoActivity;
import app.cntt.cnhp.hpwaterbarcodereader.NhapSoBangTay;
import app.cntt.cnhp.hpwaterbarcodereader.Tab1Fragment;
import app.cntt.cnhp.hpwaterbarcodereader.ThuTienKhachHang;
import model.BarcodeResponse;
import model.request.BodyInsertInvoiceDetail;
import model.request.BodyInsertInvoiceDetailBrief;
import model.request.ModelInsertInvoice;
import model.request.ModelMaDong;
import model.request.UpdateInvoice;
import model.request.UpdateMSSD;
import model.request.UpdateMoiNoi;
import model.response.GetMDSDByMoiNoi;
import model.response.UsePurposeByMoinoi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TinhTienFunction {

    CommonText common = new CommonText();
    ArrayList<GetMDSDByMoiNoi> lstMDSDByMN;
    ArrayList<UsePurposeByMoinoi> lstUsePurposeByMoinoi;
    ArrayList<ModelMaDong> lstMaDong;
    Double nDinhMuc = null;
    Integer ms_tk;
    public boolean updateInvoiceOK = false;

    Integer Ms_MoiNoi;
    BigDecimal Tieu_Thu_Toi_Thieu;
    BigDecimal Tieu_Thu_Toi_Thieu_Chung;
    double TraTienDH;
    double TraTienDH_ptn;
    Integer Ms_Dong_TTDH;
    double VAT_TTDH;
    int Tinh_PTN;
    int Tinh_Luy_Ke;
    double Gia_Cu;
    double Ptn_Cu;
    double Gia_Moi;
    double Ptn_moi;
    double Thue_VAT;
    Date Ngay_Ap_Dung;
    double Tong_So_Tieu_Thu;
    double TraTienDH_Cu;
    double tien_dho_tra;
    double tien_dho;
    Boolean TieuThuNull;
    double Chi_So_Cu;
    double Chi_So_Moi;
    double So_Tieu_Thu;
    Date Tu_Ngay;
    Date Den_Ngay;
    SimpleDateFormat sdf_temp = new SimpleDateFormat("yyyy-MM-dd");
    //Date g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi = new Date(2012, 04, 04);
    Date g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi;

    {
        try {
            g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi = sdf_temp.parse("2012-04-04");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    long So_Ngay;
    long So_NgayC;
    long So_NgayM;
    public long So_Hoa_Don;
    double Tong_Tien; //tiền nước riêng
    double Tong_Tien_DVTN; //tổng tiền DVTN (tách riêng so với tiền nước)
    double Tong_VAT; //VAT của tiền nước
    double Tong_Ptn;//chính là tổng VAT_DVTN
    //--------------------------
    double g_Gia_TTTT_Moi;
    int g_MsDong_TTTT = 344;
    int g_MsDong_PHI_DAU_NOI = 400;
    double g_TTTT_PTN;
    double g_TTTT_VAT;
    double g_Gia_TTTT_Cu = 0;
    double g_TTTT_PTN_Cu = 0;
    String sCachTinh;
    String DinhMucCB;
    String sSoNguoi;
    String sSoHo;
    int danhdauvitri = 0;
    boolean isTinhTienOk = true;
    public boolean CheckDeleteHD = false;
    public boolean CheckTinhLaiTien = false;

    private Boolean TinhLaiDM(Integer sms_mnoi, Double DinhMuc_tt) {

        GetMDSDByMoiNoi(sms_mnoi);


        for (int i = 0; i < lstMDSDByMN.size(); i++) {
            //+ Kiểm tra nếu giá trị của ms_gia_vuot!=””
            if (lstMDSDByMN.get(i).getMs_gia_vuot() != null) {
                //Tính lại định mức theo công thức : nDinhMuc = Math.Round((DinhMuc_tt *phan_tram) / 100, 2);
                //lam tron len gom 2 so thap phan: Math.round(a * 100.0) / 100.0);
                nDinhMuc = Double.valueOf(Math.round(((DinhMuc_tt * lstMDSDByMN.get(i).getPhan_tram()) / 100) * 100.0) / 100.0);
                //Cập nhật lại dinh_muc trong bảng muc_dich_sd where ms_mnoi= sms_mnoi and ms_md_sd: API 13
                capNhatMucDichSuDung(lstMDSDByMN.get(i).getMs_md_sd(), sms_mnoi, nDinhMuc);

            }
        }

        return true;
    }

    public String TinhLaiSTT(String sCachTinh, String DinhMucCB, String sSoNguoi, String sSoHo,  Date TuNgay,  Date DenNgay, String SoNgayNghi) {
        double DinhMuc;
        DinhMuc = 0;
        double DMCB;
        double SoNguoi;
        long SoNgay = 0;
        int SoHo;
        //DMCB = Convert.ToInt32((DinhMucCB == "") ? "0" : DinhMucCB);
        DMCB = Double.parseDouble((DinhMucCB == "") ? "0" : DinhMucCB);
        SoNguoi = Double.parseDouble((sSoNguoi == "") ? "0" : sSoNguoi);
        SoHo = Integer.parseInt((sSoHo == "") ? "0" : sSoHo);
        //SoNgay = DateDiff(DateInterval.Day, Convert.ToDateTime(TuNgay), Convert.ToDateTime(DenNgay), System.Globalization.DateTimeFormatInfo.CurrentInfo.FirstDayOfWeek);
        SoNgay = tinhSoNgay(TuNgay, DenNgay);
        //SoNgay = SoNgay - Convert.ToInt64(g_GetDatatoValue(SoNgayNghi,"0"));
        SoNgay = SoNgay - Integer.parseInt((SoNgayNghi == "") ? "0" : SoNgayNghi);
        switch (common.cat2kitucuoi(sCachTinh)) {
            case "1":
                DinhMuc = Math.round(((DMCB * SoNguoi * SoNgay) / 30) * 10.0) / 10.0;
                break;
            case "2":
                DinhMuc = Math.round((DMCB * SoNgay) * 10.0) / 10.0;
                break;
            case "3":
                DinhMuc = Math.round((DMCB * SoNgay * SoNguoi) * 10.0) / 10.0;
                break;
            case "4":
                DinhMuc = DMCB;
                break;
            case "5":
                DinhMuc = Math.round(((DMCB * SoNgay * SoHo) / 30) * 10.0) / 10.0;
                break;
        }
        return String.valueOf(DinhMuc);

    }


    private void capNhatMucDichSuDung(Integer ms_md_sd, Integer ms_mnoi, Double dinh_muc) {


        UpdateMSSD request = new UpdateMSSD(ms_md_sd, ms_mnoi, dinh_muc);
        //Fix03062019
        try {
            BarcodeReaderApiManager.getInstance().invoiceApi().capNhatMucDichSuDung("" + ms_mnoi, request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //EndFix03062019
        /*BarcodeReaderApiManager.getInstance().invoiceApi().capNhatMucDichSuDung("" + ms_mnoi, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("capNhatMucDichSuDung", "cập nhật MDSD thành công");

                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {
                Log.i("capNhatMucDichSuDung", "err : " + t.getMessage());

            }
        });
        */


    }


    private void capNhatMoiNoi(Integer ms_mnoi, Double tien_dho_tra) {


        UpdateMoiNoi request = new UpdateMoiNoi(ms_mnoi, tien_dho_tra);
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().invoiceApi().capNhatMoiNoi("" + ms_mnoi, request).execute().isSuccessful();
            if(!rp){
                deleteInvoice( So_Hoa_Don);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().invoiceApi().capNhatMoiNoi("" + ms_mnoi, request).enqueue(new Callback<BarcodeResponse>() {
            @Override
            public void onResponse(Call<BarcodeResponse> call, Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                }else{
                    Log.d("deleteInvoice", "216");
                    deleteInvoice( So_Hoa_Don);
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });
        */
        //Test
        //return true;
    }


    private void updateInvoice(long so_hd, Double tong_tien, Double tien_thue_vat, Double so_du, Double ptn, Double tong_tien_DVTN) {

        UpdateInvoice request = new UpdateInvoice(so_hd, tong_tien, tien_thue_vat, so_du, ptn, tong_tien_DVTN);
        //Fix03062019
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().invoiceApi().updateInvoice(So_Hoa_Don, request).execute().isSuccessful();
            if(rp==true) {
                updateInvoiceOK = true;
            }else {
                deleteInvoice( So_Hoa_Don);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //EndFix03062019

    }
    JSONObject jsonThamSoThanhToan = new JSONObject();

    public void LayThamSoThanhToan(Integer ms_mnoi, Integer ms_tk1) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ms_tk = ms_tk1;
        String url = common.URL_API + "/LayThamSoThanhToan?ms_mnoi=" + ms_mnoi ;

        JSONArray jsonArray = new JSONArray();
        double Luong_tinh_hd = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        try {
            jsonArray = ReadJson.readJSonArrayFromURL(url);
            if (jsonArray.length() > 0) {
                jsonThamSoThanhToan = jsonArray.getJSONObject(0);
                Ms_MoiNoi = jsonThamSoThanhToan.getInt("ms_mnoi");
                sSoNguoi = common.GetDataToValue(jsonThamSoThanhToan.getString("so_nguoi_o"),"");
                sCachTinh=common.GetDataToValue(jsonThamSoThanhToan.getString("ms_cach_tinh"),"");
                DinhMucCB=common.GetDataToValue(jsonThamSoThanhToan.getString("dinh_muc_cb"),"");
                sSoHo=common.GetDataToValue(jsonThamSoThanhToan.getString("so_ho"),"");
                String tieu_thu_tt=common.GetDataToValue(jsonThamSoThanhToan.getString("tieu_thu_tt"),"");
                if (!tieu_thu_tt.equals("")) {
                    Tieu_Thu_Toi_Thieu = new BigDecimal(tieu_thu_tt);
                } else {
                    Tieu_Thu_Toi_Thieu = new BigDecimal(0);
                }
                String gia_TTDH = common.GetDataToValue(jsonThamSoThanhToan.getString("gia_TTDH"),"");
                if (!gia_TTDH.equals("")) {
                    TraTienDH = Double.parseDouble(gia_TTDH);
                } else {
                    TraTienDH = 0;
                }

                String ptn_TTDH = common.GetDataToValue(jsonThamSoThanhToan.getString("ptn_TTDH"),"");
                //jsonThamSoThanhToan.getString("ptn_TTDH");
                if (!ptn_TTDH.equals("")) {
                    TraTienDH_ptn = Double.parseDouble(ptn_TTDH);
                } else {
                    TraTienDH_ptn = 0;
                }

                String ms_dong_TTDH=common.GetDataToValue(jsonThamSoThanhToan.getString("ms_dong_TTDH"),"");
                if (!ms_dong_TTDH.equals("")) {
                    Ms_Dong_TTDH = (int)Double.parseDouble(ms_dong_TTDH);
                } else {
                    Ms_Dong_TTDH = 0;
                }
                String strVAT_TTDH =common.GetDataToValue(jsonThamSoThanhToan.getString("VAT_TTDH"),"");
                //jsonThamSoThanhToan.getString("VAT_TTDH");
                if (!strVAT_TTDH.equals("")) {
                    VAT_TTDH = Double.parseDouble(strVAT_TTDH);
                } else {
                    VAT_TTDH = 0;
                }
                String tinh_ptn = common.GetDataToValue(jsonThamSoThanhToan.getString("tinh_ptn"),"");
                //jsonThamSoThanhToan.getString("tinh_ptn");
                if (tinh_ptn.equals("true")) {
                    Tinh_PTN = 1;
                } else {
                    Tinh_PTN = 0;
                }
                String tinh_luy_ke =common.GetDataToValue(jsonThamSoThanhToan.getString("tinh_luy_ke"),"");
                //jsonThamSoThanhToan.getString("tinh_luy_ke");
                if (tinh_luy_ke.equals("true")) {
                    Tinh_Luy_Ke = 1;
                } else {
                    Tinh_Luy_Ke = 0;
                }
                //Điểm dùng có đồng hồ
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                int ms_loai_mn = jsonThamSoThanhToan.getInt("ms_loai_mn");
                if (ms_loai_mn == 2) {
                    //tu ngay
                    String ngay_doc_cu =common.GetDataToValue(jsonThamSoThanhToan.getString("ngay_doc_cu"),"");
                    //jsonThamSoThanhToan.getString("ngay_doc_cu");
                    if (!ngay_doc_cu.equals("")) {

                        try {
                            Tu_Ngay = format1.parse(common.cat10kitucuoi(ngay_doc_cu));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //den ngay
                    String ngay_doc_moi = jsonThamSoThanhToan.getString("ngay_doc_moi");
                    //String ngay_doc_moi = "2019-07-03 09:59:00";
                    if (!ngay_doc_moi.equals("")) {

                        try {
                            Den_Ngay = format1.parse(common.cat10kitucuoi(ngay_doc_moi));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }//Điểm dùng không đồng hồ

                else if (ms_loai_mn == 3) {
                    //tu ngay
                    String tinh_tu =common.GetDataToValue(jsonThamSoThanhToan.getString("tinh_tu"),"");
                    //jsonThamSoThanhToan.getString("tinh_tu");
                    if (!tinh_tu.equals("")) {

                        try {
                            Tu_Ngay = format1.parse(common.cat10kitucuoi(tinh_tu));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //den ngay
                    String tinh_den =common.GetDataToValue(jsonThamSoThanhToan.getString("tinh_den"),"");
                    //jsonThamSoThanhToan.getString("tinh_den");
                    if (!tinh_den.equals("")) {
                        try {
                            Den_Ngay = format1.parse(common.cat10kitucuoi(tinh_den));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (Tinh_Luy_Ke == 0) {
                    if (!sCachTinh.equals("")) {
                        String ngay_nghi =common.GetDataToValue(jsonThamSoThanhToan.getString("ngay_nghi"),"");
                        //jsonThamSoThanhToan.getString("ngay_nghi");
                        if (ngay_nghi.equals("")||ngay_nghi.equals("null")) ngay_nghi = "0";
                        String sDinhMuc_tt = TinhLaiSTT(sCachTinh, DinhMucCB, sSoNguoi, sSoHo, Tu_Ngay, Den_Ngay, ngay_nghi);
                        if (TinhLaiDM(Ms_MoiNoi, Double.parseDouble(sDinhMuc_tt)) == false) {
                            // MessageBox.Show("Không tính lại được định mức cho điểm dùng (" + Ms_MoiNoi.ToString() + ") . Vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                            // continue;
                        }
                    }
                }

                //Bước 3:

                if (ms_loai_mn == 2) { //–Điểm dùng có đồng hồ
                    String so_tthu = common.GetDataToValue(jsonThamSoThanhToan.getString("so_tthu"),"");
                    //jsonThamSoThanhToan.getString("so_tthu");
                    if (!so_tthu.equals("")) {
                        TieuThuNull = false;
                        //chi so cu
                        String chi_so_cu = common.GetDataToValue(jsonThamSoThanhToan.getString("chi_so_cu"),"");
                        //jsonThamSoThanhToan.getString("chi_so_cu");
                        if (!chi_so_cu.equals("")) {
                            Chi_So_Cu = Double.parseDouble(chi_so_cu);
                        } else {
                            Chi_So_Cu = 0;
                        }
                        //chi so moi
                        String chi_so_moi = common.GetDataToValue(jsonThamSoThanhToan.getString("chi_so_moi"),"");
                        //jsonThamSoThanhToan.getString("chi_so_moi");
                        if (!chi_so_moi.equals("")) {
                            Chi_So_Moi = Double.parseDouble(chi_so_moi);
                        } else {
                            Chi_So_Moi = 0;
                        }
                        //so tieu thu
                        So_Tieu_Thu = Double.parseDouble(so_tthu);

                        //so ngay
                        So_Ngay = TimeUnit.DAYS.convert(Den_Ngay.getTime() - Tu_Ngay.getTime(), TimeUnit.MILLISECONDS);
                        Tieu_Thu_Toi_Thieu = (Tieu_Thu_Toi_Thieu.multiply(BigDecimal.valueOf(So_Ngay))).divide(BigDecimal.valueOf(30)).setScale(1, BigDecimal.ROUND_HALF_UP); //[làm tròn dạng 16.4]
                        //luu lai gia tri so_tieu_thu
                        Tieu_Thu_Toi_Thieu_Chung = Tieu_Thu_Toi_Thieu;
                        //Loc ra cac diem dung tính lại hóa đơn có số tiêu thụ =0 và không có tiền đồng hồ
                        if ((So_Tieu_Thu == 0) && (So_Ngay < 25)) {
                            //continue;
                        }
                    } else {
                        //Nếu không đọc được số tiêu thụ thì
                        TieuThuNull = true;
                        //tien_dho_tra = ( ["tien_dho_tra"],"") != "") ? ["tien_dho_tra"].ToString()) : 0;
                        String strtien_dho_tra = common.GetDataToValue(jsonThamSoThanhToan.getString("tien_dho_tra"),"");
                        //jsonThamSoThanhToan.getString("tien_dho_tra");
                        if (strtien_dho_tra.equals("") || strtien_dho_tra.equals("null")) {
                            tien_dho_tra = 0;
                        } else {
                            tien_dho_tra = Double.parseDouble(strtien_dho_tra);
                        }

                        //tien_dho = ["tien_dho"],"") != "") ? ["tien_dho"].ToString()) : 0;
                        String strtien_dho = common.GetDataToValue(jsonThamSoThanhToan.getString("tien_dho"),"");
                        //jsonThamSoThanhToan.getString("tien_dho");
                        if (strtien_dho.equals("") || strtien_dho.equals("null")) {
                            tien_dho = 0;
                        } else {
                            tien_dho = Double.parseDouble(strtien_dho);
                        }
                        if (tien_dho <= (tien_dho_tra - TraTienDH_Cu) || TraTienDH <= 0) {
                            double Tmp = tien_dho_tra - TraTienDH_Cu;
                            //strSQL = "update moi_noi set tien_dho_tra='"+ Tmp.ToString() +"' where ms_mnoi='"+ Ms_MoiNoi +"'"; API 4
                            //API 4
                            capNhatMoiNoi(Ms_MoiNoi, Tmp);

                        }
                    }


                    if (TieuThuNull == true) {

                        //  ms_loai_hd, ms_mnoi, ms_kieu_tt, ms_tk, ngay_hd, ngay_in, so_lan_in, tu_ngay, den_ngay, chi_so_cu, chi_so_moi,
                        // luong_tinh_hd, so_tieu_thu, tong_tien, tien_thue_vat, tong_nhan, so_du, ngay_thu, ms_ttrang, ptn, ngay_huy, tong_tien_DVTN
                        //API 8: (1, Ms_MoiNoi,null,ms_tky, DateTime.Today,null,0,null,null,null,null,0,0,0,0,0,0,null,1,0,null,0);
                        insertInvoice(1, Ms_MoiNoi, null, ms_tk, sdf.format(date).toString(), null, 0, null, null, null, null, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, null, 1, (double) 0, null, (double) 0);
                    } else {
                        //Biến tạm Luong_tinh_hd:

                        if (So_Tieu_Thu > Tieu_Thu_Toi_Thieu.doubleValue()) {
                            Luong_tinh_hd = So_Tieu_Thu;
                        } else {
                            Luong_tinh_hd = Tieu_Thu_Toi_Thieu.doubleValue();
                        }
                        //API 8: (1, Ms_MoiNoi,null,ms_tky, DateTime.Today,null,0,Tu_Ngay,Den_Ngay, Chi_So_Cu, Chi_So_Moi, Luong_tinh_hd, So_Tieu_Thu,0,0,0,0,null,1,0,null,0);
                        insertInvoice(1, Ms_MoiNoi, null, ms_tk, sdf.format(date).toString(), null, 0, sdf.format(Tu_Ngay), sdf.format(Den_Ngay), Chi_So_Cu, Chi_So_Moi, Luong_tinh_hd, So_Tieu_Thu, (double) 0, (double) 0, (double) 0, (double) 0, null, 1, (double) 0, null, (double) 0);
                    }

                    //API x:Lấy số hóa đơn mới của bản ghi insert bảng hoa_do
                    /*laySoHoaDonByMnoiTky(Ms_MoiNoi, ms_tk);
                    if (So_Hoa_Don == 0) {
                        //this.cancel(true);
                        return;
                    }*/
                    laySoHoaDonByMnoiTky(Ms_MoiNoi, ms_tk);
                    Log.i("SHD0", ""+So_Hoa_Don);
                    if (So_Hoa_Don == 0) {
                        //this.cancel(true);
                        return;
                    }


                }

                if (ms_loai_mn == 3) {
                    String strso_t_thu = common.GetDataToValue(jsonThamSoThanhToan.getString("so_t_thu"),"");
                    //jsonThamSoThanhToan.getString("so_t_thu");
                    if (!strso_t_thu.equals("")) {
                        TieuThuNull = false;
                        So_Tieu_Thu = Double.parseDouble(strso_t_thu);
                        //so ngay
                        So_Ngay = TimeUnit.DAYS.convert(Den_Ngay.getTime() - Tu_Ngay.getTime(), TimeUnit.MILLISECONDS);
                        String ngay_nghi =common.GetDataToValue(jsonThamSoThanhToan.getString("ngay_nghi"),"");
                        //jsonThamSoThanhToan.getString("ngay_nghi");
                        So_Ngay = So_Ngay - Long.valueOf(ngay_nghi);
                        Tieu_Thu_Toi_Thieu = (Tieu_Thu_Toi_Thieu.multiply(BigDecimal.valueOf(So_Ngay))).divide(BigDecimal.valueOf(30)).setScale(0, BigDecimal.ROUND_HALF_UP);
                        //luu lai gia tri so_tieu_thu
                        Tieu_Thu_Toi_Thieu_Chung = Tieu_Thu_Toi_Thieu;
                    } else {
                        TieuThuNull = true;
                    }

                    if (TieuThuNull == true) {
                        //API 8: (1, Ms_MoiNoi,null,ms_tky, DateTime.Today,null,0,null,null,null,null,0,0,0,0,0,0,null,1,0,null,0);
                        insertInvoice(1, Ms_MoiNoi, null, ms_tk, sdf.format(date).toString(), null, 0, null, null, null, null, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, (double) 0, null, 1, (double) 0, null, (double) 0);
                    } else {
                        //API 8: (1, Ms_MoiNoi,null,ms_tky, DateTime.Today,null,0,Tu_Ngay,Den_Ngay, null, null, Luong_tinh_hd, So_Tieu_Thu,0,0,0,0,null,1,0,null,0);
                        insertInvoice(1, Ms_MoiNoi, null, ms_tk, sdf.format(date).toString(), null, 0, Tu_Ngay.toString(), Den_Ngay.toString(), null, null, Luong_tinh_hd, So_Tieu_Thu, (double) 0, (double) 0, (double) 0, (double) 0, null, 1, (double) 0, null, (double) 0);
                    }
                    //Bo sung sau
                    //API x:Lấy số hóa đơn mới của bản ghi insert bảng hoa_don
                    /*laySoHoaDonByMnoiTky(Ms_MoiNoi, ms_tk);
                    if (So_Hoa_Don == 0) {
                        //this.cancel(true);
                        danhdauvitri = 1;
                        return;
                    }*/
                    laySoHoaDonByMnoiTky(Ms_MoiNoi, ms_tk);
                    Log.i("SHD0", ""+So_Hoa_Don);
                    if (So_Hoa_Don == 0) {
                        //this.cancel(true);
                        return;
                    }
                } // –Điểm dùng không có đồng hồ

                    /*
                     * B7. Lập chi tiết hóa đơn cho hóa đơn này
                        Gán 3 biến tính tổng:
                            Tong_Tien=0
                            Tong_VAT=0
                            Tong_Ptn=0
                     */
                Tong_Ptn = 0;
                Tong_Tien = 0;
                Tong_VAT = 0;
                Tong_Tien_DVTN = 0;
                     /*
                     * A. Nếu: tien_dho>(tien_dho_tra- TraTienDH_Cu)  thì:
                    1. Nếu TraTienDH>0  -- Có thu tiền đồng hồ trả dần
                        a. Nếu  tien_dho>=(tien_dho_tra- TraTienDH_Cu) + TraTienDH
                            Gán biến Tong_Tien=TraTienDH
                        b. Trái lại (tien_dho<(tien_dho_tra- TraTienDH_Cu) + TraTienDH)
                            Gán biến Tong_Tien=(tien_dho- tien_dho_tra+ TraTienDH_Cu)
                        c. Thêm 1 bản ghi vào  bảng chi_tiet_hdon như sau:
                            ms_dong= Ms_Dong_TTDH
                            so_hd=SO_HOA_DON
                            so_don_vi=1
                            tien_phai_thu= Tong_Tien
                            tien_thue_vat= round(Tong_Tien * VAT_TTDH/100)
                            la_gia_moi=1
                            ptn=iif(Tinh_PTN, round(TraTienDH_ptn* Tong_Tien/100),0)
                        d. Update lại bảng moi_noi với
                            tien_dho_tra = tien_dho_tra- TraTienDH_Cu + Tong_Tien
                        e. Gán:
                            Tong_VAT=round(Tong_Tien*VAT_TTDH/100)
                            Tong_PTN= iif(Tinh_PTN, round(TraTienDH_ptn* Tong_Tien/100),0)
                     */
                //sonnt edit 27/07/2009

                String strtien_dho_tra = null;
                String strtien_dho = null;
                try {
                    strtien_dho_tra = common.GetDataToValue(jsonThamSoThanhToan.getString("tien_dho_tra"),"");
                    strtien_dho = common.GetDataToValue(jsonThamSoThanhToan.getString("tien_dho"),"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //jsonThamSoThanhToan.getString("tien_dho_tra");

                //jsonThamSoThanhToan.getString("tien_dho");
                tien_dho_tra = Double.parseDouble((strtien_dho_tra == "") ? "0" : strtien_dho_tra);
                tien_dho = Double.parseDouble((strtien_dho == "") ? "0" : strtien_dho);
                //A:
                if (tien_dho > (tien_dho_tra - TraTienDH_Cu)) {
                    if (TraTienDH > 0) {
                        //a.
                        if (tien_dho >= ((tien_dho_tra - TraTienDH_Cu) + TraTienDH)) {
                            Tong_Tien = TraTienDH;
                        }
                        //b.
                        else {
                            Tong_Tien = (tien_dho - tien_dho_tra + TraTienDH_Cu);
                        }
                        //c.
                        double tien_thue_vat = Math.round(Tong_Tien * VAT_TTDH / 100);
                        //API 15
                        insertInvoiceDetailBrief(Ms_Dong_TTDH, So_Hoa_Don, 1, Tong_Tien, Tong_Tien, tien_thue_vat, 1);
                        //danhdauvitri = 2 : Lỗi không thêm bản ghi vào bảng chi tiết hóa đơn(547)
                        //Nếu lỗi:
                        // 1. xóa bản ghi trong bảng hóa đơn
                        // 2. Thoát tính tiền
                        /*d. Update lại bảng moi_noi với
                            tien_dho_tra = tien_dho_tra- TraTienDH_Cu + Tong_Tien
                            */
                        double tmpTien;
                        tmpTien = Math.round((tien_dho_tra - TraTienDH_Cu + Tong_Tien));
                        //API 4
                        danhdauvitri = 3;
                        capNhatMoiNoi(Ms_MoiNoi, tmpTien);
                        Log.d("Error", "Lỗi kết nối[1273]. Vui lòng kiểm tra lại!");
                        //danhdauvitri = 3 : Lỗi không cập nhật thông tin danh bạ(562)
                        //1.API 5 : xóa bản ghi trong bảng hóa đơn
                        // 2.Thoát tính tiền
                        //this.cancel(true);


                        Tong_VAT = Math.round(Tong_Tien * VAT_TTDH / 100);

                    } else {

                        if (TraTienDH_Cu > 0) {
                            danhdauvitri = 4;
                            capNhatMoiNoi(Ms_MoiNoi, tien_dho_tra - TraTienDH_Cu);
                            //if (!capNhatMoiNoi(Ms_MoiNoi, tien_dho_tra - TraTienDH_Cu)) {
                            //danhdauvitri = 4 : Lỗi không cập nhật thông tin danh bạ(579)
                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                            //deleteInvoice( So_Hoa_Don);
                            // 2.Thoát tính tiền
                            //this.cancel(true);
                            //}
                            TraTienDH_Cu = 0;
                        }

                    }
                }

                //B Nếu TieuThuNull=False -- Có tính tiêu thụ

                if (TieuThuNull == false) {
                    if (So_Tieu_Thu == 0) {
                        //Chỉ thêm dữ liệu về Chi phí duy trì đấu nối Chi_Phi_Duy_Tri_Dau_Noi
                        //Lấy thông tin về giá tiêu thụ tối thiểu gọi hàm g_GetDongHDInfo (5*)
                        //int g_MsDong_PHI_DAU_NOI = 400;
                        g_GetDongHDInfo(g_MsDong_PHI_DAU_NOI);
                        //Xác định số tiêu thụ
                        //Mốc xác định từ 04/05/2012=g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi
                        //Phần nguyên chia cho 30, phần dư >=16 là +1, nhỏ hơn 16 là +0
                        long nSo_Thang_Chi_Phi_Duy_Tri_Dau_Noi = 0;
                        long nSo_Ngay = 0;

                        if (Den_Ngay.compareTo(g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi) > 0) {
                            if (Tu_Ngay.compareTo(g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi) < 0) {
                                //Tu_Ngay = g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi;
                                //TimeUnit.DAYS.convert(date2.getTime() - date1.getTime(), TimeUnit.MILLISECONDS);
                                nSo_Ngay = TimeUnit.DAYS.convert(Den_Ngay.getTime() - g_Moc_Ngay_Tinh_Chi_Phi_Duy_Tri_Dau_Noi.getTime(), TimeUnit.MILLISECONDS);
                            } else {
                                nSo_Ngay = TimeUnit.DAYS.convert(Den_Ngay.getTime() - Tu_Ngay.getTime(), TimeUnit.MILLISECONDS);
                            }
                        }


                        nSo_Thang_Chi_Phi_Duy_Tri_Dau_Noi = nSo_Ngay / 30;
                        if ((nSo_Ngay - nSo_Thang_Chi_Phi_Duy_Tri_Dau_Noi * 30) >= 25) {
                            nSo_Thang_Chi_Phi_Duy_Tri_Dau_Noi += 1;
                        }

                        //Tính tiền phí duy trì đấu nối
                        danhdauvitri = 5;
                        if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Ngay_Ap_Dung, (double) nSo_Thang_Chi_Phi_Duy_Tri_Dau_Noi, g_MsDong_PHI_DAU_NOI, Gia_Moi, Gia_Cu, Thue_VAT, Ptn_Cu, Ptn_moi) == false) {
                            //danhdauvitri = 5 : Lỗi không tính tiền nước khách hàng(627)
                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                            Log.d("deleteInvoice", "662");
                            deleteInvoice( So_Hoa_Don);
                            // 2.Thoát tính tiền
                            return;
                        }


                    } else {
                        //API 9
                        getUsePurposeByMoinoi(Ms_MoiNoi);
                        if (lstUsePurposeByMoinoi.size() > 0) {
                            Tong_So_Tieu_Thu = So_Tieu_Thu;

                            if (Tinh_Luy_Ke == 0) {
                                for (int j = 0; j < lstUsePurposeByMoinoi.size(); j++) {

                                    //Double.parseDouble((DinhMucCB == "") ? "0" : DinhMucCB);
                                    double Gia_Chinh_Ma = lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ma() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ma();
                                    double Dinh_Muc = lstUsePurposeByMoinoi.get(j).getDinh_Muc() == null ? 0 : lstUsePurposeByMoinoi.get(j).getDinh_Muc();
                                    Date Gia_Chinh_Ngay_Ap_Dung = lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ngay_Ap_Dung();
                                    double Gia_Chinh_TienM = lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienM();
                                    double Gia_Chinh_TienC = lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienC();
                                    double Gia_Chinh_VAT = lstUsePurposeByMoinoi.get(j).getGia_Chinh_VAT() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_VAT();
                                    double Gia_Chinh_PTNM = lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNM();
                                    double Gia_Chinh_PTNC = lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNC();

                                    double Gia_Vuot_Ma = lstUsePurposeByMoinoi.get(j).getGia_Vuot_Ma() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_Ma();
                                    Date Gia_Vuot_Ngay_Ap_Dung = lstUsePurposeByMoinoi.get(j).getGia_Vuot_Ngay_Ap_Dung(); // Có thể null chú ý chỗ này để bắt có thể khi null gán 1/1/3000

                                    double Gia_Vuot_TienM = lstUsePurposeByMoinoi.get(j).getGia_Vuot_TienM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_TienM();
                                    double Gia_Vuot_TienC = lstUsePurposeByMoinoi.get(j).getGia_Vuot_TienC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_TienC();
                                    double Gia_Vuot_VAT = lstUsePurposeByMoinoi.get(j).getGia_Vuot_VAT() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_VAT();
                                    double Gia_Vuot_PTNM = lstUsePurposeByMoinoi.get(j).getGia_Vuot_PTNM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_PTNM();
                                    double Gia_Vuot_PTNC = lstUsePurposeByMoinoi.get(j).getGia_Vuot_PTNC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Vuot_PTNC();

                                    double Tmp_ = lstUsePurposeByMoinoi.get(j).getPhan_Tram() == null ? 0 : lstUsePurposeByMoinoi.get(j).getPhan_Tram();
                                    double Phan_Tram = (int) Tmp_;
                                    //Xử lý làm tròn
                                    Double temp = (double) Math.round((Phan_Tram * Tong_So_Tieu_Thu / 100) * 100.0) / 100.0;
                                    String number = temp.toString();
                                    String number1 = number.substring(number.indexOf(".")+1,number.indexOf(".")+2);
                                    int num1 = Integer.parseInt(number1);
                                    int num2 = 0;
                                    if(number.substring(number.indexOf(".")+1).length()==2)
                                    {
                                        String number2 = number.substring(number.indexOf(".")+2);
                                        num2 = Integer.parseInt(number2);
                                    }
                                    if(num1%2 ==0 && num2 == 5){
                                        temp = temp - 0.05;
                                    }
                                    //End xử lý làm tròn
                                    //So_Tieu_Thu =(double) Math.round((Phan_Tram * Tong_So_Tieu_Thu / 100) * 10.0) / 10.0;
                                    So_Tieu_Thu =(double) Math.round(temp * 10.0) / 10.0;

                                    if (Gia_Vuot_Ma == 0) {
                                        //Thêm 01 bản ghi theo giá chính
                                        //Gọi hàm
                                        danhdauvitri = 6;
                                        //danhdauvitri = 6 : Lỗi không tính tiền nước khách hàng(670)
                                        if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, (Date) Gia_Chinh_Ngay_Ap_Dung, So_Tieu_Thu, Gia_Chinh_Ma, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC) == false) {
                                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                            deleteInvoice( So_Hoa_Don);
                                            // 2.Thoát tính tiền
                                            //this.cancel(true);
                                            return;
                                        }
                                    } else {
                                        //có giá vượt
                                        double So_TTC = So_Tieu_Thu;
                                        double So_TTV = 0;
                                        if (So_Tieu_Thu > Dinh_Muc) {
                                            So_TTC = Dinh_Muc;
                                            So_TTV =(double) Math.round((So_Tieu_Thu - Dinh_Muc) * 10.0) / 10.0;
                                        }
                                        if (So_TTC > 0) {
                                            //danhdauvitri = 7 : Lỗi không tính tiền nước khách hàng(688)
                                            danhdauvitri = 7;
                                            if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Gia_Chinh_Ngay_Ap_Dung, So_TTC, Gia_Chinh_Ma, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC) == false) {
                                                //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                                Log.d("deleteInvoice", "742");
                                                deleteInvoice( So_Hoa_Don);
                                                // 2.Thoát tính tiền
                                                //this.cancel(true);
                                                return;
                                            }
                                        }
                                        if (So_TTV > 0) {
                                            //danhdauvitri = 8 : Lỗi không tính tiền nước khách hàng(704)
                                            danhdauvitri = 8;
                                            if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Gia_Vuot_Ngay_Ap_Dung, So_TTV, Gia_Vuot_Ma, Gia_Vuot_TienM, Gia_Vuot_TienC, Gia_Vuot_VAT, Gia_Vuot_PTNM, Gia_Vuot_PTNC) == false) {
                                                //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                                Log.d("deleteInvoice", "754");
                                                deleteInvoice( So_Hoa_Don);
                                                // 2.Thoát tính tiền
                                                //this.cancel(true);
                                                return;
                                            }
                                        }
                                    }
                                }

                            } else {
                                //tính lũy kế
                                for (int j = 0; j < lstUsePurposeByMoinoi.size(); j++) {

                                    double Gia_Chinh_Ma = lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ma() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ma();
                                    double Dinh_Muc = lstUsePurposeByMoinoi.get(j).getDinh_Muc() == null ? 0 : lstUsePurposeByMoinoi.get(j).getDinh_Muc();
                                    Date Gia_Chinh_Ngay_Ap_Dung = lstUsePurposeByMoinoi.get(j).getGia_Chinh_Ngay_Ap_Dung();
                                    double Gia_Chinh_TienM = lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienM();
                                    double Gia_Chinh_TienC = lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_TienC();
                                    double Gia_Chinh_VAT = lstUsePurposeByMoinoi.get(j).getGia_Chinh_VAT() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_VAT();
                                    double Gia_Chinh_PTNM = lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNM() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNM();
                                    double Gia_Chinh_PTNC = lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNC() == null ? 0 : lstUsePurposeByMoinoi.get(j).getGia_Chinh_PTNC();
                                    double Tmp_ = lstUsePurposeByMoinoi.get(j).getPhan_Tram() == null ? 0 : lstUsePurposeByMoinoi.get(j).getPhan_Tram();
                                    if (Dinh_Muc < 0) {
                                        //them 1 bản ghi giá chính
                                        //danhdauvitri = 9 : Lỗi không tính tiền nước khách hàng(733)
                                        danhdauvitri = 9;
                                        if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Gia_Chinh_Ngay_Ap_Dung, So_Tieu_Thu, Gia_Chinh_Ma, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC) == false) {
                                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                            Log.d("deleteInvoice", "784");
                                            deleteInvoice( So_Hoa_Don);
                                            // 2.Thoát tính tiền
                                            //this.cancel(true);
                                            return;
                                        }
                                        break;
                                    } else if (Dinh_Muc >= So_Tieu_Thu) {
                                        //danhdauvitri = 10 : Lỗi không tính tiền nước khách hàng(738)
                                        danhdauvitri = 10;
                                        if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Gia_Chinh_Ngay_Ap_Dung, So_Tieu_Thu, Gia_Chinh_Ma, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC) == false) {
                                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                            Log.d("deleteInvoice", "795");
                                            deleteInvoice( So_Hoa_Don);
                                            // 2.Thoát tính tiền
                                            //this.cancel(true);
                                            return;
                                        }
                                        break;
                                    } else {
                                        //danhdauvitri = 11 : Lỗi không tính tiền nước khách hàng(757)
                                        danhdauvitri = 11;
                                        if (TinhTien(So_Hoa_Don, Tu_Ngay, Den_Ngay, Gia_Chinh_Ngay_Ap_Dung, Dinh_Muc, Gia_Chinh_Ma, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC) == false) {
                                            //1.API 5 : xóa bản ghi trong bảng hóa đơn
                                            Log.d("deleteInvoice", "808");
                                            deleteInvoice( So_Hoa_Don);
                                            // 2.Thoát tính tiền
                                            //this.cancel(true);
                                            return;
                                        }
                                        //Trừ số tiêu thụ cho lũy kế bản ghi sau
                                        So_Tieu_Thu = So_Tieu_Thu - Dinh_Muc;
                                    }
                                }
                            }
                        }


                    }
                }


                    /*
                     * B8. Cập nhật lại 3 biến tổng vào hóa đơn chính hoa_don
                    Update hoa_don set tong_tien=Tong_Tien, So_du=Tong_Tien, tien_thue_vat=Tong_VAT, ptn=Tong_PTN
                    Where So_Hd=So_Hoa_Don

                    strSQL = "Update hoa_don set tong_tien=" + Tong_Tien + ",tong_tien_DVTN='"+ Tong_Tien_DVTN +"', So_du=" + (Tong_Tien + Tong_Tien_DVTN + Tong_VAT + Tong_Ptn) + ", tien_thue_vat=" + Tong_VAT + ", ptn=" + Tong_Ptn + " " +
                            " Where So_Hd='" + So_Hoa_Don + "'";*/
                //UpdateInvoice(so_hd, tong_tien, tien_thue_vat, so_du, ptn, tong_tien_DVTN);
                Double So_Du=(Tong_Tien + Tong_Tien_DVTN + Tong_VAT + Tong_Ptn);


                updateInvoice(So_Hoa_Don, Tong_Tien, Tong_VAT, So_Du, Tong_Ptn, Tong_Tien_DVTN);
                //if (!updateInvoice(So_Hoa_Don, Tong_Tien, Tong_VAT, So_Du, Tong_Ptn, Tong_Tien_DVTN)) {
                //MessageBox.Show("Lỗi kết nối[1540] khi TT [" + Ms_MoiNoi.ToString() + "]. Vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                //Lỗi: Thông báo; return;
                //this.cancel(true);
                //}



            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void updateSauTinhTien(){

    }


    private Double g_LamTron(Double DataIn, Double LamTron)
    {
        Double nKQ;
        Double nSoDu;
        Double nSoMu;
        nSoMu=LamTron+1;
        nKQ=Math.pow(10,nSoMu)*DataIn;
        nSoDu=nKQ%10;
        if (nSoDu>=5)
        {
            // nKQ=Math.round(((nKQ+1)/(Math.pow(10,nSoMu))),(double)LamTron);
        }
        else {
            // nKQ=Math.round(nKQ/(Math.pow(10,nSoMu)),LamTron);
        }
        return nKQ;

    }

    private Boolean TinhTien(
            long So_Hoa_Don,
            Date Tu_Ngay,
            Date Den_Ngay,
            Date Ngay_Ap_Dung,
            double So_Tieu_Thu,
            double iMs_Dong,
            double Gia_Moi,
            double Gia_Cu,
            double Thue_VAT,
            double Ptn_Moi,
            double Ptn_Cu
    ) {

        try {
            if (Ngay_Ap_Dung.compareTo(Den_Ngay) <= 0) {
                if (Ngay_Ap_Dung.compareTo(Tu_Ngay) < 0) {
                /*
                         * Thêm mới 01 bản ghi vào chi tiết hóa đơn
							Tmp_Tien= round(Gia_Moi*So_Tieu_Thu);
							ms_dong=iMs_Dong
							so_hd=SO_HOA_DON
							so_don_vi= So_Tieu_Thu
							tien_phai_thu= Tmp_Tien
							tien_thue_vat= round(Tmp_Tien * Thue_VAT/100)
							la_gia_moi=1
							ptn=iif(Tinh_PTN,round(Ptn_Moi* Tmp_Tien/100),0)

                         */
                    double tmp_tien = Math.round(Gia_Moi * So_Tieu_Thu);
                    //end edit
                    //double ptn1 = (Tinh_PTN == 1) ? glb.g_LamTron(Ptn_Moi * tmp_tien / 100, 0) : 0;
                    //kiem tra xem có tính giá thoát nước khong?
                    double SL_Tinh_Gia_DVTN = 0;
                    double Don_Gia_DVTN = 0;
                    double TienVAT_DVTN = 0;
                    double Tien_DVTN = 0;
                    //các biến theo cách tính riêng
                    double Gia_SHGD_MOI = 0; //dùng để tính với trường hợp MDSD là bán buôn nước SHGĐình
                    double Gia_SX_MOI = 0; //dùng để tính với trường hợp MDSD là bán buôn nước SX
                    double Gia_SHNT_MOI = 0; //dùng để tinh với trường hợp MDSD là bán buôn sinh hoạt NT
                    double Gia_BBKD_MOI = 0; //dùng để tinh với trường hợp MDSD là bán buôn kinh doanh
                    if (Tinh_PTN == 1) {
                        //Lấy giá mới, cũ của SH gia đình và bán buôn phục vụ cho cách tính đặc biệt của giá DVTN
                        //API 1
                        getFriceByInvoiceFlow();
                        if (lstMaDong.size() > 0) {
                            //04 bản ghi:
                            //Gia_SHGD_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[0]["Gia_Chinh_TienM"], "0"));
                            Gia_SHGD_MOI = lstMaDong.get(0).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                            //bản ghi thứ 2:
                            // Gia_SHNT_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[1]["Gia_Chinh_TienM"], "0"));
                            Gia_SHNT_MOI = lstMaDong.get(1).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(1).getGia_Chinh_TienM();
                            //bản ghi thứ 3:
                            //Gia_SX_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[2]["Gia_Chinh_TienM"], "0"));
                            Gia_SX_MOI = lstMaDong.get(2).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(2).getGia_Chinh_TienM();
                            //bản ghi thứ 4:
                            Gia_BBKD_MOI = lstMaDong.get(3).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(3).getGia_Chinh_TienM();
                        }

                        if (iMs_Dong == 132 || iMs_Dong == 135) {
                            SL_Tinh_Gia_DVTN = So_Tieu_Thu;  //số lượng tính giá DVTN=số tiêu thụ
                            Don_Gia_DVTN = Math.round(20 * Gia_Moi / 100); //đơn giá DVTN=20% giá nước tương ứng

                        } else if (iMs_Dong == 407) //nếu MĐSD là Sinh hoạt bán buôn
                        {
                            SL_Tinh_Gia_DVTN = So_Tieu_Thu; //số lượng tính giá DVTN=số tiêu thụ
                            //Đơn giá DVTN=20% giá nước SINH HOẠT GIA ĐÌNH (MÃ 132)
                            Don_Gia_DVTN = Math.round(20 * Gia_SHGD_MOI / 100);
                        } else if (iMs_Dong == 416) //NẾU MĐSD LÀ BÁN BUÔN NƯỚC SX
                        {
                            SL_Tinh_Gia_DVTN =(double) (Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ

                            //double d1=Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0;


                            Don_Gia_DVTN = Math.round(20 * Gia_SX_MOI / 100); //Đơn giá DVTN=20% giá nước SẢN XUẤT (MÃ 140)
                        } else if (iMs_Dong == 432) //NẾU MĐSD LÀ BÁN BUÔN KINH DOANH
                        {
                            SL_Tinh_Gia_DVTN =(double) (Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ

                            //double d2=Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0;
                            Don_Gia_DVTN = Math.round(20 * Gia_BBKD_MOI / 100); //Đơn giá DVTN=20% giá nước BBKD (MÃ 432)
                        }
                        //nếu là bán buôn sinh hoạt nông thông
                        else if (iMs_Dong == 136) {
                            SL_Tinh_Gia_DVTN = So_Tieu_Thu;
                            Don_Gia_DVTN = Math.round(20 * Gia_SHNT_MOI / 100);
                        } else {
                            //đối với trường hợp không có Số tiêu thụ và chỉ tính chi phí duy trì đấu nối (ms_dong=400)
                            if (iMs_Dong == 400) {
                                SL_Tinh_Gia_DVTN = 0;
                                Don_Gia_DVTN = 0;
                            } else {
                                SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                //double d3=Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0;
                                Don_Gia_DVTN = Math.round(20 * Gia_Moi / 100); //Đơn giá DVTN=20% giá nước tương ứng
                            }

                        }
                        Tien_DVTN = Math.round(SL_Tinh_Gia_DVTN * Don_Gia_DVTN);
                        TienVAT_DVTN = Math.round(10 * Tien_DVTN / 100); //VAT DVTN=10%

                    }
                    //ms_dong, so_hd, so_don_vi, don_gia, tien_phai_thu, tien_thue_vat, la_gia_moi, ptn, so_don_vi_DVTN, don_gia_DVTN, tien_phai_thu_DVTN
                    double VAT5=Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100));
                    insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_Thu, Gia_Moi, tmp_tien, VAT5, 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN);
                    //if (!insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_Thu, Gia_Moi, tmp_tien, VAT5, 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN)) {
                    //MessageBox.Show("Lỗi kết nối [1609] khi tính hóa đơn [" + So_Hoa_Don.ToString() + "] vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                    //    return false;
                    //}

                    //tổng tiền = tiền nước
                    Tong_Tien = Tong_Tien + tmp_tien;
                    //tổng tiền DVTN=tiền DVTN
                    Tong_Tien_DVTN = Tong_Tien_DVTN + Tien_DVTN;
                    //toongrVAT=VAT của nước tiêu thụ(VAT 5%)
                    Tong_VAT = Tong_VAT + Math.round(tmp_tien * Thue_VAT / 100);
                    //ptn bị thay thế bởi Tổng VAT10 (VAT của giá DVTN) VAT 10%
                    Tong_Ptn = Tong_Ptn + TienVAT_DVTN;


                } else {
                    //Co ca gia cu va gia moi
                    So_NgayC = TimeUnit.DAYS.convert(Ngay_Ap_Dung.getTime() - Tu_Ngay.getTime(), TimeUnit.MILLISECONDS);
                    So_NgayM = So_Ngay - So_NgayC;
                    double So_Tieu_ThuC = (double) (Math.round((So_Tieu_Thu * So_NgayC / So_Ngay) * 10.0) / 10.0);
                    //double d4=Math.round((So_Tieu_Thu * So_NgayC / So_Ngay) * 10.0) / 10.0;
                    double So_Tieu_ThuM = (double) (Math.round((So_Tieu_Thu - So_Tieu_ThuC) * 10.0) / 10.0);
                    //double d5=Math.round((So_Tieu_Thu - So_Tieu_ThuC) * 10.0) / 10.0;

                    //tinh theo gia cu
                    if (So_Tieu_ThuC > 0) {
                        //Thêm 01 bản ghi cho TT cũ
                        //sonnt edit 13/10/2009
                        double tmp_tien = Math.round(Gia_Cu * So_Tieu_ThuC);
                        //double ptn1 = (Tinh_PTN == 1) ? glb.g_LamTron(Ptn_Cu * tmp_tien / 100, 0) : 0;
                        double SL_Tinh_Gia_DVTN = 0;
                        double Don_Gia_DVTN = 0;
                        double TienVAT_DVTN = 0;
                        double Tien_DVTN = 0;
                        //các biến theo cách tính riêng
                        double Gia_SHGD_CU = 0;
                        double Gia_SX_CU = 0;
                        double Gia_SHNT_CU = 0; //dùng để tinh với trường hợp MDSD là bán buôn sinh hoạt NT
                        double Gia_BBKD_CU = 0; //dùng để tinh với trường hợp MDSD là bán buôn kinh doanh

                        if (Tinh_PTN == 1) {
                            //Lấy giá mới, cũ của SH gia đình và bán buôn phục vụ cho cách tính đặc biệt của giá DVTN
                            getFriceByInvoiceFlow();
                            if (lstMaDong.size() > 0) {

                                //04 bản ghi:
                                Gia_SHGD_CU = lstMaDong.get(0).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 2:
                                Gia_SHNT_CU = lstMaDong.get(1).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 3:
                                Gia_SX_CU = lstMaDong.get(2).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 4:
                                Gia_BBKD_CU = lstMaDong.get(3).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                            }
                            //đối với sinh hoạt gia đình
                            if (iMs_Dong == 132 || iMs_Dong == 135) {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuC;  //số lượng tính giá DVTN=số tiêu thụ cũ
                                Don_Gia_DVTN = Math.round(20 * Gia_Cu / 100); //đơn giá DVTN=20% giá nước tương ứng

                            } else if (iMs_Dong == 407) //nếu MĐSD là Sinh hoạt bán buôn
                            {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuC; //số lượng tính giá DVTN=số tiêu thụ cũ
                                //Đơn giá DVTN=20% giá nước SINH HOẠT GIA ĐÌNH (MÃ 132) cũ
                                Don_Gia_DVTN = Math.round(20 * Gia_SHGD_CU / 100);
                            } else if (iMs_Dong == 416) //NẾU MĐSD LÀ BÁN BUÔN NƯỚC SX
                            {
                                SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuC / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                //double d6=Math.round((80 * So_Tieu_ThuC / 100)*10.0)/10.0;
                                Don_Gia_DVTN = Math.round(20 * Gia_SX_CU / 100); //Đơn giá DVTN=20% giá nước SẢN XUẤT (MÃ 140)
                            } else if (iMs_Dong == 432) //NẾU MĐSD LÀ BÁN BUÔN KDOANH
                            {
                                SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuC / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                //double d7=Math.round((80 * So_Tieu_ThuC / 100)*10.0)/10.0;
                                Don_Gia_DVTN = Math.round(20 * Gia_BBKD_CU / 100); //Đơn giá DVTN=20% giá nước BBKD (MÃ 432)
                            } else if (iMs_Dong == 136) {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuC;
                                Don_Gia_DVTN = Math.round(20 * Gia_SHNT_CU / 100);
                            } else {
                                //đối với trường hợp không có Số tiêu thụ và chỉ tính chi phí duy trì đấu nối (ms_dong=400)
                                if (iMs_Dong == 400) {
                                    SL_Tinh_Gia_DVTN = 0;
                                    Don_Gia_DVTN = 0;
                                } else {
                                    SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuC / 100) * 10.0) / 10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                    //double d8=Math.round((80 * So_Tieu_ThuC / 100)*10.0)/10.0;
                                    Don_Gia_DVTN = Math.round(20 * Gia_Cu / 100); //Đơn giá DVTN=20% giá nước tương ứng
                                }
                            }
                            Tien_DVTN = Math.round(SL_Tinh_Gia_DVTN * Don_Gia_DVTN);
                            TienVAT_DVTN = Math.round(10 * Tien_DVTN / 100); //VAT DVTN=10%

                        }
                        insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_ThuC, Gia_Cu, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN);
                        //if (!insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_ThuC, Gia_Cu, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN)) {
                        //MessageBox.Show("Lỗi kết nối [1609] khi tính hóa đơn [" + So_Hoa_Don.ToString() + "] vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                        //    return false;
                        //}

                        Tong_Tien = Tong_Tien + tmp_tien;
                        //tổng tiền DVTN=tiền DVTN
                        Tong_Tien_DVTN = Tong_Tien_DVTN + Tien_DVTN;
                        Tong_VAT = Tong_VAT + Math.round(tmp_tien * Thue_VAT / 100); //VAT 5%
                        Tong_Ptn = Tong_Ptn + TienVAT_DVTN; //VAT 10%
                    }
                    //Tính theo giá mới
                    if (So_Tieu_ThuM > 0) {
                        double tmp_tien = Math.round(Gia_Moi * So_Tieu_ThuM);
                        //double ptn1 = (Tinh_PTN == 1) ? glb.g_LamTron(Ptn_Moi * tmp_tien / 100, 0) : 0;
                        double SL_Tinh_Gia_DVTN = 0;
                        double Don_Gia_DVTN = 0;
                        double TienVAT_DVTN = 0;
                        double Tien_DVTN = 0;
                        //các biến theo cách tính riêng
                        double Gia_SHGD_MOI = 0;
                        double Gia_SX_MOI = 0;
                        double Gia_SHNT_MOI = 0;
                        double Gia_BBKD_MOI = 0;
                        if (Tinh_PTN == 1) {
                            //Lấy giá mới, cũ của SH gia đình và bán buôn phục vụ cho cách tính đặc biệt của giá DVTN
                            getFriceByInvoiceFlow();
                            if (lstMaDong.size() > 0) {
                                //04 bản ghi:
                                //Gia_SHGD_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[0]["Gia_Chinh_TienM"], "0"));
                                Gia_SHGD_MOI = lstMaDong.get(0).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 2:
                                // Gia_SHNT_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[1]["Gia_Chinh_TienM"], "0"));
                                Gia_SHNT_MOI = lstMaDong.get(1).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 3:
                                //Gia_SX_MOI = Convert.ToDouble(glb.g_GetDatatoValue(ds.Tables[0].Rows[2]["Gia_Chinh_TienM"], "0"));
                                Gia_SX_MOI = lstMaDong.get(2).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                                //bản ghi thứ 4:
                                Gia_BBKD_MOI = lstMaDong.get(3).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                            }
                            //đối với sinh hoạt gia đình
                            if (iMs_Dong == 132 || iMs_Dong == 135) {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuM;  //số lượng tính giá DVTN=số tiêu thụ
                                Don_Gia_DVTN = Math.round(20 * Gia_Moi / 100); //đơn giá DVTN=20% giá nước tương ứng

                            } else if (iMs_Dong == 407) //nếu MĐSD là Sinh hoạt bán buôn
                            {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuM; //số lượng tính giá DVTN=số tiêu thụ
                                //Đơn giá DVTN=20% giá nước SINH HOẠT GIA ĐÌNH (MÃ 132)
                                Don_Gia_DVTN = Math.round(20 * Gia_SHGD_MOI / 100);
                            } else if (iMs_Dong == 416) //NẾU MĐSD LÀ BÁN BUÔN NƯỚC SX
                            {
                                SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuM / 100) * 10.0) / 10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                //double d8=Math.round((80 * So_Tieu_ThuM / 100)*10.0)/10.0;
                                Don_Gia_DVTN = Math.round(20 * Gia_SX_MOI / 100); //Đơn giá DVTN=20% giá nước SẢN XUẤT (MÃ 140)
                            } else if (iMs_Dong == 432) //NẾU MĐSD LÀ BÁN BUÔN KDOANH
                            {
                                SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuM / 100) * 10.0) / 10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                //double d9=Math.round((80 * So_Tieu_ThuM / 100)*10.0)/10.0;
                                Don_Gia_DVTN = Math.round(20 * Gia_BBKD_MOI / 100); //Đơn giá DVTN=20% giá nước BBKD (MÃ 432)
                            } else if (iMs_Dong == 136) {
                                SL_Tinh_Gia_DVTN = So_Tieu_ThuM; //số lượng tính giá DVTN=số tiêu thụ
                                //Đơn giá DVTN=20% giá nước SINH HOẠT NT (MÃ 135)
                                Don_Gia_DVTN = Math.round(20 * Gia_SHNT_MOI / 100);
                            } else {
                                //đối với trường hợp không có Số tiêu thụ và chỉ tính chi phí duy trì đấu nối (ms_dong=400)
                                if (iMs_Dong == 400) {
                                    SL_Tinh_Gia_DVTN = 0;
                                    Don_Gia_DVTN = 0;
                                } else {
                                    SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_ThuM / 100) * 10.0) / 10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                                    //double d10=Math.round((80 * So_Tieu_ThuM / 100)*10.0)/10.0;
                                    Don_Gia_DVTN = Math.round(20 * Gia_Moi / 100); //Đơn giá DVTN=20% giá nước tương ứng
                                }
                            }
                            Tien_DVTN = Math.round(SL_Tinh_Gia_DVTN * Don_Gia_DVTN);
                            TienVAT_DVTN = Math.round(10 * Tien_DVTN / 100); //VAT DVTN=10%
                        }
                        insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_ThuM, Gia_Moi, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN);
                        //if (!insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_ThuM, Gia_Moi, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN)) {
                        //MessageBox.Show("Lỗi kết nối [1658]. Vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                        //    return false;
                        //}
                        Tong_Tien = Tong_Tien + tmp_tien;
                        //tổng tiền DVTN=tiền DVTN
                        Tong_Tien_DVTN = Tong_Tien_DVTN + Tien_DVTN;
                        Tong_VAT = Tong_VAT + Math.round(tmp_tien * Thue_VAT / 100); //VAT 5%
                        Tong_Ptn = Tong_Ptn + TienVAT_DVTN; //VAT 10%

                    }

                }
            } else {
                double tmp_tien = Math.round(Gia_Cu * So_Tieu_Thu);
                //double ptn1 = (Tinh_PTN == 1) ? glb.g_LamTron(Ptn_Cu * tmp_tien / 100, 0) : 0;
                //kiem tra xem có tính giá thoát nước khong?
                double SL_Tinh_Gia_DVTN = 0;
                double Don_Gia_DVTN = 0;
                double TienVAT_DVTN = 0;
                double Tien_DVTN = 0;
                //các biến theo cách tính riêng
                double Gia_SHGD_CU = 0;
                double Gia_SX_CU = 0;
                double Gia_SHNT_CU = 0;
                double Gia_BBKD_CU = 0;

                if (Tinh_PTN == 1) {
                    //Lấy giá mới, cũ của SH gia đình và bán buôn phục vụ cho cách tính đặc biệt của giá DVTN
                    getFriceByInvoiceFlow();
                    if (lstMaDong.size() > 0) {

                        //04 bản ghi:
                        Gia_SHGD_CU = lstMaDong.get(0).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                        //bản ghi thứ 2:
                        Gia_SHNT_CU = lstMaDong.get(1).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                        //bản ghi thứ 3:
                        Gia_SX_CU = lstMaDong.get(2).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                        //bản ghi thứ 4:
                        Gia_BBKD_CU = lstMaDong.get(3).getGia_Chinh_TienM() == null ? 0 : lstMaDong.get(0).getGia_Chinh_TienM();
                    }

                    //đối với sinh hoạt gia đình
                    if (iMs_Dong == 132 || iMs_Dong == 135) {
                        SL_Tinh_Gia_DVTN = So_Tieu_Thu;  //số lượng tính giá DVTN=số tiêu thụ
                        Don_Gia_DVTN = Math.round(20 * Gia_Cu / 100); //đơn giá DVTN=20% giá nước tương ứng

                    } else if (iMs_Dong == 407) //nếu MĐSD là Sinh hoạt bán buôn
                    {
                        SL_Tinh_Gia_DVTN = So_Tieu_Thu; //số lượng tính giá DVTN=số tiêu thụ
                        //Đơn giá DVTN=20% giá nước SINH HOẠT GIA ĐÌNH (MÃ 132)
                        Don_Gia_DVTN = Math.round(20 * Gia_SHGD_CU / 100);
                    } else if (iMs_Dong == 416) //NẾU MĐSD LÀ BÁN BUÔN NƯỚC SX
                    {
                        SL_Tinh_Gia_DVTN =(double) ( Math.round((80 * So_Tieu_Thu / 100) * 10.0 )/ 10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                        Don_Gia_DVTN = Math.round(20 * Gia_SX_CU / 100); //Đơn giá DVTN=20% giá nước SẢN XUẤT (MÃ 140)
                    } else if (iMs_Dong == 432) //NẾU MĐSD LÀ BÁN BUÔN NƯỚC SX
                    {
                        SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                        Don_Gia_DVTN = Math.round(20 * Gia_BBKD_CU / 100); //Đơn giá DVTN=20% giá nước BBKD (MÃ 432)
                    } else if (iMs_Dong == 136) {
                        SL_Tinh_Gia_DVTN = So_Tieu_Thu; //số lượng tính giá DVTN=số tiêu thụ
                        //Đơn giá DVTN=20% giá nước SINH HOẠT NT
                        Don_Gia_DVTN = Math.round(20 * Gia_SHNT_CU / 100);
                    } else {
                        //đối với trường hợp không có Số tiêu thụ và chỉ tính chi phí duy trì đấu nối (ms_dong=400)
                        if (iMs_Dong == 400) {
                            SL_Tinh_Gia_DVTN = 0;
                            Don_Gia_DVTN = 0;
                        } else {
                            SL_Tinh_Gia_DVTN = (double) (Math.round((80 * So_Tieu_Thu / 100)*10.0)/10.0); //số lượng tính giá DVTN=80% số tiêu thụ
                            Don_Gia_DVTN = Math.round(20 * Gia_Cu / 100); //Đơn giá DVTN=20% giá nước tương ứng
                        }
                    }
                    Tien_DVTN = Math.round(SL_Tinh_Gia_DVTN * Don_Gia_DVTN);
                    TienVAT_DVTN = Math.round(10 * Tien_DVTN / 100); //VAT DVTN=10%

                }
                insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_Thu, Gia_Cu, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN);
                //if (!insertInvoiceDetail(iMs_Dong, So_Hoa_Don, So_Tieu_Thu, Gia_Cu, tmp_tien, Double.valueOf(Math.round(tmp_tien * Thue_VAT / 100)), 1, TienVAT_DVTN, SL_Tinh_Gia_DVTN, Don_Gia_DVTN, Tien_DVTN)) {
                // MessageBox.Show("Lỗi kết nối [1676]. Số HĐ["+So_Hoa_Don.ToString()+"]. Vui lòng kiểm tra lại!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
                //   return false;
                //}

                Tong_Tien = Tong_Tien + tmp_tien;
                //tổng tiền DVTN=tiền DVTN
                Tong_Tien_DVTN = Tong_Tien_DVTN + Tien_DVTN;
                Tong_VAT = Tong_VAT + Math.round(tmp_tien * Thue_VAT / 100); //VAT 5%
                Tong_Ptn = Tong_Ptn + TienVAT_DVTN; //VAT 10%

            }

        } catch (Exception e) {
            //MessageBox.Show("Lỗi kết nối [1687] khi tính toán chi tiết cho hóa đơn[" + So_Hoa_Don.ToString() + "]!", PublicVariable.g_MessBox, MessageBoxButtons.OK, MessageBoxIcon.Error);
            return false;
        }
        //return isTinhTienOk;
        //if(CheckTinhLaiTien){
            return isTinhTienOk;
        //}else {
        //    return false;
        //}
    }

    private void insertInvoice(Integer ms_loai_hd, Integer ms_mnoi, Integer ms_kieu_tt, Integer ms_tk1, String ngay_hd, String ngay_in, Integer so_lan_in, String tu_ngay, String den_ngay, Double chi_so_cu, Double chi_so_moi, Double luong_tinh_hd, Double so_tieu_thu, Double tong_tien, Double tien_thue_vat, Double tong_nhan, Double so_du, String ngay_thu, Integer ms_ttrang, Double ptn, String ngay_huy, Double tong_tien_DVTN) {

        ModelInsertInvoice request = new ModelInsertInvoice(ms_loai_hd, ms_mnoi, ms_kieu_tt, ms_tk1, ngay_hd, ngay_in, so_lan_in, tu_ngay, den_ngay, chi_so_cu, chi_so_moi, luong_tinh_hd, so_tieu_thu, tong_tien, tien_thue_vat, tong_nhan, so_du, ngay_thu, ms_ttrang, ptn, ngay_huy, tong_tien_DVTN);
        try {
            BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoice(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoice(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i("insertInvoice", "Insert thành công!");
                    laySoHoaDonByMnoiTky(Ms_MoiNoi, ms_tk);
                    Log.i("SHD0", ""+So_Hoa_Don);
                    if (So_Hoa_Don == 0) {
                        //this.cancel(true);
                        return;
                    }else {
                        updateSauTinhTien();
                    }

                }else {
                    return;
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("MeterResetHis", "update user pwd err : " + t.getMessage());
                ///
            }

        });*/

    }


    public void deleteInvoice(long ms_hd) {

        Log.d("deleteInvoice", ""+ms_hd);
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().invoiceApi().deleteInvoice(ms_hd).execute().isSuccessful();
            if(rp){
                So_Hoa_Don =0;
                CheckDeleteHD = true;
            }else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().invoiceApi().deleteInvoice(ms_hd).enqueue(new Callback<BarcodeResponse>() {

            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(NhapSoBangTay.this, "Cập nhật lịch sử đồng hồ thành công!", Toast.LENGTH_LONG).show();
                }else {
                    //Toast.makeText(TinhTienFunction.this, "Cập nhật lịch sử đồng hồ thành công!", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });*/
    }


    private void insertInvoiceDetailBrief(int ms_dong, long so_hd, int so_don_vi, double don_gia, double tien_phai_thu, double tien_thue_vat, int la_gia_moi) {


        BodyInsertInvoiceDetailBrief request = new BodyInsertInvoiceDetailBrief(ms_dong, so_hd, so_don_vi, don_gia, tien_phai_thu, tien_thue_vat, la_gia_moi);
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoiceDetailBrief(request).execute().isSuccessful();
            if(!rp){
                deleteInvoice( So_Hoa_Don);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoiceDetailBrief(request).enqueue(new Callback<BarcodeResponse>() {

            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                }else {
                    danhdauvitri = 2;
                    deleteInvoice( So_Hoa_Don);
                    return;
                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });*/
    }


    private void insertInvoiceDetail(Double ms_dong, long so_hd, Double so_don_vi, Double don_gia, Double tien_phai_thu, Double tien_thue_vat, int la_gia_moi, Double ptn, Double so_don_vi_DVTN, Double don_gia_DVTN, Double tien_phai_thu_DVTN) {


        BodyInsertInvoiceDetail request = new BodyInsertInvoiceDetail(ms_dong, so_hd, so_don_vi, don_gia, tien_phai_thu, tien_thue_vat, la_gia_moi, ptn, so_don_vi_DVTN, don_gia_DVTN, tien_phai_thu_DVTN);
        //Fix03062019
        try {
            boolean rp = BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoiceDetail(request).execute().isSuccessful();
            if(!rp){
                isTinhTienOk = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //EndFix03062019
        /*BarcodeReaderApiManager.getInstance().invoiceApi().insertInvoiceDetail(request).enqueue(new Callback<BarcodeResponse>() {

            @Override
            public void onResponse(Call<BarcodeResponse> call, retrofit2.Response<BarcodeResponse> response) {
                if (response.isSuccessful()) {
                }else {
                    isTinhTienOk = false;


                }
            }

            @Override
            public void onFailure(Call<BarcodeResponse> call, Throwable t) {

            }
        });*/

    }


    public long tinhSoNgay(Date tu_ngay, Date den_ngay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long SoNgay = 0;
        //Date date1 = sdf.parse(tu_ngay);
        // Date date2 = sdf.parse(den_ngay);
        SoNgay = TimeUnit.DAYS.convert(den_ngay.getTime() - tu_ngay.getTime(), TimeUnit.MILLISECONDS);


        return SoNgay;

    }

    public void getFriceByInvoiceFlow() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //getThongTinDongHoaDon
        String url = common.URL_API + "/GetFriceByInvoiceFlow";
        Log.d("getFriceByInvoiceFlow", url);
        JSONArray jsonArrayMaDong;
        // new HttpAsyncTaskgetFriceByInvoiceFlow().execute(url);

        try {
            lstMaDong = new ArrayList<>();
            jsonArrayMaDong = ReadJson.readJSonArrayFromURL(url);
            for (int i = 0; i < jsonArrayMaDong.length(); i++) {
                ModelMaDong maDong;
                JSONObject obj = jsonArrayMaDong.getJSONObject(i);

                Integer ms_dong = obj.getInt("ms_dong");
                Double Gia_Chinh_TienM = obj.getDouble("Gia_Chinh_TienM");
                Double Gia_Chinh_TienC = obj.getDouble("Gia_Chinh_TienC");
                maDong = new ModelMaDong(ms_dong, Gia_Chinh_TienM, Gia_Chinh_TienC);
                lstMaDong.add(maDong);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void g_GetDongHDInfo(long ms_dong) {

        //getThongTinDongHoaDon
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = common.URL_API + "/GetThongTinDongHoaDon?ms_dong=" + ms_dong;
        Log.d("getThongTinDongHoaDon", url);
        JSONObject jsonDongHD;
        JSONArray jsonArray = new JSONArray();
        //new HttpAsyncTaskGetThongTinDongHoaDon().execute(url);
        try {
            jsonArray = ReadJson.readJSonArrayFromURL(url);
            jsonDongHD = jsonArray.getJSONObject(0);

        if (jsonArray.length() > 0) {



            Gia_Cu = Double.parseDouble((jsonDongHD.getString("gia_cu").equals("")) ? "0" : jsonDongHD.getString("gia_cu"));
            Ptn_Cu = Double.parseDouble((jsonDongHD.getString("gia_ptn_cu").equals("")) ? "0" : jsonDongHD.getString("gia_ptn_cu"));
            Gia_Moi = Double.parseDouble((jsonDongHD.getString("gia_moi").equals("")) ? "0" : jsonDongHD.getString("gia_moi"));
            Ptn_moi = Double.parseDouble((jsonDongHD.getString("gia_ptn_moi").equals("")) ? "0" : jsonDongHD.getString("gia_ptn_moi"));
            Thue_VAT = Double.parseDouble((jsonDongHD.getString("thue_suat_vat").equals("")) ? "0" : jsonDongHD.getString("thue_suat_vat"));
            String strNgay_Ap_Dung = jsonDongHD.getString("ngay_ap_dung");
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Ngay_Ap_Dung = format1.parse(strNgay_Ap_Dung);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class HttpAsyncTaskGetThongTinDongHoaDon extends AsyncTask<String, JSONObject, Void> {


        JSONObject jsonDongHD;
        JSONArray jsonArray = new JSONArray();


        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];


            try {
                jsonArray = ReadJson.readJSonArrayFromURL(url);
                if (jsonArray.length() > 0) {
                    jsonDongHD = jsonArray.getJSONObject(0);


                    Gia_Cu = Double.parseDouble((jsonDongHD.getString("gia_cu").equals("")) ? "0" : jsonDongHD.getString("gia_cu"));
                    Ptn_Cu = Double.parseDouble((jsonDongHD.getString("gia_ptn_cu").equals("")) ? "0" : jsonDongHD.getString("gia_ptn_cu"));
                    Gia_Moi = Double.parseDouble((jsonDongHD.getString("gia_moi").equals("")) ? "0" : jsonDongHD.getString("gia_moi"));
                    Ptn_moi = Double.parseDouble((jsonDongHD.getString("gia_ptn_moi").equals("")) ? "0" : jsonDongHD.getString("gia_ptn_moi"));
                    Thue_VAT = Double.parseDouble((jsonDongHD.getString("thue_suat_vat").equals("")) ? "0" : jsonDongHD.getString("thue_suat_vat"));
                    String strNgay_Ap_Dung = jsonDongHD.getString("ngay_ap_dung");
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Ngay_Ap_Dung = format1.parse(strNgay_Ap_Dung);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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

            if (jsonArray.length() > 0) {

            }

            super.onPostExecute(result);

        }
    }

    public void getUsePurposeByMoinoi(Integer ms_mnoi) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = common.URL_API + "/GetUsePurposeByMoinoi?ms_mnoi=" + ms_mnoi;
        Log.d("getUsePurposeByMoinoi", url);
        Date Gia_Chinh_Ngay_Ap_Dung = null;
        Date Gia_Vuot_Ngay_Ap_Dung = null;
        JSONArray jsonArrMDSDByMN;
        //new HttpAsyncTaskGetUsePurposeByMoinoi().execute(url);
        try {
            lstUsePurposeByMoinoi = new ArrayList<>();
            jsonArrMDSDByMN = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            for (int i = 0; i < jsonArrMDSDByMN.length(); i++) {
                UsePurposeByMoinoi model;
                JSONObject objUsePurposeByMoinoi = jsonArrMDSDByMN.getJSONObject(i);


                Double Gia_Chinh_Ma = objUsePurposeByMoinoi.getDouble("Gia_Chinh_Ma");
                Double Dinh_Muc = objUsePurposeByMoinoi.getDouble("Dinh_Muc");
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Gia_Vuot_Ngay_Ap_Dung = dateFormat.parse("01/01/2000");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    if(!(common.GetDataToValue(objUsePurposeByMoinoi.getString("Gia_Vuot_Ngay_Ap_Dung"),"").equals(""))){
                        Gia_Vuot_Ngay_Ap_Dung = format1.parse(objUsePurposeByMoinoi.getString("Gia_Vuot_Ngay_Ap_Dung"));
                    }

                    if(!(common.GetDataToValue(objUsePurposeByMoinoi.getString("Gia_Chinh_Ngay_Ap_Dung"),"").equals(""))){
                        Gia_Chinh_Ngay_Ap_Dung = format1.parse(objUsePurposeByMoinoi.getString("Gia_Chinh_Ngay_Ap_Dung"));
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double Gia_Chinh_TienM = objUsePurposeByMoinoi.getDouble("Gia_Chinh_TienM");
                Double Gia_Chinh_TienC = objUsePurposeByMoinoi.getDouble("Gia_Chinh_TienC");
                Double Gia_Chinh_VAT = objUsePurposeByMoinoi.getDouble("Gia_Chinh_VAT");
                ;
                Double Gia_Chinh_PTNM = objUsePurposeByMoinoi.getDouble("Gia_Chinh_PTNM");
                Double Gia_Chinh_PTNC = objUsePurposeByMoinoi.getDouble("Gia_Chinh_PTNC");
                Double Gia_Vuot_Ma = objUsePurposeByMoinoi.getDouble("Gia_Vuot_Ma");
                Double Gia_Vuot_TienM = objUsePurposeByMoinoi.getDouble("Gia_Vuot_TienM");
                Double Gia_Vuot_TienC = objUsePurposeByMoinoi.getDouble("Gia_Vuot_TienC");
                Double Gia_Vuot_VAT = objUsePurposeByMoinoi.getDouble("Gia_Vuot_VAT");
                Double Gia_Vuot_PTNM = objUsePurposeByMoinoi.getDouble("Gia_Vuot_PTNM");
                Double Gia_Vuot_PTNC = objUsePurposeByMoinoi.getDouble("Gia_Vuot_PTNC");
                Double Phan_Tram = objUsePurposeByMoinoi.getDouble("Phan_Tram");
                Integer ms_md_sd = objUsePurposeByMoinoi.getInt("ms_md_sd");


                model = new UsePurposeByMoinoi(Gia_Chinh_Ma, Dinh_Muc, Gia_Chinh_Ngay_Ap_Dung, Gia_Chinh_TienM, Gia_Chinh_TienC, Gia_Chinh_VAT, Gia_Chinh_PTNM, Gia_Chinh_PTNC, Gia_Vuot_Ma, Gia_Vuot_Ngay_Ap_Dung, Gia_Vuot_TienM, Gia_Vuot_TienC, Gia_Vuot_VAT, Gia_Vuot_PTNM, Gia_Vuot_PTNC, Phan_Tram, ms_mnoi, ms_md_sd);
                lstUsePurposeByMoinoi.add(model);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetMDSDByMoiNoi(Integer ms_mnoi) {

        //GetMucDichSuDungByMoiNoi
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = common.URL_API + "/GetMucDichSuDungByMoiNoi?ms_mnoi=" + ms_mnoi;
        //String url = "http://123.26.252.98:8084/api/GetMucDichSuDungByMoiNoi?ms_mnoi=6593215";
        Log.d("GetMDSDByMoiNoi", url);
        //new HttpAsyncTaskGetMucDichSuDungByMoiNoi().execute(url).get();
        JSONArray jsonArrMDSDByMN;

        try {
            lstMDSDByMN = new ArrayList<>();
            jsonArrMDSDByMN = ReadJson.readJSonArrayFromURL(url);
            // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
            for (int i = 0; i < jsonArrMDSDByMN.length(); i++) {
                GetMDSDByMoiNoi modelMDSDByMoiNoi;
                JSONObject objMDSDByMoiNoi = jsonArrMDSDByMN.getJSONObject(i);
                Integer ms_md_sd = objMDSDByMoiNoi.getInt("ms_md_sd");
                //Integer ms_mnoi = objMDSDByMoiNoi.getInt("ms_mnoi");

                Integer ms_md_chinh = objMDSDByMoiNoi.getInt("ms_md_chinh");
                Integer ms_gia_vuot = null;
                if (!"null".equals(objMDSDByMoiNoi.getString("ms_gia_vuot"))) {
                    ms_gia_vuot = objMDSDByMoiNoi.getInt("ms_gia_vuot");
                }
                Float dinh_muc = null;
                if (!"null".equals(objMDSDByMoiNoi.getString("dinh_muc"))) {
                    dinh_muc = Float.valueOf(objMDSDByMoiNoi.getString("dinh_muc"));
                }
                Integer phan_tram = objMDSDByMoiNoi.getInt("phan_tram");
                Integer loai_cong_thuc = null;
                if (!"null".equals(objMDSDByMoiNoi.getString("loai_cong_thuc"))) {
                    loai_cong_thuc = objMDSDByMoiNoi.getInt("loai_cong_thuc");
                }

                modelMDSDByMoiNoi = new GetMDSDByMoiNoi(ms_md_sd, ms_mnoi, ms_md_chinh, ms_gia_vuot, dinh_muc, phan_tram, loai_cong_thuc);
                lstMDSDByMN.add(modelMDSDByMoiNoi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class HttpAsyncTaskGetMucDichSuDungByMoiNoi extends AsyncTask<String, JSONObject, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {



            String url = params[0];
            JSONArray jsonArrMDSDByMN;

            try {
                lstMDSDByMN = new ArrayList<>();
                jsonArrMDSDByMN = ReadJson.readJSonArrayFromURL(url);
                // Log.d("doInBackground", String.valueOf(jsonArrayTinhTrang.length()));
                for (int i = 0; i < jsonArrMDSDByMN.length(); i++) {
                    GetMDSDByMoiNoi modelMDSDByMoiNoi;
                    JSONObject objMDSDByMoiNoi = jsonArrMDSDByMN.getJSONObject(i);
                    Integer ms_md_sd = objMDSDByMoiNoi.getInt("ms_md_sd");
                    Integer ms_mnoi = objMDSDByMoiNoi.getInt("ms_mnoi");

                    Integer ms_md_chinh = objMDSDByMoiNoi.getInt("ms_md_chinh");
                    Integer ms_gia_vuot = null;
                    if (!"null".equals(objMDSDByMoiNoi.getString("ms_gia_vuot"))) {
                        ms_gia_vuot = objMDSDByMoiNoi.getInt("ms_gia_vuot");
                    }
                    Float dinh_muc = null;
                    if (!"null".equals(objMDSDByMoiNoi.getString("dinh_muc"))) {
                        dinh_muc = Float.valueOf(objMDSDByMoiNoi.getString("dinh_muc"));
                    }
                    Integer phan_tram = objMDSDByMoiNoi.getInt("ms_mnoi");
                    Integer loai_cong_thuc = null;
                    if (!"null".equals(objMDSDByMoiNoi.getString("loai_cong_thuc"))) {
                        loai_cong_thuc = objMDSDByMoiNoi.getInt("loai_cong_thuc");
                    }

                    modelMDSDByMoiNoi = new GetMDSDByMoiNoi(ms_md_sd, ms_mnoi, ms_md_chinh, ms_gia_vuot, dinh_muc, phan_tram, loai_cong_thuc);
                    lstMDSDByMN.add(modelMDSDByMoiNoi);
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

        }
    }

    public long laySoHoaDonByMnoiTky(Integer ms_mnoi, Integer ms_tk) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String url = common.URL_API + "/LaySoHoaDonByMnoiTky?ms_mnoi=" + ms_mnoi + "&ms_tk=" + ms_tk;
        Log.d("LaySoHoaDonByMnoiTky", url);
        String str = "";
        JSONObject objLaySoHD = null;


        try {
            objLaySoHD = ReadJson.readJsonFromUrl(url);

            String strms_hd = common.GetDataToValue(objLaySoHD.getString("so_hd"),"");
            long ms_hd = (long) Double.parseDouble((strms_hd == "") ? "0" : strms_hd);
            Log.d("ms_hd", String.valueOf(ms_hd));

            String strms_ttrang_tt = common.cat2kitucuoi(objLaySoHD.getString("ms_ttrang"));
            int ms_ttrang_tt = (int) Double.parseDouble((strms_ttrang_tt == "") ? "0" : strms_ttrang_tt);

            if ( ms_hd !=0) {
                // So_Hoa_Don = Integer.parseInt(common.cat2kitucuoi(str));
                So_Hoa_Don =  ms_hd;
            }
            Log.d("ms_hd111", String.valueOf(So_Hoa_Don));

        } catch (IOException e) {
            So_Hoa_Don = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  So_Hoa_Don;
    }



}
