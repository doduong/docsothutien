package model.request;

public class UpdateInvoice {
    long so_hd;
    Double tong_tien;
    Double tien_thue_vat;
    Double so_du;
    Double ptn;
    Double tong_tien_DVTN;

    public UpdateInvoice(long so_hd, Double tong_tien, Double tien_thue_vat, Double so_du, Double ptn, Double tong_tien_DVTN) {
        this.so_hd = so_hd;
        this.tong_tien = tong_tien;
        this.tien_thue_vat = tien_thue_vat;
        this.so_du = so_du;
        this.ptn = ptn;
        this.tong_tien_DVTN = tong_tien_DVTN;
    }

    public long getSo_hd() {
        return so_hd;
    }

    public void setSo_hd(long so_hd) {
        this.so_hd = so_hd;
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

    public Double getSo_du() {
        return so_du;
    }

    public void setSo_du(Double so_du) {
        this.so_du = so_du;
    }

    public Double getPtn() {
        return ptn;
    }

    public void setPtn(Double ptn) {
        this.ptn = ptn;
    }

    public Double getTong_tien_DVTN() {
        return tong_tien_DVTN;
    }

    public void setTong_tien_DVTN(Double tong_tien_DVTN) {
        this.tong_tien_DVTN = tong_tien_DVTN;
    }
}
