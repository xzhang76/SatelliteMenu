package com.zhangxt4.satellitemenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.zhangxt4.satellitemenu.R;

/**
 * Created by zhangxt4 on 2015/12/9.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {
    //菜单位置的枚举类
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    private Position mPosition = Position.RIGHT_BOTTOM; //默认是右下方
    private int mRadius; //子菜单的半径

    //主菜单的闭合
    public enum Status {
        OPEN, CLOSE
    }

    private Status mCurrentStatus = Status.CLOSE; //默认是关闭
    private View mCButton; //主按钮

    //点击子菜单项的接口回调
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    private OnMenuItemClickListener mMenuItemClickListener;

    //设置回调的接口
    public void setOnMenuItemClickListener(OnMenuItemClickListener mListener) {
        this.mMenuItemClickListener = mListener;
    }

    //将这几个位置拿出来作为常量
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()); //默认值150dp
        //获取自定义属性的值
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);
        int position = ta.getInt(R.styleable.ArcMenu_position, POS_RIGHT_BOTTOM); //获取在layout布局中指定的位置属性，默认是右下
        switch (position) {
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
        }
        mRadius = (int) ta.getDimension(R.styleable.ArcMenu_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                100, getResources().getDisplayMetrics())); //获取在layout布局中指定的半径属性值，默认是100dp
        Log.e("SatelliteMenu", "mPosition = " + mPosition + ", mRadius = " + mRadius);
        ta.recycle();
    }

    //完成ArcMenu这个ViewGroup中所有子View的测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        //逐个测量每个child
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //定位ViewGroup中的每个子View
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButton(); //定位主按钮的位置
            int count = getChildCount();
            //逐个定位每个子菜单View的位置
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1); //因为主按钮是0，所以子菜单从1开始到count-1
                child.setVisibility(View.GONE); //开始的时候先隐藏子菜单
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i)); //注意第一个子菜单的sin角度为0, 主按钮是0，所以是count-2
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                //每个子菜单View本身的宽和高
                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();
                //如果菜单位于底部（左下，右下）,相对于顶部的参数ct就需要调整
                if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                }
                //如果菜单位于右部（右下，右上）,相对于左边的参数cl就需要调整
                if (mPosition == Position.RIGHT_BOTTOM || mPosition == Position.RIGHT_TOP) {
                    cl = getMeasuredWidth() - cWidth - cl;
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }
        }
    }

    private void layoutCButton() {
        mCButton = getChildAt(0); //第一个子View就是主按钮
        mCButton.setOnClickListener(this);
        /* 定位一个view需要使用ViewGroup的layout(int l, int t, int r, int b)方法
         * 四个参数分别为相对父容器的left，top，right和bottom
         * 下面是根据主按钮的位置来确定这四个参数值
         */
        int l = 0;
        int t = 0;
        int width = mCButton.getMeasuredWidth(); //获取主按钮的宽
        int height = mCButton.getMeasuredHeight(); //获取主按钮的高
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height; //在下边时，t=ViewGroup的高-主按钮的高
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width; //在右边时，l=ViewGroup的宽-主按钮的宽
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }
        mCButton.layout(l, t, l + width, t + height); //传入四个参数来定位主按钮
    }

    /* 主按钮的点击事件
     * 需要完成1.点击button时的旋转动画
     * 2.点击button时展开/关闭子菜单
     */
    @Override
    public void onClick(View v) {
        rotateCButton(v, 0f, 360f, 300); //将主按钮旋转动画专门放在一个函数中处理
        toggleMenu(300); //处理子菜单展开和关闭的函数

    }

    /**
     * 处理点击主按钮时的展开/关闭子菜单的操作
     * duration为经历的时间
     * 设置为public是想让外面也可以调用该函数，来设置经历的时间duration
     */
    public void toggleMenu(int duration) {
        int count = getChildCount();
        //1.为每个子菜单项设置平移动画和旋转动画
        for (int i = 0; i < count-1; i++){
            final View childView = getChildAt(i+1);
            childView.setVisibility(View.VISIBLE); //起始应该是显示的
            //创建一个AnimationSet来包含这两个动画
            AnimationSet animSet = new AnimationSet(true);

            //(1)平移动画
            /* 为子菜单项平移动画设置开始和结束位置
             * 1.end位置是为（0,0）,这是它应该在的位置
             * 2.start位置会根据ViewGroup位置来设置（在主按钮处）
             */
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
            int xflag = 1;
            int yflag = 1;
            if (mPosition == Position.LEFT_BOTTOM || mPosition == Position.LEFT_TOP)
                xflag = -1; //当ViewGroup在左边时，start位置相对于end是左移的，即（-cl）
            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP)
                yflag = -1; //当ViewGroup在顶部时，start位置相对于end是上移的，即（-ct）
            Animation tranAnim = null; //平移动画
            if (mCurrentStatus == Status.CLOSE){
                //to open
                tranAnim = new TranslateAnimation(cl*xflag, 0, ct*yflag, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            }else {
                //to close
                tranAnim = new TranslateAnimation(0, cl*xflag, 0, ct*yflag);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            //设置动画的两个属性
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            //设置一个移出的先后顺序，效果像是依次展开每个子菜单
            tranAnim.setStartOffset((i*100)/count);
            /*
             为动画设置一个监听，用来设置动画结束后，子菜单View的显示/消失
             */
            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE){
                        //动画结束时，如果当前是关闭状态，则要把子菜单消失
                        childView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //（2）旋转动画
            RotateAnimation rotateAnim = new RotateAnimation(0, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);

            //（3）为子菜单view设置这两个动画
            animSet.addAnimation(rotateAnim);
            animSet.addAnimation(tranAnim);
            childView.startAnimation(animSet);

            //（4）为每个子菜单设置监听点击事件
            final int pos = i+1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMenuItemClickListener != null) {
                        //设置点击事件
                        mMenuItemClickListener.onClick(childView, pos);
                    }
                    //设置点击后的动画，即被点击者放大消失，其他菜单缩小消失
                    menuItemAnim(pos);
                    //更新菜单闭合/展开状态，如果当前是open的，点击后应该是close
                    changeStatus();
                }
            });
        }
        //2.最后要更新一下状态
        changeStatus();
    }

    /* 点击某个子菜单后的动画效果
     * 遍历所有的子菜单View，如果是被点击的菜单item，就放大消失，其他缩小消失
     * 最后点击后的动画效果完毕后，所有的子View都不能点击和聚焦了
     */
    private void menuItemAnim(int pos) {
        for(int i = 1; i<getChildCount(); i++) {
            View childView = getChildAt(i);
            if (i == pos) {
                childView.startAnimation(scaleBigAnim(300));
            } else {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    //其他菜单项会出现缩小消失两个动画效果
    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        //以自己为中心缩小到0
        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //透明度设置为从有到无
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0f);

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }
    //被点击菜单项会出现放大消失两个动画效果
    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        //以自己为中心放大4倍
        ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //透明度设置为从有到无
        AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0f);

        animationSet.addAnimation(scaleAnim);
        animationSet.addAnimation(alphaAnim);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    //更新状态
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE?Status.OPEN:Status.CLOSE);
    }

    //判断当前菜单是否是打开的
    public boolean isOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    /**
     * 主按钮的选择动画
     * @param v        主按钮View
     * @param start    旋转开始角度
     * @param end      旋转结束角度
     * @param duration 旋转时间
     */
    private void rotateCButton(View v, float start, float end, int duration) {
        //1. 创建一个旋转动画,指定起始和结束角度,以及旋转中心点
        //旋转中心是以自己为中心,0.5f表示自己View的一半
        RotateAnimation anim = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //2. 设置旋转时间，并指定在旋转后就固定住不动了
        anim.setDuration(duration);
        anim.setFillAfter(true);
        //3. 为主按钮view设置这个动画
        v.startAnimation(anim);
    }


}