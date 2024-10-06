@file:OptIn(ExperimentalMaterial3Api::class)

package com.luke.petpal.presentation.chat

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.luke.petpal.R
import com.luke.petpal.data.models.Resource
import com.luke.petpal.domain.data.Message
import com.luke.petpal.presentation.theme.PetPalTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreen(
    chatId: String,
    chatViewModel: ChatViewModel?
) {

    val userId = chatViewModel?.userId
    val messageState = chatViewModel?.messages?.collectAsState()

    var newMessage by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    LaunchedEffect(chatId) {
        chatViewModel?.fetchMessages(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
//                    .padding(vertical = 12.dp)
                        ,
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                        ) {
                            val painter = rememberAsyncImagePainter(
                                model = R.drawable.lab_1
                            )
                            Image(
                                painter = painter,
                                contentDescription = "User Image",
                                contentScale = ContentScale.FillHeight
                            )
                        }

                        Text(
                            text = "Username",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        }
    ) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValue.calculateTopPadding()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Messages list
            when (messageState?.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Failure -> {
                    Text(text = "Failed to load messages")
                }

                is Resource.Success -> {
                    val messages = (messageState.value as Resource.Success<List<Message>>).result
                    LazyColumn {
                        items(messages) { message ->
                            if (userId != null) {
                                ChatMessageItem(message = message, userId)
                            }
                        }
                    }
                }

                null -> {  }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                state = scrollState,
                reverseLayout = true
            ) {
//            items(messageState) { message ->
//                ChatMessageItem(message = message)
//            }
            }

            // Input field and send button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            Log.i("ChatScreen", "ChatId: $chatId")
                            chatViewModel?.sendMessage(chatId, newMessage)
                            newMessage = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    PetPalTheme {
        ChatScreen("", null)
    }
}

@Composable
fun ChatMessageItem(message: Message, userId: String) {

    Log.i("ChatScreen", "Message: $message")
    val isCurrentUser = message.senderId == userId // Replace with actual user ID

    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedTime = dateFormat.format(message.createdAt)

    var isTimestampVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Message bubble
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .background(
                    color = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isTimestampVisible = !isTimestampVisible
                }
        ) {
            Column {
                // Message text
                Text(
                    text = message.message,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Timestamp
                AnimatedVisibility(
                    visible = isTimestampVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(
                        text = formattedTime,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier
//                            .align(Alignment.End) // Align timestamp to the right
                    )
                }
            }
        }
    }
}