package model.request;

public class BodyInsertInvoiceDetail {

    Double ms_dong;
    long so_hd;
    Double so_don_vi;
    Double don_gia;
    Double tien_phai_thu;
    Double tien_thue_vat;
    int la_gia_moi ;
    Double ptn;
    Double so_don_vi_DVTN;
    Double don_gia_DVTN;
    Double tien_phai_thu_DVTN;

    public BodyInsertInvoiceDetail(Double ms_dong, long so_hd, Double so_don_vi, Double don_gia, Double tien_phai_thu, Double tien_thue_vat, int la_gia_moi, Double ptn, Double so_don_vi_DVTN, Double don_gia_DVTN, Double tien_phai_thu_DVTN) {
        this.ms_dong = ms_dong;
        this.so_hd = so_hd;
        this.so_don_vi = so_don_vi;
        this.don_gia = don_gia;
        this.tien_phai_thu = tien_phai_thu;
        this.tien_thue_vat = tien_thue_vat;
        this.la_gia_moi = la_gia_moi;
        this.ptn = ptn;
        this.so_don_vi_DVTN = so_don_vi_DVTN;
        this.don_gia_DVTN = don_gia_DVTN;
        this.tien_phai_thu_DVTN = tien_phai_thu_DVTN;
    }

    public Double getMs_dong() {
        return ms_dong;
    }

    public void setMs_dong(Double ms_dong) {
        this.ms_dong = ms_dong;
    }

    public long getSo_hd() {
        return so_hd;
    }

    public void setSo_hd(long so_hd) {
        this.so_hd = so_hd;
    }

    public Double getSo_don_vi() {
        return so_don_vi;
    }

    public void setSo_don_vi(Double so_don_vi) {
        this.so_don_vi = so_don_vi;
    }

    public Double getDon_gia() {
        return don_gia;
    }

    public void setDon_gia(Double don_gia) {
        this.don_gia = don_gia;
    }

    public Double getTien_phai_thu() {
        return tien_phai_thu;
    }

    public void setTien_phai_thu(Double tien_phai_thu) {
        this.tien_phai_thu = tien_phai_thu;
    }

    public Double getTien_thue_vat() {
        return tien_thue_vat;
    }

    public void setTien_thue_vat(Double tien_thue_vat) {
        this.tien_thue_vat = tien_thue_vat;
    }

    public int getLa_gia_moi() {
        return la_gia_moi;
    }

    public void setLa_gia_moi(int la_gia_moi) {
        this.la_gia_moi = la_gia_moi;
    }

    public Double getPtn() {
        return ptn;
    }

    public void setPtn(Double ptn) {
        this.ptn = ptn;
    }

    public Double getSo_don_vi_DVTN() {
        return so_don_vi_DVTN;
    }

    public void setSo_don_vi_DVTN(Double so_don_vi_DVTN) {
        this.so_don_vi_DVTN = so_don_vi_DVTN;
    }

    public Double getDon_gia_DVTN() {
        return don_gia_DVTN;
    }

    public void setDon_gia_DVTN(Double don_gia_DVTN) {
        this.don_gia_DVTN = don_gia_DVTN;
    }

    public Double getTien_phai_thu_DVTN() {
        return tien_phai_thu_DVTN;
    }

    public void setTien_phai_thu_DVTN(Double tien_phai_thu_DVTN) {
        this.tien_phai_thu_DVTN = tien_phai_thu_DVTN;
    }
}
