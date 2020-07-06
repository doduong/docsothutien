package model.request;

public class ModelMaDong {

    Integer ms_dong;
    Double Gia_Chinh_TienM;
    Double Gia_Chinh_TienC;

    public ModelMaDong(Integer ms_dong, Double gia_Chinh_TienM, Double gia_Chinh_TienC) {

        this.ms_dong = ms_dong;
        Gia_Chinh_TienM = gia_Chinh_TienM;
        Gia_Chinh_TienC = gia_Chinh_TienC;
    }

    public Integer getMs_dong() {
        return ms_dong;
    }

    public void setMs_dong(Integer ms_dong) {
        this.ms_dong = ms_dong;
    }

    public Double getGia_Chinh_TienM() {
        return Gia_Chinh_TienM;
    }

    public void setGia_Chinh_TienM(Double gia_Chinh_TienM) {
        Gia_Chinh_TienM = gia_Chinh_TienM;
    }

    public Double getGia_Chinh_TienC() {
        return Gia_Chinh_TienC;
    }

    public void setGia_Chinh_TienC(Double gia_Chinh_TienC) {
        Gia_Chinh_TienC = gia_Chinh_TienC;
    }
}
