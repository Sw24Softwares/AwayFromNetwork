package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.IntentFilter
import android.content.Context

import android.widget.ListView
import android.widget.Toast
import android.widget.ArrayAdapter

import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList

import android.util.Log

class WifiMainActivity : AppCompatActivity() {
        var mIntentFilter = IntentFilter()
        var mChannel : Channel? = null
        var mManager : WifiP2pManager? = null
        var mReceiver : WiFiDirectBroadcastReceiver? = null
        var mArrayAdapter : ArrayAdapter<String>? = null

        private var mPeers = ArrayList<WifiP2pDevice>()
        private var mListView : ListView? = null

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_wifi_main)

                mListView = findViewById(R.id.device_list) as ListView
                mArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
                mListView?.setAdapter(mArrayAdapter)

                // Initialize Wi-Fi P2P
                mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
                mChannel = mManager?.initialize(this, getMainLooper(), null)
                mReceiver = WiFiDirectBroadcastReceiver(mManager, mChannel, peerListListener)

                // Indicates a change in the Wi-Fi P2P status.
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

                // Indicates a change in the list of available peers.
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

                // Indicates the state of Wi-Fi P2P connectivity has changed.
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

                // Indicates this device's details have changed.
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

                val action = object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                                Log.d("p2p", "discoverPeers() Success")
                                Toast.makeText(this@WifiMainActivity,getString(R.string.searching), Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(reason : Int) {
                                Log.d("p2p", "discoverPeers() Failure: " + reason)
                        }
                }
                mManager?.discoverPeers(mChannel, action)
        }

        override fun onResume() {
                super.onResume()

                // Initiates Wifi P2P receiver
                registerReceiver(mReceiver, mIntentFilter)
        }

        override fun onPause() {
                super.onPause()
                unregisterReceiver(mReceiver)
        }

        // Fetches and updates the list of devices
        private val peerListListener = object : PeerListListener {
                override fun onPeersAvailable(peerList : WifiP2pDeviceList) {
                        val refreshedPeers = peerList.getDeviceList()

                        if (!refreshedPeers.equals(mPeers)) {
                                mPeers.clear()
                                mPeers.addAll(refreshedPeers)
                        }

                        if (mPeers.size == 0) {
                                Log.d("p2p" , "No devices found")
                                return
                        }

                        refreshListView()
                }
        }

        fun refreshListView() {
                mArrayAdapter?.clear()
                var i = 0

                for(item in mPeers) {
                        mArrayAdapter?.add(mPeers.get(i).deviceName.toString())
                        i++
                }
                return
        }
}
