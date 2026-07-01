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
import einkaufszettel.composeapp.generated.resources.delete_product_fail
import einkaufszettel.composeapp.generated.resources.delete_product_remote_fail
import einkaufszettel.composeapp.generated.resources.no_internet_error
import einkaufszettel.composeapp.generated.resources.no_products
import einkaufszettel.composeapp.generated.resources.not_logged_in
import einkaufszettel.composeapp.generated.resources.product_delete_from_local
import einkaufszettel.composeapp.generated.resources.product_delete_from_remote
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(

    private val getProductsR: GetAllProductsByUserIdFromRemoteUseCase,
    private val getProductsL: GetAllProductsFromLocalUseCase,
    private val deleteProductL: DeleteProductFromLocalUseCase,
    private val deleteProductR: DeleteProductFromRemoteUseCase,
    private val insertProductL: InsertProductToLocalUseCase,
    private val observer: ConnectivityObserver,
    private val auth: AuthRepository
): ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()



    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()
    private val _deleteState = MutableStateFlow<UiText?>(null)
    val deleteState: StateFlow<UiText?> = _deleteState.asStateFlow()

    private var isInitialLoad = true


    init {
        _userId.value = auth.getCurrentUserId()
        observeProduct()
    }

    fun observeProduct(){
        viewModelScope.launch {
            getProductsL().collect { res ->
                when(res){
                    is Resource.Success ->{
                        val products = res.data ?: emptyList()
                        if(products.isEmpty() && isInitialLoad){
                            isInitialLoad = false
                            _state.value = _state.value.copy(isLoading = true)
                            val currentUser = _userId.value
                            if(!currentUser.isNullOrEmpty()){
                                getAllProductsFromRemote(currentUser)

                            }
                        }else{
                            isInitialLoad = false
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = null,
                                products = products
                            )
                        }
                    }
                    is Resource.Error ->{
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                    is Resource.Loading ->{
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun getAllProductsFromRemote(userId: String){
        viewModelScope.launch {
            val online = observer.isConnected.first()
            if(!online){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_internet_error)
                )
                return@launch
            }
            if(userId.isEmpty()){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.not_logged_in)
                )
                return@launch
            }
            getProductsR(userId).collect { res ->
                when(res){
                    is Resource.Success ->{
                        insertProductIntoLocal(res.data)
                    }
                    is Resource.Error ->{
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = UiText.DynamicString(res.message!!)
                        )
                    }
                    is Resource.Loading ->{}
                }
            }
        }
    }

    private fun insertProductIntoLocal(products: List<Product>?){
        viewModelScope.launch {
            if(products.isNullOrEmpty()){
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResourceId(Res.string.no_products),
                    products = emptyList()
                )
                return@launch
            }

            products.forEach { product ->
                val result = insertProductL(product)
                if(result is Resource.Error){
                    print("Error in inserting product into local- ${result.message}")
                }
            }
        }
    }

    fun deleteProduct(product: Product){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val localResult = deleteProductL(product)
            if(localResult is Resource.Error){
                _deleteState.value = UiText.StringResourceId(Res.string.delete_product_fail)
            }else if(localResult is Resource.Success){
                _deleteState.value = UiText.StringResourceId(Res.string.product_delete_from_local)
            }
            val online = observer.isConnected.first()
            if(online){
                val remoteResult = deleteProductR(product.id)
                if(remoteResult is Resource.Error){
                    _deleteState.value = UiText.StringResourceId(Res.string.delete_product_remote_fail)
                }else if(remoteResult is Resource.Success){
                    _deleteState.value = UiText.StringResourceId(Res.string.product_delete_from_remote)
                }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }


    fun clearDeleteState(){
        _deleteState.value = null
    }

    fun clearError(){
        _state.value = _state.value.copy(error = null)
    }

}