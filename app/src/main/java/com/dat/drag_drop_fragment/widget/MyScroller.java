package com.dat.drag_drop_fragment.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public class MyScroller extends HorizontalScrollView {
    public int viewPortWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private GestureDetector mGestureDetector;
    private int mActiveFeature = 0;
    private int currentPage = 0;
    private int totalPage = 0;

    public MyScroller(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyScroller(Context context) {
        super(context);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        setOnTouchListener((v, event) -> {
            //If the user swipes
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                int scrollX = getScrollX();
                int childWidth = getChildAt(0).getMeasuredWidth();
                totalPage = childWidth / viewPortWidth + 1;
                int supposePage = (scrollX + viewPortWidth / 2) / viewPortWidth;
                scrollToPage(supposePage);
                return true;
            } else {
                return false;
            }
        });
        mGestureDetector = new GestureDetector(new MyGestureDetector());
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //right to left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    scrollToNextPage();
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    scrollToPreviousPage();
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }

    public void scrollToPage(int page) {
        if (page > totalPage || page < 0)
            scrollToPage(currentPage);
        else {
            smoothScrollTo(viewPortWidth * page, 0);
            currentPage = page;
        }
    }

    public void scrollToPreviousPage() {
        scrollToPage(currentPage -1);
    }

    public void scrollToNextPage() {
        scrollToPage(currentPage + 1);
    }

    public static List<View> getAllViews(ViewGroup viewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            views.add(viewGroup.getChildAt(i));
        }
        return views;
    }

}
