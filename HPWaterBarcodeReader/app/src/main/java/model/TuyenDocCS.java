package model;

public class TuyenDocCS {
    private int ms_tuyen;
    private int ms_tql;
    private int ms_cky;
    private String mo_ta_tuyen;
    private int ms_tn;
    private int ms_bd;


    public TuyenDocCS(int ms_tuyen, int ms_tql, int ms_cky, String mo_ta_tuyen, int ms_tn, int ms_bd) {
        this.ms_tuyen = ms_tuyen;
        this.ms_tql = ms_tql;
        this.ms_cky = ms_cky;
        this.mo_ta_tuyen = mo_ta_tuyen;
        this.ms_tn = ms_tn;
        this.ms_bd = ms_bd;
    }

    public TuyenDocCS() {
    }

    public int getMs_tuyen() {
        return ms_tuyen;
    }

    public void setMs_tuyen(int ms_tuyen) {
        this.ms_tuyen = ms_tuyen;
    }

    public int getMs_tql() {
        return ms_tql;
    }

    public void setMs_tql(int ms_tql) {
        this.ms_tql = ms_tql;
    }

    public int getMs_cky() {
        return ms_cky;
    }

    public void setMs_cky(int ms_cky) {
        this.ms_cky = ms_cky;
    }

    public String getMo_ta_tuyen() {
        return mo_ta_tuyen;
    }

    public void setMo_ta_tuyen(String mo_ta_tuyen) {
        this.mo_ta_tuyen = mo_ta_tuyen;
    }

    public int getMs_tn() {
        return ms_tn;
    }

    public void setMs_tn(int ms_tn) {
        this.ms_tn = ms_tn;
    }

    public int getMs_bd() {
        return ms_bd;
    }

    public void setMs_bd(int ms_bd) {
        this.ms_bd = ms_bd;
    }
}
