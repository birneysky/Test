package com.example.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zijin.ibeacon.util.Utils;
import com.zjjin.utils.Consts;

public class ConnectionStateActivity extends Activity {
	
	private static String TAG = ConnectionStateActivity.class.getSimpleName();
	private TextView tvConnectionState, tvConnectionNotice;
	private Button btnConnection;
	private ImageView ivConnectionState;
	private int state; // 202未发现；帮助查找/
	private String type; //scanTypeAdd；
	private TextView tvTitle;
	private ImageView ivTitleRight;
	private RelativeLayout  menuBackConstruct;
	private String device_mac;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getData();
		if(Consts.EXTRA_ADD_BEACON_FAILED == state){
			if(Consts.EXTRA_SCAN_TYPE_HELP.equals(type)){
				setContentView(R.layout.activity_connection_state_no);
			}else{
				setContentView(R.layout.activity_connection_state);
			}
		}else{
			setContentView(R.layout.activity_connection_state);
		}
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_add_beacon);
		
		setupView();
		addListener();
	}

	private void getData() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		state = data.getInt(Consts.CONNECTION_STATE);
		type = data.getString(Consts.EXTRA_KEY_SCAN_TYPE);
		device_mac = data.getString(Consts.DEVICE_MAC);
	}

	private void setupView() {
		tvTitle = (TextView)findViewById(R.id.tv_title_bar_title);
		ivTitleRight = (ImageView)findViewById(R.id.iv_title_bar_right_add);
		ivTitleRight.setVisibility(View.GONE);
		menuBackConstruct = (RelativeLayout)findViewById(R.id.menu_back_construct);
		tvConnectionState = (TextView)findViewById(R.id.tv_connection_state);
		tvConnectionNotice = (TextView)findViewById(R.id.tv_connection_notice);
		btnConnection = (Button)findViewById(R.id.btn_connection_state);
		ivConnectionState = (ImageView)findViewById(R.id.iv_connection_state);
		switch (state) {
		case Consts.EXTRA_ADD_BEACON_OK:
			if(Consts.EXTRA_SCAN_TYPE_HELP.equals(type)){
				tvTitle.setText(getResources().getString(R.string.str_searched_message_help));
				tvConnectionState.setText("已帮助搜寻到");
				btnConnection.setText("通知");
			}else{
				tvTitle.setText("添加新设备");
				tvConnectionState.setText("连接已成功");
				btnConnection.setText("设置物品信息");
			}
			ivConnectionState.setImageDrawable(getResources().getDrawable(R.drawable.connection_success));
			tvConnectionState.setTextColor(getResources().getColor(R.color.blue));
			btnConnection.setBackground(getResources().getDrawable(R.drawable.button_bg));
			tvConnectionNotice.setVisibility(View.GONE);
			break;
		case Consts.EXTRA_ADD_BEACON_FAILED:
			if(Consts.EXTRA_SCAN_TYPE_HELP.equals(type)){
				tvTitle.setText(getResources().getString(R.string.str_searched_message_help));
				tvConnectionState.setText("未帮助搜寻到");
				btnConnection.setText("");
				btnConnection.setBackgroundColor(getResources().getColor(R.color.transparent));
				tvConnectionNotice.setVisibility(View.GONE);
			}else{
				tvTitle.setText("无法连接");
				tvConnectionState.setText("连接未成功");
				btnConnection.setText("再次扫描");
				btnConnection.setBackground(getResources().getDrawable(R.drawable.button_bg_yellow));
				ivConnectionState.setImageDrawable(getResources().getDrawable(R.drawable.connection_failed));
				tvConnectionState.setTextColor(getResources().getColor(R.color.connection_state));
				tvConnectionNotice.setVisibility(View.VISIBLE);
			}
			break;
		}
		
	}

	private void addListener() {
		menuBackConstruct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 点击返回按钮
				/*switch (state) {
				case Consts.EXTRA_ADD_BEACON_OK:
					Intent foundedDevice = new Intent(ConnectionStateActivity.this, AddNewTrackerActivity.class);
					startActivityForResult(foundedDevice, Consts.REQUEST_CONNECTION_STATE_ADD_TRACKER);
					break;
				case Consts.EXTRA_ADD_BEACON_FAILED:
					setResult(Consts.RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN);
					ConnectionStateActivity.this.finish();
					break;
				}*/
				Intent intent = new Intent();
				intent.putExtra("tag", "add");
				setResult(RESULT_CANCELED, intent);
				ConnectionStateActivity.this.finish();
			}
		});
		
		btnConnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (state) {
				case Consts.EXTRA_ADD_BEACON_OK:{
						if(Consts.EXTRA_SCAN_TYPE_HELP.equals(type)){//帮助
							Intent retIntent = new Intent();
							retIntent.putExtra("isFound", true);
							setResult(Consts.RESULT_HELP_SEARCH_SATATE,retIntent);
							ConnectionStateActivity.this.finish();
						}else{//正常添加
							Intent foundedDevice = new Intent(ConnectionStateActivity.this, AddNewTrackerActivity.class);
							foundedDevice.putExtra(Consts.DEVICE_MAC, device_mac);
							startActivityForResult(foundedDevice, Consts.REQUEST_CONNECTION_STATE_ADD_TRACKER);	
						}
						Utils.isScanAddOrHelp=false;
					}
					break;
				case Consts.EXTRA_ADD_BEACON_FAILED: // 未搜索到beacon,无法添加
					if(Consts.EXTRA_SCAN_TYPE_HELP.equals(type)){
						// 帮助众寻中，未发现beacon，此按钮消失 TODO
					}else{
						Intent retIntent = new Intent();
						retIntent.putExtra("isFound", false);
						setResult(Consts.RESULT_CONNECTION_STATE_SCAN_BEACON_AGAIN,retIntent);
						ConnectionStateActivity.this.finish();
					}
					break;
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(data == null) 
			return;
		if(requestCode == Consts.REQUEST_CONNECTION_STATE_ADD_TRACKER){
			// 成功添加
			if(resultCode == AddBeaconActivity.REQEUSET_ADD_IBEACON_NAME){//add ibeacon return 
				String devive_name = data.getStringExtra("name");
				String pic_path = data.getStringExtra("picPath");
				Intent returnIntent = new Intent();
				returnIntent.putExtra("tracker_name", devive_name);returnIntent.putExtra(Consts.DEVICE_MAC, device_mac);
				if(pic_path != null)
					returnIntent.putExtra("picPath", pic_path);
				setResult(AddBeaconActivity.REQEUSET_ADD_IBEACON_NAME, returnIntent);
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connection_state, menu);
		return true;
	}

}