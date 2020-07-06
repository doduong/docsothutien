package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.TuyenDocCS;

public class AdapterTuyenDoc extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<TuyenDocCS> lsttuyendoc;
    private LayoutInflater mInflater = null;
    private TuyenDocCS tempValue = null;

    public AdapterTuyenDoc(Activity a, ArrayList<TuyenDocCS> lstTuyenDoc) {
        super();
        this.mActivity = a;
        this.lsttuyendoc = lstTuyenDoc;
    }


    @Override
    public int getCount() {
        Log.d("lsttuyendoc", String.valueOf(lsttuyendoc.size()));
        return lsttuyendoc.size();
    }

    @Override
    public Object getItem(int i) {
        return lsttuyendoc.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder{
        public TextView tvtuyendoc;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mInflater = (LayoutInflater)mActivity.getLayoutInflater();
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.adapter_tuyen_doc,null);
            holder = new ViewHolder();
            holder.tvtuyendoc = view.findViewById(R.id.tvtuyendoc);
            view.setTag(holder);
        }else {
            holder = (AdapterTuyenDoc.ViewHolder) view.getTag();
        }
        try {
            if(lsttuyendoc.size()<=0){
                holder.tvtuyendoc.setText("NoData");

            }else  {
                tempValue = null;
                tempValue = (TuyenDocCS) lsttuyendoc.get(i);
                //Log.d("ms_tuyen: ", String.valueOf(tempValue.getMs_tuyen())+ " --- mo_ta_tuyen: " + tempValue.getMo_ta_tuyen() );

                holder.tvtuyendoc.setBackgroundColor(Color.WHITE);
                holder.tvtuyendoc.setText(String.valueOf(tempValue.getMs_tuyen())+ ": "+ tempValue.getMo_ta_tuyen());

            }
        }catch (Exception ex) {

            ex.printStackTrace();
        }

        return view;
    }
}
