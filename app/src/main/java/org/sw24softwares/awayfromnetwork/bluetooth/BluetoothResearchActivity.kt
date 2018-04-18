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

class BluetoothResearchActivity : AppCompatActivity() {
        val broadCastReceiver = object : BroadcastReceiver () {
                override fun onReceive(context : Context?, intent : Intent) {
                        val action = intent.getAction()
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                                val device : BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                val deviceName = device.getName()
                                val deviceHardwareAddress = device.getAddress()
                                val label = findViewById(R.id.bluetooth_research_text) as TextView
                                label.setText(label.getText().toString() + "\n" + deviceName)
                        }
                }
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_bluetooth_research)
                
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(broadCastReceiver, filter)

                // Sample code to find PairedDevices / May be useful later
                /*val label = findViewById(R.id.bluetooth_research_text) as TextView
                val pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices()
                if (pairedDevices.size > 0) {
                        for (device in pairedDevices) {
                                val deviceName = device.getName()
                                val deviceHardwareAddress = device.getAddress()
                                label.setText(label.getText().toString() + "\n" + deviceName)
                        }
                }*/

                BluetoothAdapter.getDefaultAdapter().startDiscovery()
        }
        protected override fun onDestroy() {
                super.onDestroy()
                unregisterReceiver(broadCastReceiver)
        }
}
