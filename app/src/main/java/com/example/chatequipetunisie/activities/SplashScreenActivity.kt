package com.example.chatequipetunisie.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatequipetunisie.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Comment out the window insets code to test if that's causing the crash
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
        //     val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //     view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //     insets
        // }

        // Delay for splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Start HomeActivity
            Intent(this, AuthentificationActivity::class.java).also { intent ->
                startActivity(intent)
            }
            // Finish SplashScreenActivity
            finish()
        }, 3000) // 3000 ms = 3 seconds
    }
}
