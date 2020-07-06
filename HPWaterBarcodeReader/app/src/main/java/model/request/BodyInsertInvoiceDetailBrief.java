package model.request;

public class BodyInsertInvoiceDetailBrief {
    int ms_dong;
    long so_hd;
    int so_don_vi;
    double don_gia;
    double tien_phai_thu;
    double tien_thue_vat;
    int la_gia_moi;

    public BodyInsertInvoiceDetailBrief(int ms_dong, long so_hd, int so_don_vi, double don_gia, double tien_phai_thu, double tien_thue_vat, int la_gia_moi) {
        this.ms_dong = ms_dong;
        this.so_hd = so_hd;
        this.so_don_vi = so_don_vi;
        this.don_gia = don_gia;
        this.tien_phai_thu = tien_phai_thu;
        this.tien_thue_vat = tien_thue_vat;
        this.la_gia_moi = la_gia_moi;
    }

    public int getMs_dong() {
        return ms_dong;
    }

    public void setMs_dong(int ms_dong) {
        this.ms_dong = ms_dong;
    }

    public long getSo_hd() {
        return so_hd;
    }

    public void setSo_hd(long so_hd) {
        this.so_hd = so_hd;
    }

    public int getSo_don_vi() {
        return so_don_vi;
    }

    public void setSo_don_vi(int so_don_vi) {
        this.so_don_vi = so_don_vi;
    }

    public double getDon_gia() {
        return don_gia;
    }

    public void setDon_gia(double don_gia) {
        this.don_gia = don_gia;
    }

    public double getTien_phai_thu() {
        return tien_phai_thu;
    }

    public void setTien_phai_thu(double tien_phai_thu) {
        this.tien_phai_thu = tien_phai_thu;
    }

    public double getTien_thue_vat() {
        return tien_thue_vat;
    }

    public void setTien_thue_vat(double tien_thue_vat) {
        this.tien_thue_vat = tien_thue_vat;
    }

    public int isLa_gia_moi() {
        return la_gia_moi;
    }

    public void setLa_gia_moi(int la_gia_moi) {
        this.la_gia_moi = la_gia_moi;
    }
}
