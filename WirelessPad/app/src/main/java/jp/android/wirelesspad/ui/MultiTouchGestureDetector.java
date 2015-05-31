package jp.android.wirelesspad.ui;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

class MultiTouchGestureDetector {
    interface OnGestureListener {
        boolean onSingleTapUp(MotionEvent e);
        boolean onMultiTapUp(MotionEvent e);
        boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
    }

    private int mTouchSlopSquare;

    private final OnGestureListener mListener;

    private boolean mAlwaysInTapRegion;
    private boolean mMultiTapped;

    private MotionEvent mCurrentDownEvent;

    private float mLastFocusX;
    private float mLastFocusY;
    private float mDownFocusX;
    private float mDownFocusY;

    public MultiTouchGestureDetector(Context context, OnGestureListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }
        mListener = listener;
        init(context);
    }

    private void init(Context context) {
        int touchSlop;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
        } else {
            ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
        }
        mTouchSlopSquare = touchSlop * touchSlop;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        boolean pointerUp = (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
        int skipIndex = pointerUp ? ev.getActionIndex() : -1;

        float sumX = 0, sumY = 0;
        int count = ev.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += ev.getX(i);
            sumY += ev.getY(i);
        }
        int div = pointerUp ? count - 1 : count;
        float focusX = sumX / div;
        float focusY = sumY / div;

        boolean handled = false;

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                if (mCurrentDownEvent != null) {
                    mCurrentDownEvent.recycle();
                }
                mCurrentDownEvent = MotionEvent.obtain(ev);
                mAlwaysInTapRegion = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                if (mAlwaysInTapRegion) {
                    handled = mListener.onMultiTapUp(ev);
                    mMultiTapped = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mAlwaysInTapRegion && !mMultiTapped) {
                    handled = mListener.onSingleTapUp(ev);
                }
                mMultiTapped = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float scrollX = mLastFocusX - focusX;
                float scrollY = mLastFocusY - focusY;
                if (mAlwaysInTapRegion) {
                    int deltaX = (int) (focusX - mDownFocusX);
                    int deltaY = (int) (focusY - mDownFocusY);
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (distance > mTouchSlopSquare) {
                        handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX, scrollY);
                        mLastFocusX = focusX;
                        mLastFocusY = focusY;
                        mAlwaysInTapRegion = false;
                    }
                } else if ((Math.abs(scrollX) >= 1) || (Math.abs(scrollY) >= 1)) {
                    handled = mListener.onScroll(mCurrentDownEvent, ev, scrollX, scrollY);
                    mLastFocusX = focusX;
                    mLastFocusY = focusY;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mAlwaysInTapRegion = false;
                mMultiTapped = false;
                break;
        }
        return handled;
    }
}
