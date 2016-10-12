package org.under_side.viewchangelayout.ui;

import org.under_side.viewchangelayout.ui.DragLayout.ItemState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
/*
 * 重写ListView，判断手势操作，使其在打开状态下，item不能滑动
 */
public class MyLinearLayout extends LinearLayout {

	private DragLayout mDragLayout;

	public MyLinearLayout(Context context) {
		super(context);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setDraglayout(DragLayout mDragLayout){
		this.mDragLayout = mDragLayout;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 如果当前是关闭状态, 则将触摸事件交给ListView处理。即，可以上下滚动
		if(mDragLayout.getCurrentItemState() == ItemState.Close){
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 如果当前是关闭状态, 则将触摸事件交给ListView处理。即，可以上下滚动
		if(mDragLayout.getCurrentItemState() == ItemState.Close){
			return super.onTouchEvent(event);
		}else {
			// 手指抬起, 执行关闭操作
			if(event.getAction() == MotionEvent.ACTION_UP){
				mDragLayout.closeOperetion();
			}
			
			return true;
		}
	}

}
