package utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.cntt.cnhp.hpwaterbarcodereader.ChiTietDiemDungMatMang;
import app.cntt.cnhp.hpwaterbarcodereader.TabActivity;

public class CommonText {
    public CommonText() {
    }

    public  String queryGetMaxTK = "select tk.ms_tk, tk.thoi_ky from dbo.thoi_ky tk where ms_tk = (select MAX(ms_tk) from thoi_ky)" ;
    //SERVER THAT
    public  String URL_API = "http://113.160.100.217:8080/api";
    public  String URL_IMAGE = "http://113.160.100.217:8080";
    //SERVER TEST
    //public  String URL_API = "http://123.26.252.98:8084/api";
    //public  String URL_IMAGE = "http://123.26.252.98:8084";

    //http://123.26.252.98:8084



    public String cat2kitucuoi(String str){
        return str.substring(0,str.length()-2);
    }
    public String cat2kitudauvacuoi(String str){
        return str.substring(1,str.length()-1);
    }
    public String cat10kitucuoi(String str){
        return str.substring(0,10);
    }

    public Bitmap getBitmapFromURL(String src){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        src = URL_IMAGE+src;
        try {
            URL url = new URL(src);
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String strimBarcode(String resultscan){

        if(resultscan.length()>4) {
            resultscan = resultscan.substring(4, resultscan.length());
        }
        return resultscan;
    }

    public String GetDataToValue(Object Data, String sDefault)
    {
        String kq;
        if (Data==null||Data.toString().equals("null"))
        {
            kq=sDefault;
        }
        else
        {
            if (Data.toString().equals("")||Data.toString().equals("{}"))
            {
                kq=sDefault;
            }
            else   {kq=Data.toString();}
        }
        return kq;
    }

    public static String WriteNum(long num)
    {
        byte i;
        long sochia, T1, T2, T3;
        long luu;
        String st = "";
        String[] hang;
        String[] donvi;
        String ChuDau="";
        hang = new String[10];
        hang[0] = "";
        hang[1] = " một";
        hang[2] = " hai";
        hang[3] = " ba";
        hang[4] = " bốn";
        hang[5] = " năm";
        hang[6] = " sáu";
        hang[7] = " bảy";
        hang[8] = " tám";
        hang[9] = " chín";
        donvi = new String[4];
        donvi[0] = " tỉ";
        donvi[1] = " triệu";
        donvi[2] = " nghìn";
        donvi[3] = "";
        sochia = 1000000000;

        for (i = 0; i < 4; i++)
        {
            luu = num;
            luu = luu / sochia % 1000;
            T1 = luu / 100;
            T2 = luu / 10 % 10;
            T3 = luu % 10;
            if (T1 == 0 && T2 == 0 && T3 == 0)
            {
                sochia = sochia / 1000;
            }
            else
            {
                if (T1 == 0 && st != "") { st = st + " không"; }
                st = st + hang[(int)T1];
                if (st != "") { st = st + " trăm"; }
                if (T2 != 0)
                {
                    if (T2 == 1)
                    {
                        st = st + " mười";
                        if (T3 != 5) { st = st + hang[(int)T3]; }
                        if (T3 == 5) { st = st + " lăm"; }
                    }
                    else
                    {
                        st = st + hang[(int)T2] + " mươi";
                        if (T3 != 1 && T3 != 5) { st = st + hang[(int)T3]; }
                        if (T3 == 1 && T2 > 1) { st = st + " mốt"; }
                        if (T3 == 5) { st = st + " lăm"; }
                    }
                }
                else
                {
                    if (T3 != 0 && st != "") { st = st + " linh"; }
                    st = st + hang[(int)T3];
                }

                if (st != "") { st = st + donvi[i]; }
                sochia = sochia / 1000;
            }
        }
        if (st.trim() != "")
        {
            if (st.trim().length() > 1)
            {
                ChuDau = st.trim().substring(0, 1);
                ChuDau = ChuDau.toUpperCase();
                st = ChuDau + st.trim().substring(1, st.length() - 1);
            }
        }
        return st.trim() + " đồng";
    }

    public boolean checkGetCustomerInfoWereRead(Integer madb, Integer ms_tk, Integer ms_tuyendoc) {


            String url = URL_API + "/GetCustomerInfo?ms_mnoi=" + madb + "&ms_tk=" + ms_tk + "&ms_tuyen=" + ms_tuyendoc;
            Log.d("GetCustomerInfo", url);

            String ms_tuyen = "";
            String chi_so_moi = null;
            String so_tthu = null;
            String ngay_doc_moi = null;
            JSONObject jsonKH;
            JSONArray jsonArray = new JSONArray();

            try {
                jsonArray = ReadJson.readJSonArrayFromURL(url);
                if (jsonArray.length() > 0) {
                    jsonKH = jsonArray.getJSONObject(0);
                    chi_so_moi = jsonKH.getString("chi_so_moi");
                    so_tthu = jsonKH.getString("so_tieu_thu");
                    ngay_doc_moi = jsonKH.getString("ngay_doc_moi");

                    if ("null".equals(chi_so_moi) && "null".equals(so_tthu) && "null".equals(ngay_doc_moi)) {
                        return true;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return false;
    }

}
