package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(val repository: EduRepository) : ViewModel() {
    val getImageList = repository.getEvents().cachedIn(viewModelScope)
}