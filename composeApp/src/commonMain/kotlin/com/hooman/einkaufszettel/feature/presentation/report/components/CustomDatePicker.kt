package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    onDismiss: () -> Unit,
    onDateSelected:(startDate: Instant, endDate: Instant) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates{
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.fromEpochMilliseconds(utcTimeMillis)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                return date <= today
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis

                    if(startMillis != null && endMillis != null){
                        val startDate = Instant.fromEpochMilliseconds(startMillis)
                        val endDate = Instant.fromEpochMilliseconds(endMillis)

                        onDateSelected(startDate, endDate)
                    }
                }
            ){
                Text(
                    text = "Select Date"
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss){
                Text(
                    text = "Cancel"
                )
            }
        }
    ){
        DateRangePicker(
            state = dateRangePickerState,
            modifier = Modifier.weight(1f),
            title = {
                Text(
                    text = "Select Date Range",
                    modifier = Modifier.padding(16.dp)
                )
            },
        )
    }
}