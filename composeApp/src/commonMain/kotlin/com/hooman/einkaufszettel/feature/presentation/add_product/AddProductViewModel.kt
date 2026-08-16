package com.hooman.einkaufszettel.feature.presentation.add_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.hooman.einkaufszettel.app.Routes
import com.hooman.einkaufszettel.core.network.ConnectivityObserver
import com.hooman.einkaufszettel.core.presentation.UiText
import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.domain.model.Product
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.usecase.GetProductByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductIconsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductIconsFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.SyncDatabaseUseCase
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.data_save_just_in_local
import einkaufszettel.composeapp.generated.resources.item_added_successfully
import einkaufszettel.composeapp.generated.resources.product_added_successfully
import einkaufszettel.composeapp.generated.resources.unknown_error
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddProductViewModel(
    private val savedStateHandle: SavedStateHandle,//این رو اضافه کردم
    private val insertProductL: InsertProductToLocalUseCase,
    private val syncDatabaseUseCase: SyncDatabaseUseCase,
    private val authRepository: AuthRepository,
    private val observer: ConnectivityObserver,
    private val productIconsR: GetProductIconsFromRemoteUseCase,
    private val productIconsL: GetProductIconsFromLocalUseCase,
    private val getProductL: GetProductByIdFromLocalUseCase //این رو اضافه کردم
) : ViewModel() {
    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _userId = authRepository.getCurrentUserId()
    private val oldProductId =
        savedStateHandle.toRoute<Routes.AddProduct>().productId //این رو اضافه کردم

    init {
        getProduct()
        getProductIcons()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addProduct(
        productName: String,
        productPrice: String,
        productImage: String
    ) {
        viewModelScope.launch {
            _addProductState.value = _addProductState.value.copy(
                error = null,
                isLoading = true
            )
            val productId: String = if (oldProductId.isNullOrEmpty()) {
                Uuid.random().toString()
            } else {
                oldProductId
            }
            val product = Product(
                id = productId,
                name = productName,
                price = productPrice.replace(",", ".").toDoubleOrNull() ?: 0.0,
                image = productImage,
                userId = if(_userId.isNullOrEmpty()) "" else _userId,
                syncStatus = SyncStatus.LSL
            )
            val localResult = insertProductL(product)
            when(localResult){
                is Resource.Success ->{
                    _addProductState.value = _addProductState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.product_added_successfully)
                    )

                    triggerBackgroundSync()
                }
                is Resource.Loading ->{}
                is Resource.Error ->{
                    _addProductState.value = _addProductState.value.copy(
                        isLoading = false,
                        error = UiText.StringResourceId(Res.string.unknown_error)
                    )
                }
            }

        }
    }

    fun getProductIcons() {
        viewModelScope.launch {
            val online = observer.isConnected.first()
            if (online) {
                productIconsR().collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                error = null,
                                productIcons = res.data!!
                            )
                        }

                        is Resource.Error -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                error = UiText.DynamicString(res.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }

                    }
                }
            } else {
                productIconsL().collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                error = null,
                                productIcons = res.data!!
                            )
                        }

                        is Resource.Error -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                error = UiText.DynamicString(res.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                }

            }
        }
    }

    //این رو اضافه کردم
    fun getProduct() {
        if (oldProductId != null) {
            viewModelScope.launch {
                getProductL(oldProductId).collect { res ->
                    when (res) {
                        is Resource.Success -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                error = null,
                                oldProduct = res.data
                            )
                        }

                        is Resource.Loading -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = true,
                                error = null
                            )
                        }

                        is Resource.Error -> {
                            _addProductState.value = _addProductState.value.copy(
                                error = UiText.DynamicString(res.message!!),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _addProductState.value = _addProductState.value.copy(
            error = null
        )
    }

    private fun triggerBackgroundSync(){
        viewModelScope.launch {
            try {
                syncDatabaseUseCase()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}