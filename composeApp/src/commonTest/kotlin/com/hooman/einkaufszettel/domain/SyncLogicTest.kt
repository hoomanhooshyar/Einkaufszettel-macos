package com.hooman.einkaufszettel.domain

import com.hooman.einkaufszettel.data.local.dao.FakeAppDao
import com.hooman.einkaufszettel.data.repositrory.FakeLocalRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository

class SyncLogicTest{
    private lateinit var dao: FakeAppDao

    private lateinit var localRepository = FakeLocalRepository()
}