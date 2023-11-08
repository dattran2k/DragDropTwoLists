/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dat.drag_drop_fragment.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.core.graphics.ColorUtils;

/**
 * {@link PageIndicator} which shows dots per page. The active page is shown with the current
 * accent color.
 */
public class PageIndicatorDots extends View implements PageIndicator {

    private static final String TAG = PageIndicatorDots.class.getSimpleName();

    private static final float SHIFT_PER_ANIMATION = 0.5f;
    private static final float SHIFT_THRESHOLD = 0.1f;
    private static final long ANIMATION_DURATION = 150;

    private static final int ENTER_ANIMATION_START_DELAY = 300;
    private static final int ENTER_ANIMATION_STAGGERED_DELAY = 150;
    private static final int ENTER_ANIMATION_DURATION = 400;

    /**
     * Maximum number of page to display to prevent overlap screen
     */
    private static final int MAX_PAGE = 20;

    // This value approximately overshoots to 1.5 times the original size.
    private static final float ENTER_ANIMATION_OVERSHOOT_TENSION = 4.9f;

    private static final RectF sTempRect = new RectF();

    private static final Property<PageIndicatorDots, Float> CURRENT_POSITION
            = new Property<PageIndicatorDots, Float>(float.class, "current_position") {
        @Override
        public Float get(PageIndicatorDots obj) {
            return obj.mCurrentPosition;
        }

        @Override
        public void set(PageIndicatorDots obj, Float pos) {
            obj.mCurrentPosition = pos;
            obj.invalidate();
            obj.invalidateOutline();
        }
    };

    private final Paint mCirclePaint;
    protected float mDotRadius;
    protected int mActiveColor;
    protected int mInActiveColor;

    protected int mNumPages;
    private int mActivePage;
    /**
     * The padding for indicator because view's padding doesn't work for drawing
     */
    protected int mPaddingStartX = 0;

    /**
     * The current position of the active dot including the animation progress.
     * For ex:
     * 0.0  => Active dot is at position 0
     * 0.33 => Active dot is at position 0 and is moving towards 1
     * 0.50 => Active dot is at position [0, 1]
     * 0.77 => Active dot has left position 0 and is collapsing towards position 1
     * 1.0  => Active dot is at position 1
     */
    private float mCurrentPosition;
    private float mFinalPosition;
    private ObjectAnimator mAnimator;

    private float[] mEntryAnimationRadiusFactors;

    public PageIndicatorDots(Context context) {
        this(context, null);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Style.FILL);

        mDotRadius = 6;
        setOutlineProvider(new MyOutlineProver());

        mActiveColor = Color.WHITE;
        mInActiveColor = ColorUtils.setAlphaComponent(Color.WHITE, (int) (0.5f * 255));

    }

    @Override
    public void setScroll(int currentScroll, int totalScroll) {
        if (mNumPages > 1 && totalScroll != 0) {
            int scrollPerPage = totalScroll / mNumPages;
            int pageToLeft = currentScroll / scrollPerPage;
            int pageToLeftScroll = pageToLeft * scrollPerPage;
            int pageToRightScroll = pageToLeftScroll + scrollPerPage;

            float scrollThreshold = SHIFT_THRESHOLD * scrollPerPage;
            if (currentScroll < pageToLeftScroll + scrollThreshold) {
                // scroll is within the left page's threshold
                animateToPosition(pageToLeft);
            } else if (currentScroll > pageToRightScroll - scrollThreshold) {
                // scroll is far enough from left page to go to the right page
                animateToPosition(pageToLeft + 1);
            } else {
                // scroll is between left and right page
                animateToPosition(pageToLeft + SHIFT_PER_ANIMATION);
            }
        }
    }

    private void animateToPosition(float position) {
        if (position >= getNumPage()) {
            position = (float) (getNumPage() - 1);
        }
        mFinalPosition = position;
        if (Math.abs(mCurrentPosition - mFinalPosition) < SHIFT_THRESHOLD) {
            mCurrentPosition = mFinalPosition;
        }
        if (mAnimator == null && Float.compare(mCurrentPosition, mFinalPosition) != 0) {
            float positionForThisAnim = mCurrentPosition > mFinalPosition ?
                    mCurrentPosition - SHIFT_PER_ANIMATION : mCurrentPosition + SHIFT_PER_ANIMATION;
            if (Math.abs(mFinalPosition - mCurrentPosition) > 1) {
                positionForThisAnim = mFinalPosition;
            }
            if (positionForThisAnim < MAX_PAGE) {
                mAnimator = ObjectAnimator.ofFloat(this, CURRENT_POSITION, positionForThisAnim);
                mAnimator.addListener(new AnimationCycleListener());
                mAnimator.setDuration(ANIMATION_DURATION);
                mAnimator.start();
            }
        }
    }

    public void stopAllAnimations() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mFinalPosition = mActivePage;
        CURRENT_POSITION.set(this, mFinalPosition);
    }

    /**
     * Sets up up the page indicator to play the entry animation.
     * {@link #playEntryAnimation()} must be called after this.
     */
    public void prepareEntryAnimation() {
        mEntryAnimationRadiusFactors = new float[mNumPages];
        invalidate();
    }

    public void playEntryAnimation() {
        int count = mEntryAnimationRadiusFactors.length;
        if (count == 0) {
            mEntryAnimationRadiusFactors = null;
            invalidate();
            return;
        }

        Interpolator interpolator = new OvershootInterpolator(ENTER_ANIMATION_OVERSHOOT_TENSION);
        AnimatorSet animSet = new AnimatorSet();
        for (int i = 0; i < count; i++) {
            ValueAnimator anim = ValueAnimator.ofFloat(0, 1).setDuration(ENTER_ANIMATION_DURATION);
            final int index = i;
            anim.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mEntryAnimationRadiusFactors[index] = (Float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            anim.setInterpolator(interpolator);
            anim.setStartDelay(ENTER_ANIMATION_START_DELAY + ENTER_ANIMATION_STAGGERED_DELAY * i);
            animSet.play(anim);
        }

        animSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mEntryAnimationRadiusFactors = null;
                invalidateOutline();
                invalidate();
            }
        });
        animSet.start();
    }

    @Override
    public void setActiveMarker(int activePage) {
        setActiveMarker(activePage, false);
    }

    public void setActiveMarker(int activePage, boolean fromHomeContext) {
        if (mActivePage != activePage) {
            mActivePage = activePage;
        }
        if (mActivePage > MAX_PAGE) {
            mActivePage = MAX_PAGE;
        }
        if (fromHomeContext) {
            mCurrentPosition = mActivePage;
        }
        invalidate();
    }

    @Override
    public void setMarkersCount(int numMarkers) {
        mNumPages = numMarkers;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Add extra spacing of mDotRadius on all sides so than entry animation could be run.
        int width = MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY ?
                MeasureSpec.getSize(widthMeasureSpec) : (int) ((mNumPages * 4 + 2) * mDotRadius);
        int height = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY ?
                MeasureSpec.getSize(heightMeasureSpec) : (int) (4 * mDotRadius);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw all page indicators;
        int numPage = getNumPage();
        float circleGap = 5 * mDotRadius;
        float startX = (getWidth() - (numPage - 1) * circleGap - mDotRadius * 2) / 2 + mPaddingStartX;

        float x = startX + mDotRadius;
        float y = canvas.getHeight() / 2;

        if (mEntryAnimationRadiusFactors != null) {
            // During entry animation, only draw the circles
            for (int i = 0; i < mEntryAnimationRadiusFactors.length; i++) {
                mCirclePaint.setColor(i == mActivePage ? mActiveColor : mInActiveColor);
                canvas.drawCircle(x, y, mDotRadius * mEntryAnimationRadiusFactors[i], mCirclePaint);
                x += circleGap;
            }
        } else {
            mCirclePaint.setColor(mInActiveColor);
            for (int i = 0; i < numPage; i++) {
                drawDot(canvas, circleGap, x, y, i);
            }

            mCirclePaint.setColor(mActiveColor);
            canvas.drawRoundRect(getActiveRect(), mDotRadius, mDotRadius, mCirclePaint);
        }
    }

    protected int getNumPage() {
        return mNumPages > MAX_PAGE ? MAX_PAGE : mNumPages;
    }

    protected void drawDot(Canvas canvas, float circleGap, float x, float y, int index) {
        canvas.drawCircle(x + circleGap * index, y, mDotRadius, mCirclePaint);
    }

    private RectF getActiveRect() {
        int numPage = getNumPage();
        if (mCurrentPosition >= numPage) {
            mCurrentPosition = Math.max(numPage - 1, 0);
        }
        float startCircle = (int) mCurrentPosition;
        float delta = mCurrentPosition - startCircle;
        float diameter = 2 * mDotRadius;
        float circleGap = 5 * mDotRadius;
        float startX = (getWidth() - (numPage - 1) * circleGap - diameter) / 2 + mPaddingStartX;

        sTempRect.top = getHeight() * 0.5f - mDotRadius;
        sTempRect.bottom = getHeight() * 0.5f + mDotRadius;
        sTempRect.left = startX + startCircle * circleGap;
        sTempRect.right = sTempRect.left + diameter;

        if (mCurrentPosition < MAX_PAGE - 1) {
            if (delta < SHIFT_PER_ANIMATION) {
                // dot is capturing the right circle.
                sTempRect.right += delta * circleGap * 2;
            } else {
                // Dot is leaving the left circle.
                sTempRect.right += circleGap;

                delta -= SHIFT_PER_ANIMATION;
                sTempRect.left += delta * circleGap * 2;
            }
        }
        return sTempRect;
    }

    private class MyOutlineProver extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            if (mEntryAnimationRadiusFactors == null) {
                RectF activeRect = getActiveRect();
                outline.setRoundRect(
                        (int) activeRect.left,
                        (int) activeRect.top,
                        (int) activeRect.right,
                        (int) activeRect.bottom,
                        mDotRadius
                );
            }
        }
    }

    /**
     * Listener for keep running the animation until the final state is reached.
     */
    private class AnimationCycleListener extends AnimatorListenerAdapter {

        private boolean mCancelled = false;

        @Override
        public void onAnimationCancel(Animator animation) {
            mCancelled = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!mCancelled) {
                mAnimator = null;
                animateToPosition(mFinalPosition);
            }
        }
    }
}
