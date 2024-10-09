package com.luke.petpal.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luke.petpal.data.models.Resource
import com.luke.petpal.data.repository.ChatRepository
import com.luke.petpal.domain.data.Chat
import com.luke.petpal.domain.data.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val userId = chatRepository.currentUser?.uid

    private val _chatIdFlow = MutableStateFlow<String?>(null)
    val chatIdFlow: StateFlow<String?> = _chatIdFlow

    private val _messages = MutableStateFlow<Resource<List<Message>>>(Resource.Loading)
    val messages = _messages.asStateFlow()

    private val _messageSentState = MutableStateFlow<Resource<Unit>?>(null)
    val messageSentState: StateFlow<Resource<Unit>?> = _messageSentState

    private val _chatListState = MutableStateFlow<Resource<List<Chat>>?>(null)
    val chatListState: StateFlow<Resource<List<Chat>>?> = _chatListState

    private val _profilePic = MutableStateFlow<String?>(null)
    val profilePic: StateFlow<String?> = _profilePic

    fun sendMessage(chatId: String, message: String) {
        viewModelScope.launch {
            _messageSentState.value = Resource.Loading
            try {
                chatRepository.sendMessage(chatId, message)
                _messageSentState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _messageSentState.value = Resource.Failure(e)
            }
        }
    }

    fun fetchChats() {
        viewModelScope.launch {
            _chatListState.value = Resource.Loading
            val result = chatRepository.fetchUserChats()
            _chatListState.value = result
        }
    }

    fun fetchMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.fetchMessages(chatId).collect { result ->
                _messages.value = result
            }
        }
    }

    suspend fun getProfilePic(chatId: String) {
        val result = chatRepository.getProfilePicByChatId(chatId)
        _profilePic.value = result
    }

}
