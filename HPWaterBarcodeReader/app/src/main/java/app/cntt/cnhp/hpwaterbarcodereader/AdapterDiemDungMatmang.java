package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.DiemDungKhongDoc;
import model.DiemDungMatMang;

public class AdapterDiemDungMatmang extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<DiemDungMatMang> listddmm;
    private LayoutInflater mInflater = null;
    private DiemDungMatMang tempValue = null;

    public AdapterDiemDungMatmang(Activity a, ArrayList<DiemDungMatMang> d) {
        this.mActivity = a;
        this.listddmm = d;
    }




    @Override
    public int getCount() {
        if(listddmm.size()<=0){
            return 0;
        }
        return listddmm.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder{
        public TextView tvstt;
        public TextView tvdanhba;
        public TextView tvtenkh;
        public TextView tvmstt;
        public TextView tvngaydoc;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater)mActivity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        View vi = view;
        AdapterDiemDungMatmang.ViewHolder holder;
        if(view == null){
            holder = new AdapterDiemDungMatmang.ViewHolder();

            vi = mInflater.inflate(R.layout.adapter_diem_dung_matmang, viewGroup, false);
            holder.tvstt = vi.findViewById(R.id.tvstt);
            holder.tvdanhba = vi.findViewById(R.id.tvdanhba);
            holder.tvtenkh = vi.findViewById(R.id.tvtenkh);
            holder.tvmstt = vi.findViewById(R.id.tvmstt);
            holder.tvngaydoc = vi.findViewById(R.id.tvngaydoc);
            vi.setTag(holder);
        }
        holder = (AdapterDiemDungMatmang.ViewHolder) vi.getTag();
        try {
            if(listddmm.size()<=0){
                holder.tvstt.setText("NoData");
                holder.tvdanhba.setText("No Data");
                holder.tvtenkh.setText("No Data");
                holder.tvstt.setText("NoData");
                holder.tvstt.setText("NoData");

            }else  {
                tempValue = null;
                tempValue = (DiemDungMatMang) listddmm.get(i);

                holder.tvstt.setBackgroundColor(Color.WHITE);
                holder.tvdanhba.setBackgroundColor(Color.WHITE);
                holder.tvtenkh.setBackgroundColor(Color.WHITE);
                holder.tvstt.setBackgroundColor(Color.WHITE);

                holder.tvstt.setText(String.valueOf(i+1));
                holder.tvdanhba.setText(String.valueOf(tempValue.getMs_mnoi()));
                holder.tvtenkh.setText(tempValue.getTenkh());
                holder.tvmstt.setText(String.valueOf(tempValue.getMs_ttrang()));
                holder.tvngaydoc.setText(String.valueOf(tempValue.getNgay_doc_moi()));

            }
        }catch (Exception ex) {

            ex.printStackTrace();
        }

        return vi;
    }
}
