package ar.edu.unq.pdes.myprivateblog

import android.app.Activity
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.findNavController
import ar.edu.unq.pdes.myprivateblog.data.FirebaseUserLiveData
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateFragment
import ar.edu.unq.pdes.myprivateblog.screens.auth_signin.AuthenticateViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import io.reactivex.annotations.NonNull
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
    val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }
    private lateinit var auth: FirebaseAuth
    val user = MutableLiveData<FirebaseUser?>()
    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        RxJavaPlugins.setErrorHandler { Timber.e(it) }

        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        authenticationState.observe(this, androidx.lifecycle.Observer {
            updateUserInfoUI(auth.currentUser)
        })


    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    private fun setupMenuUINotLogged(){
        val headerView: View = navView.getHeaderView(0)
        val navUserEmail = headerView.findViewById<View>(R.id.user_email) as TextView
        navUserEmail.isVisible = false
        val menuNav : Menu = navView.menu
        val sync : MenuItem = menuNav.findItem(R.id.nav_sync)
        sync.isVisible = false
        val logout : MenuItem = menuNav.findItem(R.id.nav_logout)
        logout.isVisible = false
        val login : MenuItem = menuNav.findItem(R.id.nav_login)
        login.isVisible = true
    }

    private fun setupMenuUILogged(currentUser : FirebaseUser){
        val headerView: View = navView.getHeaderView(0)
        val navUsername = headerView.findViewById<View>(R.id.user_name) as TextView
        val navUserEmail = headerView.findViewById<View>(R.id.user_email) as TextView
        navUserEmail.isVisible = true
        navUsername.text = currentUser.displayName
        navUserEmail.text = currentUser.email
        val navUserPhoto = headerView.findViewById<ImageView>(R.id.user_photo)
        Picasso.get().load(currentUser.photoUrl).into(navUserPhoto)
        val menuNav : Menu = navView.menu
        val sync : MenuItem = menuNav.findItem(R.id.nav_sync)
        sync.isVisible = true
        val logout : MenuItem = menuNav.findItem(R.id.nav_logout)
        logout.isVisible = true
        val login : MenuItem = menuNav.findItem(R.id.nav_login)
        login.isVisible = false
    }

    fun updateUserInfoUI(currentUser : FirebaseUser?){
        if(currentUser != null){
            setupMenuUILogged(currentUser)
            //Desbloqueo el navigation view
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        else{
            setupMenuUINotLogged()
            //Bloqueo el layout para no abrir el navigation view
            //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
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
                Toast.makeText(this, "Sync clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                auth.signOut()
                Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show()
                user.value = auth.currentUser
                findNavController(R.id.nav_host_fragment).navigate(R.id.authenticateFragment)
            }
            R.id.nav_login -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.authenticateFragment)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }


}
