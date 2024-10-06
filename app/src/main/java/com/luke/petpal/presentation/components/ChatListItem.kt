package com.luke.petpal.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.luke.petpal.R
import com.luke.petpal.domain.data.Chat
import com.luke.petpal.presentation.theme.cardColorPrimaryLight

@Composable
fun ChatListItem(
    profileImageUrl: String?,
    userName: String,
    lastMessage: String,
//    onDeleteClick: () -> Unit,
    onChatClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Profile Image
        Image(
            painter = rememberImagePainter(
                data = profileImageUrl
                    ?: "https://firebasestorage.googleapis.com/v0/b/petpal-pet-adoption.appspot.com/o/profileImages%2FMro0kcJ32kb65iHPlLm31IrbuYH2?alt=media&token=f4d68948-eb3b-4648-8307-e18dc2216391"
            ),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // User Name and Last Message
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // User Name
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Last Message
            Text(
                text = lastMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Vertical Ellipsis (⋮) for Deleting the Chat
        IconButton(
            onClick = { }
//            onDeleteClick
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options"
            )
        }
    }
}

@Composable
fun ChatListCard(
    chat: Chat,
    onChatClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .clickable {
                onChatClick(chat.chatId)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardColorPrimaryLight)
                .padding(vertical = 6.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
            ) {
                val painter = rememberAsyncImagePainter(
                    model = chat.senderProfileImageUrl ?: R.drawable.lab_1
                )
                Image(
                    painter = painter,
                    contentDescription = "User Image",
                    contentScale = ContentScale.FillBounds
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = chat.senderName ?: "User name",
                    color = MaterialTheme.colorScheme.background,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
                Text(
                    text = chat.lastMessage.ifBlank { "No messages sent" },
                    color = MaterialTheme.colorScheme.background,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options"
                )
            }
        }
    }
}
