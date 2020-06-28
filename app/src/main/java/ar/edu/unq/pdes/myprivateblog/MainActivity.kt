package ar.edu.unq.pdes.myprivateblog

import android.app.Activity
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import dagger.android.AndroidInjection.inject

import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.io.InputStream
import java.net.URL
import java.util.Observer
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var spinner : ProgressBar
    val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }
    private lateinit var auth: FirebaseAuth
    val user = MutableLiveData<FirebaseUser?>()
    lateinit var authenticationState : LiveData<AuthenticationState>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)

        authenticationState = viewModel.authenticated.map { user ->
            if (user != null) {
                AuthenticationState.AUTHENTICATED
            } else {
                AuthenticationState.UNAUTHENTICATED
            }
        }
        viewModel.authService.addAuthStateListener(viewModel.authStateListener)
        auth = FirebaseAuth.getInstance()
        RxJavaPlugins.setErrorHandler { Timber.e(it) }
        inject(this)
        setContentView(R.layout.activity_main)
        spinner = findViewById(R.id.progressBar1)
        spinner.visibility = View.GONE
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        toolbar = findViewById(R.id.app_bar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, 0, 0
        )


        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.bringToFront()
        navView.setNavigationItemSelectedListener(this)

        authenticationState.observe(this, androidx.lifecycle.Observer {
            updateUserInfoUI(auth.currentUser)
        })


    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    fun updateUserInfoUI(currentUser : FirebaseUser?){
        val headerView: View = navView.getHeaderView(0)
        val navUsername = headerView.findViewById<View>(R.id.user_name) as TextView
        val navUserEmail = headerView.findViewById<View>(R.id.user_email) as TextView
        val navUserPhoto = headerView.findViewById<ImageView>(R.id.user_photo)
        val menuNav : Menu = navView.menu
        val sync : MenuItem = menuNav.findItem(R.id.nav_sync)
        sync.isVisible = currentUser != null
        val logout : MenuItem = menuNav.findItem(R.id.nav_logout)
        logout.isVisible = currentUser != null
        val login : MenuItem = menuNav.findItem(R.id.nav_login)
        login.isVisible = currentUser == null
        navUserEmail.isVisible = currentUser != null
        if(currentUser != null){
            navUsername.text = currentUser.displayName
            navUserEmail.text = currentUser.email
            Picasso.get().load(currentUser.photoUrl).into(navUserPhoto)
        }
        else{
            navUsername.text = getText(R.string.no_auth_user)
        }
    }

    fun hideKeyboard() {
        val imm: InputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_sync -> {
                spinner.visibility = View.VISIBLE
                viewModel.sync(spinner,this)
                Toast.makeText(this, "Comienzo de sincronizacion", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                viewModel.signOut()
                Toast.makeText(this, getText(R.string.signedOut), Toast.LENGTH_SHORT).show()
                user.value = null
                findNavController(R.id.nav_host_fragment).navigate(R.id.authenticateFragment)
            }
            R.id.nav_login -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.authenticateFragment)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }


}
