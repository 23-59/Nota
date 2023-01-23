package com.A_23_59.hypernote

import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.spec.DestinationStyle

object MyDialogStyle:DestinationStyle.Dialog {
    override val properties: DialogProperties
        get() = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = false)
}
