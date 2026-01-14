package com.eduplay.moblie.ui.elements

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.eduplay.moblie.R

@Composable
fun NoInternetConnectionToast() {
    val context = LocalContext.current
    Toast.makeText(context, stringResource(R.string.no_internet), Toast.LENGTH_LONG).show()
}