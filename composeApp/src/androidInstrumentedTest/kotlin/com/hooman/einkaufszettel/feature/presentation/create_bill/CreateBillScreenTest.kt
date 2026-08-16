package com.hooman.einkaufszettel.feature.presentation.create_bill

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.hooman.einkaufszettel.core.presentation.backgroundGradient
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CreateBillScreenTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clickOnSaveWithValidName() = runComposeUiTest {
        var isSavedCalled = false
        val snackbarHostState = SnackbarHostState()

        setContent {
            CreateBillScreen(
                contentPadding = PaddingValues(),
                onCancel = {},
                background = backgroundGradient,
                onSaved = {bill ->
                    if(bill != null){
                        isSavedCalled = true
                    }
                },
                snackBarHostState = snackbarHostState
            )
        }

        onNodeWithTag("bill_name_input").performTextReplacement("Weekly Groceries")

        onNodeWithTag("bill_save_button").performClick()

        assertTrue(isSavedCalled)

    }
}