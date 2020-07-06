package app.cntt.cnhp.hpwaterbarcodereader;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.DongHoNoi;

public class SQLiteCustomAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<DongHoNoi> listDH;
    private LayoutInflater mInflater = null;
    private DongHoNoi tempValue = null;

    public SQLiteCustomAdapter(Activity a, ArrayList<DongHoNoi> d) {
        this.mActivity = a;
        this.listDH = d;
        // mInflater = (LayoutInflater)mActivity.getSystemService(
        //Activity.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if(listDH.size()<=0){
            return 0;
        }
        return listDH.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder{
        public TextView tvMaKH1;
        public TextView tvTenKH1;
        //public TextView tvDiaChi1;
        public TextView tvcsc1;
        public TextView tvcsm1;
        public TextView tvth1;
        public TextView tvstt1;
        public TextView tvngaydocmoi1;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater)mActivity.getSystemService(
                Activity.LAYOUT_INFLATER_SERVICE);
        View vi = view;
        SQLiteCustomAdapter.ViewHolder holder;
        if(view == null){
            holder = new SQLiteCustomAdapter.ViewHolder();

            vi = mInflater.inflate(R.layout.doc_so_sqlite_listview, viewGroup, false);
            holder.tvstt1 = vi.findViewById(R.id.tvstt1);
            holder.tvMaKH1 = vi.findViewById(R.id.tvmakh1);
            holder.tvTenKH1 = vi.findViewById(R.id.tvtenkh1);
            //holder.tvDiaChi1 = vi.findViewById(R.id.tvdiachi1);
            holder.tvngaydocmoi1 = vi.findViewById(R.id.tvngaydocmoi1);
            holder.tvcsc1 = vi.findViewById(R.id.tvcsc1);
            holder.tvcsm1 = vi.findViewById(R.id.tvcsm1);
            holder.tvth1 = vi.findViewById(R.id.tvtt1);
            vi.setTag(holder);
        }
        //else {
            holder = (SQLiteCustomAdapter.ViewHolder) vi.getTag();
            try {
                if(listDH.size()<=0){

                }else

                    tempValue = null;
                tempValue = (DongHoNoi) listDH.get(i);
                {
                    if(tempValue.getChi_so_moi()==0){

                        holder.tvstt1.setBackgroundColor(Color.WHITE);
                        holder.tvMaKH1.setBackgroundColor(Color.WHITE);
                        holder.tvTenKH1.setBackgroundColor(Color.WHITE);
                      //  holder.tvDiaChi1.setBackgroundColor(Color.WHITE);
                        holder.tvcsc1.setBackgroundColor(Color.WHITE);
                        holder.tvcsm1.setBackgroundColor(Color.WHITE);
                        holder.tvth1.setBackgroundColor(Color.WHITE);


                        holder.tvstt1.setText(String.valueOf(i+1));
                        holder.tvMaKH1.setText(String.valueOf(tempValue.getMs_mnoi()));
                        holder.tvTenKH1.setText(String.valueOf(tempValue.getTenkh()));
                      //  holder.tvDiaChi1.setText(String.valueOf(tempValue.getDiachi()));
                        holder.tvngaydocmoi1.setText(tempValue.getNgay_doc_moi());
                        holder.tvcsc1.setText(String.valueOf(tempValue.getChi_so_cu()));
                        holder.tvcsm1.setText(String.valueOf(tempValue.getChi_so_moi()));
                        holder.tvth1.setText(String.valueOf(tempValue.getSo_tthu()));
                    }else {

                       /* holder.tvstt1.setBackgroundColor(Color.GRAY);
                        holder.tvMaKH1.setBackgroundColor(Color.GRAY);
                        holder.tvTenKH1.setBackgroundColor(Color.GRAY);
                        holder.tvDiaChi1.setBackgroundColor(Color.GRAY);
                        holder.tvngaydocmoi1.setBackgroundColor(Color.GRAY);
                        holder.tvcsc1.setBackgroundColor(Color.GRAY);
                        holder.tvcsm1.setBackgroundColor(Color.GRAY);
                        holder.tvth1.setBackgroundColor(Color.GRAY);*/

                        holder.tvstt1.setText(String.valueOf(i+1));
                        holder.tvMaKH1.setText(String.valueOf(tempValue.getMs_mnoi()));
                        holder.tvTenKH1.setText(String.valueOf(tempValue.getTenkh()));
                      //  holder.tvDiaChi1.setText(String.valueOf(tempValue.getDiachi()));
                        holder.tvngaydocmoi1.setText(tempValue.getNgay_doc_moi());
                        holder.tvcsc1.setText(String.valueOf(tempValue.getChi_so_cu()));
                        holder.tvcsm1.setText(String.valueOf(tempValue.getChi_so_moi()));
                        holder.tvth1.setText(String.valueOf(tempValue.getSo_tthu()));
                    }


                }
            }catch (Exception ex) {

                ex.printStackTrace();
            }


       // }

        return vi;
    }
}
