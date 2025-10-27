package com.hooman.einkaufszettel.core.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.hooman.einkaufszettel.data.local.dao.AppDao
import com.hooman.einkaufszettel.data.local.db.AppDatabase
import com.hooman.einkaufszettel.data.local.db.DatabaseConstructor
import com.hooman.einkaufszettel.data.local.db.DatabaseFactory
import com.hooman.einkaufszettel.data.remote.FirebaseBillDataSourceImpl
import com.hooman.einkaufszettel.data.remote.FirebaseProductDataSourceImpl
import com.hooman.einkaufszettel.data.remote.FirebaseServiceImpl
import com.hooman.einkaufszettel.data.remote.FirebaseShoppingItemDataSourceImpl
import com.hooman.einkaufszettel.data.repository.AuthRepositoryImpl
import com.hooman.einkaufszettel.data.repository.FirebaseBillRepositoryImpl
import com.hooman.einkaufszettel.data.repository.FirebaseProductRepositoryImpl
import com.hooman.einkaufszettel.data.repository.FirebaseShoppingItemRepositoryImpl
import com.hooman.einkaufszettel.data.repository.LocalRepositoryImpl
import com.hooman.einkaufszettel.domain.repository.AuthRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseBillRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseProductRepository
import com.hooman.einkaufszettel.domain.repository.FirebaseShoppingItemRepository
import com.hooman.einkaufszettel.domain.repository.LocalRepository
import com.hooman.einkaufszettel.domain.source.FirebaseBillDataSource
import com.hooman.einkaufszettel.domain.source.FirebaseProductDataSource
import com.hooman.einkaufszettel.domain.source.FirebaseService
import com.hooman.einkaufszettel.domain.source.FirebaseShoppingItemDataSource
import com.hooman.einkaufszettel.domain.usecase.DeleteBillFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteBillFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteProductFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteProductFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.DeleteShoppingItemFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllBillsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllProductsFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetAllShoppingItemsByUserIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetBillByIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByIdFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByNameFromLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.GetProductByNameFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.GetShoppingItemByBillIdFromRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertBillToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertBillToRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertProductToRemoteUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToLocalUseCase
import com.hooman.einkaufszettel.domain.usecase.InsertShoppingItemToRemoteUseCase
import com.hooman.einkaufszettel.feature.presentation.create_list.CreateListViewModel
import com.hooman.einkaufszettel.feature.presentation.home.HomeViewModel
import com.hooman.einkaufszettel.feature.presentation.list_details.ListDetailsViewModel
import com.hooman.einkaufszettel.feature.presentation.login.LoginViewModel
import com.hooman.einkaufszettel.feature.presentation.main.MainViewModel
import com.hooman.einkaufszettel.feature.presentation.product.ProductViewModel
import com.hooman.einkaufszettel.feature.presentation.report.ReportsViewModel
import com.hooman.einkaufszettel.feature.presentation.settings.SettingsViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module
val sharedModule = module {

    //Firebase

    single<FirebaseService> { FirebaseServiceImpl() }
    single<FirebaseBillDataSource> { FirebaseBillDataSourceImpl(get()) }
    single<FirebaseProductDataSource> { FirebaseProductDataSourceImpl(get()) }
    single<FirebaseShoppingItemDataSource> { FirebaseShoppingItemDataSourceImpl(get()) }
    single<FirebaseAuth> { Firebase.auth }

    //AuthRepository
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    //Repositories

    singleOf(::FirebaseBillRepositoryImpl).bind<FirebaseBillRepository>()
    singleOf(::FirebaseProductRepositoryImpl).bind<FirebaseProductRepository>()
    singleOf(::FirebaseShoppingItemRepositoryImpl).bind<FirebaseShoppingItemRepository>()
    singleOf(::LocalRepositoryImpl).bind<LocalRepository>()

    //Database
    single<AppDatabase> {
        DatabaseConstructor.initialize()
    }

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    single {
        get<AppDatabase>().dao
    }

    //ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateListViewModel)
    viewModelOf(::ListDetailsViewModel)
    viewModelOf(::ProductViewModel)
    viewModelOf(::ReportsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)




}

val useCaseModule = module {

    //Bill
    factory { DeleteBillFromLocalUseCase(get()) }
    factory { DeleteBillFromRemoteUseCase(get()) }
    factory { GetAllBillsFromLocalUseCase(get()) }
    factory { GetAllBillsByUserIdFromRemoteUseCase(get()) }
    factory { GetBillByIdFromLocalUseCase(get()) }
    factory { GetBillByIdFromRemoteUseCase(get()) }
    factory { InsertBillToLocalUseCase(get()) }
    factory { InsertBillToRemoteUseCase(get()) }

    //Product
    factory { DeleteProductFromLocalUseCase(get()) }
    factory { DeleteProductFromRemoteUseCase(get()) }
    factory { GetAllProductsByUserIdFromRemoteUseCase(get()) }
    factory { GetAllProductsFromLocalUseCase(get()) }
    factory { GetProductByIdFromLocalUseCase(get()) }
    factory { GetProductByIdFromRemoteUseCase(get()) }
    factory { GetProductByNameFromLocalUseCase(get()) }
    factory { GetProductByNameFromRemoteUseCase(get()) }
    factory { InsertProductToLocalUseCase(get()) }
    factory { InsertProductToRemoteUseCase(get()) }

    //ShoppingItem
    factory { DeleteShoppingItemFromLocalUseCase(get()) }
    factory { DeleteShoppingItemFromRemoteUseCase(get()) }
    factory { GetAllShoppingItemsByUserIdFromRemoteUseCase(get()) }
    factory { GetShoppingItemByBillIdFromRemoteUseCase(get()) }
    factory { InsertShoppingItemToRemoteUseCase(get()) }
    factory { InsertShoppingItemToLocalUseCase(get()) }

 }