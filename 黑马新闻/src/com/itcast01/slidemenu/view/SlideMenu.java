package com.itcast01.slidemenu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlideMenu extends ViewGroup {

	private int mMostRecentX; // ���µ�x��ƫ����

	private final int MENU_SCREEN = 0; // �˵�����
	private final int MAIN_SCREEN = 1; // ������
	private int currentScreen = MAIN_SCREEN; // ��ǰ��Ļ, Ĭ��Ϊ: ������

	private Scroller mScroller; // ģ�����ݶ���

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mScroller = new Scroller(context);
	}
	
	/**
	 * �˷�����SlideMenu�ؼ�������͸�ʱ�ص�.
	 * widthMeasureSpec ��Ȳ������: ������Ļ�Ŀ�
	 * heightMeasureSpec �߶Ȳ������: ������Ļ�ĸ�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// �����˵��Ŀ�͸�
		View menuView = getChildAt(0);
		menuView.measure(menuView.getLayoutParams().width, heightMeasureSpec);
		
		// ����������Ŀ�͸�
		View mainView = getChildAt(1);
		// ������Ŀ����������Ļ�Ŀ�.
		mainView.measure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ����SlideMenu�а������ӿؼ���λ��.
	 * left = 0;
	 * top = 0;
	 * right = ������Ļ�Ŀ��;
	 * bottom = ������Ļ�ĸ߶�;
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// ���ò˵������λ��: left=-�˵��Ŀ��, top=0, right=0, bottom=������Ļ�ĸ߶�
		View menuView = getChildAt(0);
		menuView.layout(-menuView.getMeasuredWidth(), 0, 0, b);
		
		// �����������λ��: left=0, top=0, right=������Ļ�Ŀ��, bottom=������Ļ�ĸ߶�;
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
			
			// 1. �����ֵ: mMostRecentX - moveX = 48 - 42 = 6;
			int diff = mMostRecentX - moveX;
			
			// 2. �޶����ұ߽�
			int currentX = getScrollX() + diff;
			if(currentX < -getChildAt(0).getMeasuredWidth()) {
				// ��������߽�, �Ѿ��ƶ�����-240
				scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
			} else if(currentX > 0) {
				scrollTo(0, 0);
			} else {
				// 3. ���ݲ�ֵ, ʹ��scrollBy�����ƶ���Ļ. scrollBy(6, 0);
				scrollBy(diff, 0);
			}
			
			// 4. ��Ҫ��mMostRecentX���¸�ֵ, ��ֵΪmoveX.    mMostRecentX = 48;
			mMostRecentX = moveX;
			break;
		case MotionEvent.ACTION_UP:
			// ȡ����ǰx���ƫ����
			int scrollX = getScrollX();
			
			if(scrollX > (-getChildAt(0).getMeasuredWidth() / 2)) {
				// ��ǰӦ���л���������
				currentScreen = MAIN_SCREEN;
			} else {
				// ��ǰӦ���л����˵�����
				currentScreen = MENU_SCREEN;
			}
			switchScreen();
			break;
		default:
			break;
		}
		return true; // ��ȫ�Լ������¼�
	}

	/**
	 * ����currentScreen���л���Ļ��ʾ�Ľ���
	 */
	private void switchScreen() {
		int startX = getScrollX();
		
		int dx = 0; 
		
		if(currentScreen == MAIN_SCREEN) {
			// �л���������
//			scrollTo(0, 0);
			// �㷨: Ŀ�ص�ֵ - startX
			dx = 0 - startX;
		} else if(currentScreen == MENU_SCREEN) {
//			scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
			dx = -getChildAt(0).getMeasuredWidth() - startX;
		}
		
		// ��ʼģ��������, ֻģ������
		mScroller.startScroll(startX, 0, dx, 0, Math.abs(dx) * 6);
		
		// �ػ�ˢ��. 
		invalidate(); // invalidate -> drawChild -> child.draw -> computeScroll
	}

	@Override
	public void computeScroll() {
		// ����scrollX����scrollY��ֵ.
		
		if(mScroller.computeScrollOffset()) {
			// ��ǰ����ģ������, ȡ��x��ģ���ֵ, ���ø�scrollTo����.
			int currX = mScroller.getCurrX();
//			System.out.println("currX: " + currX);
			
			scrollTo(currX, 0);
			
			invalidate(); // �ݹ�
		}
	}

	/**
	 * �Ƿ���ʾ�˵�
	 * @return
	 */
	public boolean isShowMenu() {
		return currentScreen == MENU_SCREEN;
	}

	/**
	 * ���ز˵�
	 */
	public void hideMenu() {
		currentScreen = MAIN_SCREEN;
		switchScreen();
	}

	/**
	 * ��ʾ�˵�
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
			
			// ����ƶ���ƫ�����ľ��볬����10
			int diff = moveX - mMostRecentX;
			
			if(Math.abs(diff) > 10) {
//				System.out.println("���Ż�����");
				return true;
			}
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
}
