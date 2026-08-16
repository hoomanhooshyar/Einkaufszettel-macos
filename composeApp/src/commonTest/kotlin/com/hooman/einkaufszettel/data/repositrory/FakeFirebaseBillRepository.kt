package com.hooman.einkaufszettel.data.repositrory

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFirebaseBillRepository: FirebaseBillRepository {

    var hasInternet = true
    var shouldErrorThrow = false

    val remoteBills = MutableStateFlow<List<Bill>>(emptyList())

    override suspend fun insertBill(bill: Bill): Resource<Unit> {
        if(!hasInternet) return Resource.Error("No Internet Connection")
        if(shouldErrorThrow) return Resource.Error("Firebase Server Error")

        remoteBills.update { currentBills ->
            currentBills.filterNot { it.id == bill.id } + bill
        }
        return Resource.Success(Unit)
    }

    override fun getAllBillsByUserId(userId: String): Flow<Resource<List<Bill>>> {
        return remoteBills.map { bills ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server Error")
            }else{
                val userBills = bills.filter { it.userId == userId }
                Resource.Success(userBills)
            }
        }
    }

    override fun getBillById(billId: String): Flow<Resource<Bill>> {
        return remoteBills.map { bills ->
            if(!hasInternet){
                Resource.Error("No Internet Connection")
            }else if(shouldErrorThrow){
                Resource.Error("Firebase Server  has Error")
            }else{
                val bill = bills.filter { it.id == billId }
                Resource.Success(bill.first())
            }
        }
    }

    override suspend fun deleteBill(billId: String): Resource<Unit> {
        if(!hasInternet) return Resource.Error("No Internet Connection")
        if(shouldErrorThrow) return Resource.Error("Firebase Server has Error")
        remoteBills.update { currentBills ->
            currentBills.filterNot { it.id == billId }
        }
        return Resource.Success(Unit)
    }
}