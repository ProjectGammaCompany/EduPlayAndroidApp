package com.eduplay.moblie.useCases

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
            var numBytes: Int

            while (true) {
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("SOCKET", "Input stream was disconnected", e)
                    break
                }

                if (numBytes > 0) {
                    val callBack = listeningSockets[this]
                    if (callBack == null) {
                        return
                    }
                    listeningSockets[this]?.obtainMessage(
                        RECIEVED_SCORE,
                        byteArrayToInt(mmBuffer),
                        0,
                        listeningSockets[this]
                    )
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

    private val listeningSockets: ConcurrentMap<ConnectedThread, Handler> = ConcurrentHashMap()


    fun listenToSocket(socket: BluetoothSocket, handler: Handler) {
        val connectedThread = ConnectedThread(socket)
        listeningSockets[connectedThread] = handler
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
        } catch (e: IOException) {
            Log.e("SEND_TO_SOCKET", "error sending points", e)
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