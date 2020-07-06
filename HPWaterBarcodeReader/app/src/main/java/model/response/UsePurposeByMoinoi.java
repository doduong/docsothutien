package model.response;

import java.util.Date;

public class UsePurposeByMoinoi {

    Double Gia_Chinh_Ma;
    Double Dinh_Muc;
    Date Gia_Chinh_Ngay_Ap_Dung;
    Double   Gia_Chinh_TienM;
    Double   Gia_Chinh_TienC;
    Double   Gia_Chinh_VAT;
    Double   Gia_Chinh_PTNM;
    Double   Gia_Chinh_PTNC;
    Double   Gia_Vuot_Ma;
    Date Gia_Vuot_Ngay_Ap_Dung;
    Double   Gia_Vuot_TienM;
    Double   Gia_Vuot_TienC;
    Double   Gia_Vuot_VAT;
    Double  Gia_Vuot_PTNM;
    Double  Gia_Vuot_PTNC;
    Double Phan_Tram ;
    Integer   ms_mnoi;
    Integer   ms_md_sd;

    public UsePurposeByMoinoi(Double gia_Chinh_Ma, Double dinh_Muc, Date gia_Chinh_Ngay_Ap_Dung, Double gia_Chinh_TienM, Double gia_Chinh_TienC, Double gia_Chinh_VAT, Double gia_Chinh_PTNM, Double gia_Chinh_PTNC, Double gia_Vuot_Ma, Date gia_Vuot_Ngay_Ap_Dung, Double gia_Vuot_TienM, Double gia_Vuot_TienC, Double gia_Vuot_VAT, Double gia_Vuot_PTNM, Double gia_Vuot_PTNC, Double phan_Tram, Integer ms_mnoi, Integer ms_md_sd) {
        Gia_Chinh_Ma = gia_Chinh_Ma;
        Dinh_Muc = dinh_Muc;
        Gia_Chinh_Ngay_Ap_Dung = gia_Chinh_Ngay_Ap_Dung;
        Gia_Chinh_TienM = gia_Chinh_TienM;
        Gia_Chinh_TienC = gia_Chinh_TienC;
        Gia_Chinh_VAT = gia_Chinh_VAT;
        Gia_Chinh_PTNM = gia_Chinh_PTNM;
        Gia_Chinh_PTNC = gia_Chinh_PTNC;
        Gia_Vuot_Ma = gia_Vuot_Ma;
        Gia_Vuot_Ngay_Ap_Dung = gia_Vuot_Ngay_Ap_Dung;
        Gia_Vuot_TienM = gia_Vuot_TienM;
        Gia_Vuot_TienC = gia_Vuot_TienC;
        Gia_Vuot_VAT = gia_Vuot_VAT;
        Gia_Vuot_PTNM = gia_Vuot_PTNM;
        Gia_Vuot_PTNC = gia_Vuot_PTNC;
        Phan_Tram = phan_Tram;
        this.ms_mnoi = ms_mnoi;
        this.ms_md_sd = ms_md_sd;
    }

    public Double getPhan_Tram() {
        return Phan_Tram;
    }

    public void setPhan_Tram(Double phan_Tram) {
        Phan_Tram = phan_Tram;
    }

    public Double getGia_Chinh_Ma() {
        return Gia_Chinh_Ma;
    }

    public void setGia_Chinh_Ma(Double gia_Chinh_Ma) {
        Gia_Chinh_Ma = gia_Chinh_Ma;
    }

    public Double getDinh_Muc() {
        return Dinh_Muc;
    }

    public void setDinh_Muc(Double dinh_Muc) {
        Dinh_Muc = dinh_Muc;
    }

    public Date getGia_Chinh_Ngay_Ap_Dung() {
        return Gia_Chinh_Ngay_Ap_Dung;
    }

    public void setGia_Chinh_Ngay_Ap_Dung(Date gia_Chinh_Ngay_Ap_Dung) {
        Gia_Chinh_Ngay_Ap_Dung = gia_Chinh_Ngay_Ap_Dung;
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

    public Double getGia_Chinh_VAT() {
        return Gia_Chinh_VAT;
    }

    public void setGia_Chinh_VAT(Double gia_Chinh_VAT) {
        Gia_Chinh_VAT = gia_Chinh_VAT;
    }

    public Double getGia_Chinh_PTNM() {
        return Gia_Chinh_PTNM;
    }

    public void setGia_Chinh_PTNM(Double gia_Chinh_PTNM) {
        Gia_Chinh_PTNM = gia_Chinh_PTNM;
    }

    public Double getGia_Chinh_PTNC() {
        return Gia_Chinh_PTNC;
    }

    public void setGia_Chinh_PTNC(Double gia_Chinh_PTNC) {
        Gia_Chinh_PTNC = gia_Chinh_PTNC;
    }

    public Double getGia_Vuot_Ma() {
        return Gia_Vuot_Ma;
    }

    public void setGia_Vuot_Ma(Double gia_Vuot_Ma) {
        Gia_Vuot_Ma = gia_Vuot_Ma;
    }

    public Date getGia_Vuot_Ngay_Ap_Dung() {
        return Gia_Vuot_Ngay_Ap_Dung;
    }

    public void setGia_Vuot_Ngay_Ap_Dung(Date gia_Vuot_Ngay_Ap_Dung) {
        Gia_Vuot_Ngay_Ap_Dung = gia_Vuot_Ngay_Ap_Dung;
    }

    public Double getGia_Vuot_TienM() {
        return Gia_Vuot_TienM;
    }

    public void setGia_Vuot_TienM(Double gia_Vuot_TienM) {
        Gia_Vuot_TienM = gia_Vuot_TienM;
    }

    public Double getGia_Vuot_TienC() {
        return Gia_Vuot_TienC;
    }

    public void setGia_Vuot_TienC(Double gia_Vuot_TienC) {
        Gia_Vuot_TienC = gia_Vuot_TienC;
    }

    public Double getGia_Vuot_VAT() {
        return Gia_Vuot_VAT;
    }

    public void setGia_Vuot_VAT(Double gia_Vuot_VAT) {
        Gia_Vuot_VAT = gia_Vuot_VAT;
    }

    public Double getGia_Vuot_PTNM() {
        return Gia_Vuot_PTNM;
    }

    public void setGia_Vuot_PTNM(Double gia_Vuot_PTNM) {
        Gia_Vuot_PTNM = gia_Vuot_PTNM;
    }

    public Double getGia_Vuot_PTNC() {
        return Gia_Vuot_PTNC;
    }

    public void setGia_Vuot_PTNC(Double gia_Vuot_PTNC) {
        Gia_Vuot_PTNC = gia_Vuot_PTNC;
    }

    public Integer getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(Integer ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public Integer getMs_md_sd() {
        return ms_md_sd;
    }

    public void setMs_md_sd(Integer ms_md_sd) {
        this.ms_md_sd = ms_md_sd;
    }
}
