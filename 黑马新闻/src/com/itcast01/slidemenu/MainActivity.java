package com.itcast01.slidemenu;

import com.itcast01.slidemenu.view.SlideMenu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private SlideMenu mSlideMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.ib_slidemenu_main_back).setOnClickListener(this);
		
		mSlideMenu = (SlideMenu) findViewById(R.id.slidemenu);
	}

	@Override
	public void onClick(View v) {
		// 切换当前屏幕显示的状态
		
		if(mSlideMenu.isShowMenu()) {
			// 显示菜单, 切换到主界面
			mSlideMenu.hideMenu();
		} else {
			// 显示主界面, 切换到菜单
			mSlideMenu.showMenu();
		}
	}

	public void clickTab(View v) {
		TextView tv = (TextView) v;
		Toast.makeText(this, tv.getText(), 0).show();
	}
}
