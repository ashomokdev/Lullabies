package com.ashomok.lullabies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends Activity {
    static final int NUM_ITEMS = 1;

    MyAdapter mAdapter;

    ViewPager mPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pager);

            mAdapter = new MyAdapter(getFragmentManager());

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);

            mPager.setCurrentItem(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //TODO button to next room will be here
//        // Watch for button clicks.
//        Button button = (Button)findViewById(R.id.goto_first);
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mPager.setCurrentItem(0);
//            }
//        });
//        button = (Button)findViewById(R.id.goto_last);
//        button.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                mPager.setCurrentItem(NUM_ITEMS-1);
//            }
//        });
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
          return FragmentFactory.newInstance(position);
        }
    }

}