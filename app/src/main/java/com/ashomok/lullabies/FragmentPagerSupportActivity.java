package com.ashomok.lullabies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;


/**
 * Created by Iuliia on 31.03.2016.
 */
public class FragmentPagerSupportActivity extends Activity {
    protected static final int NUM_ITEMS = 3;

    private MyAdapter mAdapter;

    private ViewPager mPager;

    FloatingActionButton fabPlay;
    FloatingActionButton fabPause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_pager);

            mAdapter = new MyAdapter(getFragmentManager());

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);

            int currentPage = 0;
            mPager.setCurrentItem(currentPage);

            fabPlay = (FloatingActionButton) findViewById(R.id.play);
            fabPause = (FloatingActionButton) findViewById(R.id.stop);

            mPager.addOnPageChangeListener(new OnPageChangeListenerImpl());

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


    //TODO useless?
    @Override
    public void onBackPressed() {

        int currentPageNumber = mPager.getCurrentItem();
        if (currentPageNumber > 0) {
            mPager.setCurrentItem(--currentPageNumber);
        } else {
            super.onBackPressed();
        }

    }

    private class OnPageChangeListenerImpl implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {

            fabPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MediaPlayerService.class);
                    MusicFragmentSettings settings = MusicFragment.musicFragmentSettingsList.get(position);
                    int track = settings.getTrack();
                    intent.putExtra("music_res_id", track);
                    startService(intent);
                    fabPause.setVisibility(View.VISIBLE);
                    fabPlay.setVisibility(View.GONE);

                }
            });

            fabPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    fabPlay.setVisibility(View.VISIBLE);
                    fabPause.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}