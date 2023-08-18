package com.example.ethwallet.mainscreen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import collectAsState

@Composable
fun MainScreen(navController: NavController) {
    val viewModel: MainScreenViewModel = hiltViewModel()
    val viewSate by viewModel.collectAsState()
    MainScreen(viewState = viewSate) { action ->
        when (action) {
            is MainScreenAction.NavigateToScreen -> navController.navigate(action.route)
            else -> viewModel.submitAction(action)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewState: MainScreenState,
    action: (MainScreenAction) -> Unit
) {
    val context = LocalContext.current
    Scaffold(modifier = modifier.fillMaxSize(), containerColor = Color.White, topBar = {
        Spacer(modifier = modifier.height(56.dp))
    }) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Column {
                    Text(text = "MnemonicCode", fontSize = 12.sp, color = Color.Black)

                    Box(modifier = modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(Color.Gray.copy(0.3f))
                        .height(100.dp)
                        .clickable {
                            copyToClipboard(context = context, text = viewState.mnemonicCode)
                            Toast
                                .makeText(
                                    context,
                                    "${viewState.mnemonicCode} Copy to ClipBoard!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }) {
                        Text(
                            modifier = modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            text = viewState.mnemonicCode,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }

            }
            item {
                Column {
                    Text(text = "Address", fontSize = 12.sp, color = Color.Black)
                    Box(modifier = modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(Color.Gray.copy(0.3f))
                        .height(100.dp)
                        .clickable {
                            copyToClipboard(context = context, text = viewState.walletAddress)
                            Toast
                                .makeText(
                                    context,
                                    "${viewState.walletAddress} Copy to ClipBoard!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }) {
                        Text(
                            modifier = modifier.align(Alignment.Center),
                            text = viewState.walletAddress,
                            fontSize = 12.sp,
                            color = Color.Black
                        )

                    }
                }

            }
            item {
                Column {
                    Text(text = "Private Key", fontSize = 12.sp, color = Color.Black)
                    Box(modifier = modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(Color.Gray.copy(0.3f))
                        .height(100.dp)
                        .clickable {
                            copyToClipboard(context = context, text = viewState.privayeKey)
                            Toast
                                .makeText(
                                    context,
                                    "${viewState.privayeKey} Copy to ClipBoard!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }) {
                        Text(
                            modifier = modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 8.dp),
                            text = viewState.privayeKey,
                            fontSize = 12.sp,
                            color = Color.Black
                        )

                    }
                }

            }

            item {
                TextButton(onClick = { action(MainScreenAction.GenerateAddress) }) {
                    Text(text = "Generate Address")
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("copy", text)
    clipboardManager.setPrimaryClip(clipData)
}