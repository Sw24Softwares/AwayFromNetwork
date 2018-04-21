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
        //var mListAdapter : ExpandableListAdapter? = null
        //var mExpListView : ExpandableListView? = null
        //var mListDataHeader : MutableList<String> = ArrayList<String>()
        //var mListDataChild = HashMap<String, List<String>>()
        var mFoundDevices : HashMap<String,BluetoothDevice> = hashMapOf()
        
        val broadCastReceiver = object : BroadcastReceiver () {
                override fun onReceive(context : Context?, intent : Intent) {
                        val action = intent.getAction()
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                mFoundDevices.put(device.getName() ?: device.getAddress(),device)
                                val arr : Array<String> = mFoundDevices.keys.toTypedArray()
                                val adapter = ArrayAdapter(this@BluetoothDevicesActivity, android.R.layout.simple_list_item_1,arr)
                                setListAdapter(adapter);
                        }
                }
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
//                setContentView(R.layout.activity_bluetooth_devices)

/*                mListDataHeader.add("Found")
                mListDataChild.put("Found", mutableListOf())
                
                mListDataHeader.add("Paired Devices")
                val devices : MutableList<String> = mutableListOf()
                val pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices()
                if (pairedDevices.size > 0) {
                        for (device in pairedDevices) {
                                devices.add(device.getName())
                        }
                }
                mListDataChild.put("Paired Devices", devices)

                mListAdapter = ExpandableListAdapter(this, mListDataHeader, mListDataChild)
                mExpListView = findViewById(R.id.bluetooth_devices_list) as ExpandableListView;
                
                mExpListView?.setAdapter(mListAdapter)*/

                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(broadCastReceiver, filter)
                BluetoothAdapter.getDefaultAdapter().startDiscovery()

                getListView().setOnItemClickListener { parent, view, position, id -> 
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
