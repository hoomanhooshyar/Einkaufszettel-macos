package com.hooman.einkaufszettel.feature.presentation.shopping_item_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductForShoppingItemFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetShoppingItemByBillIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemCheckStatusInLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemCheckStatusInRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemCountInLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemCountInRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemDiscountInLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.UpdateShoppingItemDiscountInRemoteUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.data_removed_just_from_local
import einkaufszettel.composeapp.generated.resources.data_removed_successfully
import einkaufszettel.composeapp.generated.resources.delete_shopping_item_local_fail
import einkaufszettel.composeapp.generated.resources.delete_shopping_item_server_fail
import einkaufszettel.composeapp.generated.resources.discount_error
import einkaufszettel.composeapp.generated.resources.no_internet_error
import einkaufszettel.composeapp.generated.resources.no_shopping_item
import einkaufszettel.composeapp.generated.resources.not_logged_in
import einkaufszettel.composeapp.generated.resources.shopping_item_is_disabled
import einkaufszettel.composeapp.generated.resources.update_fail
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShoppingListDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBillByIdL: GetBillByIdFromLocalUseCase,
    private val getShoppingItemsByBillIdR: GetShoppingItemByBillIdFromRemoteUseCase,
    private val insertShoppingItemL: InsertShoppingItemToLocalUseCase,
    private val getProductsForShoppingItemL: GetProductForShoppingItemFromLocalUseCase,
    private val deleteShoppingItemL: DeleteShoppingItemFromLocalUseCase,
    private val deleteShoppingItemR: DeleteShoppingItemFromRemoteUseCase,
    private val updateItemCountL: UpdateShoppingItemCountInLocalUseCase,
    private val updateItemCountR: UpdateShoppingItemCountInRemoteUseCase,
    private val updateItemCheckStatusL: UpdateShoppingItemCheckStatusInLocalUseCase,
    private val updateItemCheckStatusR: UpdateShoppingItemCheckStatusInRemoteUseCase,
    private val updateDiscountR: UpdateShoppingItemDiscountInRemoteUseCase,
    private val updateDiscountL: UpdateShoppingItemDiscountInLocalUseCase,

    private val observer: ConnectivityObserver,
    private val auth: AuthRepository
): ViewModel() {
    val billId = savedStateHandle.toRoute<Routes.ListDetails>().billId

    private val _listDetailsState = MutableStateFlow(ShoppingListDetailsState())
    val listDetailsState = _listDetailsState.asStateFlow()

    private val debounceJob = mutableMapOf<String, Job>()

    private val _userId = MutableStateFlow(auth.getCurrentUserId())
    val userId = _userId.asStateFlow()

    private var isInitialLoad = true

    init {
        getBillByIdFromLocal()
        observeShoppingItems()
    }

    private fun observeShoppingItems() {

        viewModelScope.launch {
            getProductsForShoppingItemL(billId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        val items = res.data ?: emptyList()
                        if (items.isEmpty() && isInitialLoad) {
                            isInitialLoad = false
                            _listDetailsState.value = _listDetailsState.value.copy(
                                isLoading = true
                            )
                            val currentUserId = _userId.value
                            if (!currentUserId.isNullOrEmpty()) {
                                getShoppingItemsFromRemote(currentUserId)
                            }
                        } else {
                            isInitialLoad = false
                            _listDetailsState.value = _listDetailsState.value.copy(
                                isLoading = false,
                                error = null,
                                shoppingDetailsItems = items
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                }
            }
        }
    }

    private fun getShoppingItemsFromRemote(userId: String) {
        viewModelScope.launch {
            val online = observer.isConnected.first()
            if (!online) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_internet_error)
                )
                return@launch
            }
            if (userId.isEmpty()) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.not_logged_in)
                )
                return@launch
            }
            getShoppingItemsByBillIdR(billId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        insertShoppingItemIntoLocal(res.data)
                    }

                    is Resource.Error -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }

                    is Resource.Loading -> {}

                }
            }
        }
    }

    private fun insertShoppingItemIntoLocal(shoppingItems: List<ShoppingItem>?) {
        viewModelScope.launch {
            if (shoppingItems.isNullOrEmpty()) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_shopping_item),
                    shoppingDetailsItems = emptyList()
                )
                return@launch
            }
            shoppingItems.forEach { item ->
                val result = insertShoppingItemL(item, billId) //Here
                if (result is Resource.Error) {
                    print("Error in inserting product into local- ${result.message}")
                }
            }
        }
    }

    private fun getBillByIdFromLocal() {
        viewModelScope.launch {
            getBillByIdL(billId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = false,
                            error = null,
                            bill = res.data,
                        )
                    }

                    is Resource.Loading -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = true,
                            error = null,

                            )
                    }

                    is Resource.Error -> {
                        _listDetailsState.value = _listDetailsState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!),
                        )
                    }
                }
            }
        }
    }

    fun onDeleteClick(
        shoppingItemId: String
    ) {
        _listDetailsState.value = _listDetailsState.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val currentList = _listDetailsState.value.shoppingDetailsItems ?: emptyList()
            val filteredList = currentList.filter { it.shoppingItemId != shoppingItemId }
            _listDetailsState.value = _listDetailsState.value.copy(
                shoppingDetailsItems = filteredList,
                isLoading = true
            )
            val localResult = deleteShoppingItemL(shoppingItemId)
            if (localResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    error = UiText.StringResourceId(Res.string.delete_shopping_item_local_fail)
                )
            }
            val online = observer.isConnected.first()
            if (!online) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.data_removed_just_from_local)
                )
                return@launch
            }
            val remoteResult = deleteShoppingItemR(billId, shoppingItemId)
            if (remoteResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.delete_shopping_item_server_fail)
                )
                return@launch
            }
            _listDetailsState.value = _listDetailsState.value.copy(
                isLoading = false,
                error = UiText.StringResourceId(Res.string.data_removed_successfully)
            )

        }
    }

    fun updateShoppingItemCount(shoppingItemId: String, itemCount: Int) {
        val item =
            _listDetailsState.value.shoppingDetailsItems?.find { it.shoppingItemId == shoppingItemId }
        if (item == null) {
            return
        }
        if (item.isChecked) {
            _listDetailsState.value = _listDetailsState.value.copy(
                error = UiText.StringResourceId(Res.string.shopping_item_is_disabled)
            )
            return
        }
        val currentList = _listDetailsState.value.shoppingDetailsItems ?: emptyList()
        val tagetItem = currentList.find { it.shoppingItemId == shoppingItemId }
        val targetProductId = tagetItem?.productId
        val updateList = currentList.map { item ->
            if (item.shoppingItemId == shoppingItemId) {
                item.copy(itemCount = itemCount)
            } else {
                item
            }
        }

        _listDetailsState.value = _listDetailsState.value.copy(shoppingDetailsItems = updateList)

        debounceJob[shoppingItemId]?.cancel()
        debounceJob[shoppingItemId] = viewModelScope.launch {
            delay(500)

            val localResult =
                updateItemCountL(shoppingItemId = shoppingItemId, itemCount = itemCount)

            if (localResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    error = UiText.StringResourceId(Res.string.update_fail)
                )
            }

            val online = observer.isConnected.first()

            if (!online) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_internet_error)
                )
                return@launch
            }

            if (targetProductId != null) {
                val remoteResult = updateItemCountR(
                    billId = billId,
                    productId = targetProductId,
                    itemCount = itemCount
                )

                if (remoteResult is Resource.Error) {
                    _listDetailsState.value = _listDetailsState.value.copy(
                        error = UiText.StringResourceId(Res.string.update_fail)
                    )
                }
            }
        }

    }

    fun onUpdateCheckedChange(shoppingItemId: String, isChecked: Boolean) {
        _listDetailsState.value = _listDetailsState.value.copy(isLoading = true)
        viewModelScope.launch {
            val localResult = updateItemCheckStatusL(shoppingItemId, isChecked)
            if (localResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    error = UiText.StringResourceId(Res.string.update_fail)
                )
            }
            val remoteResult = updateItemCheckStatusR(billId, shoppingItemId, isChecked)
            if (remoteResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    error = UiText.StringResourceId(Res.string.update_fail)
                )
            }
        }
    }

    fun updateDiscount(shoppingItemId: String, discount: Float) {

        val item =
            _listDetailsState.value.shoppingDetailsItems?.find { it.shoppingItemId == shoppingItemId }

        if (item == null) {
            _listDetailsState.value = _listDetailsState.value.copy(
                isLoading = false,
                error = UiText.StringResourceId(Res.string.no_shopping_item)
            )
            return
        }

        if (item.isChecked) {
            _listDetailsState.value = _listDetailsState.value.copy(
                error = UiText.StringResourceId(Res.string.shopping_item_is_disabled)
            )
            return
        }

        val currentList = _listDetailsState.value.shoppingDetailsItems ?: emptyList()

        val targetItem = currentList.find { it.shoppingItemId == shoppingItemId }
        val targetProductId = targetItem?.productId
        val updateList = currentList.map { item ->
            if (item.shoppingItemId == shoppingItemId) {
                item.copy(discount = discount)
                if (discount > 0) {
                    val finalPrice = item.productPrice - (item.productPrice * discount / 100)
                    item.copy(productPrice = finalPrice)
                } else {
                    item

                }
            } else {
                item
            }
        }
        _listDetailsState.value = _listDetailsState.value.copy(
            shoppingDetailsItems = updateList,
            error = null,
            isLoading = false
        )

        debounceJob[shoppingItemId]?.cancel()
        debounceJob[shoppingItemId] = viewModelScope.launch {
            delay(1000)
            val localResult = updateDiscountL(shoppingItemId = shoppingItemId, discount = discount)
            if (localResult is Resource.Error) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    error = UiText.StringResourceId(Res.string.update_fail)
                )
            }

            val online = observer.isConnected.first()

            if (!online) {
                _listDetailsState.value = _listDetailsState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_internet_error)
                )
                return@launch
            }

            if (targetProductId != null) {
                val remoteResult = updateDiscountR(
                    billId = billId,
                    productId = targetProductId,
                    discount = discount
                )

                if(remoteResult is Resource.Error){
                    _listDetailsState.value = _listDetailsState.value.copy(
                        error = UiText.DynamicString(remoteResult.message!!),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getTotalAmount():Double?{
        val totalAmount = _listDetailsState.value.shoppingDetailsItems?.sumOf { item ->
            val itemCount = item.itemCount ?: 0
            val totalPrice = itemCount * item.productPrice
            val discount = (item.discount / 100.0) * totalPrice

            totalPrice - discount
        }

        return totalAmount
    }
}