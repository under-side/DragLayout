package org.under_side.viewchangelayout.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

public class DragLayout extends FrameLayout {

	//该自定义ViewGroup的子view的引用
	private ViewGroup mBackView;
	private ViewGroup mFrontView;
	private ViewDragHelper myDragHelper;

	//获取子view的宽高变量
	private int mWidth;
	private int mHeight;
	private int mRange;

	//标志当前的item的状态
	private ItemState mCurrentState=ItemState.Close;
	
	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	//定义标志状态的枚举类
	enum ItemState{
		Open,Close,Draging;
	}
	
	//定义回调接口
	public interface onItemStateChangedListener{
		/**
		 * 当layout为打开状态时，调用该方法
		 */
		public void onOpen();
		/**
		 * 当layout为关闭状态时，调用该方法
		 */
		public void onClose();
		/**
		 * 当layout为拖拽状态时，调用该方法
		 * @param fraction 该变量表示拖拽的范围的百分比为0.0-1.0
		 */
		public void onDraging(float fraction);
	}
	
	private onItemStateChangedListener listener;
	
	public void setOnItemStateChangedListener(onItemStateChangedListener listener)
	{
		this.listener=listener;
	}
	//获取当前的item状态
	public ItemState getCurrentItemState() {
		return mCurrentState;
	}
	private void init() {
		// a.获取ViewDragHelper的全局实体变量
		myDragHelper = ViewDragHelper.create(this, 1.0f, myCallback);
	}

	// b.将该ViewGroup的touch事件交给ViewDragHelper处理
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return myDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			myDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	// c.实现ViewDragHelper中的Callback接口，实现对手势操作的处理
	ViewDragHelper.Callback myCallback = new ViewDragHelper.Callback() {
		/*
		 * 关于ViewDragHelper的使用方法，可以参看上一个侧拉删除项目中的备注
		 * 
		 * @see
		 * android.support.v4.widget.ViewDragHelper.Callback#tryCaptureView(
		 * android.view.View, int)
		 */

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			return true;
		}
		
        /*
         * 返回一个可拖动的子视图的水平像素运动范围的大小。此方法应返回0表示水平不能移动的意见。
         * 另外，该方法默认的是返回的是0，即默认不能移动。则，如果想移动哪个方向，需要重写该方法。
         */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// 限制mFrontView移动范围
			if (child == mFrontView) {
				left = fixLeft(left);
			}
			return left;
		}

		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			// 当移动的是mBackView时，将其当移动的变量传递给mFrontView
			int newLeft = left;
			if (changedView == mBackView) {
				newLeft = mFrontView.getLeft() + dx;
			}

			//将newleft进行判断后重新赋值
			newLeft = fixLeft(newLeft);

			/*
			 * 如果移动的是mBackView，则利用layout方法将其前置放回去，不让其移动。
			 * 让DownLayout的触摸事件传递给frontView，整个拖拽过程中只是移动FrontVeiw
			 * 而让mFrontView利用传递的偏移量进行移动
			 */
			if (changedView == mBackView) {
				mBackView.layout(0, 0, mWidth, mHeight);
				
				//利用mBackView传递的移动变量值，通过layout方法去移动mFrontView
				mFrontView.layout(newLeft, 0, newLeft + mWidth, mHeight);
			}

			//在view位置改变时，进行状态的判断去调用接口方法
			dispatchDragEvent(newLeft);

			// 申请重新绘制界面，使设置的变量执行
			invalidate();

		}

		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (xvel == 0 && mFrontView.getLeft() > mRange / 2.0f) {
				// open
				openOperetion();
//				Utils.showToast(getContext(), "open");
			} else if (xvel > 10) {
				// open
				openOperetion();
//				Utils.showToast(getContext(), "open");
			} else {
				// close
				closeOperetion();
//				Utils.showToast(getContext(), "close");
			}
		}
	};

	// 对移动过程中view的left值进行动态的检测、修正
	private int fixLeft(int left) {
		if (left < 0) {
			left = 0;
		} else if (left > mRange) {
			left = mRange;
		}
		return left;
	}
	
	/*
	 * 重载打开操作，默认利用smoothSlideViewTo执行平滑移动操作；
	 * 否则，利用layout方法前行瞬间移动view
	 */
	protected void closeOperetion()
	{
		closeOperetion(true);
	}
	//执行打开菜单操作
	protected void closeOperetion(boolean isSmooth) {
		if(isSmooth)
		{
			myDragHelper.smoothSlideViewTo(mFrontView, 0, 0);
		}else{
			mFrontView.layout(0, 0, mWidth, mHeight);
		}
		invalidate();
	}
	
	/*
	 * 重载打开操作，默认利用smoothSlideViewTo执行平滑移动操作；
	 * 否则，利用layout方法前行瞬间移动view
	 */
	protected void openOperetion()
	{
		openOperetion(true);
	}
	//执行关闭菜单操作
	protected void openOperetion(boolean isSmooth) {
		if(isSmooth){
			myDragHelper.smoothSlideViewTo(mFrontView, mRange, 0);
		}else{
			mFrontView.layout(mRange, 0, mRange+mWidth, mHeight);
		}
		invalidate();
	}

	// 该方法在onDraw方法调用后，将会调用该方法，可以在这里执行动画的持续执行
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (myDragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	// 当view发生改变了就会调用该方法，根据当前的状态，回调接口中的方法
	protected void dispatchDragEvent(int newLeft) {

		
		// 运用mFrontview的左坐标与mRange的比值作为渐变的一个标志变量
		float fraction = newLeft * 1.0f / mRange;

		if(listener!=null)
		{
			listener.onDraging(fraction);
		}
		
		ItemState preState=mCurrentState;
		
		mCurrentState=updateState(newLeft);
		//当状态改变时，才回去调用接口方法
		if(preState!=mCurrentState&&listener!=null)
		{
			if(mCurrentState==ItemState.Open)
			{
				listener.onOpen();
			}else if(mCurrentState==ItemState.Close)
			{
				listener.onClose();
			}
		}
		
		dragWithAnimation(fraction);
	}

	//根据mFrontView坐标的坐标与mRange的大小比较来更新当前状态
	private ItemState updateState(int newLeft) {
		if(newLeft==0)
		{
			return ItemState.Close;
		}else if(newLeft==mRange)
		{
			return ItemState.Open;
		}
		return ItemState.Draging;
	}

	// 在拖拽的过程中去执行伴随动画的操作
	private void dragWithAnimation(float fraction) {
		/*
		 * mBackView.setScaleX(0.5f+0.5f*fraction);
		 * mBackView.setScaleY(0.5f+0.5f*fraction);
		 * 该种方法只支持API11，则为了兼容低版本，可是使用nineoldandroid.jar架包
		 */
		// mBackView的缩放动画
		ViewHelper.setScaleX(mBackView, 0.5f + 0.5f * fraction);
		ViewHelper.setScaleY(mBackView, 0.5f + 0.5f * fraction);

		// mBackView的平移动画
		ViewHelper.setTranslationX(mBackView,
				evaluate(fraction, -mWidth / 2.0f, 0));

		// mBackView的背景透明度变化
		ViewHelper.setAlpha(mBackView, evaluate(fraction, 0.5f, 1.0f));

		// mFrontView的缩放动画
		ViewHelper.setScaleX(mFrontView, 1.0f - 0.2f * fraction);
		ViewHelper.setScaleY(mFrontView, 1.0f - 0.2f * fraction);

		// 背景动画: 亮度变化 (颜色变化)
		getBackground().setColorFilter(
				(Integer) evaluateColor(fraction, Color.BLACK,
						Color.TRANSPARENT), Mode.SRC_OVER);
	}

	// float估值器
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	// 颜色估值器
	public Object evaluateColor(float fraction, Object startValue,
			Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	/*
	 * 获取自定ViewGroup中子view的引用
	 * 
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackView = (ViewGroup) getChildAt(0);
		mFrontView = (ViewGroup) getChildAt(1);
	}

	// 获取子view的宽和高度，进行动态移动的判断
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = mFrontView.getMeasuredWidth();
		mHeight = mFrontView.getMeasuredHeight();
		mRange = (int) (mWidth * 0.6);
	}

}
