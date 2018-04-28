package org.sw24softwares.awayfromnetwork

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList

import android.util.Log

class WiFiDirectBroadcastReceiver(manager : WifiP2pManager?, channel : Channel?, peerListListener : PeerListListener?) : BroadcastReceiver() {
        var mManager : WifiP2pManager? = null
        var mChannel : Channel? = null
        var mPeerListListener : PeerListListener? = null

        init {
                this.mManager = manager
                this.mChannel = channel
                this.mPeerListListener = peerListListener
        }

        override fun onReceive(context : Context, intent : Intent) {
                val action = intent.getAction()

                Log.d("p2p", "Action : " + action)

                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

                        // Enables Wifi when Wifi P2P is disabled
                        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        if (!(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)) {
                                Toast.makeText(context, context.getString(R.string.activating_wifi), Toast.LENGTH_SHORT).show()
                                val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                                wifi.setWifiEnabled(true)
                        }

                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                        // Request available peers from the wifi p2p manager. This is an
                        // asynchronous call and the calling activity is notified with a
                        // callback on PeerListListener.onPeersAvailable()
                        mManager?.requestPeers(mChannel, mPeerListListener);

                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                        // The state of Wi-Fi P2P connectivity has changed.

                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

                        // Device's details have changed.

                }
        }
}
