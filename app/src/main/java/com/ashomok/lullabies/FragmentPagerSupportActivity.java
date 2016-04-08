package com.ashomok.lullabies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.Button;

import com.ashomok.lullabies.tools.NonSwipeableViewPager;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends Activity {
    static final int NUM_ITEMS = 2;

    private MyAdapter mAdapter;

    private NonSwipeableViewPager mPager;

    private Button btn_back;

    private Button btn_next;

    private int currentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pager);

            mAdapter = new MyAdapter(getFragmentManager());


            mPager = (NonSwipeableViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);

            currentPage = 0;
            mPager.setCurrentItem(currentPage);


            btn_back = (Button) findViewById(R.id.btn_back);
            btn_back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (currentPage > 1) {
                        currentPage--;
                        if (currentPage == 0) {
                            btn_back.setVisibility(View.GONE);
                        } else {
                            btn_back.setVisibility(View.VISIBLE);
                        }
                    }

                    mPager.setCurrentItem(currentPage);
                }
            });

            btn_next = (Button) findViewById(R.id.btn_next);
            btn_next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (currentPage < NUM_ITEMS - 1) {
                        currentPage++;

                        if (currentPage == NUM_ITEMS - 1) {
                            btn_next.setVisibility(View.GONE);
                        } else {
                            btn_next.setVisibility(View.VISIBLE);
                        }
                    }
                    mPager.setCurrentItem(currentPage);
                }
            });

            btn_back.setVisibility(View.GONE);
            if (NUM_ITEMS < 2) {
                btn_next.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (position < 0) {
                throw new IllegalStateException("Number of page < 0.");
            }
            return FragmentFactory.newInstance(position);
        }
    }

}