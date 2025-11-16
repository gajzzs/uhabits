/*
 * Copyright (C) 2016-2025 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.core.ui.views

import org.isoron.platform.gui.Canvas
import org.isoron.platform.gui.Color
import org.isoron.platform.gui.DataView
import org.isoron.platform.gui.TextAlign
import org.isoron.platform.time.DayOfWeek
import org.isoron.platform.time.LocalDate
import org.isoron.platform.time.LocalDateFormatter
import org.isoron.uhabits.core.models.PaletteColor
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

interface OnDateClickedListener {
    fun onDateShortPress(date: LocalDate) {}
    fun onDateLongPress(date: LocalDate) {}
}

class HistoryChart(
    var dateFormatter: LocalDateFormatter,
    var firstWeekday: DayOfWeek,
    var paletteColor: PaletteColor,
    var series: List<Square>,
    var defaultSquare: Square,
    var notesIndicators: List<Boolean>,
    var theme: Theme,
    var today: LocalDate,
    var onDateClickedListener: OnDateClickedListener = object : OnDateClickedListener {},
    var padding: Double = 0.0,
    var widgetStyle: String = "calendar",
    var showWeekdayLabels: Boolean = true,
    var showDateHeaders: Boolean = true,
    var fontFamily: String? = null
) : DataView {

    enum class Square {
        ON,
        OFF,
        GREY,
        DIMMED,
        HATCHED
    }

    var squareSpacing = 4.2
    override var dataOffset = 0

    private var squareSize = 0.0
    private var width = 0.0
    private var height = 0.0
    private var nColumns = 0
    private var topLeftOffset = 0
    private var topLeftDate = LocalDate(2020, 1, 1)
    private var lastPrintedMonth = ""
    private var lastPrintedYear = ""
    private var headerOverflow = 0.0
    private var centerOffsetX = 0.0
    private var centerOffsetY = 0.0
    
    override val dataColumnWidth: Double
        get() = squareSpacing + squareSize

    override fun onClick(x: Double, y: Double) {
        onDateClicked(x, y, false)
    }

    override fun onLongClick(x: Double, y: Double) {
        onDateClicked(x, y, true)
    }

    private fun onDateClicked(x: Double, y: Double, isLongClick: Boolean) {
        if (width <= 0.0) throw IllegalStateException("onClick must be called after draw(canvas)")
        val col = ((x - padding) / squareSize).toInt()
        val row = ((y - padding) / squareSize).toInt()
        val offset = col * 7 + (row - 1)
        if (x - padding < 0 || row == 0 || row > 7 || col == nColumns) return
        val clickedDate = topLeftDate.plus(offset)
        if (clickedDate.isNewerThan(today)) return
        if (isLongClick) {
            onDateClickedListener.onDateLongPress(clickedDate)
        } else {
            onDateClickedListener.onDateShortPress(clickedDate)
        }
    }

    override fun draw(canvas: Canvas) {
        width = canvas.getWidth()
        height = canvas.getHeight()

        if (theme.cardBackgroundColor != Color.TRANSPARENT) {
            canvas.setColor(theme.cardBackgroundColor)
            canvas.fill()
        }

        squareSize = round((height - 2 * padding) / 8.0)
        canvas.setFontSize(min(14.0, height * 0.06))
        
        val weekdayColumnWidth = if (showWeekdayLabels) {
            DayOfWeek.values().map { weekday ->
                canvas.measureText(dateFormatter.shortWeekdayName(weekday)) + squareSize * 0.15
            }.maxOrNull() ?: 0.0
        } else {
            0.0
        }
        
        nColumns = floor((width - 2 * padding - weekdayColumnWidth) / squareSize).toInt()
        
        canvas.setFontSize(min(12.0, squareSize * 0.4))
        
        val firstWeekdayOffset = (
            today.dayOfWeek.daysSinceSunday -
                firstWeekday.daysSinceSunday + 7
            ) % 7
        topLeftOffset = (nColumns - 1 + dataOffset) * 7 + firstWeekdayOffset
        topLeftDate = today.minus(topLeftOffset)
        
        centerOffsetX = padding
        centerOffsetY = padding

        lastPrintedYear = ""
        lastPrintedMonth = ""
        headerOverflow = 0.0

        // Draw main columns
        repeat(nColumns) { column ->
            val topOffset = topLeftOffset - 7 * column
            val topDate = topLeftDate.plus(7 * column)
            drawColumn(canvas, column, topDate, topOffset)
        }

        // Draw week day names
        if (showWeekdayLabels) {
            canvas.setColor(theme.mediumContrastTextColor)
            repeat(7) { row ->
                val date = topLeftDate.plus(row)
                canvas.setTextAlign(TextAlign.LEFT)
                val yOffset = if (showDateHeaders) (row + 1) else row
                canvas.drawText(
                    dateFormatter.shortWeekdayName(date),
                    centerOffsetX + nColumns * squareSize + squareSize * 0.15,
                    padding + squareSize * yOffset + squareSize / 2
                )
            }
        }
    }

    private fun drawColumn(
        canvas: Canvas,
        column: Int,
        topDate: LocalDate,
        topOffset: Int
    ) {
        if (showDateHeaders) {
            drawHeader(canvas, column, topDate)
        }
        repeat(7) { row ->
            val offset = topOffset - row
            val date = topDate.plus(row)
            if (offset < 0) return
            val yOffset = if (showDateHeaders) (row + 1) else row
            drawSquare(
                canvas,
                centerOffsetX + column * squareSize,
                centerOffsetY + yOffset * squareSize,
                squareSize - squareSpacing,
                squareSize - squareSpacing,
                date,
                offset
            )
        }
    }

    private fun drawHeader(canvas: Canvas, column: Int, date: LocalDate) {
        canvas.setColor(theme.mediumContrastTextColor)
        val monthText = dateFormatter.shortMonthName(date)
        val yearText = date.year.toString()
        val headerText: String
        when {
            monthText != lastPrintedMonth -> {
                headerText = monthText
                lastPrintedMonth = monthText
            }
            yearText != lastPrintedYear -> {
                headerText = yearText
                lastPrintedYear = headerText
            }
            else -> {
                headerText = ""
            }
        }
        canvas.setTextAlign(TextAlign.LEFT)
        canvas.drawText(
            headerText,
            headerOverflow + centerOffsetX + column * squareSize,
            padding + squareSize / 2
        )

        headerOverflow += canvas.measureText(headerText) + 0.1 * squareSize
        headerOverflow = max(0.0, headerOverflow - squareSize)
    }

    private fun drawSquare(
        canvas: Canvas,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        date: LocalDate,
        offset: Int
    ) {
        val value = if (offset >= series.size) defaultSquare else series[offset]
        val hasNotes = if (offset >= notesIndicators.size) false else notesIndicators[offset]
        val squareColor: Color
        val circleColor: Color
        val color = theme.color(paletteColor.paletteIndex)
        squareColor = when (value) {
            Square.ON -> {
                color
            }
            Square.OFF -> {color.withAlpha(0.08)
                // if (theme is WidgetTheme) {
                //     color.withAlpha(0.3)
                // } else {
                //     color.blendWith(Color.BLACK, 0.7)
                // }
            }
            Square.GREY -> {
                theme.mediumContrastTextColor
            }
            Square.DIMMED -> {
               color.withAlpha(0.08)
            }
            Square.HATCHED -> {
                color.withAlpha(0.08)
            }
        }
        
        canvas.setColor(theme.cardBackgroundColor.withAlpha(0.15))
        canvas.fillRoundRect(x+1, y+1, width, height, width * 0.3)

        canvas.setColor(squareColor)
        canvas.fillRoundRect(x, y, width, height,  width * 0.3)

        if (value == Square.HATCHED) {
            canvas.setStrokeWidth(0.75)
            canvas.setColor(theme.cardBackgroundColor)
            var k = width / 10
            repeat(5) {
                canvas.drawLine(x + k, y, x, y + k)
                canvas.drawLine(
                    x + width - k,
                    y + height,
                    x + width,
                    y + height - k
                )
                k += width / 5
            }
        }

        val textColor = if (theme.cardBackgroundColor == Color.TRANSPARENT) {
            theme.highContrastTextColor
        } else {
            val c1 = squareColor.contrast(theme.cardBackgroundColor)
            val c2 = squareColor.contrast(theme.mediumContrastTextColor)
            if (c1 > c2) theme.cardBackgroundColor else theme.mediumContrastTextColor
        }

        if (widgetStyle == "dots") {
            // Compact dots style - no text, just colored squares
        } else {
            // Calendar style - show day numbers
            canvas.setColor(textColor)
            canvas.setTextAlign(TextAlign.CENTER)
            canvas.drawText(date.day.toString(), x + width / 2, y + width / 2)
        }

        if (hasNotes) {
            circleColor = when (value) {
                Square.ON, Square.GREY -> theme.lowContrastTextColor
                else -> color
            }
            canvas.setColor(circleColor)
            canvas.fillCircle(x + width - width / 5, y + width / 5, width / 12)
        }
    }
}
