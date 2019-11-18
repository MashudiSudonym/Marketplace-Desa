package c.m.marketplacedesa.di

import c.m.marketplacedesa.data.remote.RemoteRepository
import c.m.marketplacedesa.ui.user.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { RemoteRepository() }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}