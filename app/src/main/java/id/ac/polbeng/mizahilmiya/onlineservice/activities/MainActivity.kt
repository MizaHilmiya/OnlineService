package id.ac.polbeng.mizahilmiya.onlineservice.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import id.ac.polbeng.mizahilmiya.onlineservice.R
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivityMainBinding
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.NavHeaderBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.SessionHandler
import id.ac.polbeng.mizahilmiya.onlineservice.models.User

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var session : SessionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_user_services,
                R.id.nav_user_profile, R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController,
            appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            menuItem ->
            drawerLayout.closeDrawers()
            logoutDialog()
            true
        }
        session = SessionHandler(applicationContext)
        val user: User? = session.getUser()

        if(user != null) {
            val headerView = binding.navView.getHeaderView(0)
            val headerBinding = NavHeaderBinding.bind(headerView)
            val url = Config.PROFILE_IMAGE_URL + user.gambar
            Glide.with(applicationContext)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user))
                .into(headerBinding.ivUser)
            headerBinding.tvName.text = user.nama
            headerBinding.tvEmail.text = user.email
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    private fun logoutDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Keluar Akun")
        builder.setMessage("Apakah anda yakin keluar dari akun saat ini?")
        builder.setIcon(R.drawable.ic_baseline_exit_to_app_24)
        builder.setPositiveButton("Ya") { dialog, _ ->
            dialog.dismiss()
            session.removeUser()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        builder.setNegativeButton("Tidak"){ dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
