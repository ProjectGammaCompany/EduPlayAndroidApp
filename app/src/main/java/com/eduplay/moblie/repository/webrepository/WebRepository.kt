package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.Repository
import javax.inject.Inject

class WebRepository @Inject constructor(private val api: WebApi): Repository {
}