package model.request;

public class ModelInsertInvoice {

    Integer ms_loai_hd;
    Integer ms_mnoi;
    Integer ms_kieu_tt;
    Integer ms_tk;
    String ngay_hd;
    String ngay_in;
    Integer so_lan_in;
    String tu_ngay;
    String den_ngay;
    Double chi_so_cu;
    Double chi_so_moi;
    Double luong_tinh_hd;
    Double so_tieu_thu;
    Double tong_tien;
    Double tien_thue_vat;
    Double tong_nhan;
    Double so_du;
    String ngay_thu;
    Integer ms_ttrang;
    Double ptn;
    String ngay_huy;
    Double tong_tien_DVTN;

    public ModelInsertInvoice(Integer ms_loai_hd, Integer ms_mnoi, Integer ms_kieu_tt, Integer ms_tk, String ngay_hd, String ngay_in, Integer so_lan_in, String tu_ngay, String den_ngay, Double chi_so_cu, Double chi_so_moi, Double luong_tinh_hd, Double so_tieu_thu, Double tong_tien, Double tien_thue_vat, Double tong_nhan, Double so_du, String ngay_thu, Integer ms_ttrang, Double ptn, String ngay_huy, Double tong_tien_DVTN) {
        this.ms_loai_hd = ms_loai_hd;
        this.ms_mnoi = ms_mnoi;
        this.ms_kieu_tt = ms_kieu_tt;
        this.ms_tk = ms_tk;
        this.ngay_hd = ngay_hd;
        this.ngay_in = ngay_in;
        this.so_lan_in = so_lan_in;
        this.tu_ngay = tu_ngay;
        this.den_ngay = den_ngay;
        this.chi_so_cu = chi_so_cu;
        this.chi_so_moi = chi_so_moi;
        this.luong_tinh_hd = luong_tinh_hd;
        this.so_tieu_thu = so_tieu_thu;
        this.tong_tien = tong_tien;
        this.tien_thue_vat = tien_thue_vat;
        this.tong_nhan = tong_nhan;
        this.so_du = so_du;
        this.ngay_thu = ngay_thu;
        this.ms_ttrang = ms_ttrang;
        this.ptn = ptn;
        this.ngay_huy = ngay_huy;
        this.tong_tien_DVTN = tong_tien_DVTN;
    }

    public Integer getMs_loai_hd() {
        return ms_loai_hd;
    }

    public void setMs_loai_hd(Integer ms_loai_hd) {
        this.ms_loai_hd = ms_loai_hd;
    }

    public Integer getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(Integer ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public Integer getMs_kieu_tt() {
        return ms_kieu_tt;
    }

    public void setMs_kieu_tt(Integer ms_kieu_tt) {
        this.ms_kieu_tt = ms_kieu_tt;
    }

    public Integer getMs_tk() {
        return ms_tk;
    }

    public void setMs_tk(Integer ms_tk) {
        this.ms_tk = ms_tk;
    }

    public String getNgay_hd() {
        return ngay_hd;
    }

    public void setNgay_hd(String ngay_hd) {
        this.ngay_hd = ngay_hd;
    }

    public String getNgay_in() {
        return ngay_in;
    }

    public void setNgay_in(String ngay_in) {
        this.ngay_in = ngay_in;
    }

    public Integer getSo_lan_in() {
        return so_lan_in;
    }

    public void setSo_lan_in(Integer so_lan_in) {
        this.so_lan_in = so_lan_in;
    }

    public String getTu_ngay() {
        return tu_ngay;
    }

    public void setTu_ngay(String tu_ngay) {
        this.tu_ngay = tu_ngay;
    }

    public String getDen_ngay() {
        return den_ngay;
    }

    public void setDen_ngay(String den_ngay) {
        this.den_ngay = den_ngay;
    }

    public Double getChi_so_cu() {
        return chi_so_cu;
    }

    public void setChi_so_cu(Double chi_so_cu) {
        this.chi_so_cu = chi_so_cu;
    }

    public Double getChi_so_moi() {
        return chi_so_moi;
    }

    public void setChi_so_moi(Double chi_so_moi) {
        this.chi_so_moi = chi_so_moi;
    }

    public double getLuong_tinh_hd() {
        return luong_tinh_hd;
    }

    public void setLuong_tinh_hd(Double luong_tinh_hd) {
        this.luong_tinh_hd = luong_tinh_hd;
    }

    public Double getSo_tieu_thu() {
        return so_tieu_thu;
    }

    public void setSo_tieu_thu(Double so_tieu_thu) {
        this.so_tieu_thu = so_tieu_thu;
    }

    public Double getTong_tien() {
        return tong_tien;
    }

    public void setTong_tien(Double tong_tien) {
        this.tong_tien = tong_tien;
    }

    public Double getTien_thue_vat() {
        return tien_thue_vat;
    }

    public void setTien_thue_vat(Double tien_thue_vat) {
        this.tien_thue_vat = tien_thue_vat;
    }

    public Double getTong_nhan() {
        return tong_nhan;
    }

    public void setTong_nhan(Double tong_nhan) {
        this.tong_nhan = tong_nhan;
    }

    public Double getSo_du() {
        return so_du;
    }

    public void setSo_du(Double so_du) {
        this.so_du = so_du;
    }

    public String getNgay_thu() {
        return ngay_thu;
    }

    public void setNgay_thu(String ngay_thu) {
        this.ngay_thu = ngay_thu;
    }

    public Integer getMs_ttrang() {
        return ms_ttrang;
    }

    public void setMs_ttrang(Integer ms_ttrang) {
        this.ms_ttrang = ms_ttrang;
    }

    public Double getPtn() {
        return ptn;
    }

    public void setPtn(Double ptn) {
        this.ptn = ptn;
    }

    public String getNgay_huy() {
        return ngay_huy;
    }

    public void setNgay_huy(String ngay_huy) {
        this.ngay_huy = ngay_huy;
    }

    public Double getTong_tien_DVTN() {
        return tong_tien_DVTN;
    }

    public void setTong_tien_DVTN(Double tong_tien_DVTN) {
        this.tong_tien_DVTN = tong_tien_DVTN;
    }
}
