/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 */

package org.isoron.uhabits.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Loop Habit Tracker - Desktop")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { }) {
                Text("Add Habit")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Loop Habit Tracker") {
        App()
    }
}