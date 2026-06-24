package com.example.discgolfscorer

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "scorecard_prefs")

data class Player(
    val name: String,
    val scores: MutableList<Int>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")

            LaunchedEffect(Unit) {
                val preferences = context.dataStore.data.first()
                isDarkMode = preferences[DARK_MODE_KEY] ?: false
            }

            val colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DarkModeDiscGolfApp(
                        isDarkMode = isDarkMode,
                        onThemeToggle = { targetMode ->
                            isDarkMode = targetMode
                            CoroutineScope(Dispatchers.IO).launch {
                                context.dataStore.edit { it[DARK_MODE_KEY] = targetMode }
                            }
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkModeDiscGolfApp(isDarkMode: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val totalHoles = 18
    val defaultPar = 3
    
    val pars = remember { mutableStateListOf(*Array(totalHoles) { defaultPar }) }
    val players = remember { mutableStateListOf<Player>() }
    var newPlayerName by remember { mutableStateOf("") }
    var currentHoleIndex by remember { mutableIntStateOf(0) }

    val PARS_KEY = stringPreferencesKey("course_pars")
    val PLAYERS_KEY = stringPreferencesKey("round_players")

    fun saveStateToDisk() {
        coroutineScope.launch {
            context.dataStore.edit { preferences ->
                preferences[PARS_KEY] = pars.joinToString(",")
                val serializedPlayers = players.joinToString("#") { "${it.name}|${it.scores.joinToString(",")}" }
                preferences[PLAYERS_KEY] = serializedPlayers
            }
        }
    }

    LaunchedEffect(Unit) {
        val preferences = context.dataStore.data.first()
        preferences[PARS_KEY]?.let { savedPars ->
            if (savedPars.isNotBlank()) {
                val parsedPars = savedPars.split(",").map { it.toInt() }
                parsedPars.forEachIndexed { index, value -> if (index < totalHoles) pars[index] = value }
            }
        }
        preferences[PLAYERS_KEY]?.let { savedPlayers ->
            if (savedPlayers.isNotBlank()) {
                players.clear()
                savedPlayers.split("#").forEach { playerRaw ->
                    val parts = playerRaw.split("|")
                    if (parts.size == 2) {
                        players.add(Player(parts[0], parts[1].split(",").map { it.toInt() }.toMutableList()))
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disc Golf Scorecard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    IconButton(onClick = { onThemeToggle(!isDarkMode) }) {
                        Icon(imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Theme")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Players", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = newPlayerName, onValueChange = { newPlayerName = it }, label = { Text("Player Name") }, modifier = Modifier.weight(1f), singleLine = true)
                        IconButton(onClick = {
                            if (newPlayerName.isNotBlank()) {
                                players.add(Player(newPlayerName.trim(), MutableList(totalHoles) { defaultPar }))
                                newPlayerName = ""
                                saveStateToDisk()
                            }
                        }) { Icon(Icons.Default.PersonAdd, "Add") }
                    }
                }
            }
                        if (players.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledIconButton(onClick = { if (pars[currentHoleIndex] > 2) { pars[currentHoleIndex]--; saveStateToDisk() } }, modifier = Modifier.size(28.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Icon(Icons.Default.Remove, "Less Par", modifier = Modifier.size(16.dp)) }
                        Text("Par ${pars[currentHoleIndex]}", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                        FilledIconButton(onClick = { if (pars[currentHoleIndex] < 6) { pars[currentHoleIndex]++; saveStateToDisk() } }, modifier = Modifier.size(28.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Icon(Icons.Default.Add, "More Par", modifier = Modifier.size(16.dp)) }
                    }
                }
                Button(onClick = { currentHoleIndex++ }, enabled = currentHoleIndex < totalHoles - 1) { Text("Next") }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(players) { playerIndex, player ->
                    val currentStrokeCount = player.scores[currentHoleIndex]
                    val currentPar = pars[currentHoleIndex]
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(player.name, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                IconButton(onClick = {
                                    if (currentStrokeCount > 1) {
                                        val updatedScores = ArrayList(player.scores)
                                        updatedScores[currentHoleIndex] = currentStrokeCount - 1
                                        players[playerIndex] = player.copy(scores = updatedScores)
                                        saveStateToDisk()
                                    }
                                }) { Icon(Icons.Default.Remove, "Less") }
                                Text(currentStrokeCount.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (currentStrokeCount < currentPar) Color(0xFF4CAF50) else if (currentStrokeCount > currentPar) Color(0xFFF44336) else MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(onClick = {
                                    val updatedScores = ArrayList(player.scores)
                                    updatedScores[currentHoleIndex] = currentStrokeCount + 1
                                    players[playerIndex] = player.copy(scores = updatedScores)
                                    saveStateToDisk()
                                }) { Icon(Icons.Default.Add, "More") }
                            }
                        }
                    }
                }
            }

            if (players.isNotEmpty()) {
                Button(onClick = { players.clear(); currentHoleIndex = 0; for (i in 0 until totalHoles) pars[i] = defaultPar; coroutineScope.launch { context.dataStore.edit { it.clear() } } }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Clear Round & Storage", color = Color.White)
                }
            }
        }
    }
}
