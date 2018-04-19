package org.sw24softwares.awayfromnetwork

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

class BluetoothDevicesActivity : AppCompatActivity() {
        var mListAdapter : ExpandableListAdapter? = null
        var mExpListView : ExpandableListView? = null
        var mListDataHeader : MutableList<String> = ArrayList<String>()
        var mListDataChild = HashMap<String, List<String>>()
        
        val broadCastReceiver = object : BroadcastReceiver () {
                override fun onReceive(context : Context?, intent : Intent) {
                        val action = intent.getAction()
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                val list = mListDataChild.remove("Found")?.toMutableList() ?: mutableListOf()
                                list.add(device.getName())
                                mListDataChild.put("Found", list)
                        }
                }
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_bluetooth_devices)

                mListDataHeader.add("Found")
                mListDataChild.put("Found", mutableListOf())
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(broadCastReceiver, filter)

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
                mExpListView?.setAdapter(mListAdapter)

                BluetoothAdapter.getDefaultAdapter().startDiscovery()
        }
        protected override fun onDestroy() {
                super.onDestroy()
                unregisterReceiver(broadCastReceiver)
        }
}
