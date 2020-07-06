package model.response;

public class InvoiceReceipt {

    double so_don_vi;
    long don_gia;
    long tien_phai_thu;
    long VAT;
    long tien_VAT;

    public InvoiceReceipt(double so_don_vi, long don_gia, long tien_phai_thu, long VAT, long tien_VAT) {
        this.so_don_vi = so_don_vi;
        this.don_gia = don_gia;
        this.tien_phai_thu = tien_phai_thu;
        this.VAT = VAT;
        this.tien_VAT = tien_VAT;

    }

    public double getSo_don_vi() {
        return so_don_vi;
    }

    public void setSo_don_vi(double so_don_vi) {
        this.so_don_vi = so_don_vi;
    }

    public long getDon_gia() {
        return don_gia;
    }

    public void setDon_gia(long don_gia) {
        this.don_gia = don_gia;
    }

    public long getTien_phai_thu() {
        return tien_phai_thu;
    }

    public void setTien_phai_thu(long tien_phai_thu) {
        this.tien_phai_thu = tien_phai_thu;
    }

    public long getVAT() {
        return VAT;
    }

    public void setVAT(long VAT) {
        this.VAT = VAT;
    }

    public long getTien_VAT() {
        return tien_VAT;
    }

    public void setTien_VAT(long tien_VAT) {
        this.tien_VAT = tien_VAT;
    }

}
