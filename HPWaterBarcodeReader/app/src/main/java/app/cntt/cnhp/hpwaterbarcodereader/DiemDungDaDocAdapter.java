package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import model.ThongTinTieuThu;
import utils.DBManager;

public class DiemDungDaDocAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<ThongTinTieuThu> listKH;
    private ThongTinTieuThu tempValue = null;
    private DBManager dbManager;
    Integer tuyen = null;
    int stt = 0;


    public DiemDungDaDocAdapter(Activity a, ArrayList<ThongTinTieuThu> d, DBManager dbManager, Integer tuyen) {
        this.mActivity = a;
        this.listKH = d;
        this.dbManager = dbManager;
        this.tuyen = tuyen;
        stt = 0;
        // mInflater = (LayoutInflater)mActivity.getSystemService(
        //Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (listKH.size() <= 0) {
            return 1;
        }
        return listKH.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public static class ViewHolder {
        private TextView tvMaKH;
        private TextView tvTenKH;
        private TextView tvDiaChi;
        private TextView tvth;
        private TextView tvstt;
        private CheckBox chkdatinhtien;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        //int stt = 0;

        LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);

        View vi = view;
        DiemDungDaDocAdapter.ViewHolder holder;
        if (view == null) {
            holder = new DiemDungDaDocAdapter.ViewHolder();

            vi = mInflater.inflate(R.layout.diem_dung_da_doc_list, viewGroup, false);
            vi.requestFocus();
            vi.setMinimumHeight(50);
            holder.tvstt = vi.findViewById(R.id.tvstt);
            holder.tvMaKH = vi.findViewById(R.id.tvmakh);
            holder.tvTenKH = vi.findViewById(R.id.tvtenkh);
            holder.tvDiaChi = vi.findViewById(R.id.tvdiachi);
            holder.tvth = vi.findViewById(R.id.tvtt);
            holder.chkdatinhtien = vi.findViewById(R.id.chkdatinhtien);
            vi.setTag(holder);
        }
        //else {
        holder = (DiemDungDaDocAdapter.ViewHolder) vi.getTag();
        //holder = new ViewHolder();
        try {
            if (listKH.size() <= 0) {
                holder.tvMaKH.setText("");
                holder.tvTenKH.setText("");

            } else {
                tempValue = null;
                tempValue = (ThongTinTieuThu) listKH.get(i);


                if (tempValue.getMs_ttrang_tt()==1||tempValue.getMs_ttrang_tt()==0) {
                    holder.tvstt.setBackgroundColor(Color.WHITE);
                    holder.tvMaKH.setBackgroundColor(Color.WHITE);
                    holder.tvTenKH.setBackgroundColor(Color.WHITE);
                    holder.tvDiaChi.setBackgroundColor(Color.WHITE);
                    holder.tvth.setBackgroundColor(Color.WHITE);
                    holder.chkdatinhtien.setBackgroundColor(Color.WHITE);
                } else if (tempValue.getMs_ttrang_tt()==3) {
                    holder.tvstt.setBackgroundResource(R.color.dathu);
                    holder.tvMaKH.setBackgroundResource(R.color.dathu);
                    holder.tvTenKH.setBackgroundResource(R.color.dathu);
                    holder.tvDiaChi.setBackgroundResource(R.color.dathu);
                    holder.tvth.setBackgroundResource(R.color.dathu);
                    holder.chkdatinhtien.setBackgroundResource(R.color.dathu);
                }


                //holder.tvstt.setText(String.valueOf(i + 1));
                holder.tvstt.setText(String.valueOf(tempValue.getStt_lo_trinh()));
                holder.tvMaKH.setText(String.valueOf(tempValue.getMs_moinoi()));
                holder.tvTenKH.setText(tempValue.getNguoi_thue());
                holder.tvDiaChi.setText(tempValue.getDia_chi());
                holder.tvth.setText(String.valueOf(tempValue.getSo_tthu()));
                if(tempValue.getMs_hd()!=0) {
                    holder.chkdatinhtien.setChecked(true);
                }else {
                    holder.chkdatinhtien.setChecked(false);
                }


            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }



        return vi;
    }
}
