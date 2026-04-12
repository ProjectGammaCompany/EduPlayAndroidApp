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
import com.eduplay.moblie.repository.localrepository.LocalRepository
import com.eduplay.moblie.repository.localrepository.entity.BlockEntity
import com.eduplay.moblie.repository.localrepository.entity.ConditionEntity
import com.eduplay.moblie.repository.localrepository.entity.CorrectAnswerEntity
import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.localrepository.entity.GroupEntity
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.eduplay.moblie.repository.webrepository.EventFilesApi
import com.eduplay.moblie.useCases.downloadTaskTypes.FullEventData
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class EventDownloadService : Service() {

    @Inject
    lateinit var repository: LocalRepository
    @Inject
    lateinit var fileDownloader: TaskDownloadUseCase
    @Inject
    lateinit var downloadStatusKeeper: DownloadStatusObserver



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
            msg.arg1 - service id
            msg.obj: string - id файла события
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
                downloadStatusKeeper.downloadFailed(eventUrl)
                toastFail()
                stopSelf(msg.arg1)
            }
            parseFile(eventFile)
            eventFile.delete()
            downloadStatusKeeper.updateDownloaded(eventUrl)

            // TODO("сделать уведу про скачивание с обновлением состояния") // https://stackoverflow.com/questions/73725629/how-to-legally-prevent-notification-get-removed-in-android
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
        val eventId = intent.getStringExtra("eventId")
        if (eventId != null && eventUrl != null) {
            downloadStatusKeeper.addToDownloading(eventId, eventUrl)
        }
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.obj = eventUrl
            serviceHandler?.sendMessage(msg)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
    }

    private fun parseFile(file: File) {
        val json = file.readText()
        val event = Gson().fromJson<FullEventData>(json, FullEventData::class.java)
        val fileLocations = mutableMapOf<String, String>()
        // downloading external files
        for (file in event.files) {
            val location = fileDownloader.downloadToAppStorage(
                fileUri = file,
                fileName = file,
                directory = this.filesDir.absolutePath
            )
            fileLocations[file] = file
        }
        runBlocking {
            // add event
            val eventEntity = EventEntity(event.event, fileLocations[event.event.cover] ?: "")
            repository.addEvent(eventEntity)

            // add eventBlocks
            for (block in event.blocks) {
                repository.addBlock(BlockEntity(block))
            }

            //add conditions
            for (condition in event.conditions) {
                repository.addCondition(ConditionEntity(condition))
            }

            //add groups
            for (group in event.groups) {
                repository.addGroup(GroupEntity(group))
            }

            //add tasks
            for (task in event.tasks) {
                val files = task.files.map { fileLocations[it] ?: "" }.toList()
                repository.addTask(TaskEntity(task, files))
            }

            for (option in event.options) {
                repository.addOption(OptionEntity(option))
            }

            for (answer in event.correctAnswers) {
                for (value in answer.values) {
                    repository.addAnswer(CorrectAnswerEntity(answer.taskId, value))
                }
            }
        }

    }
}