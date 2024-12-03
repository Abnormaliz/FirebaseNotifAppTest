package com.example.firebasenotifapp

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import com.example.firebasenotifapp.ui.theme.FirebaseNotifAppTheme

class MainActivity : ComponentActivity() {
    private var isPermissionGranted by mutableStateOf(false)

    private fun checkNotificationPermission() {
        isPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(this, "Уведомления включены", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Уведомления выключены", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    isPermissionGranted = true
                    Toast.makeText(
                        this, "Уведомления включены", Toast.LENGTH_SHORT
                    ).show()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(
                        this, "Разрешите приложению принимать уведомления (в настройках)", Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseNotifAppTheme {
                Surface(modifier = Modifier.background(colorResource(R.color.gray))) {
                    TokenScreen(
                        onCopyToClipboard = { token ->
                            copyToClipboard(token)
                        },
                        onRequestPermission = { requestNotificationPermission() },
                        isPermissionGranted = isPermissionGranted
                    )
                }

            }
        }
    }

    private fun copyToClipboard(token: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Token", token)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Токен скопирован в буфер обмена", Toast.LENGTH_SHORT).show()
    }
}