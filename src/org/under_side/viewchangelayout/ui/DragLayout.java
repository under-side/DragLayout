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

	//���Զ���ViewGroup����view������
	private ViewGroup mBackView;
	private ViewGroup mFrontView;
	private ViewDragHelper myDragHelper;

	//��ȡ��view�Ŀ�߱���
	private int mWidth;
	private int mHeight;
	private int mRange;

	//��־��ǰ��item��״̬
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

	//�����־״̬��ö����
	enum ItemState{
		Open,Close,Draging;
	}
	
	//����ص��ӿ�
	public interface onItemStateChangedListener{
		/**
		 * ��layoutΪ��״̬ʱ�����ø÷���
		 */
		public void onOpen();
		/**
		 * ��layoutΪ�ر�״̬ʱ�����ø÷���
		 */
		public void onClose();
		/**
		 * ��layoutΪ��ק״̬ʱ�����ø÷���
		 * @param fraction �ñ�����ʾ��ק�ķ�Χ�İٷֱ�Ϊ0.0-1.0
		 */
		public void onDraging(float fraction);
	}
	
	private onItemStateChangedListener listener;
	
	public void setOnItemStateChangedListener(onItemStateChangedListener listener)
	{
		this.listener=listener;
	}
	//��ȡ��ǰ��item״̬
	public ItemState getCurrentItemState() {
		return mCurrentState;
	}
	private void init() {
		// a.��ȡViewDragHelper��ȫ��ʵ�����
		myDragHelper = ViewDragHelper.create(this, 1.0f, myCallback);
	}

	// b.����ViewGroup��touch�¼�����ViewDragHelper����
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

	// c.ʵ��ViewDragHelper�е�Callback�ӿڣ�ʵ�ֶ����Ʋ����Ĵ���
	ViewDragHelper.Callback myCallback = new ViewDragHelper.Callback() {
		/*
		 * ����ViewDragHelper��ʹ�÷��������Բο���һ������ɾ����Ŀ�еı�ע
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
         * ����һ�����϶�������ͼ��ˮƽ�����˶���Χ�Ĵ�С���˷���Ӧ����0��ʾˮƽ�����ƶ��������
         * ���⣬�÷���Ĭ�ϵ��Ƿ��ص���0����Ĭ�ϲ����ƶ�����������ƶ��ĸ�������Ҫ��д�÷�����
         */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// ����mFrontView�ƶ���Χ
			if (child == mFrontView) {
				left = fixLeft(left);
			}
			return left;
		}

		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			// ���ƶ�����mBackViewʱ�����䵱�ƶ��ı������ݸ�mFrontView
			int newLeft = left;
			if (changedView == mBackView) {
				newLeft = mFrontView.getLeft() + dx;
			}

			//��newleft�����жϺ����¸�ֵ
			newLeft = fixLeft(newLeft);

			/*
			 * ����ƶ�����mBackView��������layout��������ǰ�÷Ż�ȥ���������ƶ���
			 * ��DownLayout�Ĵ����¼����ݸ�frontView��������ק������ֻ���ƶ�FrontVeiw
			 * ����mFrontView���ô��ݵ�ƫ���������ƶ�
			 */
			if (changedView == mBackView) {
				mBackView.layout(0, 0, mWidth, mHeight);
				
				//����mBackView���ݵ��ƶ�����ֵ��ͨ��layout����ȥ�ƶ�mFrontView
				mFrontView.layout(newLeft, 0, newLeft + mWidth, mHeight);
			}

			//��viewλ�øı�ʱ������״̬���ж�ȥ���ýӿڷ���
			dispatchDragEvent(newLeft);

			// �������»��ƽ��棬ʹ���õı���ִ��
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

	// ���ƶ�������view��leftֵ���ж�̬�ļ�⡢����
	private int fixLeft(int left) {
		if (left < 0) {
			left = 0;
		} else if (left > mRange) {
			left = mRange;
		}
		return left;
	}
	
	/*
	 * ���ش򿪲�����Ĭ������smoothSlideViewToִ��ƽ���ƶ�������
	 * ��������layout����ǰ��˲���ƶ�view
	 */
	protected void closeOperetion()
	{
		closeOperetion(true);
	}
	//ִ�д򿪲˵�����
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
	 * ���ش򿪲�����Ĭ������smoothSlideViewToִ��ƽ���ƶ�������
	 * ��������layout����ǰ��˲���ƶ�view
	 */
	protected void openOperetion()
	{
		openOperetion(true);
	}
	//ִ�йرղ˵�����
	protected void openOperetion(boolean isSmooth) {
		if(isSmooth){
			myDragHelper.smoothSlideViewTo(mFrontView, mRange, 0);
		}else{
			mFrontView.layout(mRange, 0, mRange+mWidth, mHeight);
		}
		invalidate();
	}

	// �÷�����onDraw�������ú󣬽�����ø÷���������������ִ�ж����ĳ���ִ��
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (myDragHelper.continueSettling(true)) {
			invalidate();
		}
	}

	// ��view�����ı��˾ͻ���ø÷��������ݵ�ǰ��״̬���ص��ӿ��еķ���
	protected void dispatchDragEvent(int newLeft) {

		
		// ����mFrontview����������mRange�ı�ֵ��Ϊ�����һ����־����
		float fraction = newLeft * 1.0f / mRange;

		if(listener!=null)
		{
			listener.onDraging(fraction);
		}
		
		ItemState preState=mCurrentState;
		
		mCurrentState=updateState(newLeft);
		//��״̬�ı�ʱ���Ż�ȥ���ýӿڷ���
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

	//����mFrontView�����������mRange�Ĵ�С�Ƚ������µ�ǰ״̬
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

	// ����ק�Ĺ�����ȥִ�а��涯���Ĳ���
	private void dragWithAnimation(float fraction) {
		/*
		 * mBackView.setScaleX(0.5f+0.5f*fraction);
		 * mBackView.setScaleY(0.5f+0.5f*fraction);
		 * ���ַ���ֻ֧��API11����Ϊ�˼��ݵͰ汾������ʹ��nineoldandroid.jar�ܰ�
		 */
		// mBackView�����Ŷ���
		ViewHelper.setScaleX(mBackView, 0.5f + 0.5f * fraction);
		ViewHelper.setScaleY(mBackView, 0.5f + 0.5f * fraction);

		// mBackView��ƽ�ƶ���
		ViewHelper.setTranslationX(mBackView,
				evaluate(fraction, -mWidth / 2.0f, 0));

		// mBackView�ı���͸���ȱ仯
		ViewHelper.setAlpha(mBackView, evaluate(fraction, 0.5f, 1.0f));

		// mFrontView�����Ŷ���
		ViewHelper.setScaleX(mFrontView, 1.0f - 0.2f * fraction);
		ViewHelper.setScaleY(mFrontView, 1.0f - 0.2f * fraction);

		// ��������: ���ȱ仯 (��ɫ�仯)
		getBackground().setColorFilter(
				(Integer) evaluateColor(fraction, Color.BLACK,
						Color.TRANSPARENT), Mode.SRC_OVER);
	}

	// float��ֵ��
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	// ��ɫ��ֵ��
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
	 * ��ȡ�Զ�ViewGroup����view������
	 * 
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mBackView = (ViewGroup) getChildAt(0);
		mFrontView = (ViewGroup) getChildAt(1);
	}

	// ��ȡ��view�Ŀ�͸߶ȣ����ж�̬�ƶ����ж�
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = mFrontView.getMeasuredWidth();
		mHeight = mFrontView.getMeasuredHeight();
		mRange = (int) (mWidth * 0.6);
	}

}
