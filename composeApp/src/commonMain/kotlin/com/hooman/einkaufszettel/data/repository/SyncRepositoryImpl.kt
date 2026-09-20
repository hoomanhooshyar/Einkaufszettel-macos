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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SyncRepositoryImpl(
    private val localRepository: LocalRepository,
    private val apiProduct: FirebaseProductRepository,
    private val apiBill: FirebaseBillRepository,
    private val apiItem: FirebaseShoppingItemRepository,
    private val authRepository: AuthRepository
): SyncRepository {
    private val syncMutex = Mutex()

    override suspend fun syncDatabase() = syncMutex.withLock {
        withContext(Dispatchers.IO){
            try {
                //=================
                //1. Step: Push(Send data from Local to Server)
                //=================

                val userId = authRepository.getCurrentUserId()

                if(userId.isNullOrEmpty()) return@withContext

                fun ensureSameAccount() {
                    if (authRepository.getCurrentUserId() != userId) {
                        throw CancellationException("Account changed during synchronization")
                    }
                }

                //Product
                val productResource =
                    localRepository.getProductUnSyncData().first { it !is Resource.Loading }

                if(productResource is Resource.Success){
                    val productList = productResource.data ?: emptyList()

                    for (product in productList){
                        try {
                            ensureSameAccount()
                            require(product.userId == userId)
                            val finalProduct = product.copy(syncStatus = SyncStatus.SUCCESS)
                            val response = apiProduct.insertProduct(finalProduct)
                            if(response is Resource.Success){
                                ensureSameAccount()
                                localRepository.acknowledgeProduct(product, SyncStatus.SUCCESS)
                            }else{
                                ensureSameAccount()
                                localRepository.acknowledgeProduct(product, SyncStatus.RSF)
                            }
                        }catch (e: Exception){
                            if (e is CancellationException) throw e
                            ensureSameAccount()
                            localRepository.acknowledgeProduct(product, SyncStatus.RSF)
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
                            ensureSameAccount()
                            require(bill.userId == userId)
                            val finalBill = bill.copy(syncStatus = SyncStatus.SUCCESS)
                            val response = apiBill.insertBill(finalBill)
                            if(response is Resource.Success){
                                ensureSameAccount()
                                localRepository.acknowledgeBill(bill, SyncStatus.SUCCESS)
                            }else{
                                ensureSameAccount()
                                localRepository.acknowledgeBill(bill, SyncStatus.RSF)
                            }

                        }catch (e: Exception){
                            if (e is CancellationException) throw e
                            ensureSameAccount()
                            localRepository.acknowledgeBill(bill, SyncStatus.RSF)
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

                            ensureSameAccount()
                            require(item.userId == userId)
                            val domainItem = item.toShoppingItem(currentUserId = userId)
                            val finalItem = domainItem.copy(syncStatus = SyncStatus.SUCCESS)
                            val response = apiItem.insertShoppingItem(finalItem)
                            if(response is Resource.Success){
                                ensureSameAccount()
                                localRepository.acknowledgeShoppingItem(item.toShoppingItem(currentUserId = userId), SyncStatus.SUCCESS)
                            }else{
                                ensureSameAccount()
                                localRepository.acknowledgeShoppingItem(item.toShoppingItem(currentUserId = userId), SyncStatus.RSF)
                            }
                        }catch (e: Exception){
                            if (e is CancellationException) throw e
                            ensureSameAccount()
                            localRepository.acknowledgeShoppingItem(item.toShoppingItem(currentUserId = userId), SyncStatus.RSF)
                        }
                    }
                }

                // ==========================================
                // 2. Step: Pull (Get data from Server to Local)
                // ==========================================


                //Product
                ensureSameAccount()
                val remoteProductResource = apiProduct.getAllProductsByUserId(userId).first{it !is Resource.Loading}
                if(remoteProductResource is Resource.Success){
                    val remoteProduct = remoteProductResource.data ?: emptyList()
                    if(remoteProduct.isNotEmpty()){
                        val syncProduct = remoteProduct.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        ensureSameAccount()
                        localRepository.insertProductList(syncProduct)
                    }
                }

                //Bill
                ensureSameAccount()
                val remoteBillResource = apiBill.getAllBillsByUserId(userId).first{it !is Resource.Loading}
                if(remoteBillResource is Resource.Success){
                    val remoteBill = remoteBillResource.data ?: emptyList()
                    if(remoteBill.isNotEmpty()){
                        val syncBill = remoteBill.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        ensureSameAccount()
                        localRepository.insertBillList(syncBill)
                    }
                }

                //ShoppingItem
                ensureSameAccount()
                val remoteItemResource = apiItem.getAllShoppingItemsByUserId(userId).first{it !is Resource.Loading}
                if(remoteItemResource is Resource.Success){
                    val remoteItem = remoteItemResource.data ?: emptyList()
                    if(remoteItem.isNotEmpty()){
                        val syncItem = remoteItem.map { it.copy(syncStatus = SyncStatus.SUCCESS) }
                        ensureSameAccount()
                        localRepository.insertItemList(syncItem)
                    }
                }

            }catch (e: Exception){
                if (e is CancellationException) throw e
                e.printStackTrace()
            }
        }
    }
}
