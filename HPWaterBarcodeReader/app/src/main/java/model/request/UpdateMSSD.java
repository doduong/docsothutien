package model.request;

public class UpdateMSSD {

    private Integer ms_md_sd;
    private Integer ms_mnoi;
    private Double dinh_muc;

    public UpdateMSSD() {
    }

    public UpdateMSSD(Integer ms_md_sd, Integer ms_mnoi, Double dinh_muc) {
        this.ms_md_sd = ms_md_sd;
        this.ms_mnoi = ms_mnoi;
        this.dinh_muc = dinh_muc;
    }

    public Integer getMs_md_sd() {
        return ms_md_sd;
    }

    public void setMs_md_sd(Integer ms_md_sd) {
        this.ms_md_sd = ms_md_sd;
    }

    public Integer getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(Integer ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public Double getDinh_muc() {
        return dinh_muc;
    }

    public void setDinh_muc(Double dinh_muc) {
        this.dinh_muc = dinh_muc;
    }
}
