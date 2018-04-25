package org.sw24softwares.awayfromnetwork

import java.lang.Thread
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException
import java.util.UUID

import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothServerSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message

class  BluetoothInteraction : Communication() {
        companion object {
                const val NAME = "AwayFromNetwork"
                const val TAG = "AwayFromNetwork"
                val APP_UUID = UUID.fromString("91bc109b-32ea-4acd-9ff5-103c94ca6742")
        }
        private var mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        private var mAcceptThread : AcceptThread? = null
        private var mConnectThread : ConnectThread? = null
        private var mConnectedThread : ConnectedThread? = null

        fun listen() {
                mAcceptThread = AcceptThread()
                mAcceptThread?.start()
        }
        fun connect(device : BluetoothDevice) {
                mConnectThread = ConnectThread(device)
                mConnectThread?.start()
        }
        fun connected(socket : BluetoothSocket, device : BluetoothDevice) {
                mConnectedThread = ConnectedThread(socket)
                mConnectedThread?.start()

                Communication.registerCommunication(this)
                mDeviceName = device.getName()
                
                val msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME)
                val bundle = Bundle()
                bundle.putString(Constants.DEVICE_NAME, device.getName())
                msg.setData(bundle)
                mHandler.sendMessage(msg)
        }

        override fun write(bytes : ByteArray) {
                var r : ConnectedThread? = null
                // Synchronize a copy of the ConnectedThread
                synchronized (this) {
                        if (mConnectedThread == null) return
                        r = mConnectedThread
                }
                // Perform the write unsynchronized
                r?.write(bytes);
        }
        
        // ---------------------------------------------------------------------
        private inner class AcceptThread : Thread() {
                private val mmServerSocket : BluetoothServerSocket?

                init {
                        // Use a temporary object that is later assigned to mmServerSocket
                        // because mmServerSocket is final.
                        var tmp : BluetoothServerSocket? = null
                        try {
                                // APP_UUID is the app's UUID string, also used by the client code.
                                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, APP_UUID)
                        } catch (e : IOException) {
                                Log.e(TAG, "Socket's listen() method failed", e)
                        }
                        mmServerSocket = tmp
                }

                public override fun run() {
                        var socket : BluetoothSocket?
                        // Keep listening until exception occurs or a socket is returned.
                        while (true) {
                                try {
                                        socket = mmServerSocket?.accept()
                                } catch (e : IOException) {
                                        Log.e(TAG, "Socket's accept() method failed", e);
                                        break;
                                }

                                if (socket != null) {
                                        // A connection was accepted. Perform work associated with
                                        // the connection in a separate thread.
                                        connected(socket, socket.getRemoteDevice())
                                        mmServerSocket?.close()
                                        break;
                                }
                        }
                }

                // Closes the connect socket and causes the thread to finish.
                public fun cancel() {
                        try {
                                mmServerSocket?.close();
                        } catch (e : IOException) {
                                Log.e(TAG, "Could not close the connect socket", e);
                        }
                }
        }
        //----------------------------------------------------------------------
        private inner class ConnectThread (device : BluetoothDevice)  : Thread() {
                private val mmSocket : BluetoothSocket?
                private val mmDevice : BluetoothDevice = device

                init {
                        // Use a temporary object that is later assigned to mmSocket
                        // because mmSocket is final.
                        var tmp : BluetoothSocket? = null

                        try {
                                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                                // APP_UUID is the app's UUID string, also used in the server code.
                                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
                        } catch (e : IOException) {
                                Log.e(TAG, "Socket's create() method failed", e);
                        }
                        mmSocket = tmp;
                }

                override fun run() {
                        // Cancel discovery because it otherwise slows down the connection.
                        mBluetoothAdapter.cancelDiscovery();

                        try {
                                // Connect to the remote device through the socket. This call blocks
                                // until it succeeds or throws an exception.
                                mmSocket?.connect();
                        } catch (connectException : IOException) {
                                // Unable to connect; close the socket and return.
                                try {
                                        mmSocket?.close();
                                } catch (closeException : IOException) {
                                        Log.e(TAG, "Could not close the client socket", closeException);
                                }
                                return;
                        }

                        // The connection attempt succeeded. Perform work associated with
                        // the connection in a separate thread.
                        if(mmSocket != null) connected(mmSocket, mmSocket.getRemoteDevice())
                }

                // Closes the client socket and causes the thread to finish.
                fun cancel() {
                        try {
                                mmSocket?.close();
                        } catch (e : IOException) {
                                Log.e(TAG, "Could not close the client socket", e);
                        }
                }
        }
        //----------------------------------------------------------------------
        private inner class ConnectedThread (socket : BluetoothSocket) : Thread() {
                private val mmSocket : BluetoothSocket = socket
                private val mmInStream : InputStream?
                private val mmOutStream : OutputStream?
                private var mmBuffer : ByteArray = ByteArray(1024) // mmBuffer store for the stream

                init {
                        var tmpIn : InputStream? = null
                        var tmpOut : OutputStream? = null

                        // Get the input and output streams; using temp objects because
                        // member streams are final.
                        try {
                                tmpIn = socket.getInputStream()
                        } catch (e : IOException) {
                                Log.e(TAG, "Error occurred when creating input stream", e)
                        }
                        try {
                                tmpOut = socket.getOutputStream();
                        } catch (e : IOException) {
                                Log.e(TAG, "Error occurred when creating output stream", e)
                        }

                        mmInStream = tmpIn
                        mmOutStream = tmpOut
                }

                override fun run() {
                        var numBytes : Int // bytes returned from read()

                        // Keep listening to the InputStream until an exception occurs.
                        while (true) {
                                try {
                                        // Read from the InputStream.
                                        numBytes = mmInStream?.read(mmBuffer) ?: 0
                                        // Send the obtained bytes to the UI activity.
                                        val readMsg : Message = mHandler.obtainMessage(MessageConstants.MESSAGE_READ.flag, numBytes, -1 ,mmBuffer)
                                        readMsg.sendToTarget()
                                } catch (e : IOException ) {
                                        Log.d(TAG, "Input stream was disconnected", e)
                                        break;
                                }
                        }
                }
                
                // Call this from the main activity to send data to the remote device.
                fun write(bytes : ByteArray) {
                        try {
                                mmOutStream?.write(bytes)

                                // Share the sent message with the UI activity.
                                val writtenMsg : Message = mHandler.obtainMessage(MessageConstants.MESSAGE_WRITE.flag, -1, -1, mmBuffer)
                                writtenMsg.sendToTarget()
                        } catch (e : IOException) {
                                Log.e(TAG, "Error occurred when sending data", e);
                                
                                // Send a failure message back to the activity.
                                val writeErrorMsg : Message = mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST.flag)
                                val bundle : Bundle = Bundle();
                                bundle.putString("toast",
                                "Couldn't send data to the other device");
                                writeErrorMsg.setData(bundle);
                                mHandler.sendMessage(writeErrorMsg);
                        }
                }
                
                // Call this method from the main activity to shut down the connection.
                fun cancel() {
                        try {
                                mmSocket.close()
                        } catch (e : IOException) {
                                Log.e(TAG, "Could not close the connect socket", e)
                        }
                }
        }
        //----------------------------------------------------------------------
}
