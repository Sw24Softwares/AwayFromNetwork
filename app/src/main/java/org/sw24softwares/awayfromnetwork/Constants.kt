package org.sw24softwares.awayfromnetwork

class Constants {
        companion object {
                const val MESSAGE_DEVICE_NAME = 1                
                const val DEVICE_NAME = "device_name"
        }
}

enum class MessageConstants(val flag : Int) {
        MESSAGE_READ(0),
        MESSAGE_WRITE(1),
        MESSAGE_TOAST(2)
}
