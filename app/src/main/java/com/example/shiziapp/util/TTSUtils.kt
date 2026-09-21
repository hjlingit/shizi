package com.example.shiziapp.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * 普通话语音朗读工具（TextToSpeech）。
 * 用于读汉字、拼音、鼓励语。
 */
class TTSUtils(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 优先中文
            val zh = tts?.setLanguage(Locale.SIMPLIFIED_CHINESE)
            val result = tts?.setLanguage(Locale.CHINESE) ?: TextToSpeech.LANG_MISSING_DATA
            ready = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /** 播放一段文本；结束回调（用于串联"字+拼音+词"） */
    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!ready) {
            onDone?.invoke()
            return
        }
        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onDone?.invoke()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onDone?.invoke()
            }
        }
        tts?.setOnUtteranceProgressListener(listener)
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, "utt")
    }

    /** 播放一个字及其拼音、词语，串联朗读 */
    fun speakHanzi(char: String, pinyin: String, phrase: String) {
        speak("$char") { speak(pinyin) { speak(phrase) } }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
