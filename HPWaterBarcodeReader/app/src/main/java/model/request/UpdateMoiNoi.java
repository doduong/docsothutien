package model.request;

public class UpdateMoiNoi {
    Integer ms_mnoi;
    Double tien_dho_tra;

    public UpdateMoiNoi() {
    }

    public UpdateMoiNoi(Integer ms_mnoi, Double tien_dho_tra) {
        this.ms_mnoi = ms_mnoi;
        this.tien_dho_tra = tien_dho_tra;
    }

    public Integer getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(Integer ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public Double getTien_dho_tra() {
        return tien_dho_tra;
    }

    public void setTien_dho_tra(Double tien_dho_tra) {
        this.tien_dho_tra = tien_dho_tra;
    }
}
