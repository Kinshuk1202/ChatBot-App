package com.kinshuk.gemnichatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kinshuk.gemnichatbot.databinding.BotMsgBinding
import com.kinshuk.gemnichatbot.databinding.SenderMsgBinding

class ConversationAdapter(private val conversation: MutableList<Chat>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENDER = 0
        private const val VIEW_TYPE_BOT = 1
    }

    inner class SenderViewHolder(private val binding: SenderMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.senderMsg.text = chat.prompt
            binding.card.setBackgroundResource(R.drawable.sending_bg)
        }
    }

    inner class BotViewHolder(private val binding: BotMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.botMsg.text = chat.prompt
            binding.card.setBackgroundResource(R.drawable.bot_bg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENDER -> {
                val binding = SenderMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SenderViewHolder(binding)
            }
            VIEW_TYPE_BOT -> {
                val binding = BotMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BotViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return conversation.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = conversation[position]
        when (holder.itemViewType) {
            VIEW_TYPE_SENDER -> {
                val senderViewHolder = holder as SenderViewHolder
                senderViewHolder.bind(chat)
            }
            VIEW_TYPE_BOT -> {
                val botViewHolder = holder as BotViewHolder
                botViewHolder.bind(chat)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (conversation[position].isUser) VIEW_TYPE_SENDER else VIEW_TYPE_BOT
    }
}
