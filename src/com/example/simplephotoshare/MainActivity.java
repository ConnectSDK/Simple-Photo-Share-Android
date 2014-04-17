package com.example.simplephotoshare;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MediaPlayer.MediaLaunchObject;
import com.connectsdk.service.command.ServiceCommandError;

public class MainActivity extends Activity {
	private DevicePicker mDevicePicker;
	private ConnectableDevice mDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DiscoveryManager.init(getApplicationContext());
		
		CapabilityFilter imageFilter = new CapabilityFilter(MediaPlayer.Display_Image);
		
		DiscoveryManager.getInstance().setCapabilityFilters(imageFilter);
		DiscoveryManager.getInstance().start();
		
		mDevicePicker = new DevicePicker(this);
		
		Button imageButton = (Button) findViewById(R.id.imageButton);
		imageButton.setOnClickListener(mShowImageClickListener);
	};
	
	private OnClickListener mShowImageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog dialog = mDevicePicker.getPickerDialog("Select a device", mPickerClickListener);
			dialog.show();
		}
	};
	
	private AdapterView.OnItemClickListener mPickerClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mDevice = (ConnectableDevice)arg0.getItemAtPosition(arg2);
			mDevice.addListener(mDeviceListener);
			mDevice.connect();
		};
	};
	
	private ConnectableDeviceListener mDeviceListener = new ConnectableDeviceListener() {
		
		@Override
		public void onPairingRequired(ConnectableDevice device,
				DeviceService service, PairingType pairingType) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDeviceReady(ConnectableDevice device) {
			device.getMediaPlayer().displayImage(
					"http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photo.jpg",
					"image/jpg",
					"Sintel Character Design",
					"Blender Open Movie Project",
					"http://ec2-54-201-108-205.us-west-2.compute.amazonaws.com/samples/media/photoIcon.jpg",
					mLaunchListener);
		}
		
		@Override
		public void onDeviceDisconnected(ConnectableDevice device) {
			mDevice.removeListener(mDeviceListener);
			mDevice = null;
		}
		
		@Override
		public void onConnectionFailed(ConnectableDevice device,
				ServiceCommandError error) {
			mDevice.removeListener(mDeviceListener);
			mDevice = null;
		}
		
		@Override
		public void onCapabilityUpdated(ConnectableDevice device,
				List<String> added, List<String> removed) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private MediaPlayer.LaunchListener mLaunchListener = new MediaPlayer.LaunchListener() {
		
		@Override
		public void onError(ServiceCommandError error) {
			Log.d("Connect SDK Sample App", "Could not launch image: " + error.toString());
		}
		
		@Override
		public void onSuccess(MediaLaunchObject object) {
			Log.d("Connect SDK Sample App", "Successfully launched image!");
			
			mDevice.removeListener(mDeviceListener);
			mDevice.disconnect();
			mDevice = null;
		}
	};

}
