package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.content.Intent
import android.bluetooth.BluetoothAdapter

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.Manifest 

import kotlin.system.exitProcess

class BluetoothMainActivity : AppCompatActivity() {
        companion object {
                const val REQUEST_ENABLE_BT = 1
                const val REQUEST_COARSE_LOCATION_PERMISSIONS = 2
        }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_bluetooth_main)

                val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (mBluetoothAdapter == null) exitProcess(1)
                if (!mBluetoothAdapter.isEnabled()) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
                val hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        val arr : Array<String> = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                        ActivityCompat.requestPermissions(this, arr, REQUEST_COARSE_LOCATION_PERMISSIONS)
                }

                val button = findViewById(R.id.bluetooth_research) as Button
                button.setOnClickListener {
                        val intent = Intent(this, BluetoothResearchActivity::class.java)
                        startActivity(intent)
                }

        }
}
