package ar.edu.unq.pdes.myprivateblog.screens.posts_listing

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unq.pdes.myprivateblog.BaseFragment
import ar.edu.unq.pdes.myprivateblog.ColorUtils
import ar.edu.unq.pdes.myprivateblog.R
import ar.edu.unq.pdes.myprivateblog.data.BlogEntry
import ar.edu.unq.pdes.myprivateblog.data.EntityID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_posts_listing.*
import kotlinx.android.synthetic.main.nav_header.*

class PostsListingFragment : BaseFragment() {
    override val layoutId = R.layout.fragment_posts_listing
    private lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<PostsListingViewModel> { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        //TODO: Mostrar info del usuario autenticado
        val currentUser = auth.currentUser
        if(currentUser != null){
            val name = currentUser.displayName
            val mail = currentUser.email
            val photo = currentUser.photoUrl

            //user_email.text = currentUser.email
        }

        getMainActivity().hideKeyboard()

        context?.apply { applyStatusBarStyle(this.getColor(R.color.palette_pastel_yellow_02)) }

        create_new_post.setOnClickListener {
            findNavController().navigate(PostsListingFragmentDirections.navActionCreatePost())
        }

        viewModel.posts.observe(viewLifecycleOwner, Observer { postList ->
            if(viewModel.posts.value.isNullOrEmpty()) empty_concept_illustration_l.visibility = View.VISIBLE
            else empty_concept_illustration_l.visibility = View.INVISIBLE

            posts_list_recyclerview.adapter = PostsListAdapter(postList) {
                findNavController().navigate(PostsListingFragmentDirections.navActionOpenDetail(it))
            }

            posts_list_recyclerview.layoutManager = LinearLayoutManager(context)
    })

    }
}

class BlogEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.item_title)
}

class PostsListAdapter(
    private val postList: List<BlogEntry>,
    private val onItemClicked: (EntityID) -> Unit
) : RecyclerView.Adapter<BlogEntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogEntryViewHolder {
        val postViewItem =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return BlogEntryViewHolder(postViewItem)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: BlogEntryViewHolder, position: Int) {
        val blogEntry = postList[position]
        holder.title.text = blogEntry.title
        holder.title.backgroundTintList = ColorStateList.valueOf(blogEntry.cardColor)
        holder.title.setTextColor(ColorUtils.findTextColorGivenBackgroundColor(blogEntry.cardColor))
        holder.itemView.setOnClickListener { onItemClicked(blogEntry.uid) }
    }

}