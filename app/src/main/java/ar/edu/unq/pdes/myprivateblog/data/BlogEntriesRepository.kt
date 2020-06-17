package ar.edu.unq.pdes.myprivateblog.data

import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.schedulers.Schedulers

class BlogEntriesRepository(val appDatabase: AppDatabase) {
    fun getAllBlogEntries() =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().getAll())

    fun fetchLiveById(entryId: EntityID) =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().loadById(entryId))

    fun fetchById(entryId: EntityID) = appDatabase.blogEntriesDao().loadById(entryId)

    fun getBlogEntriesWith(deleted: Boolean? = null, synced: Boolean? = null) =
        LiveDataReactiveStreams.fromPublisher(appDatabase.blogEntriesDao().getAll(deleted, synced))

    fun createBlogEntry(blogEntry: BlogEntry) = appDatabase.blogEntriesDao()
        .insert(blogEntry)
        .subscribeOn(Schedulers.io())

    fun updateBlogEntry(album: BlogEntry) =
        appDatabase.blogEntriesDao()
            .update(album)
            .subscribeOn(Schedulers.io())

    fun deleteBlogEntry(blogEntry: BlogEntry) =
        appDatabase.blogEntriesDao()
            .delete(blogEntry)
            .subscribeOn(Schedulers.io())

    fun insertAll(blogEntries : List<BlogEntry>) =
        appDatabase.blogEntriesDao()
            .insertAll(blogEntries)
            .subscribeOn(Schedulers.io())

    fun updateAll(blogEntries: List<BlogEntry>) =
        appDatabase.blogEntriesDao()
            .updateAll(blogEntries)
            .subscribeOn(Schedulers.io())
}