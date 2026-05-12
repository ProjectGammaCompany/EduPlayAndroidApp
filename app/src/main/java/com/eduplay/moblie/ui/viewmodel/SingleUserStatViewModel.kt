package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.SingleUserStat
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SingleUserStatViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    private val displayOptions = MutableStateFlow(mapOf<String, List<DisplayOption>>())
    val options = displayOptions.asStateFlow()

    private val displayBlocks = MutableStateFlow(SingleUserStat(listOf()))
    val blocks = displayBlocks.asStateFlow()

    fun getStat(eventId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getEventEditorStatsPerUser(eventId, userId)
                displayBlocks.value = result
                val taskOptions = mutableMapOf<String,  List<DisplayOption>>()
                for (block in result.blocks) {
                    for (task in block.tasks) {
                        if (task.id.isBlank()) continue
                        val answerOptions = mutableListOf<DisplayOption>()
                        val userChoices = task.userAnswers?.toSet() ?: setOf()
                        for (option in task.options) {
                            answerOptions.add(
                                DisplayOption(
                                    value = option.value,
                                    isChosen = userChoices.contains(option.id),
                                    isCorrect = option.isCorrect
                                )
                            )
                        }
                        taskOptions[task.id] = answerOptions
                    }
                }
                displayOptions.value = taskOptions

            } catch (e: Exception) {
                Log.e("UserStat", e.message ?: "", e)
            }
        }
    }

    data class DisplayOption(
        val value: String,
        val isChosen: Boolean,
        val isCorrect: Boolean
    )
}