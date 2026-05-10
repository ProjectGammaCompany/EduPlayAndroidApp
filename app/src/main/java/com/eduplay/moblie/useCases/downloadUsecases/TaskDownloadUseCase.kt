package com.eduplay.moblie.useCases.downloadUsecases

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.repository.webrepository.EventFilesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream


class TaskDownloadUseCase(private val context: Context) {
    private val downloadingFiles: MutableMap<String, Long> = mutableMapOf()
    private val fileUriBase: String = BuildConfig.BACKEND_FILE_URL
    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val fileApi = Retrofit.Builder().baseUrl(BuildConfig.BACKEND_EVENT_FILE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build()).build().create(EventFilesApi::class.java)

    fun download(fileUri: String, fileName: String): Flow<FileDownloadStatus> {
        val request = DownloadManager.Request((fileUriBase + fileUri).toUri())
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(fileName)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val fileId = downloadManager.enqueue(request)
        downloadingFiles.put(fileUri, fileId)
        return flow {
            do {
                val status = getLoadedStatus(fileId)
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        emit(FileDownloadStatus.SUCCESS)
                        return@flow
                    }

                    DownloadManager.STATUS_FAILED -> {
                        emit(FileDownloadStatus.FAILED)
                        return@flow
                    }

                    DownloadManager.STATUS_PAUSED -> emit(FileDownloadStatus.PAUSED)
                    DownloadManager.STATUS_PENDING -> emit(FileDownloadStatus.LOADING)
                    DownloadManager.STATUS_RUNNING -> emit(FileDownloadStatus.LOADING)
                    else -> {
                        FileDownloadStatus.FAILED
                        return@flow
                    }
                }

                delay(1000)
            } while (true)
        }
    }

    fun openFile(fileUri: String, fileName: String) {
        if (!downloadingFiles.contains(fileUri)) throw IllegalAccessException("this file is not being downloaded")
        val fileId = downloadingFiles[fileUri]!!
        if (isDownloaded(fileId)) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val file: Uri = getFileUrl(fileId, fileName)
            intent.setDataAndType(file, context.contentResolver.getType(file))
            context.startActivity(intent)
        }
    }

    fun downloadToAppStorage(fileUri: String, fileName: String, directory: String): String {
        val eventFile: File = File(directory, fileName)
        if (!eventFile.exists()) {
            eventFile.createNewFile()
        }
        val response = fileApi.getRegularFile(fileUri).execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            body.byteStream().use {
                FileOutputStream(eventFile).use { targetOutputStream ->
                    it.copyTo(targetOutputStream)
                }
            }

            Log.d("downloaded", "$eventFile")
        } else {
            Log.d("not downloaded", response.message())
        }
        return eventFile.absolutePath
    }

    private fun getFileUrl(fileId: Long, fileName: String): Uri {
        val query = DownloadManager.Query()
            .setFilterById(fileId)

        val downloadResult = downloadManager.query(query)
        if (downloadResult.moveToFirst()) {
            val status = downloadResult.getStringOrNull(
                downloadResult.getColumnIndexOrThrow(DownloadManager.COLUMN_URI)
            )
            if (status == null) {
                throw IllegalAccessException("no such file $fileId")
            }
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            val contentUri: Uri = getUriForFile(context, "com.eduplay.fileprovider", file)
                ?: throw IllegalAccessException("no such file $fileId")

            return contentUri
        }
        throw IllegalAccessException("no such file $fileId")
    }

    private fun isDownloaded(fileId: Long): Boolean {
        return getLoadedStatus(fileId) == DownloadManager.STATUS_SUCCESSFUL
    }

    private fun getLoadedStatus(fileId: Long): Int {
        val query = DownloadManager.Query()
            .setFilterById(fileId)

        val downloadResult = downloadManager.query(query)
        if (downloadResult.moveToFirst()) {
            val status = downloadResult.getIntOrNull(
                downloadResult.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
            )
            if (status == null) {
                return -1
            }
            return status
        }
        return -1
    }
}