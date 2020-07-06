package model.response;

public class GetMDSDByMoiNoi {

    Integer ms_md_sd ;
    Integer ms_mnoi;
    Integer ms_md_chinh;
    Integer ms_gia_vuot;
    Float dinh_muc;
    Integer phan_tram;
    Integer loai_cong_thuc;

    public GetMDSDByMoiNoi() {
    }

    public GetMDSDByMoiNoi(Integer ms_md_sd, Integer ms_mnoi, Integer ms_md_chinh, Integer ms_gia_vuot, Float dinh_muc, Integer phan_tram, Integer loai_cong_thuc) {
        this.ms_md_sd = ms_md_sd;
        this.ms_mnoi = ms_mnoi;
        this.ms_md_chinh = ms_md_chinh;
        this.ms_gia_vuot = ms_gia_vuot;
        this.dinh_muc = dinh_muc;
        this.phan_tram = phan_tram;
        this.loai_cong_thuc = loai_cong_thuc;
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

    public Integer getMs_md_chinh() {
        return ms_md_chinh;
    }

    public void setMs_md_chinh(Integer ms_md_chinh) {
        this.ms_md_chinh = ms_md_chinh;
    }




    public Integer getMs_gia_vuot() {
        return ms_gia_vuot;
    }

    public void setMs_gia_vuot(Integer ms_gia_vuot) {
        this.ms_gia_vuot = ms_gia_vuot;
    }

    public Float getDinh_muc() {
        return dinh_muc;
    }

    public void setDinh_muc(Float dinh_muc) {
        this.dinh_muc = dinh_muc;
    }

    public Integer getPhan_tram() {
        return phan_tram;
    }

    public void setPhan_tram(Integer phan_tram) {
        this.phan_tram = phan_tram;
    }

    public Integer getLoai_cong_thuc() {
        return loai_cong_thuc;
    }

    public void setLoai_cong_thuc(Integer loai_cong_thuc) {
        this.loai_cong_thuc = loai_cong_thuc;
    }
}
