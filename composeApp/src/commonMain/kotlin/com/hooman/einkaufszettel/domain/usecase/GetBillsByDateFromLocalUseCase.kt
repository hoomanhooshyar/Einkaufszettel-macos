package com.hooman.einkaufszettel.domain.usecase

import com.hooman.einkaufszettel.core.util.Resource
import com.hooman.einkaufszettel.domain.model.Bill
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime


class GetBillsByDateFromLocalUseCase(
    private val repository: LocalRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(startDate: Long, endDate: Long): Flow<Resource<List<Bill>>> {
        return repository.getBillByDate(startDate, endDate)
    }
}