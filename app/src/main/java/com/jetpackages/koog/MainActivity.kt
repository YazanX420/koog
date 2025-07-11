package com.jetpackages.koog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.jetpackages.koog.presentation.Agent
import com.jetpackages.koog.presentation.theme.KoogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Agent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}