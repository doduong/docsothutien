package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import model.ThongTinTieuThu;
import utils.CommonText;

public class AdapterDiemDungKhongDoc extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<ThongTinTieuThu> listddkd;
    private LayoutInflater mInflater = null;
    private ThongTinTieuThu tempValue = null;
    CommonText common = new CommonText();

    public AdapterDiemDungKhongDoc(Activity a, ArrayList<ThongTinTieuThu> d) {
        this.mActivity = a;
        this.listddkd = d;
        // mInflater = (LayoutInflater)mActivity.getSystemService(
        //Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(listddkd.size()<=0){
            return 0;
        }
        return listddkd.size();
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
        public ImageView imgAnh;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater = (LayoutInflater)mActivity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        View vi = view;
        AdapterDiemDungKhongDoc.ViewHolder holder;
        if(view == null){
            holder = new AdapterDiemDungKhongDoc.ViewHolder();

            vi = mInflater.inflate(R.layout.adapter_diem_dung_khong_doc, viewGroup, false);
            holder.tvstt = vi.findViewById(R.id.tvstt);
            holder.tvdanhba = vi.findViewById(R.id.tvdanhba);
            holder.tvtenkh = vi.findViewById(R.id.tvtenkh);
            holder.tvmstt = vi.findViewById(R.id.tvmstt);
            holder.imgAnh = vi.findViewById(R.id.imgAnh);
            vi.setTag(holder);
        }
        holder = (AdapterDiemDungKhongDoc.ViewHolder) vi.getTag();
        try {
            if(listddkd.size()<=0){
                holder.tvstt.setText("NoData");
                holder.tvdanhba.setText("No Data");
                holder.tvtenkh.setText("No Data");
                holder.tvstt.setText("NoData");

            }else  {
                tempValue = null;
                tempValue = (ThongTinTieuThu) listddkd.get(i);

                holder.tvstt.setBackgroundColor(Color.WHITE);
                holder.tvdanhba.setBackgroundColor(Color.WHITE);
                holder.tvtenkh.setBackgroundColor(Color.WHITE);
                holder.tvstt.setBackgroundColor(Color.WHITE);

                holder.tvstt.setText(String.valueOf(i+1));
               holder.tvdanhba.setText(String.valueOf(tempValue.getMs_moinoi()));
                holder.tvtenkh.setText(tempValue.getNguoi_thue());
                holder.tvmstt.setText(String.valueOf(tempValue.getMs_ttrang_docdh()));
                String url_image = tempValue.getUrl_image();
               // byte[] hinhAnh = tempValue.getAnh();
               Bitmap bitmap = common.getBitmapFromURL(url_image);
                holder.imgAnh.setImageBitmap(bitmap);

            }
        }catch (Exception ex) {

            ex.printStackTrace();
        }

        return vi;
    }

    /*public Bitmap getBitmapFromURL(String src){

        try {
            URL url = new URL(src);
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }*/
}
