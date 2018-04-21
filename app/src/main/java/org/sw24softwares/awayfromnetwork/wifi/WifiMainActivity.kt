package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.content.IntentFilter
import android.content.Context
import android.widget.Button
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.util.Log

class WifiMainActivity : AppCompatActivity() {
        var mIntentFilter = IntentFilter()
        var mChannel : Channel? = null
        var mManager : WifiP2pManager? = null
        var mReceiver : WiFiDirectBroadcastReceiver? = null

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_wifi_main)

                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

                mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
                mChannel = mManager?.initialize(this, getMainLooper(), null)

                val research_button = findViewById(R.id.wifi_research) as Button
                research_button.setOnClickListener {
                        val action = object : WifiP2pManager.ActionListener {
                                override fun onSuccess() {
                                        Log.d("p2p", "discoverPeers() Success")
                                }

                                override fun onFailure(reason : Int) {
                                        Log.d("p2p", "discoverPeers() Failure: " + reason)
                                }
                        }
                        mManager?.discoverPeers(mChannel, action)
                }
        }

        override fun onResume() {
                super.onResume()
                mReceiver = WiFiDirectBroadcastReceiver(mManager, mChannel, this)
                registerReceiver(mReceiver, mIntentFilter)
        }

        override fun onPause() {
                super.onPause()
                unregisterReceiver(mReceiver)
        }
}
