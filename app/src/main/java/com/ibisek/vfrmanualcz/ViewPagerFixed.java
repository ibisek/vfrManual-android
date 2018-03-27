package com.ibisek.vfrmanualcz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ibisek on 27.3.18.
 *
 * @see https://github.com/chrisbanes/PhotoView/issues/31
 */

public class ViewPagerFixed extends android.support.v4.view.ViewPager {

    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("## The exception above IS fixed by ViewPagerFixed ##");
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            System.out.println("## The exception above IS fixed by ViewPagerFixed ##");
        }
        return false;
    }
}