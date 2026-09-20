package com.hooman.einkaufszettel.feature.presentation.report.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.core.presentation.blackColor
import org.jetbrains.compose.resources.stringResource
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.cancel
import einkaufszettel.composeapp.generated.resources.report_select_dates
import einkaufszettel.composeapp.generated.resources.report_date_range
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected:(startDate: LocalDate, endDate: LocalDate) -> Unit
) {
    val selectableDates = remember { object : SelectableDates{
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.fromEpochMilliseconds(utcTimeMillis)
                    .toLocalDateTime(TimeZone.UTC).date
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                return date <= today
            }
        }
    }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
        initialSelectedEndDateMillis = initialEndDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
        selectableDates = selectableDates
    )
    val startMillis = dateRangePickerState.selectedStartDateMillis
    val endMillis = dateRangePickerState.selectedEndDateMillis
    val canConfirm = startMillis != null && endMillis != null && startMillis <= endMillis &&
        selectableDates.isSelectableDate(startMillis) && selectableDates.isSelectableDate(endMillis)

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(
        surface = blackColor,
        onSurface = whiteColor,
        onSurfaceVariant = whiteColor,
        primary = whiteColor,
        onPrimary = whiteColor,
        onSecondaryContainer = whiteColor,
        error = whiteColor
    )) {
    DatePickerDialog(
        colors = DatePickerDefaults.colors(containerColor = blackColor),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = canConfirm,
                onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis

                    if(startMillis != null && endMillis != null){
                        val startDate = Instant.fromEpochMilliseconds(startMillis).toLocalDateTime(TimeZone.UTC).date
                        val endDate = Instant.fromEpochMilliseconds(endMillis).toLocalDateTime(TimeZone.UTC).date

                        onDateSelected(startDate, endDate)
                    }
                }
            ){
                Text(
                    text = stringResource(Res.string.report_select_dates),
                    color = whiteColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss){
                Text(
                    text = stringResource(Res.string.cancel),
                    color = whiteColor
                )
            }
        }
    ){
        DateRangePicker(
            state = dateRangePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = blackColor,
                selectedDayContainerColor = blackColor,
                selectedYearContainerColor = blackColor,
                titleContentColor = whiteColor,
                headlineContentColor = whiteColor,
                weekdayContentColor = whiteColor,
                subheadContentColor = whiteColor,
                navigationContentColor = whiteColor,
                yearContentColor = whiteColor,
                disabledYearContentColor = whiteColor,
                currentYearContentColor = whiteColor,
                selectedYearContentColor = whiteColor,
                disabledSelectedYearContentColor = whiteColor,
                dayContentColor = whiteColor,
                disabledDayContentColor = whiteColor,
                selectedDayContentColor = whiteColor,
                disabledSelectedDayContentColor = whiteColor,
                todayContentColor = whiteColor,
                dayInSelectionRangeContentColor = whiteColor,
                dateTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = whiteColor,
                    focusedLabelColor = whiteColor,
                    focusedPlaceholderColor = whiteColor,
                    focusedLeadingIconColor = whiteColor,
                    focusedTrailingIconColor = whiteColor,
                    focusedSupportingTextColor = whiteColor,
                    focusedPrefixColor = whiteColor,
                    focusedSuffixColor = whiteColor,
                    unfocusedTextColor = whiteColor,
                    unfocusedLabelColor = whiteColor,
                    unfocusedPlaceholderColor = whiteColor,
                    unfocusedLeadingIconColor = whiteColor,
                    unfocusedTrailingIconColor = whiteColor,
                    unfocusedSupportingTextColor = whiteColor,
                    unfocusedPrefixColor = whiteColor,
                    unfocusedSuffixColor = whiteColor,
                    disabledTextColor = whiteColor,
                    disabledLabelColor = whiteColor,
                    disabledPlaceholderColor = whiteColor,
                    disabledLeadingIconColor = whiteColor,
                    disabledTrailingIconColor = whiteColor,
                    disabledSupportingTextColor = whiteColor,
                    disabledPrefixColor = whiteColor,
                    disabledSuffixColor = whiteColor,
                    errorTextColor = whiteColor,
                    errorLabelColor = whiteColor,
                    errorPlaceholderColor = whiteColor,
                    errorLeadingIconColor = whiteColor,
                    errorTrailingIconColor = whiteColor,
                    errorSupportingTextColor = whiteColor,
                    errorPrefixColor = whiteColor,
                    errorSuffixColor = whiteColor
                )
            ),
            modifier = Modifier.weight(1f),
            title = {
                Text(
                    text = stringResource(Res.string.report_date_range),
                    color = whiteColor,
                    modifier = Modifier.padding(16.dp)
                )
            },
        )
    }
}
}
