/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 */

package org.isoron.uhabits.web

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

fun main() {
    window.onload = {
        val container = document.createElement("div") as HTMLDivElement
        container.innerHTML = """
            <h1>Loop Habit Tracker - Web</h1>
            <button id="addHabit">Add Habit</button>
            <div id="habits"></div>
        """.trimIndent()
        
        document.body?.appendChild(container)
        
        val addButton = document.getElementById("addHabit") as HTMLButtonElement
        addButton.onclick = {
            val habitsDiv = document.getElementById("habits") as HTMLDivElement
            habitsDiv.innerHTML += "<p>New Habit</p>"
        }
    }
}