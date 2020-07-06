package app.cntt.cnhp.hpwaterbarcodereader;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends FragmentStatePagerAdapter{

    //Thu tien
    private String listTab[] = {"CHƯA ĐỌC", "ĐÃ ĐỌC", "KHÔNG ĐỌC", "SỐ ĐỌC", "MẤT K.NỐI", "THỐNG KÊ"};
    //End Thu tien
    private Tab1Fragment tab1 ;
    private  Tab2Fragment tab2;
    private  Tab3Fragment tab3;
    private Tab4_KhongDocDuoc tab4;
    private Tab5_MatMang tab5;
    private Tab6_ThongKe tab6;


    public MyAdapter(FragmentManager fm) {
        super(fm);
        tab1 = new Tab1Fragment();
        tab2 = new Tab2Fragment();
        tab3 = new Tab3Fragment();
        tab4 = new Tab4_KhongDocDuoc();
        tab5 = new Tab5_MatMang();
        tab6 = new Tab6_ThongKe();
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return tab1;

        }else if (position ==1) {
            return tab2;

        }else if (position ==2) {
            return tab4;

        }else if (position == 3){
            return tab3;
        }
        else if (position == 4){
            return tab5;
        }
        //Thu tien
        else if (position == 5){
            return tab6;
        }
        //End Thu tien
        return null;
    }

    @Override
    public int getCount() {
        return listTab.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTab[position];
    }
}
