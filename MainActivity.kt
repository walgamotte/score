package com.example.discgolfscorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiscGolfScorerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscGolfScorerApp() {
    // Initialize an 18-hole round. Default Par is 3, initial strokes match the par.
    val totalHoles = 18
    val defaultPar = 3
    val pars = remember { mutableStateListOf(*Array(totalHoles) { defaultPar }) }
    val scores = remember { mutableStateListOf(*Array(totalHoles) { defaultPar }) }

    // Calculate totals
    val totalPar = pars.sum()
    val totalStrokes = scores.sum()
    val totalToPar = totalStrokes - totalPar

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disc Golf Scorecard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Summary Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total Strokes: $totalStrokes", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Course Par: $totalPar", fontSize = 14.sp, color = Color.Gray)
                    }
                    
                    // Style the relative score (e.g., -2, E, +4)
                    val scoreText = when {
                        totalToPar > 0 -> "+$totalToPar"
                        totalToPar < 0 -> "$totalToPar"
                        else -> "E"
                    }
                    val scoreColor = when {
                        totalToPar > 0 -> Color(0xFFD32F2F) // Red for over par
                        totalToPar < 0 -> Color(0xFF388E3C) // Green for under par
                        else -> Color.DarkGray
                    }

                    Text(
                        text = scoreText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }

            // 18-Hole List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(scores) { index, strokeCount ->
                    HoleRow(
                        holeNumber = index + 1,
                        par = pars[index],
                        strokes = strokeCount,
                        onStrokesChanged = { newStrokes ->
                            scores[index] = newStrokes
                        }
                    )
                }
            }

            // Reset Button
            Button(
                onClick = { 
                    for (i in scores.indices) scores[i] = pars[i] 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reset Scorecard", color = Color.White)
            }
        }
    }
}

@Composable
fun HoleRow(
    holeNumber = Int,
    par = Int,
    strokes = Int,
    onStrokesChanged = (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hole info
            Column {
                Text(text = "Hole $holeNumber", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Par $par", fontSize = 12.sp, color = Color.Gray)
            }

            // Score Counter Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = { if (strokes > 1) onStrokesChanged(strokes - 1) }
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                }

                Text(
                    text = strokes.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        strokes < par -> Color(0xFF388E3C) // Birdie or better
                        strokes > par -> Color(0xFFD32F2F) // Bogey or worse
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                IconButton(
                    onClick = { onStrokesChanged(strokes + 1) }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
