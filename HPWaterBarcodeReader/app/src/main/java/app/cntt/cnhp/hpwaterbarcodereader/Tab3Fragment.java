package app.cntt.cnhp.hpwaterbarcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.util.ArrayList;

import model.DongHoNoi;
import utils.CommonText;
import utils.DBManager;
import utils.SharedPref;

public class Tab3Fragment extends Fragment
{
    private View mRootView;

    Connection conn;
    private SQLiteCustomAdapter adapter;
    public ArrayList<DongHoNoi> customlistViewValueArray = new ArrayList<DongHoNoi>();
    ListView lstpro1;
    DBManager dbManager ;
    String ms_tuyen = "";
    String ms_tk="";
    String ms_bd = "";
    Button btnxoabangtam;
    String tongsodiemdung="";
   // TextView tvtiledadoc2;
   // TextView tvtongluongtieuthu2;
    CommonText common = new CommonText();
    SharedPref config;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab3_fragment, container, false);
        initView();
        return mRootView;
    }

    private void initView(){
         dbManager = new DBManager(getActivity());
        btnxoabangtam = (Button) mRootView.findViewById(R.id.btnxoabangtam);
        final Intent intent = getActivity().getIntent();
        config = new SharedPref(getActivity());
        ms_tuyen = config.getString("ms_tuyen", "");
        ms_tk =  config.getString("ms_tk", "");
        ms_bd =  config.getString("ms_bd","");
        tongsodiemdung= intent.getStringExtra("tongsodiemdung");

        lstpro1 = (ListView) mRootView.findViewById(R.id.lstpro1);
        //tvtiledadoc2 = (TextView) mRootView.findViewById(R.id.tvtiledadoc2);
        //tvtongluongtieuthu2 = (TextView) mRootView.findViewById(R.id.tvtongluongtieuthu2) ;


        int tuyen = Integer.parseInt(ms_tuyen);
        customlistViewValueArray  =  dbManager.getAllDongHoNoi(tuyen);
        adapter = new SQLiteCustomAdapter(getActivity(), customlistViewValueArray);
        lstpro1.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        int tongtieuthu = 0;
        for(int i =0; i<customlistViewValueArray.size(); i++){
            tongtieuthu = tongtieuthu + customlistViewValueArray.get(i).getSo_tthu();

        }
        String sodadoc = String.valueOf(customlistViewValueArray.size());
        String tongtieuthuString = String.valueOf(tongtieuthu);
       // tvtiledadoc2.setText("Đã đoc: "+sodadoc+"/"+tongsodiemdung);
        //tvtongluongtieuthu2.setText("T/L.T.Thu: "+tongtieuthuString);

        btnxoabangtam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbManager.deleteTableTemp();
                adapter.notifyDataSetChanged();
                startActivity(intent);

            }
        });

    }
}
