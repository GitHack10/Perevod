package com.perevod.perevodkassa

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import com.perevod.perevodkassa.di.dataModule
import com.perevod.perevodkassa.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.perevod.perevodkassa.di.navigationModule
import com.perevod.perevodkassa.di.viewModelModule
import timber.log.Timber

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
class AppDelegate : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        initKoin()
        initTimber()
    }

    private fun initKoin() {

        startKoin {
            androidContext(this@AppDelegate)
            modules(
                dataModule,
                domainModule,
                navigationModule,
                viewModelModule
            )
        }
    }

    private fun initTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}