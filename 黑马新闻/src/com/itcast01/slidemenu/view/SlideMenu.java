package com.itcast01.slidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlideMenu extends ViewGroup {

	private int mMostRecentX; // 最新的x轴偏移量

	private final int MENU_SCREEN = 0; // 菜单界面
	private final int MAIN_SCREEN = 1; // 主界面
	private int currentScreen = MAIN_SCREEN; // 当前屏幕, 默认为: 主界面

	private Scroller mScroller; // 模拟数据对象

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mScroller = new Scroller(context);
	}
	
	/**
	 * 此方法是SlideMenu控件测量宽和高时回调.
	 * widthMeasureSpec 宽度测量规格: 整个屏幕的宽
	 * heightMeasureSpec 高度测量规格: 整个屏幕的高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// 测量菜单的宽和高
		View menuView = getChildAt(0);
		menuView.measure(menuView.getLayoutParams().width, heightMeasureSpec);
		
		// 测量主界面的宽和高
		View mainView = getChildAt(1);
		// 主界面的宽度是整个屏幕的宽.
		mainView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 布置SlideMenu中包含的子控件的位置.
	 * left = 0;
	 * top = 0;
	 * right = 整个屏幕的宽度;
	 * bottom = 整个屏幕的高度;
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 布置菜单界面的位置: left=-菜单的宽度, top=0, right=0, bottom=整个屏幕的高度
		View menuView = getChildAt(0);
		menuView.layout(-menuView.getMeasuredWidth(), 0, 0, b);
		
		// 布置主界面的位置: left=0, top=0, right=整个屏幕的宽度, bottom=整个屏幕的高度;
		View mainView = getChildAt(1);
		mainView.layout(l, t, r, b);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMostRecentX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getX();
			
			// 1. 计算差值: mMostRecentX - moveX = 48 - 42 = 6;
			int diff = mMostRecentX - moveX;
			
			// 2. 限定左右边界
			int currentX = getScrollX() + diff;
			if(currentX < -getChildAt(0).getMeasuredWidth()) {
				// 超出了左边界, 已经移动超过-240
				scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
			} else if(currentX > 0) {
				scrollTo(0, 0);
			} else {
				// 3. 根据差值, 使用scrollBy方法移动屏幕. scrollBy(6, 0);
				scrollBy(diff, 0);
			}
			
			// 4. 需要把mMostRecentX重新赋值, 赋值为moveX.    mMostRecentX = 48;
			mMostRecentX = moveX;
			break;
		case MotionEvent.ACTION_UP:
			// 取出当前x轴的偏移量
			int scrollX = getScrollX();
			
			if(scrollX > (-getChildAt(0).getMeasuredWidth() / 2)) {
				// 当前应该切换到主界面
				currentScreen = MAIN_SCREEN;
			} else {
				// 当前应该切换到菜单界面
				currentScreen = MENU_SCREEN;
			}
			switchScreen();
			break;
		default:
			break;
		}
		return true; // 完全自己处理事件
	}

	/**
	 * 根据currentScreen来切换屏幕显示的界面
	 */
	private void switchScreen() {
		int startX = getScrollX();
		
		int dx = 0; 
		
		if(currentScreen == MAIN_SCREEN) {
			// 切换到主界面
//			scrollTo(0, 0);
			// 算法: 目地的值 - startX
			dx = 0 - startX;
		} else if(currentScreen == MENU_SCREEN) {
//			scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
			dx = -getChildAt(0).getMeasuredWidth() - startX;
		}
		
		// 开始模拟数据了, 只模拟数据
		mScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 6);
		
		// 重绘刷新. 
		invalidate(); // invalidate -> drawChild -> child.draw -> computeScroll
	}

	@Override
	public void computeScroll() {
		// 更新scrollX或者scrollY的值.
		
		if(mScroller.computeScrollOffset()) {
			// 当前正在模拟数据, 取出x轴模拟的值, 设置给scrollTo方法.
			int currX = mScroller.getCurrX();
//			System.out.println("currX: " + currX);
			
			scrollTo(currX, 0);
			
			invalidate(); // 递归
		}
	}

	/**
	 * 是否显示菜单
	 * @return
	 */
	public boolean isShowMenu() {
		return currentScreen == MENU_SCREEN;
	}

	/**
	 * 隐藏菜单
	 */
	public void hideMenu() {
		currentScreen = MAIN_SCREEN;
		switchScreen();
	}

	/**
	 * 显示菜单
	 */
	public void showMenu() {
		currentScreen = MENU_SCREEN;
		switchScreen();
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMostRecentX = (int) ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			
			// 如果移动的偏移量的距离超过了10
			int diff = moveX - mMostRecentX;
			
			if(Math.abs(diff) > 10) {
//				System.out.println("横着滑动了");
				return true;
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}
