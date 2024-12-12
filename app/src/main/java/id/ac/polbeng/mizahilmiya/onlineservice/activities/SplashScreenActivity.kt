package id.ac.polbeng.mizahilmiya.onlineservice.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.ac.polbeng.mizahilmiya.onlineservice.databinding.ActivitySplashScreenBinding
import id.ac.polbeng.mizahilmiya.onlineservice.helpers.Config

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(mainLooper).postDelayed({
            startActivity(Intent(this@SplashScreenActivity,
                LoginActivity::class.java))
            finish()
        }, Config.SPLASH_SCREEN_DELAY)
    }
}