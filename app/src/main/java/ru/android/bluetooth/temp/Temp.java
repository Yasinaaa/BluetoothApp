package ru.android.bluetooth.temp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import ru.android.bluetooth.R;
import ru.android.bluetooth.root.RootActivity;

/**
 * Created by yasina on 01.10.17.
 */

public class Temp extends RootActivity {

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp3);
        start();
    }


    @Override
    public void init() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setClickListeners() {

    }

    @Override
    public void setTag() {

    }

    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Январь");
        adapter.addFragment(new OneFragment(), "Февраль");
        adapter.addFragment(new OneFragment(), "Март");

        adapter.addFragment(new OneFragment(), "Апрель");
        adapter.addFragment(new OneFragment(), "Май");
        adapter.addFragment(new OneFragment(), "Июнь");

        adapter.addFragment(new OneFragment(), "Июль");
        adapter.addFragment(new OneFragment(), "Август");
        adapter.addFragment(new OneFragment(), "Сентябрь");

        adapter.addFragment(new OneFragment(), "Октябрь");
        adapter.addFragment(new OneFragment(), "Ноябрь");
        adapter.addFragment(new OneFragment(), "Декабрь");

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
