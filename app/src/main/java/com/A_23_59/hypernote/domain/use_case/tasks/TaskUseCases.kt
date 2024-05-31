package com.A_23_59.hypernote.domain.use_case.tasks

/**
 * this class is for wrap use cases into one class for cleaner access when get called in parameter
 */
data class TaskUseCases(
    val getTasks: GetTasks,
    val deleteTask: DeleteTask,
    val addTask: AddTask,
    val getTask: GetTask,
    val searchTask: SearchTask
)
