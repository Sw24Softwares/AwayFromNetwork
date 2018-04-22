package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message

import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ArrayAdapter

class MessageActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_message)

                val listview = findViewById(R.id.message_listview) as ListView
                val adapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1);
                listview.setAdapter(adapter);
                
                val communication = Communication.mRegisteredCommunications.get(Communication.mRegisteredCommunications.size-1)
                communication.setHandler(object : Handler() {
                        override fun handleMessage(msg : Message) {
                                when (msg.what) {
                                        BluetoothInteraction.MessageConstants.MESSAGE_READ.flag -> {
                                                val readBuf = msg.obj as ByteArray
                                                val readMessage = String(readBuf, 0, msg.arg1)
                                                adapter.add(readMessage)
                                        }
                                }
                        }
                })

                val text = findViewById(R.id.message_text) as EditText
                val send_image = findViewById(R.id.message_image_send) as ImageView
                send_image.setOnClickListener {
                        if (text.getText().toString().length > 0) {
                                val send = text.getText().toString().toByteArray()
                                communication.write(send)
                                text.setText("")
                        }
                }
        }
}
