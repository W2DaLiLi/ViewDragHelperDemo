package com.example.hzxr.myapplication;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import static android.support.v4.widget.ViewDragHelper.EDGE_BOTTOM;

/**
 * Created by hzxr on 2017/7/22.
 */

public class MyVDHLayout extends LinearLayout {

    private static final int MIN_TOP = 50;//设置上移距离
    private static final int MIN_VELOCITY = 300;//设置最低移动速度
    private static final String t = "TAG";
    private static float density;//像素密度，用于转换px，dp
    private ViewDragHelper  mViewDragHelper;
    private View mMainLayout;//主视图
    private View mMenu;//菜单视图
    private float mMeunOnScreen;//滑动中侧栏所占比例
    private ObjectAnimator animator;

    public MyVDHLayout(Context context){
        this(context,null);
    }

    public MyVDHLayout(Context context,AttributeSet attr){
        this(context,attr,0);
    }


    public MyVDHLayout(Context context, AttributeSet attr,int defStyle){
        super(context,attr,defStyle);
        mViewDragHelper = ViewDragHelper.create(this,1.0f,new ViewCallBack());//工厂方式创建实例，设置父类View，回调接口
        mViewDragHelper.setEdgeTrackingEnabled(EDGE_BOTTOM);//设置边缘使能，允许下边缘接受滑动
        mViewDragHelper.setMinVelocity(MIN_VELOCITY*density);
        density = getResources().getDisplayMetrics().density;//获取像素密度
        Log.d(t,"constructor is ok");
    }

    private class ViewCallBack extends ViewDragHelper.Callback{//回调接口实现

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.d(t,"tryCaptureView");
            return child == mMenu;//允许滑动的子View

        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {//在边缘拖动时回调
            Log.d(t,"onEdgeDragStarted");
            mViewDragHelper.captureChildView(mMenu,pointerId);//主动捕获子View
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {//计算移动的距离和限制到达终点时不改变
            Log.d(t,"clampViewPositionVertical");
            int newTop = Math.min(mMainLayout.getHeight(),Math.max(mMainLayout.getHeight()-child.getHeight(),child.getTop()));
            return newTop;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(t,"onViewPositionChanged"+"left:"+left+"top:"+top);
            int childHeight = changedView.getHeight();
            float offset = (float)(mMainLayout.getHeight()-top)*1.0f/childHeight;
            Log.d(t,"offset:"+offset);
            changedView.setVisibility(offset == 0 ? View.INVISIBLE:View.VISIBLE);
            mMeunOnScreen = offset;
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {//手指释放时回调
            Log.d(t,"onViewReleased,yvel:"+yvel);
            if (yvel < 0 ||yvel == 0 && releasedChild.getTop() < mMainLayout.getHeight()-releasedChild.getHeight()*0.5){
                mViewDragHelper.settleCapturedViewAt(0,mMainLayout.getHeight()-releasedChild.getHeight());
                Log.d("SHOW","LH:"+mMainLayout.getHeight()+"MH"+releasedChild.getHeight());
                animator.start();
            }else {
                Log.d("Released","NO");
                mViewDragHelper.settleCapturedViewAt(0,mMainLayout.getHeight());
                animator = ObjectAnimator.ofFloat(mMainLayout,"alpha",0.5f,1f);
                animator.start();
            }
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)){
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if(count >= 2){
            mMenu = getChildAt(1);
            mMainLayout = getChildAt(0);
        }
        animator = ObjectAnimator.ofFloat(mMainLayout,"alpha",1f,0.5f);
        animator.setDuration(500);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize,heightSize);

        View mMenu = getChildAt(1);
        MarginLayoutParams lp = (MarginLayoutParams) mMenu.getLayoutParams();

        int minheight = (int)(density*MIN_TOP+0.5f);

        final int drawerWidthSpec = getChildMeasureSpec(widthMeasureSpec,lp.leftMargin+lp.rightMargin,lp.width);
        final int drawerHeightSpec = getChildMeasureSpec(heightMeasureSpec,minheight+lp.topMargin+lp.bottomMargin,lp.height);
        mMenu.measure(drawerWidthSpec,drawerHeightSpec);

        View mainLayout = getChildAt(0);
        lp = (MarginLayoutParams) mainLayout.getLayoutParams();

        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(widthSize-lp.leftMargin-lp.rightMargin,MeasureSpec.EXACTLY);
        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(heightSize-lp.topMargin-lp.bottomMargin,MeasureSpec.EXACTLY);
        mainLayout.measure(contentWidthSpec,contentHeightSpec);
        this.mMenu = mMenu;
        mMainLayout = mainLayout;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View menu = mMenu;
        View content = mMainLayout;

        MarginLayoutParams lp = (MarginLayoutParams) menu.getLayoutParams();
        int childTop = content.getHeight()-(int) (menu.getHeight()*mMeunOnScreen);
        menu.layout(lp.leftMargin,childTop,lp.rightMargin+menu.getMeasuredWidth(),childTop+menu.getMeasuredHeight());

        lp = (MarginLayoutParams) content.getLayoutParams();
        content.layout(lp.leftMargin,lp.topMargin,lp.leftMargin+content.getMeasuredWidth(),lp.topMargin+content.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean event = mViewDragHelper.shouldInterceptTouchEvent(ev);
        return event;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
