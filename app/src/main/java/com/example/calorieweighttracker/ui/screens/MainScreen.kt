package com.example.calorieweighttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorieweighttracker.ui.viewmodels.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Main screen with swipe navigation and data entry
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToGraph: () -> Unit,
    viewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentDate by viewModel.currentDate.collectAsState()
    val currentEntry by viewModel.currentEntry.collectAsState()

    var caloriesText by remember(currentEntry) {
        mutableStateOf(currentEntry?.calories?.toString() ?: "")
    }
    var weightText by remember(currentEntry) {
        mutableStateOf(currentEntry?.weight?.toString() ?: "")
    }

    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Calorie Weight Tracker") },
                actions = {
                    IconButton(onClick = onNavigateToGraph) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "View Graph",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date display with navigation
            DateNavigationRow(
                currentDate = currentDate,
                onPreviousDay = { viewModel.navigateToPreviousDay() },
                onNextDay = { viewModel.navigateToNextDay() },
                canNavigateNext = viewModel.canNavigateToNext()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Today indicator
            if (currentDate == LocalDate.now()) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Today",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Input fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Calories input
                    OutlinedTextField(
                        value = caloriesText,
                        onValueChange = { caloriesText = it },
                        label = { Text("Calories Consumed") },
                        placeholder = { Text("Enter calories") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Weight input
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Body Weight (lbs)") },
                        placeholder = { Text("Enter weight") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Save button
                Button(
                    onClick = {
                        val calories = caloriesText.toFloatOrNull()
                        val weight = weightText.toFloatOrNull()
                        viewModel.saveEntry(calories, weight)
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = caloriesText.isNotBlank() || weightText.isNotBlank()
                ) {
                    Text("Save")
                }

                // Delete button
                if (currentEntry != null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteEntry()
                            caloriesText = ""
                            weightText = ""
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

/**
 * Date navigation row with swipe gesture support
 */
@Composable
fun DateNavigationRow(
    currentDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    canNavigateNext: Boolean
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPreviousDay) {
            Text("◀", fontSize = 20.sp)
        }

        Text(
            text = currentDate.format(dateFormatter),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        TextButton(
            onClick = onNextDay,
            enabled = canNavigateNext
        ) {
            Text(
                "▶",
                fontSize = 20.sp,
                color = if (canNavigateNext)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}