package com.example.bluetooth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.R.string;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	BoxView boxView;
	EditText editText;

    private static final String TAG = "LED_DRAW";
	private static final boolean D = true;
	
	// Debugging
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_SETTING = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
	
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
	
	private Intent serverIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.editText1);
		boxView = (BoxView) findViewById(R.id.BoxView);
		serverIntent =  new Intent(this, DeviceListActivity.class);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        Fixed_Packet.add(StringToByteArray("7ECD01E000AE7E"));
        Fixed_Packet.add(StringToByteArray("7ECD03C000907E"));
        Fixed_Packet.add(StringToByteArray("7ECD05A000727E"));
        Fixed_Packet.add(StringToByteArray("7ECD078000547E"));
        Fixed_Packet.add(StringToByteArray("7ECD096000367E"));
        Fixed_Packet.add(StringToByteArray("7ECD0B4000187E"));
        Fixed_Packet.add(StringToByteArray("7ECD0D2000FA7E"));
        Fixed_Packet.add(StringToByteArray("7ECD0F0000DC7E"));
        Fixed_Packet.add(StringToByteArray("7ECD10E000BD7E"));
        Fixed_Packet.add(StringToByteArray("7E9912C0006B7E"));
        
	}

    byte[] PrepareHeader = { 0x7E, (byte) 0xF2, 0x00, 0x00, 0x00, (byte) 0xF2, 0x7E };
    byte PacketHeader = 0x7E;
    List<byte[]> Fixed_Packet = new ArrayList<byte[]>();
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.textsendbutton:
			// send message
			
			boxView.str = editText.getText().toString();
			boxView.invalidate(); // OnDraw 재호출

			break;
		case R.id.resetbutton:
			boxView.arVertex.clear();
			boxView.str="";
			boxView.invalidate();
			break;
		case R.id.patternsendbutton:
			// send message
			String RawData = boxView.update_RawData();
			Log.d(TAG, "Raw Data Length : " + RawData.length());
			mChatService.write(PrepareHeader);
			LED_TransCheck(300);
			
			StringBuilder Packet_String = new StringBuilder();
			Packet_String.append("DD0000");
			Packet_String.append("0000000000000002010101000100");
			Packet_String.append(RawData.substring(0, 484));

			Packet_String.append(String.format("%02X", Calc_CheckSum(StringToByteArray(Packet_String.toString()))));
			Packet_String.append(String.format("%02X", PacketHeader));
			Packet_String.insert(0, String.format("%02X", PacketHeader));

			Log.d(TAG, "Packet1 Length : " + Packet_String.toString().length());
			mChatService.write(StringToByteArray(Packet_String.toString()));
			LED_TransCheck(100);
			
			Packet_String = new StringBuilder();
			Packet_String.append("DD0001");
			Packet_String.append(RawData.substring(484, 484+512));

			Packet_String.append(String.format("%02X", Calc_CheckSum(StringToByteArray(Packet_String.toString()))));
			Packet_String.append(String.format("%02X", PacketHeader));
			Packet_String.insert(0, String.format("%02X", PacketHeader));

			Log.d(TAG, "Packet2 Length : " + Packet_String.toString().length());
			mChatService.write(StringToByteArray(Packet_String.toString()));
			LED_TransCheck(100);

			Packet_String = new StringBuilder();
			Packet_String.append("DD0002");
			Packet_String.append(RawData.substring(996, 996+28));
			Packet_String.append("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

			Packet_String.append(String.format("%02X", Calc_CheckSum(StringToByteArray(Packet_String.toString()))));
			Packet_String.append(String.format("%02X", PacketHeader));
			Packet_String.insert(0, String.format("%02X", PacketHeader));

			Log.d(TAG, "Packet3 Length : " + Packet_String.toString().length());
			mChatService.write(StringToByteArray(Packet_String.toString()));
			LED_TransCheck(100);
			
			for (byte[] fpacket : Fixed_Packet)
            {
				mChatService.write(fpacket);
				LED_TransCheck(100);
            }
			break;
		}

	}
	
	private void LED_TransCheck(int time)
	{
		set_time = System.currentTimeMillis();
		while(!SetPass(time));
		
	}
	private byte[] StringToByteArray(String hex)
	{
		return new java.math.BigInteger(hex, 16).toByteArray();
	}
	
	private byte Calc_CheckSum(byte[] pac)
    {
        byte sum = 0;
        for (int i = 0; i < pac.length; i++) {
            sum += pac[i];			
		}
        return sum;
    }
	long set_time = System.currentTimeMillis();	
    private boolean SetPass(long passtime)
    {
    	long curr = System.currentTimeMillis();
    	if(curr - set_time > passtime)
    		return true;
    	else
    		return false;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
      
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private StringBuilder sb = new StringBuilder();
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            
            case MESSAGE_STATE_CHANGE:
            	
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    break;
                    
                case BluetoothChatService.STATE_CONNECTING:
                    break;
                    
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    break;
                    
                }
                break;
                
            case MESSAGE_WRITE:
                break;
                
            case MESSAGE_READ:             
                break;
                
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
                
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_SETTING:   	
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            	
            }
        }
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mChatService.connect(device);
            }
            break;
            
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }    
    
 // 하드웨어메뉴버튼 준비 이벤트 오버라이드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     super.onCreateOptionsMenu(menu);
     menu.add(Menu.FIRST, Menu.FIRST, Menu.NONE, "블루투스 설정");
     return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     if (item.getItemId() == Menu.FIRST) {
         startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
     } else {
      return false;
     }
     return true;
    }
}
