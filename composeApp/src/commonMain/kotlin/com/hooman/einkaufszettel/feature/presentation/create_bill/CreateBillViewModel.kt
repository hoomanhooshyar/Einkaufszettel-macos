package com.hooman.einkaufszettel.feature.presentation.create_bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.bill_is_null
import einkaufszettel.composeapp.generated.resources.user_is_not_logged_in
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CreateBillViewModel(
    private val insertBillL: InsertBillToLocalUseCase,
    private val authRepository: AuthRepository,
    private val syncDatabaseUseCase: SyncDatabaseUseCase,
) : ViewModel() {
    private val _createListState = MutableStateFlow(CreateBillState())
    val createListState = _createListState.asStateFlow()

    fun onSaveHandled() {
        _createListState.value = _createListState.value.copy(savedBill = null, isSaved = false)
    }

    fun addBillIntoLocal(bill: Bill?) {
        if (_createListState.value.isLoading || _createListState.value.isSaved) return

        _createListState.value = _createListState.value.copy(
            isLoading = true,
            error = null,
            isSaved = false,
            savedBill = null
        )

        viewModelScope.launch {
            try {
                if (bill == null) {
                    _createListState.value = _createListState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.bill_is_null)
                    )
                    return@launch
                }
                val userId = authRepository.getCurrentUserId()
                if (userId.isNullOrBlank()) {
                    _createListState.value = _createListState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.user_is_not_logged_in)
                    )
                    return@launch
                }
                val finalBill = bill.copy(userId = userId, syncStatus = SyncStatus.LSL)
                val localResult = insertBillL(finalBill)

                when (localResult) {
                    is Resource.Success -> {
                        _createListState.value = _createListState.value.copy(
                            isLoading = false,
                            isSaved = true,
                            savedBill = finalBill,
                            error = null
                        )

                        triggerBackgroundSync()
                    }

                    is Resource.Error -> {
                        _createListState.value = _createListState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(localResult.message ?: "Unknown Error")
                        )

                        return@launch
                    }

                    is Resource.Loading -> {
                        _createListState.value = _createListState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString("Save did not complete")
                        )
                    }
                }
            }catch (e: Exception){
                if (e is CancellationException) throw e
                _createListState.value = _createListState.value.copy(
                    isLoading = false,
                    error = UiText.DynamicString("Unknown Error")
                )
            }


        }
    }

    private fun triggerBackgroundSync(){
        viewModelScope.launch {
            try {
                syncDatabaseUseCase()
            }catch (e: Exception){
                if (e is CancellationException) throw e
                e.printStackTrace()
            }
        }
    }
}