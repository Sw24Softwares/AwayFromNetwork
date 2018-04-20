package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                val button_bt = findViewById(R.id.main_bluetooth) as Button
                button_bt.setOnClickListener {
                        val intent = Intent(this, BluetoothMainActivity::class.java)
                        startActivity(intent)
                }

                val button_wf = findViewById(R.id.main_wifi) as Button
                button_wf.setOnClickListener {
                        val intent = Intent(this, WifiMainActivity::class.java)
                        startActivity(intent)
                }
        }
}
