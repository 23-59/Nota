package com.golden_minute.nota.domain.use_case.tasks

/**
 * this class is for wrap use cases into one class for cleaner access when get called in viewModel parameter
 */
data class TaskUseCases(
    val getTasks: GetTasks,
    val deleteTask: DeleteTask,
    val deleteTasks: DeleteTasks,
    val updateTasks: UpdateTasks,
    val updateTask: UpdateTask,
    val checkSelectedTasks: CheckSelectedTasks,
    val addTask: AddTask,
    val getTask: GetTask,
    val searchTask: SearchTask
)
