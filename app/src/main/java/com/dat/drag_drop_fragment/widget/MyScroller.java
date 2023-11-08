package com.dat.drag_drop_fragment.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.core.view.GestureDetectorCompat;

public class MyScroller extends HorizontalScrollView {
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private GestureDetectorCompat mGestureDetector;
    private int mActiveFeature = 0;
    private int currentPageIndex = 0;
    private int totalPage = 0;
    private OnPageChangeListener onPageChangeListener;
    private final Handler handlerDelayUpdatePage = new Handler();

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
                int supposePage = (scrollX + (getMeasuredWidth() / 2)) / getMeasuredWidth();
                scrollToPage(supposePage);
                return true;
            } else {
                return false;
            }
        });
        mGestureDetector = new GestureDetectorCompat(getContext(), new MyGestureDetector());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getChildAt(0).addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            handlerDelayUpdatePage.removeCallbacksAndMessages(null);
            handlerDelayUpdatePage.postDelayed(() -> {
                setTotalPage(getChildAt(0).getMeasuredWidth() / getMeasuredWidth());
            }, 100);
        });
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            try {
                //right to left
                if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && velocityX < 0) {
                    scrollToNextPage();
                    return true;
                }
                //left to right
                else if (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && velocityX > 0) {
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

        if (page >= totalPage || page < 0) {
            smoothScrollTo(currentPageIndex * getMeasuredWidth(), 0);
        } else {
            smoothScrollTo(getMeasuredWidth() * page, 0);
            currentPageIndex = page;
        }
        if (onPageChangeListener != null)
            onPageChangeListener.onPageSelected(currentPageIndex);
        Log.d("MyScroller", "scrollToPage " + currentPageIndex);
    }

    public void setTotalPage(int totalPage) {
        try {
            if (this.totalPage != totalPage && onPageChangeListener != null) {
                onPageChangeListener.onUpdateTotalPage(totalPage);
            }
            this.totalPage = totalPage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scrollToPreviousPage() {
        scrollToPage(currentPageIndex - 1);
    }

    public void scrollToNextPage() {
        scrollToPage(currentPageIndex + 1);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);

        void onUpdateTotalPage(int totalPage);
    }
}