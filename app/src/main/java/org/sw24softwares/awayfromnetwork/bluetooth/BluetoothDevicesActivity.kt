package org.sw24softwares.awayfromnetwork

import android.app.ListActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.Context
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.widget.TextView
import android.widget.ExpandableListView
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

class BluetoothDevicesActivity : ListActivity() {
        var mFoundDevices : HashMap<String,BluetoothDevice> = hashMapOf()
        var arrayAdapter : ArrayAdapter<String>? = null
        
        val broadCastReceiver = object : BroadcastReceiver () {
                override fun onReceive(context : Context?, intent : Intent) {
                        val action = intent.getAction()
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                mFoundDevices.put(device.getName() ?: device.getAddress(),device)
                                arrayAdapter?.add(device.getName() ?: device.getAddress())
                        }
                }
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
                setListAdapter(arrayAdapter);
                
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(broadCastReceiver, filter)
                BluetoothAdapter.getDefaultAdapter().startDiscovery()

                getListView().setOnItemClickListener { _, view, _, _ -> 
                        try {
                                val device = mFoundDevices.get((view as TextView).getText().toString())
                                if(device != null) BluetoothMainActivity.mBluetoothInteraction.connect(device)
                        } catch (x : ClassNotFoundException) {
                                System.err.format("ClassNotFoundException: %s%n", x)
                        }
                }
        }
        protected override fun onDestroy() {
                super.onDestroy()
                unregisterReceiver(broadCastReceiver)
        }
}
