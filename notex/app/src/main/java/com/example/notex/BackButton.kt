package com.example.notex

import android.widget.Button
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BackButton(navController: NavHostController) {
    Button(onClick = {
        navController.navigate("HomePage") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }) {
        Text("Back to Home")
    }
}