package com.okamoba.okaevent_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        // タッチした箇所のViewを取得
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            // onInterceptTouchEventのタイミングだとアイテムのtouch feedbackがつく前にonItemClickが呼ばれてしまうので、明示的にsetPressed(true)を呼んでいます。
            childView.setPressed(true);
            mListener.onItemClick(childView, view.getChildPosition(childView));
        }
        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        // Do nothing
    }

    /**
     * Called when a child of RecyclerView does not want RecyclerView and its ancestors to
     * intercept touch events with
     * {@link ViewGroup#onInterceptTouchEvent(MotionEvent)}.
     *
     * @param disallowIntercept True if the child does not want the parent to
     *                          intercept touch events.
     * @see ViewParent#requestDisallowInterceptTouchEvent(boolean)
     */
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}
