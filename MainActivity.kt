package com.example.discgolfscorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Player(
    val name: String,
    val scores: MutableList<Int>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MultiPlayerDiscGolfApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiPlayerDiscGolfApp() {
    val totalHoles = 18
    val defaultPar = 3
    val pars = remember { mutableStateListOf(*Array(totalHoles) { defaultPar }) }
    val players = remember { mutableStateListOf<Player>() }
    var newPlayerName by remember { mutableStateOf("") }
    var currentHoleIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multiplayer Scorecard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Players", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newPlayerName,
                            onValueChange = { newPlayerName = it },
                            label = { Text("Player Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        IconButton(onClick = {
                            if (newPlayerName.isNotBlank()) {
                                players.add(Player(newPlayerName.trim(), MutableList(totalHoles) { defaultPar }))
                                newPlayerName = ""
                            }
                        }) { Icon(Icons.Default.PersonAdd, "Add") }
                    }
                }
            }

            if (players.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Leaderboard", fontWeight = FontWeight.Bold)
                        players.sortedBy { it.scores.sum() - pars.sum() }.forEachIndexed { rank, player ->
                            val totalToPar = player.scores.sum() - pars.sum()
                            val scoreText = when { totalToPar > 0 -> "+$totalToPar"; totalToPar < 0 -> "$totalToPar"; else -> "E" }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${rank + 1}. ${player.name}")
                                Text(scoreText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { currentHoleIndex-- }, enabled = currentHoleIndex > 0) { Text("Prev") }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hole ${currentHoleIndex + 1}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Par ${pars[currentHoleIndex]}", color = Color.Gray)
                }
                Button(onClick = { currentHoleIndex++ }, enabled = currentHoleIndex < totalHoles - 1) { Text("Next") }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(players) { playerIndex, player ->
                    val currentStrokeCount = player.scores[currentHoleIndex]
                    val currentPar = pars[currentHoleIndex]
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(player.name, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                IconButton(onClick = {
                                    if (currentStrokeCount > 1) {
                                        val updatedScores = ArrayList(player.scores)
                                        updatedScores[currentHoleIndex] = currentStrokeCount - 1
                                        players[playerIndex] = player.copy(scores = updatedScores)
                                    }
                                }) { Icon(Icons.Default.Remove, "Less") }
                                Text(currentStrokeCount.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (currentStrokeCount < currentPar) Color(0xFF388E3C) else if (currentStrokeCount > currentPar) Color(0xFFD32F2F) else Color.Unspecified)
                                IconButton(onClick = {
                                    val updatedScores = ArrayList(player.scores)
                                    updatedScores[currentHoleIndex] = currentStrokeCount + 1
                                    players[playerIndex] = player.copy(scores = updatedScores)
                                }) { Icon(Icons.Default.Add, "More") }
                            }
                        }
                    }
                }
            }

            if (players.isNotEmpty()) {
                Button(onClick = { players.clear(); currentHoleIndex = 0 }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Clear All Players", color = Color.White)
                }
            }
        }
    }
}



