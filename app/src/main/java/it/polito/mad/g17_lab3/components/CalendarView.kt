package it.polito.mad.g17_lab3.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.mad.g17_lab3.enums.Sport
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString(),
    onDateSelected: (selectedDate: String) -> Unit,
    activeDays: List<String>,
    selectedSport: Sport = Sport.ALL
) {
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
    var selectedMonth by remember { mutableStateOf(date.substring(3..4).toInt()) }
    var selectedYear by remember { mutableStateOf(date.substring(6..9).toInt()) }
    val selectedDate = remember { mutableStateOf(date.substring(0..1).toInt()) }

    val startDate = LocalDate.of(selectedYear, selectedMonth, 1)
    val endDate = startDate.plusMonths(1).minusDays(1)
    val daysInMonth = startDate.month.length(startDate.isLeapYear)
    val startDayOfWeek = startDate.dayOfWeek.value % 7
    val endDayOfWeek = endDate.dayOfWeek.value % 7

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                if (selectedMonth == 1) {
                    selectedMonth = 12
                    selectedYear--
                } else {
                    selectedMonth--
                }
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth - 1)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDate.value)
                val retDate = calendar.time
                val dateFormat =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                onDateSelected(dateFormat.format(retDate))
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Previous month")
            }
            Text(
                text = "${
                    Month.of(selectedMonth).getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                } $selectedYear",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            IconButton(onClick = {
                if (selectedMonth == 12) {
                    selectedMonth = 1
                    selectedYear++
                } else {
                    selectedMonth++
                }
            }) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Next month")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Weekday headers
            for (dayOfWeek in DayOfWeek.values()) {
                item {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Days
            var dayOfMonth = 1
            val cells = mutableListOf<Int>()

            // Fill cells before start day of month with zeroes
            repeat(startDayOfWeek - 1) { cells.add(0) }

            // Fill cells with days of month
            while (dayOfMonth <= daysInMonth) {
                cells.add(dayOfMonth)
                dayOfMonth++
            }

            // Fill cells after end day of month with zeroes
            repeat(6 - endDayOfWeek) { cells.add(0) }

            // Draw cells
            cells.forEach { date ->
                val isSelected = (date == selectedDate.value)
                var isActive = false
                if (activeDays.isNotEmpty()) {
                    isActive = activeDays.filter {
                        (date == it.substring(0..1).toInt() && selectedMonth == it.substring(3..4)
                            .toInt() && selectedYear == it.substring(6..9).toInt())
                    }.isNotEmpty()
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    calendar.set(Calendar.YEAR, selectedYear)
                                    calendar.set(Calendar.MONTH, selectedMonth - 1)
                                    calendar.set(Calendar.DAY_OF_MONTH, date)
                                    val retDate = calendar.time
                                    val dateFormat =
                                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    onDateSelected(dateFormat.format(retDate))
                                    if (date != 0) {
                                        selectedDate.value = date
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (date != 0) date.toString() else "",
                            color = if (isSelected) Color.White else if (isActive) Color.Black else if (today.substring(0..1).toInt() == date && today.substring(3..4).toInt() == selectedMonth) MaterialTheme.colorScheme.primary else Color.LightGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}


