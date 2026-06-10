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
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetCheckedProductsForShoppingItemFromLocal
import com.hooman.einkaufszettel.domain.usecase.InsertProductToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToRemoteUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.allStringResources
import einkaufszettel.composeapp.generated.resources.data_save_just_in_local
import einkaufszettel.composeapp.generated.resources.get_data_from_server
import einkaufszettel.composeapp.generated.resources.item_added_successfully
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddShoppingItemViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val insertItemL: InsertShoppingItemToLocalUseCase,
    private val insertItemR: InsertShoppingItemToRemoteUseCase,
    private val deleteItemR: DeleteShoppingItemFromRemoteUseCase,
    private val deleteItemL: DeleteShoppingItemFromLocalUseCase,
    private val getAllProductsL: GetAllProductsFromLocalUseCase,
    private val getAllProductsR: GetAllProductsByUserIdFromRemoteUseCase,
    private val getCheckedProductsForShoppingItemL: GetCheckedProductsForShoppingItemFromLocal,
    private val insertProductL: InsertProductToLocalUseCase,
    private val authRepository: AuthRepository,

    private val observer: ConnectivityObserver
): ViewModel() {

    private val _addBillItemState = MutableStateFlow(AddShoppingItemState())
    val addBillItemState = _addBillItemState.asStateFlow()

    private val _userId = MutableStateFlow(authRepository.getCurrentUserId())
    val userId = _userId.asStateFlow()

    val billId = savedStateHandle.toRoute<Routes.AddShoppingItem>().billId
    init {
        observeItems()
        getCheckedProductFromShoppingItemList(billId)
    }

    private fun observeItems(){
        viewModelScope.launch {
            getAllProductsL().collect { res ->
                when(res){
                    is Resource.Success ->{

                        if(res.data.isNullOrEmpty()){
                            _addBillItemState.value = _addBillItemState.value.copy(isLoading = true)
                            getAllProductFromRemote(_userId.value!!)
                        }else{
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

    fun insertShoppingItemIntoBill(shoppingItem: ShoppingItem){
        _addBillItemState.value = _addBillItemState.value.copy(
            isLoading = true,
            error = null
        )
        viewModelScope.launch {
            val localResult = insertItemL(shoppingItem,billId)

            if(localResult is Resource.Error){
                _addBillItemState.value = _addBillItemState.value.copy(
                    isLoading = false,
                    error = UiText.DynamicString(localResult.message!!)
                )

                return@launch
            }
            val online = observer.isConnected.first()
            if(!online){
                _addBillItemState.value = _addBillItemState.value.copy(
                    error = UiText.StringResourceId(Res.string.data_save_just_in_local),
                    isLoading = false
                )
                return@launch
            }
            val remoteResult = insertItemR(shoppingItem)

            when(remoteResult){
                is Resource.Success ->{
                    _addBillItemState.value = _addBillItemState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.item_added_successfully)
                    )
                }
                is Resource.Error ->{
                    _addBillItemState.value = _addBillItemState.value.copy(
                        isLoading = false,
                        error = UiText.DynamicString(remoteResult.message!!)
                    )
                }
                is Resource.Loading ->{}
            }
        }
    }


    fun removeShoppingItemByProductId(shoppingItemId: String){
        _addBillItemState.value = _addBillItemState.value.copy(
            error = null,
            isLoading = true
        )
        viewModelScope.launch {
            val localResult = deleteItemL(shoppingItemId = shoppingItemId)

            if(localResult is Resource.Error){
                _addBillItemState.value = _addBillItemState.value.copy(
                    isLoading = false,
                    error = UiText.DynamicString(localResult.message!!)
                )
                return@launch
            }
            val online = observer.isConnected.first()
            if(!online){
                _addBillItemState.value = _addBillItemState.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.data_save_just_in_local)
                )
                return@launch
            }

            val remoteResult = deleteItemR(billId, shoppingItemId)

            if(remoteResult is Resource.Error){
                _addBillItemState.value = _addBillItemState.value.copy(
                    error = UiText.DynamicString(remoteResult.message!!)
                )
                return@launch
            }
        }
    }

    private fun getCheckedProductFromShoppingItemList(billId: String){
        viewModelScope.launch {
            getCheckedProductsForShoppingItemL(billId).collect { res ->
                if(res is Resource.Success){
                    _addBillItemState.value = _addBillItemState.value.copy(
                        checkedProductIds = res.data?.toSet() ?: emptySet()
                    )
                }
            }
        }
    }

    private fun     getAllProductFromRemote(userId: String){
        viewModelScope.launch {
            getAllProductsR(userId).collect { res ->
                when(res){
                    is Resource.Success ->{
                        insertProductIntoLocal(res.data)
                        _addBillItemState.value = _addBillItemState.value.copy(
                            isLoading = true,
                            error = null,
                        )
                    }
                    is Resource.Error ->{
                        _addBillItemState.value = _addBillItemState.value.copy(
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                    is Resource.Loading ->{

                    }
                }
            }
        }
    }

    private fun insertProductIntoLocal(products: List<Product>?){
        viewModelScope.launch {
            products?.forEach { product ->
                val result = insertProductL(product)
                if(result is Resource.Error){
                    print("Error in inserting product into local- ${result.message}")
                }
            }
            _addBillItemState.value = _addBillItemState.value.copy(isLoading = false)
        }
    }
}