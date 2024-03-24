package com.kinshuk.gemnichatbot

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import com.kinshuk.gemnichatbot.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var generativeModel: GenerativeModel
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversation: MutableList<Chat>
    private lateinit var menu:Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        conversation = mutableListOf()
        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "your_api_key"
        )
        setupAdapter()
        binding.sendbtn.setOnClickListener{
            if(binding.message.text.isNotEmpty())
            {
                sendMessage(binding.message.text.toString())
                binding.message.text.clear()
            }
        }
    }

    private fun sendMessage(msg: String) {
        conversation.add(Chat(msg,true))
        conversationAdapter.notifyItemInserted(conversation.size-1)
        binding.recyclerView.scrollToPosition(conversation.size-1)
        CoroutineScope(Dispatchers.Main).launch {
            val botResponse = fetchBotResponse(msg)
            conversation.add(Chat(botResponse!!, false)) // Add bot response to list
            conversationAdapter.notifyItemInserted(conversation.size - 1) // Notify adapter
            binding.recyclerView.scrollToPosition(conversation.size - 1)
            Log.d("TAGY",botResponse)
        }
    }

    private suspend fun fetchBotResponse(msg: String): String? {
        return withContext(Dispatchers.IO) {
            try {

                val conversationHistory = conversation.joinToString("\n") { it.prompt }
                generativeModel.generateContent(conversationHistory).text
            }
            catch (e:Exception){
                "Sorry,unexpected error occured"
            }
        }
    }

    private fun setupAdapter() {
        conversationAdapter =   ConversationAdapter(conversation)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = conversationAdapter
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        this.menu = menu ?: return false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.clear->{
                conversation.clear()
                conversationAdapter.notifyDataSetChanged()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}