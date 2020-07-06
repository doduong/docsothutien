package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.sql.Connection;
import java.util.ArrayList;

import model.DiemDungKhongDoc;
import model.DiemDungMatMang;
import model.ThongTinTieuThu;
import utils.DBManager;
import utils.SharedPref;

public class Tab5_MatMang extends Fragment {

    private View mRootView;
    private static final String TAG = "Tab1Fragment";

    Connection conn;
    private AdapterDiemDungMatmang adapter;
    public ArrayList<DiemDungMatMang> customlistViewValueArray = new ArrayList<DiemDungMatMang>();
    ListView lstpro1;
    DBManager dbManager;
    String ms_tuyen = "";
    String ms_tk = "";
    String ms_bd = "";
    Button btnxoabangtam;
    String tongsodiemdung = "";
    SharedPref config ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab5__mat_mang, container, false);
        initView();
        return mRootView;
    }

    private void initView() {

        config = new SharedPref(getActivity());
        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk =  config.getString("ms_tk", "");
        ms_bd =  config.getString("ms_bd","");
        dbManager = new DBManager(getActivity());
        btnxoabangtam = (Button) mRootView.findViewById(R.id.btnxoabangtam);
        final Intent intent = getActivity().getIntent();

        tongsodiemdung = intent.getStringExtra("tongsodiemdung");

        lstpro1 = (ListView) mRootView.findViewById(R.id.lstkhongdocduoc);

        int tuyen = Integer.parseInt(ms_tuyen);
        customlistViewValueArray = dbManager.getAllDiemDungMatMang(tuyen);
        Log.d("listTab5", String.valueOf(customlistViewValueArray.size()));
        adapter = new AdapterDiemDungMatmang(getActivity(), customlistViewValueArray);
        lstpro1.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        

        lstpro1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiemDungMatMang ddmm = customlistViewValueArray.get(i);
                Log.d("masotky", String.valueOf(ddmm.getMs_tk()));
                Intent intent = new Intent(getActivity(), ChiTietDiemDungMatMang.class);
               // intent.putExtra("arrlist",customlistViewValueArray );
                intent.putExtra("ddmm", ddmm);
                intent.putExtra("ms_tuyen", ms_tuyen);
                intent.putExtra("ms_tk", String.valueOf(ddmm.getMs_tk()));
                startActivity(intent);

            }
        });
        btnxoabangtam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbManager.deleteTableMatMang();
                adapter.notifyDataSetChanged();
                startActivity(intent);

            }
        });
    }


}
