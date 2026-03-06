package com.example.picturepuzzle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.picturepuzzle.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        navigateToMain()
    }

    private fun setupAnimations() {
        // Fade in logo
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.imageAppLogo.startAnimation(fadeIn)

        // Scale animation
        binding.imageAppLogo.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(1000)
            .withEndAction {
                binding.imageAppLogo.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(500)
                    .start()
            }
            .start()

        // Fade in text
        binding.textAppName.alpha = 0f
        binding.textAppName.animate()
            .alpha(1f)
            .setStartDelay(500)
            .setDuration(800)
            .start()

        binding.textLoading.alpha = 0f
        binding.textLoading.animate()
            .alpha(1f)
            .setStartDelay(800)
            .setDuration(800)
            .start()
    }

    private fun navigateToMain() {
        lifecycleScope.launch {
            delay(2500) // 2.5 seconds
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}