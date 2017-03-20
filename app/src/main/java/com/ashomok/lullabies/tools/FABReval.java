package com.ashomok.lullabies.tools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by iuliia on 3/20/17.
 */

public class FABReval extends FloatingActionButton {

    View myView;

    public FABReval(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * set view, which appers after reval animation
     *
     * @param view
     */
    public void setViewAppears(View view) {
        myView = view;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton();
            }
        });

    }

    private void animateButton() {

        animate().translationXBy(0.5f).translationY(150).translationXBy(-0.9f)
                .translationX(-150).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                animateReavel((int) getX(), 150);
            }
        });

    }

    private void animateReavel(int cx, int cy) {

        // get the final radius for the clipping circle
        float finalRadius = hypo(myView.getWidth(), myView.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        animator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {
                setVisibility(View.INVISIBLE);
                myView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
            }

            @Override
            public void onAnimationCancel() {
            }

            @Override
            public void onAnimationRepeat() {
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();

    }

    static float hypo(int a, int b) {
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}
