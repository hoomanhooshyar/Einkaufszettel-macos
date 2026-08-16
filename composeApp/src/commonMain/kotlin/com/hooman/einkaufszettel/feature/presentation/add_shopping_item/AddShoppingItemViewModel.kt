package com.hooman.einkaufszettel.feature.presentation.add_shopping_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemByProductAndBillFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemByProductAndBillFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetCheckedProductsForShoppingItemFromLocal
import com.hooman.einkaufszettel.domain.usecase.InsertProductListToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.data_save_just_in_local
import einkaufszettel.composeapp.generated.resources.item_added_successfully
import einkaufszettel.composeapp.generated.resources.no_products
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddShoppingItemViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val insertItemL: InsertShoppingItemToLocalUseCase,
    private val deleteItemR: DeleteShoppingItemByProductAndBillFromRemoteUseCase,
    private val deleteItemL: DeleteShoppingItemByProductAndBillFromLocalUseCase,
    private val getAllProductsL: GetAllProductsFromLocalUseCase,
    private val getAllProductsR: GetAllProductsByUserIdFromRemoteUseCase,
    private val getCheckedProductsForShoppingItemL: GetCheckedProductsForShoppingItemFromLocal,
    private val insertProductListL: InsertProductListToLocalUseCase,
    private val authRepository: AuthRepository,
    private val syncDatabaseUseCase: SyncDatabaseUseCase,
    private val observer: ConnectivityObserver
) : ViewModel() {

    private val _addBillItemState = MutableStateFlow(AddShoppingItemState())
    val addBillItemState = _addBillItemState.asStateFlow()

    private val _userId = MutableStateFlow(authRepository.getCurrentUserId())
    val userId = _userId.asStateFlow()

    val billId = savedStateHandle.toRoute<Routes.AddShoppingItem>().billId

    init {
        observeItems()
        getCheckedProductFromShoppingItemList(billId)
    }

    private fun observeItems() {
        viewModelScope.launch {
            getAllProductsL().collect { res ->
                when (res) {
                    is Resource.Success -> {

                        if (res.data.isNullOrEmpty()) {
                            _addBillItemState.value = _addBillItemState.value.copy(isLoading = true)
                            getAllProductFromRemote(_userId.value!!)
                        } else {
                            _addBillItemState.value = _addBillItemState.value.copy(
                                isLoading = false,
                                error = null,
                                products = res.data
                            )
                        }

                    }

                    is Resource.Error -> {
                        _addBillItemState.value = _addBillItemState.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )


                    }

                    is Resource.Loading -> {
                        _addBillItemState.value = _addBillItemState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }

            }
        }
    }

    fun insertShoppingItemIntoBill(shoppingItem: ShoppingItem) {
        _addBillItemState.value = _addBillItemState.value.copy(
            isLoading = true,
            error = null
        )
        viewModelScope.launch {
            if(_userId.value?.isNotEmpty() == true && shoppingItem.userId.equals("")){

                shoppingItem.copy(userId = _userId.value!!)
            }
            val localResult = insertItemL(shoppingItem, billId)
            when (localResult) {
                is Resource.Success -> {
                    _addBillItemState.value = _addBillItemState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.item_added_successfully)
                    )
                    triggerBackgroundSync()
                }

                is Resource.Error -> {
                    _addBillItemState.value = _addBillItemState.value.copy(
                        isLoading = false,
                        error = UiText.DynamicString(localResult.message!!)
                    )
                }

                is Resource.Loading -> {}
            }

        }
    }


    fun removeShoppingItemByProductIdAndBillId(productId: String) {
        _addBillItemState.value = _addBillItemState.value.copy(
            error = null,
            isLoading = true
        )
        viewModelScope.launch {
            val localResult = deleteItemL(
                billId = billId,
                productId = productId
            )

            if (localResult is Resource.Error) {
                _addBillItemState.value = _addBillItemState.value.copy(
                    isLoading = false,
                    error = UiText.DynamicString(localResult.message!!)
                )
                return@launch
            }
            val online = observer.isConnected.first()
            if (!online) {
                _addBillItemState.value = _addBillItemState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.data_save_just_in_local)
                )
                return@launch
            }

            val remoteResult = deleteItemR(billId, productId)

            if (remoteResult is Resource.Error) {
                _addBillItemState.value = _addBillItemState.value.copy(
                    error = UiText.DynamicString(remoteResult.message!!)
                )
                return@launch
            }
        }
    }

    fun clearError() {

        _addBillItemState.value = _addBillItemState.value.copy(error = null)

    }

    private fun getCheckedProductFromShoppingItemList(billId: String) {
        viewModelScope.launch {
            getCheckedProductsForShoppingItemL(billId).collect { res ->
                if (res is Resource.Success) {
                    _addBillItemState.value = _addBillItemState.value.copy(
                        checkedProductIds = res.data?.toSet() ?: emptySet()
                    )
                }
            }
        }
    }

    private fun getAllProductFromRemote(userId: String) {
        viewModelScope.launch {
            getAllProductsR(userId).collect { res ->
                when (res) {
                    is Resource.Success -> {
                        insertProductIntoLocal(res.data)
                        _addBillItemState.value = _addBillItemState.value.copy(
                            isLoading = true,
                            error = null,
                        )
                    }

                    is Resource.Error -> {
                        _addBillItemState.value = _addBillItemState.value.copy(
                            error = UiText.DynamicString(res.message!!)
                        )
                    }

                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    private fun insertProductIntoLocal(products: List<Product>?) {
        viewModelScope.launch {
            if (products.isNullOrEmpty()) {
                _addBillItemState.value = _addBillItemState.value.copy(
                    error = UiText.StringResourceId(Res.string.no_products)
                )
            } else {
                insertProductListL(products)
            }

            _addBillItemState.value = _addBillItemState.value.copy(isLoading = false)
        }
    }

    private fun triggerBackgroundSync() {
        viewModelScope.launch {
            try {
                syncDatabaseUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}