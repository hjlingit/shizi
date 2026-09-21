package com.example.shiziapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shiziapp.data.ProgressRepository
import com.example.shiziapp.databinding.ActivityMainBinding
import com.example.shiziapp.ui.HomeFragment
import com.example.shiziapp.util.TTSUtils

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var tts: TTSUtils
    lateinit var progress: ProgressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tts = TTSUtils(this)
        progress = ProgressRepository(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, HomeFragment())
                .commit()
        }
    }

    /** 全局播放 */
    fun speak(text: String) {
        tts.speak(text)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}
