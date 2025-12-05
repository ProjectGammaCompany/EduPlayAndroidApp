package com.eduplay.moblie.repository

import com.eduplay.moblie.repository.webrepository.WebRepository
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val webRepository: WebRepository
): Repository {
}