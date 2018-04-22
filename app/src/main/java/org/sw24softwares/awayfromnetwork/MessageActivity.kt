package org.sw24softwares.awayfromnetwork

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.ArrayAdapter
import android.content.res.Configuration

class MessageUI (s : String, m : String) {
        val sender = s
        val message = m
}
class MessageAdapter (context : Context, msg : ArrayList<MessageUI>) : ArrayAdapter<MessageUI>(context, 0, msg) {
        override fun getView(position : Int, convertView_ : View?, parent : ViewGroup) : View {
                var convertView = convertView_
                val user : MessageUI = getItem(position)
                if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false)
                }
                val sender = convertView?.findViewById(R.id.sender) as TextView
                val msg = convertView.findViewById(R.id.message) as TextView
                sender.setText(user.sender);
                msg.setText(user.message);
                return convertView;
        }
}

class MessageActivity : AppCompatActivity() {
        var mAdapter : MessageAdapter? = null
        var mMessages = mutableListOf<MessageUI>()
        
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_message)

                mAdapter = MessageAdapter(this, ArrayList<MessageUI>())
                val listview = findViewById(R.id.message_listview) as ListView 
                listview.setAdapter(mAdapter);
                
                val communication = Communication.mRegisteredCommunications.get(Communication.mRegisteredCommunications.size-1)
                communication.setHandler(object : Handler() {
                        override fun handleMessage(msg : Message) {
                                when (msg.what) {
                                        MessageConstants.MESSAGE_READ.flag -> {
                                                val readBuf = msg.obj as ByteArray
                                                val readMessage = String(readBuf, 0, msg.arg1)
                                                mAdapter?.add(MessageUI(communication.mDeviceName,readMessage))
                                        }
                                }
                        }
                })

                val text = findViewById(R.id.message_text) as EditText
                val send_image = findViewById(R.id.message_image_send) as ImageView
                send_image.setOnClickListener {
                        if (text.getText().toString().length > 0) {
                                val txt = text.getText().toString()
                                mAdapter?.add(MessageUI("You",txt))
                                val send = txt.toByteArray()
                                communication.write(send)
                                text.setText("")
                        }
                }
        }
}
