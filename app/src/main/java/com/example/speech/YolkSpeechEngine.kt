package com.example.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class YolkSpeechEngine(context: Context) : TextToSpeech.OnInitListener {
  private val appContext = context.applicationContext
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _isMuted = MutableStateFlow(false)
  val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

  private val _lastSpokenText = MutableStateFlow(YolkVoiceLines.getGreeting())
  val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

  private val scope = CoroutineScope(Dispatchers.Main)

  init {
    try {
      tts = TextToSpeech(appContext, this)
    } catch (e: Exception) {
      Log.e("YolkSpeechEngine", "Error initializing TextToSpeech", e)
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      tts?.let { engine ->
        val result = engine.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          engine.language = Locale.getDefault()
        }
        // Energetic, cheerful talking egg voice: higher pitch & brisk rate
        engine.setPitch(1.22f)
        engine.setSpeechRate(1.08f)

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            scope.launch { _isSpeaking.value = true }
          }

          override fun onDone(utteranceId: String?) {
            scope.launch { _isSpeaking.value = false }
          }

          @Deprecated("Deprecated in Java")
          override fun onError(utteranceId: String?) {
            scope.launch { _isSpeaking.value = false }
          }
        })
        isInitialized = true
      }
    } else {
      Log.w("YolkSpeechEngine", "TextToSpeech init returned status: $status")
    }
  }

  fun toggleMute(): Boolean {
    val newMute = !_isMuted.value
    _isMuted.value = newMute
    if (newMute) {
      stop()
    }
    return newMute
  }

  fun speak(text: String, onDone: (() -> Unit)? = null) {
    _lastSpokenText.value = text
    if (_isMuted.value) {
      onDone?.invoke()
      return
    }

    if (!isInitialized || tts == null) {
      Log.d("YolkSpeechEngine", "TTS not ready yet: $text")
      onDone?.invoke()
      return
    }

    val utteranceId = UUID.randomUUID().toString()
    val params = Bundle().apply {
      putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
    }

    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
    } catch (e: Exception) {
      Log.e("YolkSpeechEngine", "Error stopping TTS", e)
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
      isInitialized = false
    } catch (e: Exception) {
      Log.e("YolkSpeechEngine", "Error shutting down TTS", e)
    }
  }
}
