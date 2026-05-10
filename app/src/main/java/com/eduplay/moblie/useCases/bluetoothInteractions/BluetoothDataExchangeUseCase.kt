package com.eduplay.moblie.useCases.bluetoothInteractions

import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class BluetoothDataExchangeUseCase() {
    private val RECIEVED_SCORE = 1001

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            Log.e("SEND_TO_BLUETOOTH_SOCKET", "listening")
            var numBytes: Int

            while (true) {
                numBytes = try {
                    mmInStream.read(mmBuffer)
                    Log.e("SEND_TO_BLUETOOTH_SOCKET", "read smth")
                } catch (e: IOException) {
                    Log.i("SOCKET", "Input stream was disconnected", e)
                    break
                }

                if (numBytes > 0) {
                    val callBack = handlers[this]
                    if (callBack == null) {
                        Log.e("SEND_TO_BLUETOOTH_SOCKET", "no callback")
                        return
                    }
                    Log.e("SEND_TO_BLUETOOTH_SOCKET", "${byteArrayToInt(mmBuffer)}")
                    val message = callBack.obtainMessage()
                    message.what = RECIEVED_SCORE
                    message.arg1 = byteArrayToInt(mmBuffer)
                    message.arg2 = 0
                    message.obj = sockets[this]
                    callBack.sendMessage(message)

                }
            }
        }
    }

    private fun byteArrayToInt(buffer: ByteArray): Int {
        return (buffer[3].toInt() shl 24) or
                (buffer[2].toInt() and 0xff shl 16) or
                (buffer[1].toInt() and 0xff shl 8) or
                (buffer[0].toInt() and 0xff)
    }

    private val handlers: ConcurrentMap<ConnectedThread, Handler> = ConcurrentHashMap()
    private val sockets: ConcurrentMap<ConnectedThread, BluetoothSocket> = ConcurrentHashMap()


    fun listenToSocket(socket: BluetoothSocket, handler: Handler) {
        val connectedThread = ConnectedThread(socket)
        handlers[connectedThread] = handler
        sockets[connectedThread] = socket
        connectedThread.start()
    }

    fun writePointsToSocket(points: Int, socket: BluetoothSocket) {
        val outStream: OutputStream = socket.outputStream
        val bytes = mutableListOf<Byte>()
        bytes.add((points shr 0).toByte())
        bytes.add((points shr 8).toByte())
        bytes.add((points shr 16).toByte())
        bytes.add((points shr 24).toByte())
        try {
            outStream.write(bytes.toByteArray())
            Log.e("SEND_TO_BLUETOOTH_SOCKET", "${bytes.first()} $points")
        } catch (e: IOException) {
            Log.e("SEND_TO_BLUETOOTH_SOCKET", "error sending points", e)
            return
        }
    }

    fun cancel(socket: BluetoothSocket) {
        try {
            socket.close()
        } catch (e: IOException) {
            Log.e("CLOSE_SOCKET", "could not close socket", e)
        }
    }

}