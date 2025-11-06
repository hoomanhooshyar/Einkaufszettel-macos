package com.hooman.einkaufszettel.feature.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.source.FirebaseService
import com.hooman.einkaufszettel.domain.usecase.DeleteProductFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteProductFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToLocalUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.not_logged_in
import einkaufszettel.composeapp.generated.resources.product_delete_from_local
import einkaufszettel.composeapp.generated.resources.product_delete_from_remote
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(

    private val getProductsR: GetAllProductsByUserIdFromRemoteUseCase,
    private val getProductsL: GetAllProductsFromLocalUseCase,
    private val getOneProductL: GetProductByIdFromLocalUseCase,
    private val deleteProductL: DeleteProductFromLocalUseCase,
    private val deleteProductR: DeleteProductFromRemoteUseCase,
    private val insertProductL: InsertProductToLocalUseCase,
    private val observer: ConnectivityObserver,
    private val svc: FirebaseService,
    private val auth: AuthRepository
): ViewModel() {

    init {
        viewModelScope.launch {
            runCatching { svc.requiredUserId() }
        }
    }
    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state
            private val _deleteState = MutableStateFlow<String?>(null)
            val deleteState: StateFlow<String?> = _deleteState


    fun observeProduct(){
        viewModelScope.launch {
            getProductsL().collect { res ->
                when(res){
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            products = res.data!!,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            products = emptyList(),
                            isLoading = false,
                            errorMessage = if(res.message == null) UiText.StringResourceId(Res.string.unknown_error)
                            else UiText.DynamicString(res.message)
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            products = emptyList(),
                            errorMessage = null,
                            isLoading = true
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            observer.isConnected
                .onStart{ emit(false)}
                .distinctUntilChanged()
                .collect { online ->
                if(!online) return@collect
                    val uid = auth.getCurrentUserId()
                    if(uid == null){
                        _state.value = _state.value.copy(
                            products = emptyList(),
                            isLoading = false,
                            errorMessage = UiText.StringResourceId(Res.string.not_logged_in)
                        )
                        return@collect
                    }
                getProductsR(uid).collect { res ->
                    when(res){
                        is Resource.Success -> {
                            res.data?.forEach { product ->
                                insertProductL(product).collect {  }
                            }
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                errorMessage = if(res.message == null) UiText.StringResourceId(Res.string.unknown_error)
                                else UiText.DynamicString(res.message)
                            )
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                products = emptyList(),
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }
                }
            }

        }
    }

    fun deleteProduct(product: Product){
        deleteProductFromLocal(product)
        deleteProductFromRemote(product)
    }

    private fun deleteProductFromRemote(product: Product){
        viewModelScope.launch {
            deleteProductR(product.id).collect { res ->
                when(res){
                    is Resource.Success -> {
                        _deleteState.value = UiText.StringResourceId(Res.string.product_delete_from_remote).resolve()
                    }
                    is Resource.Error -> {
                        _deleteState.value =
                            res.message ?: UiText.StringResourceId(Res.string.unknown_error).resolve()
                    }
                    is Resource.Loading -> {
                        _deleteState.value = null
                    }
                }
            }
        }
    }

    private fun deleteProductFromLocal(product: Product){
        viewModelScope.launch {
            deleteProductL(product).collect { res ->
                when(res){
                    is Resource.Success -> {
                        _deleteState.value = UiText.StringResourceId(Res.string.product_delete_from_local).resolve()
                    }
                    is Resource.Error -> {
                        _deleteState.value =
                            res.message ?: UiText.StringResourceId(Res.string.unknown_error).resolve()
                    }
                    is Resource.Loading -> {
                        _deleteState.value = null
                    }
                }
            }
        }
    }

    fun clearDeleteState(){
        _deleteState.value = null
    }

}