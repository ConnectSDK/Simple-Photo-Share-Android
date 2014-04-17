package com.example.simplephotoshare;

import java.util.List;

import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.CapabilityFilter;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.capability.MediaPlayer.MediaLaunchObject;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.device.ConnectableDevice;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
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
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	protected void onResume() {
		super.onResume();
		
		PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().getFragments().get(0);
		Button imageButton = (Button) fragment.getView().findViewById(R.id.imageButton);
		imageButton.setOnClickListener(showImageClickListener);
	};
	
	OnClickListener showImageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			AlertDialog dialog = mDevicePicker.getPickerDialog("Select a device", pickerClickListener);
			dialog.show();
		}
	};
	
	AdapterView.OnItemClickListener pickerClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			mDevice = (ConnectableDevice)arg0.getItemAtPosition(arg2);
			mDevice.addListener(deviceListener);
			mDevice.connect();
		};
	};
	
	ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {
		
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
					launchListener);
		}
		
		@Override
		public void onDeviceDisconnected(ConnectableDevice device) {
			mDevice.removeListener(deviceListener);
			mDevice = null;
		}
		
		@Override
		public void onConnectionFailed(ConnectableDevice device,
				ServiceCommandError error) {
			mDevice.removeListener(deviceListener);
			mDevice = null;
		}
		
		@Override
		public void onCapabilityUpdated(ConnectableDevice device,
				List<String> added, List<String> removed) {
			// TODO Auto-generated method stub
			
		}
	};
	
	MediaPlayer.LaunchListener launchListener = new MediaPlayer.LaunchListener() {
		
		@Override
		public void onError(ServiceCommandError error) {
			Log.d("Connect SDK Sample App", "Could not launch image: " + error.toString());
		}
		
		@Override
		public void onSuccess(MediaLaunchObject object) {
			Log.d("Connect SDK Sample App", "Successfully launched image!");
			
			mDevice.removeListener(deviceListener);
			mDevice.disconnect();
			mDevice = null;
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
