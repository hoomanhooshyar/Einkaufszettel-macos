package com.hooman.einkaufszettel.data.repositrory

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.ShoppingItem
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFirebaseShoppingItemRepository: FirebaseShoppingItemRepository {
    var hasInternet = true
    var shouldErrorThrow = false
    val noInternetError = "No Internet Connection"
    val serverError = "Firebase Server Error"
    val remoteItems = MutableStateFlow<List<ShoppingItem>>(emptyList())

    override suspend fun insertShoppingItem(shoppingItem: ShoppingItem): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)

        remoteItems.update { currentRemotes ->
            currentRemotes.filterNot { it.id == shoppingItem.id } + shoppingItem
        }
        return Resource.Success(Unit)
    }

    override fun getAllShoppingItemsByUserId(userId: String): Flow<Resource<List<ShoppingItem>>> {
        return remoteItems.map { items ->
            if(!hasInternet){
                Resource.Error(noInternetError)
            }else if(shouldErrorThrow){
                Resource.Error(serverError)
            }else{
                val userItems = items.filter { it.userId == userId }
                Resource.Success(userItems)
            }
        }
    }

    override suspend fun deleteShoppingItem(
        billId: String,
        shoppingItemId: String
    ): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)
        remoteItems.update { currentItems ->
            currentItems.filterNot { it.id == shoppingItemId && it.billId == billId }
        }
        return Resource.Success(Unit)
    }

    override suspend fun deleteShoppingItemByProductAndBill(
        billId: String,
        productId: String
    ): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)
        remoteItems.update { currentItems ->
            currentItems.filterNot { it.billId == billId && it.productId == productId }
        }
        return Resource.Success(Unit)
    }

    override fun getShoppingItemByBillId(billId: String): Flow<Resource<List<ShoppingItem>>> {
        return remoteItems.map { items ->
           if(!hasInternet){
               Resource.Error(noInternetError)
           }else if(shouldErrorThrow){
               Resource.Error(serverError)
           }else{
               val billItem = items.filter { it.billId == billId }
               Resource.Success(billItem)
           }
        }
    }

    override suspend fun updateShoppingItemCheckStatus(
        billId: String,
        itemId: String,
        isChecked: Boolean
    ): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)
        remoteItems.update { items ->
            items.map {
                if(it.billId == billId && it.id == itemId){
                    it.copy(isChecked = isChecked)
                }else{
                    it
                }

            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun updateShoppingItemCount(
        billId: String,
        productId: String,
        itemCount: Int
    ): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)
        remoteItems.update { items ->
            items.map {
                if(it.billId == billId && it.productId == productId){
                    it.copy(itemCount = itemCount)
                }else{
                    it
                }
            }
        }
        return Resource.Success(Unit)
    }

    override suspend fun updateShoppingItemDiscount(
        billId: String,
        productId: String,
        discount: Float
    ): Resource<Unit> {
        if(!hasInternet) return Resource.Error(noInternetError)
        if(shouldErrorThrow) return Resource.Error(serverError)
        remoteItems.update { items ->
            items.map {
                if(it.billId == billId && it.productId == productId){
                    it.copy(discount = discount)
                }else{
                    it
                }
            }
        }
        return Resource.Success(Unit)
    }
}