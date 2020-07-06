package model.request;

public class BodyUpdateInvoiceSendSMSStatus {

    private long so_hd;
    private int tt_tin_nhan;

    public BodyUpdateInvoiceSendSMSStatus(long so_hd, int tt_tin_nhan) {
        this.so_hd = so_hd;
        this.tt_tin_nhan = tt_tin_nhan;
    }

    public long getSo_hd() {
        return so_hd;
    }

    public void setSo_hd(long so_hd) {
        this.so_hd = so_hd;
    }

    public int getTt_tin_nhan() {
        return tt_tin_nhan;
    }

    public void setTt_tin_nhan(int tt_tin_nhan) {
        this.tt_tin_nhan = tt_tin_nhan;
    }
}
