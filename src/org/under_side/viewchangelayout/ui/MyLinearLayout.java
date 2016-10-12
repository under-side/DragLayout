package org.under_side.viewchangelayout.ui;

import org.under_side.viewchangelayout.ui.DragLayout.ItemState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
/*
 * ��дListView���ж����Ʋ�����ʹ���ڴ�״̬�£�item���ܻ���
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
		// �����ǰ�ǹر�״̬, �򽫴����¼�����ListView���������������¹���
		if(mDragLayout.getCurrentItemState() == ItemState.Close){
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// �����ǰ�ǹر�״̬, �򽫴����¼�����ListView���������������¹���
		if(mDragLayout.getCurrentItemState() == ItemState.Close){
			return super.onTouchEvent(event);
		}else {
			// ��ָ̧��, ִ�йرղ���
			if(event.getAction() == MotionEvent.ACTION_UP){
				mDragLayout.closeOperetion();
			}
			
			return true;
		}
	}

}
