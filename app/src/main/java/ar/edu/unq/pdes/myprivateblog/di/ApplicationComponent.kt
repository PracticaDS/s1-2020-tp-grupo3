package ar.edu.unq.pdes.myprivateblog.di

import android.content.Context
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.BaseApplication
import ar.edu.unq.pdes.myprivateblog.MainActivity
import ar.edu.unq.pdes.myprivateblog.MainActivityViewModel
import ar.edu.unq.pdes.myprivateblog.data.*
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateFragment
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateViewModel
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateFragment
import ar.edu.unq.pdes.myprivateblog.screens.post_create.PostCreateViewModel
import ar.edu.unq.pdes.myprivateblog.screens.post_detail.PostDetailFragment
import ar.edu.unq.pdes.myprivateblog.screens.post_detail.PostDetailViewModel
import ar.edu.unq.pdes.myprivateblog.screens.post_edit.PostEditFragment
import ar.edu.unq.pdes.myprivateblog.screens.post_edit.PostEditViewModel
import ar.edu.unq.pdes.myprivateblog.screens.posts_listing.PostsListingFragment
import ar.edu.unq.pdes.myprivateblog.screens.posts_listing.PostsListingViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.*
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        MainActivityModule::class,
        GoogleAnalytics::class
    ])
interface ApplicationComponent : AndroidInjector<BaseApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}

@Module
open class ApplicationModule {

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.generateDatabase(context)
    }

    @Singleton
    @Provides
    fun provideBlogEntriesRepository(appDatabase: AppDatabase): BlogEntriesRepository {
        return BlogEntriesRepository(appDatabase)
    }
}

@Module
open class GoogleAnalytics {

    @Singleton
    @Provides
    fun provideTrackEvents(context:Context): EventTracker {
        return GoogleAnalytics(FirebaseAnalytics.getInstance(context))
    }
}

@Module(
    includes = [
        PostsListingModule::class,
        PostDetailModule::class,
        PostEditModule::class,
        PostCreateModule::class,
        AuthenticateModule::class
    ]
)
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun mainActivity(): MainActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindViewModel(viewmodel: MainActivityViewModel): ViewModel
}

@Module
abstract class PostsListingModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun postsListingFragment(): PostsListingFragment

    @Binds
    @IntoMap
    @ViewModelKey(PostsListingViewModel::class)
    abstract fun bindViewModel(viewmodel: PostsListingViewModel): ViewModel
}

@Module
abstract class PostDetailModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun postDetailFragment(): PostDetailFragment

    @Binds
    @IntoMap
    @ViewModelKey(PostDetailViewModel::class)
    abstract fun bindViewModel(viewmodel: PostDetailViewModel): ViewModel
}

@Module
abstract class PostEditModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun postEditFragment(): PostEditFragment

    @Binds
    @IntoMap
    @ViewModelKey(PostEditViewModel::class)
    abstract fun bindViewModel(viewmodel: PostEditViewModel): ViewModel
}

@Module
abstract class PostCreateModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun postCreateFragment(): PostCreateFragment

    @Binds
    @IntoMap
    @ViewModelKey(PostCreateViewModel::class)
    abstract fun bindViewModel(viewmodel: PostCreateViewModel): ViewModel
}

@Module
abstract class AuthenticateModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun authenticateFragment(): AuthenticateFragment

    @Binds
    @IntoMap
    @ViewModelKey(AuthenticateViewModel::class)
    abstract fun bindViewModel(viewmodel: AuthenticateViewModel): ViewModel
}