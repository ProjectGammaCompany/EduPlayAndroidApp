package com.eduplay.moblie.useCases

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import android.widget.Toast
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.R
import com.eduplay.moblie.repository.webrepository.EventFilesApi
import jakarta.inject.Inject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class EventDownloadService @Inject constructor() : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val eventFilesApi: EventFilesApi =
        Retrofit.Builder().baseUrl(BuildConfig.BACKEND_EVENT_FILE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build()).build().create(EventFilesApi::class.java)
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        /*
            msg.arg1: string - id файла события
         */
        override fun handleMessage(msg: Message) {
            val eventUrl = msg.obj.toString()

            val eventFile = File.createTempFile(eventUrl, ".json")

            val response = eventFilesApi.getEventFile(eventUrl).execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                body.byteStream().use {
                    FileOutputStream(eventFile).use { targetOutputStream ->
                        it.copyTo(targetOutputStream)
                    }
                }
                Log.d("downloaded", "${eventFile.length()}")
            } else {
                Log.d("not downloaded", response.message())
                toastFail()
                stopSelf(msg.arg1)
            }



            eventFile.delete()
            // stream everything to eventFile https://stackoverflow.com/questions/32878478/how-to-download-file-in-android-using-retrofit-library
            // send everything to db
            // TODO("сделать запросы к бд")
            // TODO("сделать уведу про скачивание с обновлением состояния") // https://stackoverflow.com/questions/73725629/how-to-legally-prevent-notification-get-removed-in-android

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1)
        }
    }

    private fun toastFail() {
        Toast.makeText(this, resources.getString(R.string.try_again_later), Toast.LENGTH_LONG)
            .show()
    }

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val eventUrl = intent.getStringExtra("eventUrl")
        Toast.makeText(this, "start download $eventUrl", Toast.LENGTH_SHORT).show()

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.obj = eventUrl
            serviceHandler?.sendMessage(msg)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }
}