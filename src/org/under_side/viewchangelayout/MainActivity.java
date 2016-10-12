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

	//实现初始化操作
	private void init() {
		initDragLayout();
		initBackView();
		initFrontView();
	}

	//实现DragLayout实例变量，并重写该自定义ViewGroup中的接口方法
	private void initDragLayout() {

		myDragLayout = (DragLayout) findViewById(R.id.dl);
		myDragLayout
				.setOnItemStateChangedListener(new onItemStateChangedListener() {

					@Override
					public void onOpen() {
						//在打开item时，去随机跳转到mBackView中的item
						Random random = new Random();
						int next = random.nextInt(30);
						//利用listView的该方法，可以很平滑的跳转到指定的position的item上
						mBackListView.smoothScrollToPosition(next);
					}

					@Override
					public void onDraging(float fraction) {

						//当拖拽时，去处理mFrontView的透明度显示效果
						ViewHelper.setAlpha(mFrontHeader, 1 - fraction);
					}

					@Override
					public void onClose() {
						// 使用的是nineoldandroid.jar架包中的属性动画，提高了兼容性
						ObjectAnimator animator = ObjectAnimator.ofFloat(
								mFrontHeader, "translationX", 15.0f);
						animator.setInterpolator(new CycleInterpolator(3.0f));
						animator.setDuration(500);
						animator.start();
					}
				});
	}

	//初始化backView的实例对象，并添加逻辑操作
	private void initBackView() {
		mBackListView = (ListView) findViewById(R.id.lv_back);
		ArrayAdapter<String> backAdapter = new ArrayAdapter<String>(
				MainActivity.this,
				android.R.layout.simple_expandable_list_item_1,
				Beans.sCheeseStrings) {
			//可以直接重写ArrayAdapter中系统定义的方法，重载该方法，实现自己的需求
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				//先获取系统返回的自定义view对象，其实，定义的是TextView控件
				View view = super.getView(position, convertView, parent);

				//获取到系统自定义的view对象，修改其属性，并返回自己定义的view
				TextView textView = (TextView) view;
				textView.setTextColor(Color.WHITE);
				return view;
			}
		};

		mBackListView.setAdapter(backAdapter);
	}

	//初始化FrontView的实例对象，并添加逻辑操作
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
