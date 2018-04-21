package org.sw24softwares.awayfromnetwork

import android.os.Handler

open class Communication {
        companion object {
                var mRegisteredCommunications : MutableList<Communication> = mutableListOf()
                fun registerCommunication(com : Communication) {
                        mRegisteredCommunications.add(com)
                }
        }
        
        protected var mHandler = Handler()
        fun setHandler(handler : Handler) {
                mHandler = handler
        }
        fun getHandler() : Handler {
                return mHandler
        }

        open fun write (bytes : ByteArray) {
        }
}
