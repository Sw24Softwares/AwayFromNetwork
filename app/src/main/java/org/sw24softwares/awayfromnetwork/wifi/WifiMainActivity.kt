package org.sw24softwares.awayfromnetwork

import android.app.ListActivity
import android.os.Bundle

import android.content.IntentFilter
import android.content.Context

import android.widget.ListView
import android.widget.Toast
import android.widget.ArrayAdapter

import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pConfig

import android.util.Log

class WifiMainActivity : ListActivity() {
        var mIntentFilter = IntentFilter()
        var mChannel : Channel? = null
        var mManager : WifiP2pManager? = null
        var mReceiver : WiFiDirectBroadcastReceiver? = null
        var mArrayAdapter : ArrayAdapter<String>? = null
        var mSelectedDevice = WifiP2pConfig()

        private var mPeers = ArrayList<WifiP2pDevice>()

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                mArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
                setListAdapter(mArrayAdapter)

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
                                Toast.makeText(this@WifiMainActivity, getString(R.string.searching), Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(reason : Int) {
                                Log.d("p2p", "discoverPeers() Failure: " + reason)
                        }
                }
                mManager?.discoverPeers(mChannel, action)

                getListView().setOnItemClickListener {_, _, position, _ ->
                        mSelectedDevice.deviceAddress = mPeers.get(position).deviceAddress

                        mManager?.connect(mChannel, mSelectedDevice, object : ActionListener {
                                override fun onSuccess() {
                                        Toast.makeText(this@WifiMainActivity, "Connection success", 5).show()
                                }

                                override fun onFailure(reason : Int) {
                                        Toast.makeText(this@WifiMainActivity, "Connection failure", 5).show()
                                }
                        })
                }
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

                for(item in mPeers)
                        mArrayAdapter?.add(item.deviceName.toString())
                return
        }
}
