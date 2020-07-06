package model;

public class ThongTinThanhToanTuyen {

    Integer ms_mnoi;
    String ten_kh;
    long Tong_tien;
    int ms_ttrang;
    int ms_kieu_tt;
    int stt_lo_trinh;

    public ThongTinThanhToanTuyen(Integer ms_mnoi, String ten_kh, long tong_tien, int ms_ttrang, int ms_kieu_tt, int stt_lo_trinh) {
        this.ms_mnoi = ms_mnoi;
        this.ten_kh = ten_kh;
        Tong_tien = tong_tien;
        this.ms_ttrang = ms_ttrang;
        this.ms_kieu_tt = ms_kieu_tt;
        this.stt_lo_trinh = stt_lo_trinh;
    }

    public int getStt_lo_trinh() {
        return stt_lo_trinh;
    }

    public void setStt_lo_trinh(int stt_lo_trinh) {
        this.stt_lo_trinh = stt_lo_trinh;
    }

    public int getMs_kieu_tt() {
        return ms_kieu_tt;
    }

    public void setMs_kieu_tt(int ms_kieu_tt) {
        this.ms_kieu_tt = ms_kieu_tt;
    }

    public Integer getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(Integer ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public String getTen_kh() {
        return ten_kh;
    }

    public void setTen_kh(String ten_kh) {
        this.ten_kh = ten_kh;
    }

    public long getTong_tien() {
        return Tong_tien;
    }

    public void setTong_tien(long tong_tien) {
        Tong_tien = tong_tien;
    }

    public int getMs_ttrang() {
        return ms_ttrang;
    }

    public void setMs_ttrang(int ms_ttrang) {
        this.ms_ttrang = ms_ttrang;
    }
}
