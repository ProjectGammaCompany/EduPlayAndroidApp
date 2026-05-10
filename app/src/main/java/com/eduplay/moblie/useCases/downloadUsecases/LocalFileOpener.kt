package com.eduplay.moblie.useCases.downloadUsecases

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object LocalFileOpener {
    fun openFile(fileId: String, context: Context) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val file = File(context.filesDir, fileId)
        val uri = FileProvider.getUriForFile(context, "com.eduplay.fileprovider", file)
        intent.setDataAndType(uri, context.contentResolver.getType(uri))
        context.startActivity(intent)
    }
}