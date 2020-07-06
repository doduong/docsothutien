package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.TuyenDocCS;
import utils.BaseActivity;
import utils.CommonText;
import utils.ReadJson;
import utils.Server;
import utils.SharedPref;

public class TuyenDoc extends BaseActivity {

    TextView lblheader;
    ListView lsttuyendoc;
    Connection conn;
    TextView lsttentuyendoc;
    String ms_bd;
    String ms_tk = "";
    SharedPref config;
    ProgressDialog progress;
    private Activity ac;
    ArrayList<TuyenDocCS> listTuyenDoc;
    AdapterTuyenDoc adapterTuyenDoc;
    CommonText common = new CommonText();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_option);
        Intent intent = this.getIntent();

        lblheader = (TextView) findViewById(R.id.lblheader);
        lsttuyendoc = (ListView) findViewById(R.id.lsttuyendoc);
        lsttentuyendoc = (TextView) findViewById(R.id.lbltentuyendoc);
        config = new SharedPref(this);
        ms_bd = config.getString("ms_bd", "");
        ms_tk = config.getString("ms_tk", "");

        if (isConnected()) {
            String url = common.URL_API + "/GetReadingLineByUser?userId=" + ms_bd;
            new HttpAsyncTaskGetTuyenDoc().execute(url);


            lsttuyendoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (isConnected()) {
                        TuyenDocCS objtuyendoc = listTuyenDoc.get(i);
                        config.putString("ten_tuyen", objtuyendoc.getMo_ta_tuyen());
                        config.putString("ms_tuyen", String.valueOf(objtuyendoc.getMs_tuyen()));
                        config.commit();

                        Intent intent = new Intent(TuyenDoc.this, TabActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(TuyenDoc.this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(TuyenDoc.this, "Chưa kết nối internet", Toast.LENGTH_LONG).show();
        }

    }

    public String catchuoi(String chuoi) {

        chuoi = chuoi.substring(chuoi.indexOf("[") + 1);
        chuoi = chuoi.substring(0, chuoi.indexOf("]"));

        return chuoi;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        doubleBackToExitPressedOnce = true;


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                myOwnBackPress();
            }
        }, 500);
    }

    private void myOwnBackPress() {
        if (!isFinishing()) {
            //super.onBackPressed();
             Intent intent = new Intent(TuyenDoc.this, Login.class);
            startActivity(intent);
        }
    }

    private class HttpAsyncTaskGetTuyenDoc extends AsyncTask<String, JSONObject, Void> {
        ProgressDialog progressDialog = new ProgressDialog(TuyenDoc.this);

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Nạp tuyến...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];
            JSONArray jsonArrayTuyen;

            try {
                listTuyenDoc = new ArrayList<>();
                jsonArrayTuyen = ReadJson.readJSonArrayFromURL(url);
                Log.d("doInBackground", String.valueOf(jsonArrayTuyen.length()));
                for (int i = 0; i < jsonArrayTuyen.length(); i++) {
                    TuyenDocCS tuyenDocCS;
                    JSONObject objTuyen = jsonArrayTuyen.getJSONObject(i);
                    int ms_tuyen = objTuyen.getInt("ms_tuyen");
                    int ms_tql = objTuyen.getInt("ms_tql");
                    int ms_cky = objTuyen.getInt("ms_cky");
                    String mo_ta_tuyen = objTuyen.getString("mo_ta_tuyen");
                    int ms_tn = objTuyen.getInt("ms_tn");
                    int ms_bd = objTuyen.getInt("ms_bd");
                    tuyenDocCS = new TuyenDocCS(ms_tuyen, ms_tql, ms_cky, mo_ta_tuyen, ms_tn, ms_bd);
                    listTuyenDoc.add(tuyenDocCS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapterTuyenDoc = new AdapterTuyenDoc(TuyenDoc.this, listTuyenDoc);
            lsttuyendoc.setAdapter(adapterTuyenDoc);
            adapterTuyenDoc.notifyDataSetChanged();
            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }


}
