package com.A_23_59.hypernote

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import com.A_23_59.hypernote.ui.theme.Gold200
import com.A_23_59.hypernote.ui.theme.HyperNoteTheme
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        itemList.add(Item("task one", "this text is for testing", Color.Red))
        itemList.add(Item("task two", "", Gold200))
        itemList.add(Item("task three", "", Color.Blue))
        itemList.add(Item("task four", "", Color.Red))
        itemList.add(Item("task five", "", Gold200))
        itemList.add(Item("task sixth", "", Color.Blue))
        itemList.add(Item("task seven", "", Color.Blue))
        itemList.add(Item("task eight", "", Gold200))
        itemList.add(Item("task nine", "", Color.Blue))
        itemList.add(Item("task ten", "", Color.Red))
        itemList.add(Item("task eleven", "", Color.Red))
        itemList.add(Item("task twelve", "", Color.Blue))
        setContent {
            themeSpecifier.themeModifier()
            HyperNoteTheme(themeSpecifier.themeIsDark) {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }

}

