package com.A_23_59.hypernote.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.A_23_59.hypernote.domain.model.InvalidItemException
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.use_case.notes.NoteUseCases
import com.A_23_59.hypernote.domain.use_case.tasks.TaskUseCases
import com.A_23_59.hypernote.domain.util.Add_Edit_Events
import com.A_23_59.hypernote.domain.util.Add_Edit_State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Add_EditScreenViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val taskUseCases: TaskUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = mutableStateOf(Add_Edit_State())
    val state: State<Add_Edit_State> = _state



    var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("id")?.let {
            if (it != -1) {
                if (currentPage == 1) {
                    viewModelScope.launch {
                        noteUseCases.getNote(it)?.also { note ->
                            currentNoteId = note.id
                            _state.value = state.value.copy(
                                title = note.title,
                                description = note.description,
                                tagNumber1 = note.tagNumber1 ?: "",
                                tagNumber2 = note.tagNumber2 ?: "",
                                tagNumber3 = note.tagNumber3 ?: "",
                            )
                        }

                    }
                }
            }


        } ?: println("title is null , from ViewModel")


    }

    fun onEvent(events: Add_Edit_Events) {
        when (events) {
            is Add_Edit_Events.EnteredTitle -> _state.value = state.value.copy(title = events.title)
            is Add_Edit_Events.EnteredDescription -> _state.value =
                state.value.copy(description = events.description)

            is Add_Edit_Events.ChangePriority -> _state.value =
                state.value.copy(taskPriority = events.priority)

            is Add_Edit_Events.EnteredDueDate -> _state.value =
                state.value.copy(dateAndTime = events.dueDate.format(Task.timeFormat))

            is Add_Edit_Events.EnteredTaskPeriod -> _state.value =
                state.value.copy(repeatTime = events.period)

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
                        if (currentPage == 0) {

                            taskUseCases.addTask(
                                Task(
                                    title = state.value.title,
                                    description = state.value.description,
                                    priority = state.value.taskPriority,
                                    tagNumber1 = state.value.tagNumber1,
                                    tagNumber2 = state.value.tagNumber2,
                                    tagNumber3 = state.value.tagNumber3,
                                    dueDate = state.value.dateAndTime.toLong(), // this might be a bug source in future
                                    repeatTime = state.value.repeatTime,
                                    isChecked = false,
                                    hasReminder = state.value.itHasReminder
                                )
                            )
                        } else {

                            noteUseCases.addNote(
                                Note(
                                    title = state.value.title,
                                    description = state.value.description,
                                    tagNumber1 = state.value.tagNumber1,
                                    tagNumber2 = state.value.tagNumber2,
                                    tagNumber3 = state.value.tagNumber3,
                                    id = currentNoteId
                                )
                            )
                        }

                    } catch (e: InvalidItemException) {
                        Log.e(TAG, "onEvent: ${e.message}")
                    }
                }
            }

            is Add_Edit_Events.PressedDeleteTagButton -> {

                when (events.selectedTag) {
                    "tagNumber1" -> _state.value = state.value.copy(tagNumber1 = "")
                    "tagNumber2" -> _state.value = state.value.copy(tagNumber2 = "")
                    "tagNumber3" -> _state.value = state.value.copy(tagNumber3 = "")
                }
            }

            Add_Edit_Events.ShowValidationSnackBar -> TODO()
        }

    }

}