package org.under_side.viewchangelayout;

import java.util.Random;

import org.under_side.viewchangelayout.ui.DragLayout;
import org.under_side.viewchangelayout.ui.DragLayout.onItemStateChangedListener;
import org.under_side.viewchangelayout.ui.MyLinearLayout;
import org.under_side.viewchangelayout.utils.Beans;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity {

	private ListView mBackListView;
	private ListView mFrontListView;
	private DragLayout myDragLayout;
	private ImageView mFrontHeader;
	private MyLinearLayout myLinearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
	}

	//ʵ�ֳ�ʼ������
	private void init() {
		initDragLayout();
		initBackView();
		initFrontView();
	}

	//ʵ��DragLayoutʵ������������д���Զ���ViewGroup�еĽӿڷ���
	private void initDragLayout() {

		myDragLayout = (DragLayout) findViewById(R.id.dl);
		myDragLayout
				.setOnItemStateChangedListener(new onItemStateChangedListener() {

					@Override
					public void onOpen() {
						//�ڴ�itemʱ��ȥ�����ת��mBackView�е�item
						Random random = new Random();
						int next = random.nextInt(30);
						//����listView�ĸ÷��������Ժ�ƽ������ת��ָ����position��item��
						mBackListView.smoothScrollToPosition(next);
					}

					@Override
					public void onDraging(float fraction) {

						//����קʱ��ȥ����mFrontView��͸������ʾЧ��
						ViewHelper.setAlpha(mFrontHeader, 1 - fraction);
					}

					@Override
					public void onClose() {
						// ʹ�õ���nineoldandroid.jar�ܰ��е����Զ���������˼�����
						ObjectAnimator animator = ObjectAnimator.ofFloat(
								mFrontHeader, "translationX", 15.0f);
						animator.setInterpolator(new CycleInterpolator(3.0f));
						animator.setDuration(500);
						animator.start();
					}
				});
	}

	//��ʼ��backView��ʵ�����󣬲�����߼�����
	private void initBackView() {
		mBackListView = (ListView) findViewById(R.id.lv_back);
		ArrayAdapter<String> backAdapter = new ArrayAdapter<String>(
				MainActivity.this,
				android.R.layout.simple_expandable_list_item_1,
				Beans.sCheeseStrings) {
			//����ֱ����дArrayAdapter��ϵͳ����ķ��������ظ÷�����ʵ���Լ�������
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				//�Ȼ�ȡϵͳ���ص��Զ���view������ʵ���������TextView�ؼ�
				View view = super.getView(position, convertView, parent);

				//��ȡ��ϵͳ�Զ����view�����޸������ԣ��������Լ������view
				TextView textView = (TextView) view;
				textView.setTextColor(Color.WHITE);
				return view;
			}
		};

		mBackListView.setAdapter(backAdapter);
	}

	//��ʼ��FrontView��ʵ�����󣬲�����߼�����
	private void initFrontView() {
		myLinearLayout = (MyLinearLayout) findViewById(R.id.my_linear);
		myLinearLayout.setDraglayout(myDragLayout);

		mFrontHeader = (ImageView) findViewById(R.id.front_head);
		mFrontListView = (ListView) findViewById(R.id.lv_front);
		ArrayAdapter<String> frontAdapter = new ArrayAdapter<String>(
				MainActivity.this,
				android.R.layout.simple_expandable_list_item_1, Beans.NAMES);
		mFrontListView.setAdapter(frontAdapter);
	}
}
