package model.request;

public class MeterUpdateRequest {
    private int ms_mnoi;
    private int ms_tk;
    private Integer chiSoMoi;
    private  Integer soTieuThu;
    private int ms_ttrang;
    private boolean hasAmountFlag;
    private String Base64Image;
    private String GhiChu;

    public MeterUpdateRequest(int ms_mnoi, int ms_tk, Integer chiSoMoi, Integer soTieuThu, int ms_ttrang, boolean hasAmountFlag, String base64Image, String ghiChu) {
        this.ms_mnoi = ms_mnoi;
        this.ms_tk = ms_tk;
        this.chiSoMoi = chiSoMoi;
        this.soTieuThu = soTieuThu;
        this.ms_ttrang = ms_ttrang;
        this.hasAmountFlag = hasAmountFlag;
        Base64Image = base64Image;
        GhiChu = ghiChu;
    }

    public void setChiSoMoi(Integer chiSoMoi) {
        this.chiSoMoi = chiSoMoi;
    }

    public void setSoTieuThu(Integer soTieuThu) {
        this.soTieuThu = soTieuThu;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String ghiChu) {
        GhiChu = ghiChu;
    }

    public int getMs_mnoi() {
        return ms_mnoi;
    }

    public void setMs_mnoi(int ms_mnoi) {
        this.ms_mnoi = ms_mnoi;
    }

    public int getMs_tk() {
        return ms_tk;
    }

    public void setMs_tk(int ms_tk) {
        this.ms_tk = ms_tk;
    }

    public Integer getChiSoMoi() {
        return chiSoMoi;
    }

    public void setChiSoMoi(int chiSoMoi) {
        this.chiSoMoi = chiSoMoi;
    }

    public Integer getSoTieuThu() {
        return soTieuThu;
    }

    public void setSoTieuThu(int soTieuThu) {
        this.soTieuThu = soTieuThu;
    }

    public int getMs_ttrang() {
        return ms_ttrang;
    }

    public void setMs_ttrang(int ms_ttrang) {
        this.ms_ttrang = ms_ttrang;
    }

    public boolean isHasAmountFlag() {
        return hasAmountFlag;
    }

    public void setHasAmountFlag(boolean hasAmountFlag) {
        this.hasAmountFlag = hasAmountFlag;
    }

    public String getBase64Image() {
        return Base64Image;
    }

    public void setBase64Image(String base64Image) {
        Base64Image = base64Image;
    }
}
