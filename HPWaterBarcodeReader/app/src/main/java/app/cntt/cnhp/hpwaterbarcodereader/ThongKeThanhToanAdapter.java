package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.ThongTinThanhToanTuyen;
import model.ThongTinTieuThu;

public class ThongKeThanhToanAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<ThongTinThanhToanTuyen> listTTTT;
    private LayoutInflater mInflater = null;
    private ThongTinThanhToanTuyen tempValue = null;

    public ThongKeThanhToanAdapter(Activity mActivity, ArrayList<ThongTinThanhToanTuyen> listTTTT) {
        this.mActivity = mActivity;
        this.listTTTT = listTTTT;
    }

    @Override
    public int getCount() {
        if (listTTTT.size() <= 0) {
            return 1;
        }
        return listTTTT.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public static class ViewHolder {
        private TextView tvMaKH;
        private TextView tvTenKH;
        private TextView tvTongTien;
        private TextView tvstt;

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);

        View vi = view;
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();

            vi = mInflater.inflate(R.layout.thanhtoantuyenreport, parent, false);
            vi.requestFocus();
            vi.setMinimumHeight(50);
            holder.tvstt = vi.findViewById(R.id.tvstt);
            holder.tvMaKH = vi.findViewById(R.id.tvmakh);
            holder.tvTenKH = vi.findViewById(R.id.tvtenkh);
            holder.tvTongTien = vi.findViewById(R.id.tvTongTien);
            vi.setTag(holder);
        }

        holder = (ViewHolder) vi.getTag();
        //holder = new ViewHolder();
        try {
            if (listTTTT.size() <= 0) {
                holder.tvMaKH.setText("");
                holder.tvTenKH.setText("");

            } else {
                tempValue = null;
                tempValue = (ThongTinThanhToanTuyen) listTTTT.get(i);
                if (tempValue.getMs_ttrang() == 1|| tempValue.getMs_ttrang()==0) {

                    holder.tvstt.setBackgroundColor(Color.WHITE);
                    holder.tvMaKH.setBackgroundColor(Color.WHITE);
                    holder.tvTenKH.setBackgroundColor(Color.WHITE);
                    holder.tvTongTien.setBackgroundColor(Color.WHITE);
                    /*holder.tvstt.setText(String.valueOf(tempValue.getStt_lo_trinh()));
                    holder.tvstt.setText(String.valueOf(i + 1));
                    holder.tvMaKH.setText(String.valueOf(tempValue.getMs_mnoi()));
                    holder.tvTenKH.setText(tempValue.getTen_kh());
                    holder.tvTongTien.setText(String.valueOf(tempValue.getTong_tien()));*/

                } else if(tempValue.getMs_ttrang() == 3){
                    if(tempValue.getMs_kieu_tt() ==1) {
                        holder.tvstt.setBackgroundResource(R.color.dathu);
                        holder.tvMaKH.setBackgroundResource(R.color.dathu);
                        holder.tvTenKH.setBackgroundResource(R.color.dathu);
                        holder.tvTongTien.setBackgroundResource(R.color.dathu);
                    }else {
                        holder.tvstt.setBackgroundResource(R.color.ttkhac);
                        holder.tvMaKH.setBackgroundResource(R.color.ttkhac);
                        holder.tvTenKH.setBackgroundResource(R.color.ttkhac);
                        holder.tvTongTien.setBackgroundResource(R.color.ttkhac);
                    }
                }

                holder.tvstt.setText(String.valueOf(tempValue.getStt_lo_trinh()));
                holder.tvMaKH.setText(String.valueOf(tempValue.getMs_mnoi()));
                holder.tvTenKH.setText(tempValue.getTen_kh());
                holder.tvTongTien.setText(String.valueOf(tempValue.getTong_tien()));


            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return vi;
    }
}
