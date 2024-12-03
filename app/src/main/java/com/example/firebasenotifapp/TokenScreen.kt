package com.example.firebasenotifapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun TokenScreen(
    onCopyToClipboard: (String) -> Unit,
    onRequestPermission: () -> Unit,
    isPermissionGranted: Boolean
) {
    var token by remember { mutableStateOf("Получение токена...") }

    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                token = "Ошибка при получении токена"
            } else {
                token = task.result ?: "Токен недоступен"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = token,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )

            Button(
                onClick = { onCopyToClipboard(token) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Скопировать токен в буфер обмена")
            }
            Button(
                onClick = onRequestPermission,
                enabled = !isPermissionGranted,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(if (isPermissionGranted) "Уведомления включены" else "Включить уведомления")
            }
        }
    }
}
