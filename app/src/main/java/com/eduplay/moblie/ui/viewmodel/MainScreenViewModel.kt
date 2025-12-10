package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class MainScreenViewModel @Inject constructor(val repository: EduRepository) : ViewModel() {
    val events = mutableStateMapOf<Int, List<QuestShortInfo>>()

    fun getEventsByPage(page: Int): List<QuestShortInfo> {
        if (events.keys.contains(page) && !events[page].isNullOrEmpty()) {
            return events[page]!!
        }
        viewModelScope.launch(Dispatchers.IO) {
            events[page] = repository.getEvents(page)
        }
        return listOf()
    }
}