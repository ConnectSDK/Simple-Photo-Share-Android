package com.example.simplephotoshare;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.connectsdk.core.LaunchSession;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.listeners.LaunchListener;
import com.connectsdk.service.command.ServiceCommandError;

public class MainActivity extends Activity {
	DiscoveryManager _discoveryManager;
	Dialog _pickerDialog;
	ConnectableDevice _device;
	TextView _statusTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		DiscoveryManager.getInstance(getApplicationContext()).start();
		
		setupPicker();
		
		_statusTextView = (TextView) this.findViewById(R.id.statusTextView);
		Button shareImageButton = (Button) this.findViewById(R.id.shareImageButton);
		
		shareImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_pickerDialog.show();
			}
		});
	}
	
	private void setupPicker()
    {
    	DiscoveryManager.getInstance(getApplicationContext()).registerDefaultDeviceTypes();

        DevicePicker dp = new DevicePicker(this);
        _pickerDialog = dp.getPickerDialog("Device List", new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            	DiscoveryManager.getInstance(getApplicationContext()).stop();

                _device = (ConnectableDevice)arg0.getItemAtPosition(arg2);
                _device.addListener(deviceListener);
                _device.connect();
            }
        });
    }
	
	private ConnectableDeviceListener deviceListener = new com.connectsdk.device.ConnectableDeviceListener() {
		
		@Override
		public void onPairingRequired(PairingType pairingType) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDeviceReady() {
			_device.getMediaPlayer().displayImage(
					"http://www.freesoftwaremagazine.com/files/nodes/3466/fig_sintel_style_study.jpg",
					"image/png",
					"Sintel",
					"Blender Open Movie Project",
					null,
					new LaunchListener() {
				
				@Override
				public void onLaunchSuccess(LaunchSession launchSession) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							_statusTextView.setText("Successfully displayed image!");
						}
					});
				}
				
				@Override
				public void onLaunchFailed(ServiceCommandError error) {
					final String errorDesc = error.getDesc();
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							_statusTextView.setText("An error occured while displaying image: " + errorDesc);
						}
					});
				}
			});
		}
		
		@Override
		public void onDeviceDisconnected() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectionFailed(ServiceCommandError error) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onCapabilityUpdated(List<String> added, List<String> removed) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
