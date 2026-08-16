package com.hooman.einkaufszettel.data.repository

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.data.local.entity.SyncStatus
import com.hooman.einkaufszettel.data.mapper.toShoppingItem
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import com.hooman.einkaufszettel.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val localRepository: LocalRepository,
    private val apiProduct: FirebaseProductRepository,
    private val apiBill: FirebaseBillRepository,
    private val apiItem: FirebaseShoppingItemRepository,
    private val authRepository: AuthRepository
): SyncRepository {
    override suspend fun syncDatabase() {
        withContext(Dispatchers.IO){
            try {
                //=================
                //1. Step: Push(Send data from Local to Server)
                //=================

                val userId = authRepository.getCurrentUserId()

                if(userId.isNullOrEmpty()) return@withContext

                //Product
                val productResource =
                    localRepository.getProductUnSyncData().first { it !is Resource.Loading }

                if(productResource is Resource.Success){
                    val productList = productResource.data ?: emptyList()

                    for (product in productList){
                        try {
                            val finalProduct = product.copy(userId = userId, syncStatus = SyncStatus.SUCCESS )
                            val response = apiProduct.insertProduct(finalProduct)
                            if(response is Resource.Success){
                                localRepository.updateProductSyncStatus(product.id, SyncStatus.SUCCESS)
                            }else{
                                localRepository.updateProductSyncStatus(product.id, SyncStatus.RSF)
                            }
                        }catch (e: Exception){
                            localRepository.updateProductSyncStatus(product.id, SyncStatus.RSF)
                        }
                    }
                }
                //Bill
                val billResource =
                    localRepository.getBillUnSyncData().first { it !is Resource.Loading }

                if(billResource is Resource.Success){
                    val billList = billResource.data ?: emptyList()

                    for(bill in billList){
                        try {
                            val finalBill = bill.copy(userId = userId, syncStatus = SyncStatus.SUCCESS)
                            val response = apiBill.insertBill(finalBill)
                            if(response is Resource.Success){
                                localRepository.updateBillSyncStatus(bill.id, SyncStatus.SUCCESS)
                            }else{
                                localRepository.updateBillSyncStatus(bill.id, SyncStatus.RSF)
                            }

                        }catch (e: Exception){
                            localRepository.updateBillSyncStatus(bill.id, SyncStatus.RSF)
                        }
                    }
                }

                //ShoppingItem
                val itemResource =
                    localRepository.getShoppingItemUnSyncData().first { it !is Resource.Loading }

                if(itemResource is Resource.Success){
                    val itemList = itemResource.data ?: emptyList()
                    for(item in itemList){
                        try {

                            val domainItem = item.toShoppingItem(currentUserId = userId)
                            val finalItem = domainItem.copy(syncStatus = SyncStatus.SUCCESS)
                            val response = apiItem.insertShoppingItem(finalItem)
                            if(response is Resource.Success){
                                localRepository.updateShoppingItemSyncStatus(item.shoppingItemId, SyncStatus.SUCCESS)
                            }else{
                                localRepository.updateShoppingItemSyncStatus(item.shoppingItemId, SyncStatus.RSF)
                            }
                        }catch (e: Exception){
                            localRepository.updateShoppingItemSyncStatus(item.shoppingItemId, SyncStatus.RSF)
                        }
                    }
                }

                // ==========================================
                // 2. Step: Pull (Get data from Server to Local)
                // ==========================================


                //Product
                val remoteProductResource = apiProduct.getAllProductsByUserId(userId).first{it !is Resource.Loading}
                if(remoteProductResource is Resource.Success){
                    val remoteProduct = remoteProductResource.data ?: emptyList()
                    if(remoteProduct.isNotEmpty()){
                        val syncProduct = remoteProduct.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        localRepository.insertProductList(syncProduct)
                    }
                }

                //Bill
                val remoteBillResource = apiBill.getAllBillsByUserId(userId).first{it !is Resource.Loading}
                if(remoteBillResource is Resource.Success){
                    val remoteBill = remoteBillResource.data ?: emptyList()
                    if(remoteBill.isNotEmpty()){
                        val syncBill = remoteBill.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        localRepository.insertBillList(syncBill)
                    }
                }

                //ShoppingItem
                val remoteItemResource = apiItem.getAllShoppingItemsByUserId(userId).first{it !is Resource.Loading}
                if(remoteItemResource is Resource.Success){
                    val remoteItem = remoteItemResource.data ?: emptyList()
                    if(remoteItem.isNotEmpty()){
                        val syncItem = remoteItem.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        localRepository.insertItemList(syncItem)
                    }
                }

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}