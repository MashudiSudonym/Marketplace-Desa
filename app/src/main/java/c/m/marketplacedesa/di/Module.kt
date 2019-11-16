package c.m.marketplacedesa.di

import c.m.marketplacedesa.data.remote.RemoteRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { RemoteRepository() }
}

val viewModelModule = module {

}