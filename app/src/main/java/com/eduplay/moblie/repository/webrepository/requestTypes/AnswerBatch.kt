package com.eduplay.moblie.repository.webrepository.requestTypes

data class AnswerBatch(
    val userId: String,
    val answers: List<Answer>,
    val totalPoints: Int, // рассчет баллов
    val currentBlock: String, // предварительно тот блок в котором был дан последний ответ
    val currentTask: String, // предварительно последнее задание на которое был дан ответ
    val isDone: Boolean // событие завершено или нет
) {
    data class Answer(val taskId: String, val options: List<String>)
}
