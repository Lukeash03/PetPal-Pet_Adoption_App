package com.luke.petpal.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.luke.petpal.presentation.theme.appColorPrimary

@Composable
fun ChatHomeScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    val chatViewModel: ChatViewModel = hiltViewModel()

    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
            ) {
//                items(channels.value) { channel ->
//                    Column {
//                        Text(
//                            text = channel.name,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(8.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(appColorPrimary.copy(0.5f))
//                                .clickable {
//
//                                }
//                                .padding(16.dp),
//                            color = MaterialTheme.colorScheme.onBackground
//                        )
//                    }
//                }
            }
        }
    }
}
