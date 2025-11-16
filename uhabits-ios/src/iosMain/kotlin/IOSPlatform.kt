/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 */

package org.isoron.uhabits.ios

import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitList

class IOSHabitTracker {
    private val habitList = HabitList()
    
    fun addHabit(name: String): Boolean {
        val habit = Habit()
        habit.name = name
        habitList.add(habit)
        return true
    }
    
    fun getHabits(): List<String> {
        return habitList.map { it.name }
    }
}