package c.m.marketplacedesa

import android.app.Application
import c.m.marketplacedesa.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MarketplaceDesaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Koin
        startKoin {
            androidLogger()
            androidContext(this@MarketplaceDesaApplication)
            modules(
                listOf(
                    repositoryModule
                )
            )
        }
    }
}