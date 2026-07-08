package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SyllabusTopic
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SyllabusScreen(
    viewModel: MainViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topics by viewModel.syllabusTopics.collectAsState()
    val filterGrade by viewModel.syllabusFilterGrade.collectAsState()
    val filterSubject by viewModel.syllabusFilterSubject.collectAsState()

    val grades = listOf("Class 8", "Class 10", "Class 12")
    val subjects = listOf("All", "Math", "Science", "Social Science", "English", "Physics", "Chemistry")

    // Filter topics dynamically
    val filteredTopics = remember(topics, filterGrade, filterSubject) {
        topics.filter { topic ->
            topic.grade == filterGrade && (filterSubject == "All" || topic.subject == filterSubject)
        }
    }

    // Progress statistics
    val totalCount = filteredTopics.size
    val masteredCount = filteredTopics.count { it.status == "MASTERED" }
    val inProgressCount = filteredTopics.count { it.status == "IN_PROGRESS" }
    val completionPercent = if (totalCount > 0) (masteredCount.toFloat() / totalCount.toFloat()) else 0f
    
    val animatedProgress by animateFloatAsState(targetValue = completionPercent, label = "progress")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tracker Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "NCERT Syllabus Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage and track chapters for CBSE exams. Tap any topic to revise with AI.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar and Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress: $masteredCount of $totalCount Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(completionPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mastered ($masteredCount)", fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFFF59E0B), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Learning ($inProgressCount)", fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color.Gray.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pending (${totalCount - masteredCount - inProgressCount})", fontSize = 11.sp)
                    }
                }
            }
        }

        // Dropdowns / Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Grade Selector
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Grade:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row {
                        grades.forEach { grade ->
                            val active = filterGrade == grade
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { viewModel.setSyllabusFilterGrade(grade) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = grade,
                                    fontSize = 11.sp,
                                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Subject Selector Scrollable
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subject: $filterSubject",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject",
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject) },
                            onClick = {
                                viewModel.setSyllabusFilterSubject(subject)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Syllabus Topic List
        if (filteredTopics.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = "Empty",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No chapters found for this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
            ) {
                items(filteredTopics, key = { it.id }) { topic ->
                    SyllabusTopicCard(
                        topic = topic,
                        onStatusChange = { newStatus ->
                            viewModel.updateTopicStatus(topic, newStatus)
                        },
                        onReviseWithAi = {
                            viewModel.setChatGrade(topic.grade)
                            viewModel.setChatSubject(topic.subject)
                            viewModel.sendChatMessage("Give me a comprehensive overview, core conceptual doubts, and key formulas of the NCERT chapter: ${topic.title} for ${topic.grade}.")
                            viewModel.selectTab("chat")
                            onNavigateToChat()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SyllabusTopicCard(
    topic: SyllabusTopic,
    onStatusChange: (String) -> Unit,
    onReviseWithAi: () -> Unit
) {
    val statusColor = when (topic.status) {
        "MASTERED" -> Color(0xFF10B981)
        "IN_PROGRESS" -> Color(0xFFF59E0B)
        else -> MaterialTheme.colorScheme.outline
    }

    val statusLabel = when (topic.status) {
        "MASTERED" -> "Mastered"
        "IN_PROGRESS" -> "Learning"
        else -> "Pending"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("syllabus_item_${topic.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = getSubjectIcon(topic.subject),
                            contentDescription = topic.subject,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${topic.grade} • ${topic.subject}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (topic.status == "MASTERED") TextDecoration.LineThrough else TextDecoration.None,
                        color = if (topic.status == "MASTERED") MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Current Status Badge
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            fontSize = 11.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Change status triggers
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val statuses = listOf("PENDING", "IN_PROGRESS", "MASTERED")
                    statuses.forEach { st ->
                        val isActive = topic.status == st
                        val btnColor = when (st) {
                            "MASTERED" -> Color(0xFF10B981)
                            "IN_PROGRESS" -> Color(0xFFF59E0B)
                            else -> Color.Gray
                        }
                        
                        OutlinedButton(
                            onClick = { onStatusChange(st) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isActive) btnColor else Color.Transparent,
                                contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = !isActive),
                            modifier = Modifier
                                .height(26.dp)
                                .testTag("btn_status_${topic.id}_$st")
                        ) {
                            val label = when (st) {
                                "MASTERED" -> "Done"
                                "IN_PROGRESS" -> "Study"
                                else -> "Reset"
                            }
                            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Revise with AI Action Button
                Button(
                    onClick = onReviseWithAi,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("btn_revise_ai_${topic.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask AI",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Study with AI", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
