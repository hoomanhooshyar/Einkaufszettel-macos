package com.hooman.einkaufszettel.feature.presentation.create_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import com.hooman.einkaufszettel.core.presentation.greenGradient
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.model.PurchaseType
import com.hooman.einkaufszettel.domain.model.getDisplayTypename
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import com.hooman.einkaufszettel.feature.presentation.components.CEComboBox
import com.hooman.einkaufszettel.feature.presentation.components.CETextField
import com.hooman.einkaufszettel.feature.utils.DateTime
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.bill_name
import einkaufszettel.composeapp.generated.resources.bill_name_empty_error
import einkaufszettel.composeapp.generated.resources.created_date
import einkaufszettel.composeapp.generated.resources.enter_bill_name
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource

import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun CreateBillScreenRoot(
    viewModel: CreateBillViewModel,
    onCancel: () -> Unit,
    onSaved: (Bill?) -> Unit,
    contentPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    navController: NavHostController
) {
    val state by viewModel.createListState.collectAsState()
    LaunchedEffect(state.error){
        if(state.error != null){
            snackBarHostState.showSnackbar(state.error.toString(), duration = SnackbarDuration.Short)

        }


    }
    CreateBillScreen(
        contentPadding = contentPadding,
        onCancel = onCancel,
        background = backgroundGradient,
        onSaved = {
            viewModel.addBillIntoLocal(it)
            navController.popBackStack()
        },
        snackBarHostState = snackBarHostState
    )
}

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
@Composable
fun CreateBillScreen(
    contentPadding: PaddingValues,
    onCancel: () -> Unit,
    background: Brush,
    onSaved: (bill: Bill?) -> Unit,
    snackBarHostState: SnackbarHostState
) {
    var billNameState by remember { mutableStateOf("")}
    val date = Clock.System.now()
    val formattedDate = DateTime.getFormattedDate(date.toString())
    val billType = PurchaseType.entries
    var selectedType by remember { mutableStateOf(billType.first()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(
            modifier = Modifier
                .height(32.dp)
        )
        CETextField(
            modifier = Modifier,
            readOnly = false,
            value = formattedDate,
            onValueChange = {},
            label = {
                Text(
                    text = stringResource(Res.string.created_date)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(Res.string.created_date)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null
                )
            },
            keyboardType = KeyboardType.Text
        )
        CETextField(
            modifier = Modifier,
            value = billNameState,
            onValueChange = { billNameState = it },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null
                )
            },
            label = {Text(
                text = stringResource(Res.string.bill_name)
            )},
            placeholder = {Text(
                text = stringResource(Res.string.enter_bill_name)
            )},
            keyboardType = KeyboardType.Text
        )

        CEComboBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 8.dp
                ),
            items = billType,
            selectedItem = selectedType,
            onItemSelected = {selectedType = it},
            itemLabel = {it.getDisplayTypename()},
            background = backgroundGradient
        )

        Spacer(
            modifier = Modifier.height(36.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            val errorMessage = stringResource(Res.string.bill_name_empty_error)
            val scope = rememberCoroutineScope()
            CEButton(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    scope.launch {
                        val bill = Bill(
                            id = Uuid.random().toString(),
                            billDate = date,
                            name = billNameState,
                            type = selectedType,
                            userId = "",
                            items = emptyList()
                        )
                        if(bill.name.isEmpty() || bill.name.equals("")){

                            snackBarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
                            return@launch
                        }
                        onSaved(bill)
                    }

                },
                icon = Icons.Default.Save,
                text = "Save",
                contentColor = whiteColor,
                containerColor = greenGradient
            )

            CEButton(
                modifier = Modifier
                    .weight(1f),
                onClick = onCancel,
                icon = Icons.Default.Cancel,
                text = "Cancel",
                contentColor = whiteColor,
                containerColor = redGradient
            )
        }
    }
}