package ar.edu.unq.pdes.myprivateblog.screens.posts_listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ar.edu.unq.pdes.myprivateblog.data.BlogEntriesRepository
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class PostsListingViewModel @Inject constructor(
    val blogEntriesRepository: BlogEntriesRepository
) : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    val posts: LiveData<List<BlogEntry>> by lazy {
        blogEntriesRepository.getBlogEntriesWith(false)
    }
}
