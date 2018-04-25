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
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.widget.Toast
import android.view.View
import android.content.res.Configuration
import android.os.Handler
import android.os.Message

class BluetoothDevicesActivity : ListActivity() {
        var mFoundDevices : HashMap<String,BluetoothDevice> = hashMapOf()
        var mArrayAdapter : ArrayAdapter<String>? = null
        
        val broadCastReceiver = object : BroadcastReceiver () {
                override fun onReceive(context : Context?, intent : Intent) {
                        val action = intent.getAction()
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                mFoundDevices.put(device.getName() ?: device.getAddress(),device)
                                mArrayAdapter?.add(device.getName() ?: device.getAddress())
                        }
                        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                                Toast.makeText(this@BluetoothDevicesActivity, "Discovery stopped", 5).show()
                        }
                }
        }

        fun setupHandler() {
                val handler = object : Handler() {
                        override fun handleMessage(msg : Message) {
                                if (msg.what == Constants.MESSAGE_DEVICE_NAME) {
                                        this@BluetoothDevicesActivity.finish()
                                        val intent = Intent(this@BluetoothDevicesActivity, MessageActivity::class.java)
                                        startActivity(intent)
                                }
                        }
                }
                BluetoothMainActivity.mBluetoothInteraction?.setHandler(handler)
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                setupHandler()
                
                mArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
                setListAdapter(mArrayAdapter)
                
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                registerReceiver(broadCastReceiver, filter)
                BluetoothAdapter.getDefaultAdapter().startDiscovery()
                Toast.makeText(this, getString(R.string.searching), 5).show()

                getListView().setOnItemClickListener { _, view, _, _ ->
                        try {
                                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                                val device = mFoundDevices.get((view as TextView).getText().toString())
                                if(device != null) {
                                        Toast.makeText(this, "Trying to establish a connection to " + device.getName(), 5).show()
                                        BluetoothMainActivity.mBluetoothInteraction?.connect(device)
                                }
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
