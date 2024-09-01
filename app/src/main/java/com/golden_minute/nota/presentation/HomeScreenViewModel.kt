package com.golden_minute.nota.presentation

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.golden_minute.nota.data.Alarm_manager.AlarmSchedulerImpl
import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.model.Tag
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.use_case.notes.NoteUseCases
import com.golden_minute.nota.domain.use_case.tags.TagsUseCases
import com.golden_minute.nota.domain.use_case.tasks.TaskUseCases
import com.golden_minute.nota.domain.util.HomeScreenNotesEvent
import com.golden_minute.nota.domain.util.HomeScreenTasksEvent
import com.golden_minute.nota.domain.util.NoteOrderType
import com.golden_minute.nota.domain.util.NotesState
import com.golden_minute.nota.domain.util.TaskOrderType
import com.golden_minute.nota.domain.util.TasksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SelectModeEvents {
    data object DeleteTasks : SelectModeEvents()
    data object DeleteNotes : SelectModeEvents()
    data object SelectAllNotes : SelectModeEvents()
    data object SelectAllTasks : SelectModeEvents()
    data object CheckSelectedTasks : SelectModeEvents()
}


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    val tagsUseCases: TagsUseCases,
    app: Application
) : AndroidViewModel(app) {

    var currentNoteOrder: NoteOrderType? = null
    var taskCurrentOrder: TaskOrderType? = null

    private var getNotesJob: Job? = null
    private var getTasksJob: Job? = null
    private var getTagsJob: Job? = null

    private val _taskState = mutableStateOf(TasksState())
    val taskState: State<TasksState> = _taskState

    private val alarmManager = AlarmSchedulerImpl(app)


    private val _noteState = mutableStateOf(NotesState())
    val noteState: State<NotesState> = _noteState

    var tagsList = mutableStateListOf(Tag())

    var selectedNotesList = mutableStateListOf(Note(title = "", description = ""))

    var selectedTasksList = mutableStateListOf<Task>()

    var searchValue by mutableStateOf("")

    private var tagsFromSelectedNotes = ArrayList<Tag>()

    private var tagsFromSelectedTasks = ArrayList<Tag>()


    init {
        selectedNotesList.clear()
        selectedTasksList.clear()

        getTasks(TaskOrderType.Ascending)
        getTags()
    }

    @SuppressLint("SuspiciousIndentation")
    fun onEvent(homeScreenNotesEvent: HomeScreenNotesEvent) {
        when (homeScreenNotesEvent) {

            is HomeScreenNotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(homeScreenNotesEvent.note)
                }
            }

            is HomeScreenNotesEvent.OrderNote -> {
                if (noteState.value.noteOrder::class == homeScreenNotesEvent.noteOrder::class)
                    return

                currentNoteOrder = homeScreenNotesEvent.noteOrder
                getNotes( noteOrderType = homeScreenNotesEvent.noteOrder)
                getTags()
            }

            is HomeScreenNotesEvent.ToggleToolbarActionsVisibility -> {
                _noteState.value =
                    noteState.value.copy(isToolbarActionVisible = !noteState.value.isToolbarActionVisible)
            }

            is HomeScreenNotesEvent.ToggleSearchbarVisibility -> {
                _noteState.value =
                    noteState.value.copy(isSearchbarVisible = !noteState.value.isSearchbarVisible)
            }

            HomeScreenNotesEvent.NoteScreenLoaded -> {
                getNotes(noteOrderType = currentNoteOrder ?: NoteOrderType.Ascending)
                getTags()
            }


            is HomeScreenNotesEvent.SearchValueChanged -> {


                if (homeScreenNotesEvent.input.isNotBlank()) {
                    noteUseCases.searchNote(
                        homeScreenNotesEvent.input,
                        currentNoteOrder ?: NoteOrderType.Ascending
                    ).takeWhile { searchValue.isNotBlank() }.onEach {
                        _noteState.value = noteState.value.copy(
                            notes = it,
                        )
                    }.launchIn(viewModelScope)
                } else
                    getNotes(noteOrderType = NoteOrderType.Descending)


            }

            is HomeScreenNotesEvent.OnTagDialogClick -> getTags()

            is HomeScreenNotesEvent.ToolbarActionClicked -> {

                when (homeScreenNotesEvent.action) {
                    is SelectModeEvents.CheckSelectedTasks -> return
                    is SelectModeEvents.DeleteNotes -> {
                        viewModelScope.launch(Dispatchers.IO) {

                            noteUseCases.deleteNotes(selectedNotesList)

                            selectedNotesList.clear()
                        }
                    }

                    is SelectModeEvents.DeleteTasks -> return
                    SelectModeEvents.SelectAllNotes -> {
                        selectedNotesList.clear()
                        tagsFromSelectedNotes.clear()
                        selectedNotesList.addAll(noteState.value.notes)
                        tagsFromSelectedNotes.addAll(tagsList)
                    }

                    SelectModeEvents.SelectAllTasks -> return
                }
            }

            is HomeScreenNotesEvent.OnSelectionNoteClick -> {
                if (homeScreenNotesEvent.isSelected && selectedNotesList.none { it.id == homeScreenNotesEvent.note.id })
                    selectedNotesList.add(homeScreenNotesEvent.note)
                else
                    selectedNotesList.remove(homeScreenNotesEvent.note)
            }

            is HomeScreenNotesEvent.OnDeleteTagsInDialog -> {
                val tags = arrayListOf<Tag>()
                noteState.value.notes.forEach { note ->
                    homeScreenNotesEvent.selectedTags.forEach{ tag ->
                        if ( note.id == tag.noteID ){
                            if(tagsList.count { it.tagName == tag.tagName } > 1) {
                                tags.addAll(tagsList.filter { it.tagName == tag.tagName })
                            }
                            else tags.add(tag)
                        }
                    }
                }


                viewModelScope.launch {
                    val deletionIsFinished = async(Dispatchers.IO) { tagsUseCases.deleteTags(tags) }

                    launch {
                        if (deletionIsFinished.await() > 0)
                            getNotes( noteOrderType = NoteOrderType.Default)
                    }
                }
            }

            is HomeScreenNotesEvent.OnNotesFilteredByTags -> {

                var filteredNotes = arrayListOf<Note>()
                val normalNotes = arrayListOf<Note>()

                noteState.value.notes.forEach { note ->
                    homeScreenNotesEvent.selectedTags.forEach { tag ->
                        if (note.id == tag.noteID && filteredNotes.none { it.id == note.id }){
                            if(tagsList.count { it.tagName == tag.tagName } > 1) {

                                val noteIds =  tagsList.filter { it.tagName == tag.tagName }.map { it.noteID }
                                noteIds.forEach { duplicateTag ->
                                    noteState.value.notes.find { it.id == duplicateTag }
                                        ?.let { filteredNotes.add(it) }
                                }

                            }
                            else
                                filteredNotes.add(note)
                        }

                        else if (normalNotes.none { it.id == note.id } && filteredNotes.none { it.id == note.id })
                            normalNotes.add(note)
                    }
                }
                filteredNotes = filteredNotes.distinctBy { it.id } as ArrayList<Note>
                normalNotes.removeIf { normNote -> filteredNotes.any { filteredNote -> filteredNote.id == normNote.id } }
                filteredNotes.addAll(normalNotes)
                getNotes(givenNotes = filteredNotes, noteOrderType = NoteOrderType.Default)
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun onEvent(taskEvents: HomeScreenTasksEvent) {
        when (taskEvents) {

            is HomeScreenTasksEvent.HomeScreenTasksOrder -> {
                if (taskState.value.taskOrder::class == taskEvents.taskOrderType::class)
                    return
                taskCurrentOrder = taskEvents.taskOrderType
                getTasks(taskEvents.taskOrderType)
                getTags()
            }

            is HomeScreenTasksEvent.DeleteTask -> {
                viewModelScope.launch {
                    taskUseCases.deleteTask(taskEvents.task)

                    if (taskEvents.task.dueDate?.isNotBlank() == true)
                        alarmManager.cancel(taskEvents.task)
                }
            }

            is HomeScreenTasksEvent.ToggleSearchbarVisibility -> {
                _taskState.value =
                    taskState.value.copy(isSearchBarVisible = !taskState.value.isSearchBarVisible)
            }

            is HomeScreenTasksEvent.ToggleToolbarActions -> {
                _taskState.value =
                    taskState.value.copy(isToolbarActionsVisible = !taskState.value.isToolbarActionsVisible)
            }

            HomeScreenTasksEvent.TaskScreenLoaded -> {
                getTasks(
                    taskCurrentOrder ?: TaskOrderType.Ascending
                )
                getTags() // tags for task list is different than tags for note list , so you should change it
            }

            is HomeScreenTasksEvent.SearchValueChanged -> {


                if (searchValue.isNotBlank()) {
                    taskUseCases.searchTask(
                        taskEvents.input,
                        taskCurrentOrder ?: TaskOrderType.Ascending
                    ).takeWhile { searchValue.isNotBlank() }.onEach {

                        _taskState.value = taskState.value.copy(
                            tasks = it,
                        )
                    }.launchIn(viewModelScope)
                } else {
                    getTasks(TaskOrderType.Ascending)
                }

            }


            is HomeScreenTasksEvent.ToolbarActionClicked -> {
                when (taskEvents.action) {
                    is SelectModeEvents.CheckSelectedTasks -> {

                        val taskList: ArrayList<Task> =
                            taskState.value.tasks.toCollection(ArrayList())
                        taskList.forEach { task ->

                            selectedTasksList.forEach { selectedTask ->
                                task.isChecked = task.id == selectedTask.id
                            }
                        }

                        viewModelScope.launch {
                            _taskState.value = taskState.value.copy(tasks = taskList)
                            getTasks(TaskOrderType.Ascending)
                            taskUseCases.checkSelectedTasks(selectedTasksList.map { it.id!! })
                            selectedTasksList.clear()
                        }

                    }

                    SelectModeEvents.DeleteNotes -> return
                    SelectModeEvents.DeleteTasks -> {
                        viewModelScope.launch(Dispatchers.IO) {

                            taskUseCases.deleteTasks(selectedTasksList)

                            selectedTasksList.clear()
                        }

                    }

                    SelectModeEvents.SelectAllNotes -> return
                    SelectModeEvents.SelectAllTasks -> {
                        selectedTasksList.clear()
                        selectedTasksList.addAll(taskState.value.tasks)
//                        tagsFromSelectedTasks.clear()
//                        tagsFromSelectedTasks.addAll(tagsList)
                    }
                }
            }

            is HomeScreenTasksEvent.OnSelectionTaskClick -> {
                if (taskEvents.isSelected && selectedTasksList.none { it.id == taskEvents.task.id })
                    selectedTasksList.add(taskEvents.task)
                else
                    selectedTasksList.remove(taskEvents.task)
            }

            is HomeScreenTasksEvent.OnCheckChange -> {
                viewModelScope.launch {
                    taskUseCases.addTask(taskEvents.checkedTask)
                }

            }

            is HomeScreenTasksEvent.OnTagDialogClick -> getTags()

            is HomeScreenTasksEvent.OnDeleteTagsInDialog -> {

                val tags = arrayListOf<Tag>()
                taskState.value.tasks.forEach { task ->
                    taskEvents.selectedTags.forEach { tag ->
                       if ( task.id == tag.taskID ){
                           if(tagsList.count { it.tagName == tag.tagName } > 1) {
                               tags.addAll(tagsList.filter { it.tagName == tag.tagName })
                           }
                           else tags.add(tag)
                       }
                    }
                }


                viewModelScope.launch {
                   val deletionIsFinished = async(Dispatchers.IO) { tagsUseCases.deleteTags(tags) }

                launch {
                    if (deletionIsFinished.await() > 0)
                    getTasks(TaskOrderType.Default)
                }
                }

            }

            is HomeScreenTasksEvent.OnTasksFilteredByTags -> {

                var filteredTasks = arrayListOf<Task>()
                val normalTasks = arrayListOf<Task>()

                taskState.value.tasks.forEach { task ->
                    taskEvents.selectedTags.forEach { tag ->
                        if (task.id == tag.taskID && filteredTasks.none { it.id == task.id }){
                            if(tagsList.count { it.tagName == tag.tagName } > 1) {

                              val taskIds =  tagsList.filter { it.tagName == tag.tagName }.map { it.taskID }
                                taskIds.forEach { duplicateTag ->
                                    taskState.value.tasks.find { it.id == duplicateTag }
                                        ?.let { filteredTasks.add(it) }
                                }

                            }
                            else
                            filteredTasks.add(task)
                        }

                        else if (normalTasks.none { it.id == task.id } && filteredTasks.none { it.id == task.id })
                            normalTasks.add(task)
                    }
                }
               filteredTasks = filteredTasks.distinctBy { it.id } as ArrayList<Task>
                normalTasks.removeIf { normTask -> filteredTasks.any { filteredTask -> filteredTask.id == normTask.id } }
                filteredTasks.addAll(normalTasks)
                getTasks(givenTasks = filteredTasks, taskOrderType = TaskOrderType.Default)
            }
        }
    }



    private fun getNotes(
        givenNotes: List<Note> = emptyList(),
        noteOrderType: NoteOrderType
    ) {
        if (givenNotes.isEmpty()){
            getNotesJob?.cancel()
            getNotesJob = noteUseCases.getNotes(noteOrderType).onEach { notes ->
                _noteState.value = noteState.value.copy(
                    notes = notes,
                    noteOrder = noteOrderType,
                    listIsEmpty = noteState.value.notes.isEmpty()
                )
            }.launchIn(viewModelScope)
        }
        else
            _noteState.value = noteState.value.copy(notes = givenNotes, noteOrder = noteOrderType)

    }

    private fun getTasks(
        taskOrderType: TaskOrderType,
        givenTasks:List<Task> = emptyList()
    ) {
        if (givenTasks.isEmpty()){
            getTasksJob?.cancel()
            getTasksJob = taskUseCases.getTasks(taskOrderType).onEach { tasks ->
                _taskState.value = taskState.value.copy(
                    tasks = tasks,
                    taskOrder = taskOrderType,
                    listIsEmpty = taskState.value.tasks.isEmpty()
                )

            }.launchIn(viewModelScope)
        }
        else{
            _taskState.value = taskState.value.copy(tasks = givenTasks, taskOrder = taskOrderType)
        }

    }

    private fun getTags() {

        getTagsJob?.cancel()
        getTagsJob = tagsUseCases.getTags().onEach { tags ->

            tagsList = tags.toMutableStateList()

        }.launchIn(viewModelScope)


    }

}