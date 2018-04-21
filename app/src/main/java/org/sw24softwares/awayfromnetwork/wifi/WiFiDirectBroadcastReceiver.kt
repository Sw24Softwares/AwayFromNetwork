package org.sw24softwares.awayfromnetwork

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel

class WiFiDirectBroadcastReceiver(manager : WifiP2pManager?, channel : Channel?, activity : WifiMainActivity?) : BroadcastReceiver() {
        var mManager : WifiP2pManager? = null
        var mChannel : Channel? = null
        var mActivity : WifiMainActivity? = null

        init {
                //super()
                this.mManager = manager
                this.mChannel = channel
                this.mActivity = activity
        }

        override fun onReceive(context : Context, intent : Intent) {
                val action = intent.getAction()
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                        // Determine if Wifi P2P mode is enabled or not, alert
                        // the Activity.
                        //val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

                        //if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) activity.setIsWifiP2pEnabled(true)
                        //else activity.setIsWifiP2pEnabled(false)

                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                        // The peer list has changed! We should probably do something about
                        // that.

                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                        // Connection state changed! We should probably do something about
                        // that.

                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                        //val fragment = activity.getFragmentManager().findFragmentById(R.id.frag_list) as DeviceListFragment
                        //fragment.updateThisDevice(intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice)
                }
        }
}
