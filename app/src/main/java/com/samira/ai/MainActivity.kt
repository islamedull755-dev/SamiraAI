package com.samira.ai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var input: EditText
    private lateinit var response: TextView
    private lateinit var tts: TextToSpeech
    private val executor = Executors.newSingleThreadExecutor()
    private val speechRequestCode = 7001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input = findViewById(R.id.messageInput)
        response = findViewById(R.id.responseText)
        tts = TextToSpeech(this, this)

        findViewById<Button>(R.id.sendButton).setOnClickListener {
            val message = input.text.toString().trim()
            if (message.isNotEmpty()) sendToBackend(message)
        }
        findViewById<Button>(R.id.voiceButton).setOnClickListener { startVoiceInput() }

        findViewById<Button>(R.id.chatAction).setOnClickListener {
            input.requestFocus()
            Toast.makeText(this, "Type a message or use Speak", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.callAction).setOnClickListener { startVoiceInput() }
        findViewById<Button>(R.id.codingAction).setOnClickListener {
            input.setText("Help me write and explain code: ")
            input.setSelection(input.text.length)
        }
        findViewById<Button>(R.id.musicAction).setOnClickListener {
            openWebSearch("music recognition song search")
        }
        findViewById<Button>(R.id.phoneAction).setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
        }
        findViewById<Button>(R.id.settingsAction).setOnClickListener {
            Toast.makeText(this, "Settings screen will be added in the next build.", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendToBackend(message: String) {
        val endpoint = BuildConfig.AI_BACKEND_URL.trim()
        if (endpoint.isBlank()) {
            response.text = "AI backend is not connected yet. Add your HTTPS backend URL in app/build.gradle.kts. Do not put a Gemini API key inside the APK."
            return
        }

        response.text = "Samira is thinking..."
        executor.execute {
            var connection: HttpURLConnection? = null
            try {
                connection = (URL(endpoint.trimEnd('/') + "/chat").openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 15000
                    readTimeout = 30000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }
                val body = JSONObject().put("message", message).toString()
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
                val result = stream.bufferedReader().use { it.readText() }
                val reply = JSONObject(result).optString("reply", "The server did not return a reply.")
                runOnUiThread {
                    response.text = reply
                    if (::tts.isInitialized) tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "samira-reply")
                }
            } catch (e: Exception) {
                runOnUiThread { response.text = "Connection error: ${e.localizedMessage ?: "Please check your backend and internet."}" }
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Samira")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        try {
            startActivityForResult(intent, speechRequestCode)
        } catch (_: Exception) {
            Toast.makeText(this, "Speech recognition is not available on this device.", Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Legacy activity result callback used for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == speechRequestCode && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                input.setText(spoken)
                sendToBackend(spoken)
            }
        }
    }

    private fun openWebSearch(query: String) {
        val uri = android.net.Uri.parse("https://www.youtube.com/results?search_query=" + android.net.Uri.encode(query))
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) tts.shutdown()
        executor.shutdown()
        super.onDestroy()
    }
}
