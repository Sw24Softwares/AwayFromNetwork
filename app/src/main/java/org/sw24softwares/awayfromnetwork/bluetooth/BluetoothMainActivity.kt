package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.content.Intent
import android.bluetooth.BluetoothAdapter

import android.os.Handler
import android.os.Message
import android.util.Log

import kotlin.system.exitProcess

class BluetoothMainActivity : AppCompatActivity() {
        companion object {
                const val REQUEST_ENABLE_BT = 1
                const val REQUEST_COARSE_LOCATION_PERMISSIONS = 2
                var mBluetoothInteraction : BluetoothInteraction? = null
        }

        fun setupHandler() {
                val handler = object : Handler() {
                        override fun handleMessage(msg : Message) {
                                if (msg.what == Constants.MESSAGE_DEVICE_NAME) {
                                        val intent = Intent(this@BluetoothMainActivity, MessageActivity::class.java)
                                        startActivity(intent)
                                }
                        }
                }
                mBluetoothInteraction?.setHandler(handler)
        }
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_bluetooth_main)

                Bluetooth.setActivity(this)
                if(Bluetooth.getSingleton().isAvailable()) {
                        Log.e("AwayFromNetwork", "Bluetooth is unavailable, this part of the application is useless, please check if you can activate Bluetooth or use the Wifi part");
                }
                Bluetooth.getSingleton().activate()
                Bluetooth.getSingleton().askPermission()
                
                // Button ClickListener
                val button_devices = findViewById(R.id.bluetooth_devices) as Button
                button_devices.setOnClickListener {
                        val intent = Intent(this, BluetoothDevicesActivity::class.java)
                        startActivity(intent)
                }
                val button_discoverable = findViewById(R.id.bluetooth_discoverable) as Button
                button_discoverable.setOnClickListener {
                        Bluetooth.getSingleton().makeDiscoverable(120)
                }
        }
        override fun onResume() {
                super.onResume()


                // Set up Handler for accept connection
                mBluetoothInteraction = BluetoothInteraction()
                setupHandler()
                mBluetoothInteraction?.listen()
        }
}
