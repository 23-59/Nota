package com.A_23_59.hypernote.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Tag
import com.A_23_59.hypernote.domain.use_case.notes.NoteUseCases
import com.A_23_59.hypernote.domain.use_case.tags.TagsUseCases
import com.A_23_59.hypernote.domain.use_case.tasks.TaskUseCases
import com.A_23_59.hypernote.domain.util.HomeScreenNotesEvent
import com.A_23_59.hypernote.domain.util.HomeScreenTasksEvent
import com.A_23_59.hypernote.domain.util.NoteOrderType
import com.A_23_59.hypernote.domain.util.NotesState
import com.A_23_59.hypernote.domain.util.TaskOrderType
import com.A_23_59.hypernote.domain.util.TasksState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    private val tagsUseCases: TagsUseCases
) : ViewModel() {

    var currentNoteOrder: NoteOrderType? = null
    var taskCurrentOrder: TaskOrderType? = null

    private var getNotesJob: Job? = null
    private var getTasksJob: Job? = null
    private var getTagsJob: Job? = null

    private val _taskState = mutableStateOf(TasksState())
    val taskState: State<TasksState> = _taskState

    private val _noteState = mutableStateOf(NotesState())
    val noteState: State<NotesState> = _noteState

    private var _distinctNoteTagsList = mutableStateListOf<String>()
    val distinctNoteTagsList: List<String> = _distinctNoteTagsList

    private var _distinctTaskTagsList = mutableStateListOf<String>()
    val distinctTaskTagsList: List<String> = _distinctNoteTagsList

    private var _notesTagsList = mutableStateListOf(Tag())
    val notesTagsList: List<Tag> = _notesTagsList

    private var _taskTagsList = mutableStateListOf(Tag())
    val tasksTagsList: List<Tag> = _taskTagsList


    private fun createDistinctNoteTagList() {
        _distinctNoteTagsList = notesTagsList.distinctBy { it.tagName }
            .filterIsInstance<String>() as SnapshotStateList<String>
    }

    private fun createDistinctTagsTaskList() {
        _distinctTaskTagsList = tasksTagsList.distinctBy { it.tagName }
            .filterIsInstance<String>() as SnapshotStateList<String>
    }

    init {
        getTasks(TaskOrderType.Ascending)

        viewModelScope.launch {
            if (tagsUseCases.getTags().first().isEmpty()){
                tagsUseCases.getTags().onEach {
                    for (i in noteState.value.notes.indices) {
                        noteState.value.notes[i].tagNumber1?.let { Tag(tagName = it) }
                            ?.let { _notesTagsList.add(it) }
                        noteState.value.notes[i].tagNumber2?.let { Tag(tagName = it) }
                            ?.let { _notesTagsList.add(it) }
                        noteState.value.notes[i].tagNumber3?.let { Tag(tagName = it) }
                            ?.let { _notesTagsList.add(it) }
                    }
                }.launchIn(viewModelScope)
            }
            else  tagsUseCases.getTags().onEach { tags ->
                _notesTagsList = tags.toMutableStateList()
            }.launchIn(viewModelScope)
        }

        if (currentPage == 0 && tasksTagsList.isNotEmpty())
            createDistinctTagsTaskList()
        else if (currentPage == 1 && notesTagsList.isNotEmpty())
            createDistinctNoteTagList()
    }

    fun onEvent(homeScreenNotesEvent: HomeScreenNotesEvent) {
        when (homeScreenNotesEvent) {

            is HomeScreenNotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(homeScreenNotesEvent.note)
                    deleteTags(homeScreenNotesEvent.note)

                }
            }

            is HomeScreenNotesEvent.OrderNote -> {
                if (noteState.value.noteOrder::class == homeScreenNotesEvent.noteOrder::class)
                    return

                currentNoteOrder = homeScreenNotesEvent.noteOrder
                getNotes(homeScreenNotesEvent.noteOrder)
                createDistinctNoteTagList()

            }

            is HomeScreenNotesEvent.ToggleToolbarActions -> {
                _noteState.value =
                    noteState.value.copy(isToolbarActionVisible = !noteState.value.isToolbarActionVisible)
            }

            is HomeScreenNotesEvent.ToggleSearchbarVisibility -> {
                _noteState.value =
                    noteState.value.copy(isSearchbarVisible = !noteState.value.isSearchbarVisible)
            }

            HomeScreenNotesEvent.NoteScreenLoaded -> {
                getNotes(currentNoteOrder ?: NoteOrderType.Ascending)
                createDistinctNoteTagList()
            }


            is HomeScreenNotesEvent.SearchValueChanged -> {

                if (homeScreenNotesEvent.input.isNotBlank()) {
                    noteUseCases.searchNote(
                        homeScreenNotesEvent.input,
                        currentNoteOrder ?: NoteOrderType.Ascending
                    ).onEach {
                        _noteState.value = noteState.value.copy(
                            notes = it,
                            searchBarValue = homeScreenNotesEvent.input
                        )
                    }.launchIn(viewModelScope)
                } else
                    noteUseCases.getNotes(currentNoteOrder ?: NoteOrderType.Ascending)
                        .onEach {
                            _noteState.value =
                                noteState.value.copy(
                                    notes = it,
                                    searchBarValue = homeScreenNotesEvent.input
                                )
                        }.launchIn(viewModelScope)


            }

            HomeScreenNotesEvent.ClearSearchValue -> {
                getNotes(NoteOrderType.Ascending)
                _noteState.value = noteState.value.copy(searchBarValue = "")
            }

            is HomeScreenNotesEvent.OnTagDialogClick -> {
                getTagsJob?.cancel()
                getTasksJob = tagsUseCases.getTags().onEach {tags ->
                    _noteState.value = noteState.value.copy(notesTagsList = tags)
                }.launchIn(viewModelScope)
            }
        }
    }

    fun onEvent(taskEvents: HomeScreenTasksEvent) {
        when (taskEvents) {

            is HomeScreenTasksEvent.HomeScreenTasksOrder -> {
                if (taskState.value.taskOrder::class == taskEvents.taskOrderType::class)
                    return
                taskCurrentOrder = taskEvents.taskOrderType
                getTasks(taskEvents.taskOrderType)
            }

            is HomeScreenTasksEvent.DeleteTask -> {
                viewModelScope.launch {
                    taskUseCases.deleteTask(taskEvents.task)
//                    deleteTags(taskEvents.task)
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
                createDistinctTagsTaskList()
            }

            is HomeScreenTasksEvent.SearchValueChanged -> if (taskEvents.input.isNotBlank()) {
                taskUseCases.searchTask(
                    taskEvents.input,
                    taskCurrentOrder ?: TaskOrderType.Ascending
                ).onEach {
                    _taskState.value = taskState.value.copy(tasks = it)
                }.launchIn(viewModelScope)
            } else {
                getTasks(taskCurrentOrder ?: TaskOrderType.Ascending)
                createDistinctTagsTaskList()
            }
        }
    }

    private fun deleteTags(note: Note) {

//        notesTagsList.forEach { tag ->
//            if (notesTagsList.count {
//                note.tagNumber1 == tag.tagName
//            } == 1) {
//                notesTagsList.remove(tag)
//            }
//            if (notesTagsList.count { note.tagNumber2 == tag.tagName } == 1) {
//                notesTagsList.remove(tag)
//            }
//            if (notesTagsList.count { note.tagNumber3 == tag.tagName } == 1) {
//                notesTagsList.remove(tag)
//            }
//        }
//            notesTagsList.find { it.tagName == note.tagNumber1 }?.let {
//                notesTagsList.remove(it)
//            }
//            notesTagsList.find { it.tagName == note.tagNumber2 }?.let {
//                notesTagsList.remove(it)
//            }
//            notesTagsList.find { it.tagName == note.tagNumber3 }?.let {
//                notesTagsList.remove(it)
//            }


    }

//    private fun deleteTags(task: Task) {
//        taskTagsList.find { it.tagName == task.tagNumber1 }?.let {
//            _taskTagsList.remove(it)
//        }
//        taskTagsList.find { it.tagName == task.tagNumber2 }?.let {
//            _taskTagsList.remove(it)
//        }
//        taskTagsList.find { it.tagName == task.tagNumber3 }?.let {
//            _taskTagsList.remove(it)
//        }
//    }

    private fun getNotes(noteOrderType: NoteOrderType) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrderType).onEach { notes ->
            _noteState.value = noteState.value.copy(
                notes = notes,
                noteOrder = noteOrderType,
                listIsEmpty = noteState.value.notes.isEmpty()
            )
        }.launchIn(viewModelScope)
    }


    private fun getTasks(taskOrderType: TaskOrderType) {
        getTasksJob?.cancel()
        getTasksJob = taskUseCases.getTasks(taskOrderType).onEach { tasks ->
            _taskState.value = taskState.value.copy(
                tasks = tasks,
                taskOrder = taskOrderType,
                listIsEmpty = taskState.value.tasks.isEmpty()
            )

        }.launchIn(viewModelScope)
    }
}