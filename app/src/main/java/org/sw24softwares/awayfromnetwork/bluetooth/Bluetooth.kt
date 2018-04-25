package org.sw24softwares.awayfromnetwork

import android.app.Activity 
import android.bluetooth.BluetoothAdapter
import android.support.v4.app.ActivityCompat
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest

class Bluetooth {
        val mAdapter = BluetoothAdapter.getDefaultAdapter()
        var mActivity : Activity? = null

        companion object {
                const val REQUEST_ENABLE_BT = 1
                const val REQUEST_COARSE_LOCATION_PERMISSIONS = 2
                
                private var mSingleton = Bluetooth()
                fun getSingleton() : Bluetooth {
                        return mSingleton
                }
                fun setActivity(activity_ : Activity) {
                        mSingleton.mActivity = activity_
                }
        }

        fun isAvailable() : Boolean {
                return mAdapter != null
        }
        fun activate() {
                if (!mAdapter.isEnabled()) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        mActivity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
        }
        fun askPermission() {
                val activity_ = mActivity
                if(activity_ != null) {
                        val hasPermission = ActivityCompat.checkSelfPermission(activity_, Manifest.permission.ACCESS_COARSE_LOCATION)
                        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                                val arr : Array<String> = arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                                ActivityCompat.requestPermissions(activity_, arr, REQUEST_COARSE_LOCATION_PERMISSIONS)
                        }
                }
        }
        fun makeDiscoverable(duration : Int) {
                val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
                mActivity?.startActivity(discoverableIntent);
        }
}
