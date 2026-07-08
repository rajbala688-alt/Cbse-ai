package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SavedCardsScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.MxeDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import androidx.compose.material.icons.filled.Bolt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppShell()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell() {
    val viewModel: MainViewModel = viewModel()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MY SCHOOL CBSE AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == "chat",
                    onClick = { viewModel.selectTab("chat") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "chat") Icons.Default.ChatBubble else Icons.Default.ChatBubbleOutline,
                            contentDescription = "Doubt Solver"
                        )
                    },
                    label = { Text("Doubt Solver", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_chat")
                )

                NavigationBarItem(
                    selected = selectedTab == "syllabus",
                    onClick = { viewModel.selectTab("syllabus") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Syllabus"
                        )
                    },
                    label = { Text("Syllabus", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_syllabus")
                )

                NavigationBarItem(
                    selected = selectedTab == "quiz",
                    onClick = { viewModel.selectTab("quiz") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = "Mock Quiz"
                        )
                    },
                    label = { Text("Mock Quiz", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_quiz")
                )

                NavigationBarItem(
                    selected = selectedTab == "saved",
                    onClick = { viewModel.selectTab("saved") },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == "saved") Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Notebook"
                        )
                    },
                    label = { Text("Notebook", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_saved")
                )

                NavigationBarItem(
                    selected = selectedTab == "mxe",
                    onClick = { viewModel.selectTab("mxe") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "MXE AI"
                        )
                    },
                    label = { Text("MXE AI", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_tab_mxe")
                )
            }
        }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        
        when (selectedTab) {
            "chat" -> ChatScreen(
                viewModel = viewModel,
                modifier = contentModifier
            )
            "syllabus" -> SyllabusScreen(
                viewModel = viewModel,
                onNavigateToChat = {},
                modifier = contentModifier
            )
            "quiz" -> QuizScreen(
                viewModel = viewModel,
                modifier = contentModifier
            )
            "saved" -> SavedCardsScreen(
                viewModel = viewModel,
                onNavigateToChat = {},
                modifier = contentModifier
            )
            "mxe" -> MxeDashboardScreen(
                viewModel = viewModel,
                modifier = contentModifier
            )
        }
    }
}
