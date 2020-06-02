package ar.edu.unq.pdes.myprivateblog.instrumentation

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.BaseApplication
import ar.edu.unq.pdes.myprivateblog.di.*
import ar.edu.unq.pdes.myprivateblog.services.AuthenticationService
import ar.edu.unq.pdes.myprivateblog.services.MockAuthService
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        MainActivityModule::class,
        GoogleAnalytics::class,
        FakeAuthModule::class
    ]
)
interface TestApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): TestApplicationComponent
    }
}

@Module
open class FakeAuthModule {

    @Singleton
    @Provides
    open fun provideAuthService(): AuthenticationService {
        return MockAuthService()
    }
}