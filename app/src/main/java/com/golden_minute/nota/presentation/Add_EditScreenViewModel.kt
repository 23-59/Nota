package com.golden_minute.nota.presentation

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.golden_minute.nota.R
import com.golden_minute.nota.data.Alarm_manager.AlarmSchedulerImpl
import com.golden_minute.nota.domain.model.InvalidItemException
import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.model.Tag
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.use_case.notes.NoteUseCases
import com.golden_minute.nota.domain.use_case.tags.TagsUseCases
import com.golden_minute.nota.domain.use_case.tasks.TaskUseCases
import com.golden_minute.nota.domain.util.Add_Edit_Events
import com.golden_minute.nota.domain.util.Add_Edit_State
import com.golden_minute.nota.domain.util.ChooseDateDialogState
import com.golden_minute.nota.domain.util.ChooseDateTimeDialogEvents
import com.golden_minute.nota.domain.util.compareDateTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class Add_EditScreenViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    private val tagsUseCases: TagsUseCases,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val _state = mutableStateOf(Add_Edit_State())
    val state: State<Add_Edit_State> = _state

    private val _dialogState = mutableStateOf(ChooseDateDialogState())
    val dialogState: State<ChooseDateDialogState> = _dialogState

    private val alarmManager = AlarmSchedulerImpl(application)

    private var _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    sealed class UiEvent {
        data class ShowSnackBar(val snackBarTitle: String) : UiEvent()
        data object Save : UiEvent()
    }

    var titleError by mutableStateOf(false)


    var descriptionError by mutableStateOf(false)

    private var tagsList = mutableStateListOf<Tag>()

    var selectedRepeatTaskOption by mutableStateOf("")

    var showRepeatDialog by mutableStateOf(false)

    private var timeComparison by mutableIntStateOf(0)

    private var deletedTag1InEditMode by mutableStateOf("")
    private var deletedTag2InEditMode by mutableStateOf("")
    private var deletedTag3InEditMode by mutableStateOf("")


    var currentId: Int? = null

    init {
        savedStateHandle.get<Int>("id")?.let {
            if (it != -1) {
                if (currentPage == 1) {
                    viewModelScope.launch(Dispatchers.IO) {
                        noteUseCases.getNote(it)?.also { note ->
                           tagsList = tagsUseCases.getTags().first().toMutableStateList()
                            currentId = note.id
                            _state.value = state.value.copy(
                                title = note.title,
                                description = note.description,
                                tagNumber1 = tagsList.find { tag -> tag.noteID == note.id && tag.tagNumber == 1 }?.tagName ?: "",
                                tagNumber2 = tagsList.find { tag -> tag.noteID == note.id && tag.tagNumber == 2 }?.tagName ?: "",
                                tagNumber3 = tagsList.find { tag -> tag.noteID == note.id && tag.tagNumber == 3 }?.tagName ?: "",
                            )
                        }



                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        taskUseCases.getTask(it)?.also { task ->
                           tagsList = tagsUseCases.getTags().first().toMutableStateList()
                            currentId = task.id
                            _state.value = state.value.copy(
                                title = task.title,
                                description = task.description,
                                taskPriority = task.priority,
                                dueDate = task.dueDate ?: "",
                                repeatTime = task.repeatTime.toString(),
                                itHasReminder = task.hasReminder,
                                tagNumber1 = tagsList.find { tag -> tag.taskID == task.id && tag.tagNumber == 1 }?.tagName ?: "",
                                tagNumber2 = tagsList.find { tag -> tag.taskID == task.id && tag.tagNumber == 2 }?.tagName ?: "",
                                tagNumber3 = tagsList.find { tag -> tag.taskID == task.id && tag.tagNumber == 3 }?.tagName ?: ""
                            )
                        }
                    }
                }


            } else return@let

        } ?: println("title is null , from ViewModel")


    }

    @SuppressLint("SuspiciousIndentation")
    fun onEvent(events: Add_Edit_Events) {
        when (events) {
            is Add_Edit_Events.EnteredTitle -> _state.value = state.value.copy(title = events.title)
            is Add_Edit_Events.EnteredDescription -> _state.value =
                state.value.copy(description = events.description)

            is Add_Edit_Events.ChangePriority -> _state.value =
                state.value.copy(taskPriority = events.priority)

            is Add_Edit_Events.SaveDueDate -> {
                _state.value = state.value.copy(dueDate = events.dueDate.format(Task.timeFormat))

            }


            is Add_Edit_Events.PressedAddTagButton -> {

                if (state.value.tagNumber1.isBlank())
                    _state.value = state.value.copy(tagNumber1 = events.tag)
                else if (state.value.tagNumber2.isBlank())
                    _state.value = state.value.copy(tagNumber2 = events.tag)
                else if (state.value.tagNumber3.isBlank())
                    _state.value = state.value.copy(tagNumber3 = events.tag)
            }

            Add_Edit_Events.ToggleTaskReminder -> _state.value =
                state.value.copy(itHasReminder = !state.value.itHasReminder)

            Add_Edit_Events.Save -> {
                viewModelScope.launch {

                    try {
                        if (state.value.dueDate.isNotBlank()) {
                            if (compareDateTimes(
                                    state.value.dueDate,
                                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                                    "yyyy-MM-dd'T'HH:mm:ss"
                                ) == -1
                            ) {
                                timeComparison = -1
                                throw InvalidItemException(application.getString(R.string.time_or_date_is_before_current_time))
                            }
                        }

                        if (currentPage == 0) {

                            val tempDeletionTagList = arrayListOf<Tag>()

                            tagsList.find { it.tagName == deletedTag1InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }
                            tagsList.find { it.tagName == deletedTag2InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }
                            tagsList.find { it.tagName == deletedTag3InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }

                            if (tempDeletionTagList.isNotEmpty())
                                tagsUseCases.deleteTags(tempDeletionTagList)

                            deletedTag1InEditMode = ""
                            deletedTag2InEditMode = ""
                            deletedTag3InEditMode = ""

                            val tempTagList = arrayListOf<Tag>()

                            val task = Task(
                                id = currentId,
                                title = state.value.title,
                                description = state.value.description,
                                priority = state.value.taskPriority,
                                dueDate = state.value.dueDate.ifBlank { null },
                                repeatTime = state.value.repeatTime,
                                isChecked = false,
                                hasReminder = dialogState.value.reminderIsChecked
                            )
                           val taskId = taskUseCases.addTask(task).also {

                               if (dialogState.value.reminderIsChecked && state.value.dueDate.isNotBlank())
                                   alarmManager.schedule(task.copy(id = it.toInt()))
                               else if (currentId != null)
                                   alarmManager.cancel(task.copy(id = it.toInt()))

                           }

                            if (state.value.tagNumber1.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber1, taskID = taskId.toInt(), tagNumber = 1))

                            if (state.value.tagNumber2.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber2,taskID = taskId.toInt(), tagNumber = 2))

                            if (state.value.tagNumber3.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber3, taskID = taskId.toInt(), tagNumber = 3))

                            tagsUseCases.addTags(tempTagList)

                        } else {

                            val tempDeletionTagList = arrayListOf<Tag>()

                            tagsList.find { it.tagName == deletedTag1InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }
                            tagsList.find { it.tagName == deletedTag2InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }
                            tagsList.find { it.tagName == deletedTag3InEditMode }?.let {
                                tempDeletionTagList.add(it)
                            }

                            if (tempDeletionTagList.isNotEmpty())
                                tagsUseCases.deleteTags(tempDeletionTagList)

                            deletedTag1InEditMode = ""
                            deletedTag2InEditMode = ""
                            deletedTag3InEditMode = ""


                            val note = Note(
                                title = state.value.title,
                                description = state.value.description,
                                id = currentId,
                                creationDate = System.currentTimeMillis()
                            )

                            val noteId = noteUseCases.addNote(note)

                            val tempTagList = arrayListOf<Tag>()

                            if (state.value.tagNumber1.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber1, noteID = noteId.toInt(), tagNumber = 1))

                            if (state.value.tagNumber2.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber2, noteID = noteId.toInt(), tagNumber = 2))

                            if (state.value.tagNumber3.isNotBlank())
                                tempTagList.add(Tag(tagName = state.value.tagNumber3, noteID = noteId.toInt(), tagNumber = 3))

                            tagsUseCases.addTags(tempTagList)


                        }
                        _eventFlow.emit(UiEvent.Save)
                    } catch (e: InvalidItemException) {
                        if (state.value.title.isBlank())
                            titleError = true
                        else if (state.value.description.isBlank() && currentPage != 0)
                            descriptionError = true
                        else if (timeComparison == -1) {
                            timeComparison = 0
                        }




                        _eventFlow.emit(UiEvent.ShowSnackBar(e.message ?: "Unknown Error"))

                    }
                }
            }

            is Add_Edit_Events.PressedDeleteTagButton -> {

                when (events.selectedTag) {
                    "tagNumber1" -> {
                        deletedTag1InEditMode = state.value.tagNumber1
                        _state.value = state.value.copy(tagNumber1 = "")
                    }

                    "tagNumber2" -> {
                        deletedTag2InEditMode = state.value.tagNumber2
                        _state.value = state.value.copy(tagNumber2 = "")
                    }

                    "tagNumber3" -> {
                        deletedTag3InEditMode = state.value.tagNumber3
                        _state.value = state.value.copy(tagNumber3 = "")
                    }
                }
            }

            Add_Edit_Events.ShowValidationSnackBar -> TODO()

            is Add_Edit_Events.EnteredRepeatDialog -> {
                _state.value = state.value.copy(repeatTime = events.period)
            }
        }

    }

    fun dialogEvents(event: ChooseDateTimeDialogEvents) {
        when (event) {
            is ChooseDateTimeDialogEvents.EnteredDateType ->
                _dialogState.value =
                    dialogState.value.copy(dateTypeIsPredefined = event.isPredefined)

            is ChooseDateTimeDialogEvents.ToggleDueDate -> {
                _dialogState.value = dialogState.value.copy(dueDateHasBeenSet = event.dueDateIsSet)
            }

            is ChooseDateTimeDialogEvents.EnteredDay -> {
                _dialogState.value = dialogState.value.copy(dayFromTextField = event.day)
            }

            is ChooseDateTimeDialogEvents.EnteredMonth ->
                _dialogState.value = dialogState.value.copy(monthFromTextField = event.month)

            is ChooseDateTimeDialogEvents.EnteredYear ->
                _dialogState.value = dialogState.value.copy(yearFromTextField = event.year)

            is ChooseDateTimeDialogEvents.ReminderStatusIsChanged ->
                _dialogState.value = dialogState.value.copy(reminderIsChecked = event.isChecked)

            is ChooseDateTimeDialogEvents.ShowDateAndTimeDialog ->
                _dialogState.value = dialogState.value.copy(showDateAndTimeDialog = event.status)

            ChooseDateTimeDialogEvents.ClearAllValues -> {
                _dialogState.value = ChooseDateDialogState()
                _state.value = state.value.copy(dueDate = "")
            }

            is ChooseDateTimeDialogEvents.TimeDialogVisibility -> {
                _dialogState.value = dialogState.value.copy(showTimePickerDialog = event.status)
            }

            is ChooseDateTimeDialogEvents.EnteredHour -> {
                _dialogState.value = dialogState.value.copy(hourValue = event.hour)
            }

            is ChooseDateTimeDialogEvents.EnteredMinute -> {
                _dialogState.value = dialogState.value.copy(minuteValue = event.minute)
            }
        }
    }


}