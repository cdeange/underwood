package com.deange.underwood;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlagView extends View {

    private static final int ROWS = 13;
    private static final int WIDTH = 329;
    private static final int HEIGHT = 208;
    private static final float RATIO = WIDTH / (float) HEIGHT;

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOW = 1;
    private static final int STATE_ANIMATING = 2;

    private final Paint mRed = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint[] mRowPaints = new Paint[]{mRed, mWhite};

    private final ValueAnimator[] mAnimators = new ValueAnimator[ROWS];
    private ValueAnimator mBlueAnimator;
    private final ValueAnimator.AnimatorUpdateListener mInvalidateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(final ValueAnimator anim) {
            invalidate();
        }
    };

    private int mState = STATE_HIDDEN;

    public FlagView(final Context context) {
        this(context, null);
    }

    public FlagView(
            final Context context,
            final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlagView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRed.setColor(getResources().getColor(R.color.hoc_red));
        mWhite.setColor(getResources().getColor(R.color.hoc_white));
        mBlue.setColor(getResources().getColor(R.color.hoc_blue));
    }

    @Override
    protected void onMeasure(
            final int widthMeasureSpec,
            final int heightMeasureSpec) {
        final int minWidth = getSuggestedMinimumWidth();
        final int minHeight = getSuggestedMinimumHeight();
        int width, height;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = Math.max(minWidth, WIDTH);
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = Math.max(minHeight, HEIGHT);
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        if (height * RATIO < width) {
            height = (int) (width / RATIO);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        final float w = canvas.getWidth();
        final float h = canvas.getHeight();
        final float rowHeight = h / (float) ROWS;

        for (int row = 0; row < ROWS; ++row) {
            final float totalWidth = (row <= 5) ? w : w * 0.55f;
            final float top = rowHeight * row;
            final float bottom = rowHeight * (row + 1);

            float width = getInterpolatedWidth(mAnimators[row], totalWidth);

            canvas.drawRect(0, top, width, bottom, mRowPaints[row % mRowPaints.length]);
        }

        final float totalBlueW = w * 0.45f;
        final float totalBlueH = rowHeight * 7f;
        final float blueW = getInterpolatedWidth(mBlueAnimator, totalBlueW);

        canvas.drawRect(w - totalBlueW, h - totalBlueH, w - totalBlueW + blueW, h, mBlue);
    }

    private float getInterpolatedWidth(final ValueAnimator animator, final float totalWidth) {
        if (mState == STATE_HIDDEN) {
            return 0;
        } else if (mState == STATE_SHOW) {
            return totalWidth;
        } else {
            return animator.getAnimatedFraction() * totalWidth;
        }
    }

    public void show() {
        cancel(mAnimators);
        cancel(mBlueAnimator);
        mState = STATE_SHOW;
        invalidate();
    }

    public void hide() {
        cancel(mAnimators);
        cancel(mBlueAnimator);
        mState = STATE_HIDDEN;
        invalidate();
    }

    private static void cancel(final Animator... animators) {
        for (final Animator animator : animators) {
            if (animator != null) {
                animator.cancel();
            }
        }
    }

    public void animateIn() {
        cancel(mAnimators);
        cancel(mBlueAnimator);
        mState = STATE_ANIMATING;

        final long rowDuration = 500L;
        final Interpolator interpolator = new DecelerateInterpolator(2);

        for (int i = 0; i < ROWS; ++i) {
            final ValueAnimator animator = ValueAnimator.ofFloat(0, getMeasuredWidth());
            mAnimators[i] = animator;

            final long originalStartDelay = (i > 0)
                    ? mAnimators[i - 1].getStartDelay()
                    : 0;

            final long startDelay = (long) (originalStartDelay + rowDuration / 6f);

            animator.setStartDelay(startDelay);
            animator.setDuration(rowDuration);
            animator.setInterpolator(interpolator);
            animator.addUpdateListener(mInvalidateListener);
        }

        final ValueAnimator last = mAnimators[mAnimators.length - 1];

        mBlueAnimator = ValueAnimator.ofFloat(0, 1);
        mBlueAnimator.setStartDelay((long) (last.getStartDelay() + (last.getDuration() * 0.55f)));
        mBlueAnimator.setDuration(rowDuration);
        mBlueAnimator.setInterpolator(interpolator);
        mBlueAnimator.addUpdateListener(mInvalidateListener);

        final List<Animator> animators = new ArrayList<>();
        animators.addAll(Arrays.asList(mAnimators));
        animators.add(mBlueAnimator);

        final AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.start();
    }
}
