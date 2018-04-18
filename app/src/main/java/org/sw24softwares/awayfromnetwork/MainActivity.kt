package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                val button = findViewById(R.id.main_bluetooth) as Button
                button.setOnClickListener {
                        val intent = Intent(this, BluetoothMainActivity::class.java)
                        startActivity(intent)
                }
        }
}
