package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuizQuestion
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val quizIndex by viewModel.quizIndex.collectAsState()
    val selectedOption by viewModel.quizSelectedOption.collectAsState()
    val answered by viewModel.quizAnswered.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val loading by viewModel.quizLoading.collectAsState()
    val finished by viewModel.quizFinished.collectAsState()
    val error by viewModel.quizError.collectAsState()

    val quizGrade by viewModel.quizGrade.collectAsState()
    val quizSubject by viewModel.quizSubject.collectAsState()
    val topicInput by viewModel.quizTopicInput.collectAsState()

    val grades = listOf("Class 8", "Class 10", "Class 12")
    val subjects = listOf("Math", "Science", "Social Science", "English", "Physics", "Chemistry")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (quizQuestions.isEmpty() && !loading && !finished) {
            // Quiz Setup Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = "Quiz Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "CBSE Board Mock Test",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Test your conceptual depth with dynamically generated syllabus-based exams created by our CBSE AI examiner.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Grade Chips
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Grade Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            grades.forEach { grade ->
                                val active = quizGrade == grade
                                FilterChip(
                                    selected = active,
                                    onClick = { viewModel.setQuizGrade(grade) },
                                    label = { Text(grade) },
                                    modifier = Modifier.testTag("quiz_grade_$grade")
                                )
                            }
                        }
                    }
                }

                // Subject Chips
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Select Subject", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    subjects.forEach { subject ->
                                        val active = quizSubject == subject
                                        FilterChip(
                                            selected = active,
                                            onClick = { viewModel.setQuizSubject(subject) },
                                            label = { Text(subject, fontSize = 12.sp) },
                                            modifier = Modifier.testTag("quiz_subject_$subject")
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Topic Input Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Topic / Chapter Name", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { viewModel.setQuizTopicInput(it) },
                            placeholder = { Text("e.g. Quadratic Equations, Light") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quiz_topic_input_field"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                if (error.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Action Button
                Button(
                    onClick = { viewModel.startNewQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_quiz_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assemble Mock Exam", fontWeight = FontWeight.Bold)
                }
            }
        } else if (loading) {
            // Classroom Blackboard style Loading View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Drafting",
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        CircularProgressIndicator()
                        Text(
                            text = "CBSE Mock Exam Generator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Please wait while our AI Examiner drafts 5 NCERT curriculum-aligned questions for topic '$topicInput'...",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        } else if (finished) {
            // Quiz finished Report card
            ReportCardView(
                score = score,
                total = quizQuestions.size,
                topic = topicInput,
                grade = quizGrade,
                subject = quizSubject,
                onRestart = { viewModel.resetQuizModule() }
            )
        } else {
            // Quiz Player View
            val currentQuestion = quizQuestions.getOrNull(quizIndex)
            if (currentQuestion != null) {
                QuizPlayerView(
                    question = currentQuestion,
                    index = quizIndex,
                    total = quizQuestions.size,
                    score = score,
                    selectedOption = selectedOption,
                    answered = answered,
                    onSelectOption = { viewModel.selectQuizOption(it) },
                    onSubmit = { viewModel.submitQuizAnswer() },
                    onNext = { viewModel.advanceQuiz() }
                )
            }
        }
    }
}

@Composable
fun QuizPlayerView(
    question: QuizQuestion,
    index: Int,
    total: Int,
    score: Int,
    selectedOption: String,
    answered: Boolean,
    onSelectOption: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${index + 1} of $total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Score: $score",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LinearProgressIndicator(
            progress = { (index + 1).toFloat() / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )

        // Question Statement Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Text(
                text = question.question,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )
        }

        // Options List
        val letters = listOf("A", "B", "C", "D")
        question.options.forEach { option ->
            // Extract option letter prefix, e.g. "A"
            val prefix = option.trim().firstOrNull()?.toString()?.uppercase() ?: ""
            val isSelected = selectedOption == prefix
            
            val correctAnswerLetter = question.answer.trim().firstOrNull()?.toString()?.uppercase() ?: ""
            
            // Border color configuration
            val borderStroke = when {
                answered && prefix == correctAnswerLetter -> BorderStroke(2.dp, Color(0xFF10B981)) // Green Correct
                answered && isSelected && prefix != correctAnswerLetter -> BorderStroke(2.dp, Color(0xFFEF4444)) // Red Selected Wrong
                isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            }

            // Container color configuration
            val containerColor = when {
                answered && prefix == correctAnswerLetter -> Color(0xFF10B981).copy(alpha = 0.15f)
                answered && isSelected && prefix != correctAnswerLetter -> Color(0xFFEF4444).copy(alpha = 0.15f)
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !answered) { onSelectOption(prefix) }
                    .testTag("quiz_option_$prefix"),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = borderStroke,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prefix,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (answered) {
                        Spacer(modifier = Modifier.weight(1f))
                        if (prefix == correctAnswerLetter) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Correct", tint = Color(0xFF10B981))
                        } else if (isSelected) {
                            Icon(imageVector = Icons.Default.Cancel, contentDescription = "Incorrect", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }

        // Explanation & Controls
        AnimatedVisibility(
            visible = answered,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Examiner's Explanation:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!answered) {
            Button(
                onClick = onSubmit,
                enabled = selectedOption.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_answer_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Submit Answer", fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("next_question_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (index + 1 >= total) "Finish Exam" else "Next Question",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ReportCardView(
    score: Int,
    total: Int,
    topic: String,
    grade: String,
    subject: String,
    onRestart: () -> Unit
) {
    val percentage = (score.toFloat() / total.toFloat() * 100).toInt()
    
    // CBSE grade conversion
    val remarkAndGrade = when {
        percentage >= 95 -> Pair("A1", "Exemplary performance! Complete mastery.")
        percentage >= 80 -> Pair("A2", "Outstanding! Excellent understanding of topics.")
        percentage >= 60 -> Pair("B1", "Good effort! Revise minor doubts with CBSE AI.")
        percentage >= 40 -> Pair("B2", "Average score. Study study-cards to score better.")
        else -> Pair("C1", "Need Improvement. Spend more time studying with AI.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "CBSE MOCK EXAM RESULT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Text(
                    text = "Subject: $subject • $grade",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Topic: $topic",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                // Marks obtained
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$score",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " / $total",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }

                Text(
                    text = "Score Percent: $percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Grade / Remarks board
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CBSE Grade: ${remarkAndGrade.first}",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = remarkAndGrade.second,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Actions
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("quiz_restart_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Another Mock Exam", fontWeight = FontWeight.Bold)
        }
    }
}
