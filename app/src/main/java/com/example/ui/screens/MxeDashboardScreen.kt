package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MxeDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isSecurityPassed by viewModel.isSecurityPassed.collectAsState()
    
    if (!isSecurityPassed) {
        MxePinLockScreen(viewModel, modifier)
    } else {
        MxeMasterDashboard(viewModel, modifier)
    }
}

@Composable
fun MxePinLockScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .testTag("pin_lock_card"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "🔒 MXE CORE AI SECURITY SYSTEM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "आगे बढ़ने के लिए अपना 4-डिजिट सीक्रेट पिन दर्ज करें।\n(Default: 1234)",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { input ->
                        if (input.length <= 4 && input.all { it.isDigit() }) {
                            pinInput = input
                            errorMessage = ""
                        }
                    },
                    label = { Text("Enter 4-Digit PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier
                        .width(180.dp)
                        .testTag("pin_input_field")
                )
                
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = {
                        if (pinInput.length == 4) {
                            val success = viewModel.verifyPin(pinInput)
                            if (!success) {
                                errorMessage = "गलत पिन! सुरक्षा कारणों से एक्सेस ब्लॉक। ❌"
                            }
                        } else {
                            errorMessage = "कृपया 4 अंकों का पिन दर्ज करें।"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pin_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verify")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("लॉगिन करें (Access System)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MxeMasterDashboard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val proUnlocked by viewModel.isProUnlocked.collectAsState()
    val promoCode by viewModel.promoCode.collectAsState()
    val codeLimit by viewModel.codeLimit.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()
    val targetPhysique by viewModel.targetPhysique.collectAsState()
    val historyLogs by viewModel.historyLogs.collectAsState()
    val currentTip by viewModel.currentStudyTip.collectAsState()
    
    var promoInput by remember { mutableStateOf("") }
    var promoMessage by remember { mutableStateOf("") }
    var promoSuccess by remember { mutableStateOf(false) }
    
    var activeModuleTab by remember { mutableStateOf("edu") } // "edu", "gym", "sports", "business", "logs", "admin"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
    ) {
        // Master Dashboard Title Banner
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("mxe_dashboard_header")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "💎 MXE CORE AI MASTER 💎",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Class 8 DAV / CBSE • Gym, Sports & Business",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.logOutMxe() },
                            modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Shutdown",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Physique",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Weight: ${userWeight}kg | $targetPhysique",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        val tagText = if (proUnlocked) "PRO ACTIVE" else "FREE VERSION"
                        val tagColor = if (proUnlocked) Color(0xFF10B981) else Color(0xFFF59E0B)
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tagColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tagText,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // PRO VERSION & PROMO CODE UNLOCK BANNER
        if (!proUnlocked) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star Pro",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "⚡ PRO VERSION ACTIVE ($10) | फ्री कोड",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "3D Interactive Workout Posture Detector अनलॉक करने के लिए प्रो कोड डालें।",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = promoInput,
                                onValueChange = { promoInput = it },
                                placeholder = { Text("Code (e.g. MXE200)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).height(48.dp).testTag("promo_input_field"),
                                shape = RoundedCornerShape(8.dp)
                            )
                            
                            Button(
                                onClick = {
                                    val success = viewModel.applyPromoCode(promoInput)
                                    if (success) {
                                        promoSuccess = true
                                        promoMessage = "🎉 कोड एक्टिवेटेड! प्रो वर्जन फ्री में अनलॉक हो गया है।"
                                    } else {
                                        promoSuccess = false
                                        promoMessage = "⚠️ अमान्य कोड या लिमिट समाप्त!"
                                    }
                                    promoInput = ""
                                },
                                modifier = Modifier.height(40.dp).testTag("promo_apply_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Apply", fontSize = 12.sp)
                            }
                        }
                        
                        if (promoMessage.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = promoMessage,
                                color = if (promoSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE0F2FE) // Sky blue container
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Pro active icon",
                            tint = Color(0xFF0369A1),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🎉 CODE ACTIVATED: PRO VERSION FREE!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Premium 3D Posture Correction & Gym guides are fully unlocked.",
                                color = Color(0xFF0284C7),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // DAILY STUDY TIP OF THE DAY CARD (Requested feature)
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("study_tip_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Study Tip of the day",
                                tint = Color(0xFFEAB308), // Yellow
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "STUDY TIP OF THE DAY",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.nextStudyTip() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next tip",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = currentTip,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // SUB-MODULE TAB SELECTION ROW
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabList = listOf(
                    "edu" to "📚 Edu",
                    "gym" to "🏋️ Gym",
                    "sports" to "⚽ Sports",
                    "business" to "💼 Business",
                    "logs" to "⏳ History",
                    "admin" to "⚙️ Admin"
                )
                items(tabList) { (key, label) ->
                    val isSelected = activeModuleTab == key
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { activeModuleTab = key },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("mxe_chip_$key")
                    )
                }
            }
        }

        // CONDITIONAL RENDER OF SELECTED SUB-MODULE
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (activeModuleTab) {
                        "edu" -> MxeEduModule(viewModel)
                        "gym" -> MxeGymModule(viewModel, proUnlocked)
                        "sports" -> MxeSportsModule()
                        "business" -> MxeBusinessModule()
                        "logs" -> MxeLogsModule(historyLogs)
                        "admin" -> MxeAdminModule(viewModel, promoCode, codeLimit)
                    }
                }
            }
        }
    }
}

// =================== SUB-MODULE COMPOSABLES ===================

@Composable
fun MxeEduModule(viewModel: MainViewModel) {
    var doubtInput by remember { mutableStateOf("") }
    var doubtAnswer by remember { mutableStateOf("") }
    var doubtLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Text To Speech logic for Doubt Answer
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {}
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "📚 EDUCATION MASTER MODULE (Class 8 DAV)",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "• Science: Force & Pressure, Friction, Combustion, Cell Structure\n• Maths: Rational Numbers, Squares & Roots, Linear Equations\n• SST: Resources, The Indian Constitution, Colonialism",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()
        
        Text(
            text = "📄 DAV CLASS 8 SPECIMEN QUESTION BANK",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Section A (5 Marks):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Q1. What is the SI unit of pressure? Define 1 Pascal.\nQ2. Why are the soles of shoes grooved?", fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Section B (10 Marks):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Q3. Differentiate between thermoplastic and thermosetting.\nQ4. Explain why sliding friction is less than static friction.", fontSize = 10.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()

        // ASKING DOUBT SECTION WITH LISTENING OPTION (Pedagogical Answer with TTS)
        Text(
            text = "🤖 ASK AI ANYTHING & LISTEN (CBSE Tutor)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = doubtInput,
                onValueChange = { doubtInput = it },
                placeholder = { Text("How does friction produce heat?", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("edu_doubt_input"),
                shape = RoundedCornerShape(8.dp)
            )
            
            Button(
                onClick = {
                    if (doubtInput.isNotBlank() && !doubtLoading) {
                        doubtLoading = true
                        viewModel.logActivity("पूछा गया सवाल (Edu-Dashboard): '$doubtInput'")
                        scope.launch {
                            val answer = com.example.data.GeminiHelper.askCbseDoubt(
                                prompt = doubtInput,
                                subject = "Science",
                                grade = "Class 8"
                            )
                            doubtAnswer = answer
                            doubtLoading = false
                            viewModel.logActivity("AI शिक्षक ने सवाल का जवाब दिया।")
                        }
                    }
                },
                modifier = Modifier.testTag("edu_doubt_ask_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (doubtLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Ask")
                }
            }
        }
        
        if (doubtAnswer.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Teacher's Explanation:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    tts?.stop()
                                    isSpeaking = false
                                } else {
                                    tts?.stop()
                                    isSpeaking = true
                                    tts?.speak(doubtAnswer, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            },
                            modifier = Modifier.size(32.dp).testTag("edu_tts_speak_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Listen to answer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doubtAnswer,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MxeGymModule(viewModel: MainViewModel, proUnlocked: Boolean) {
    val weight by viewModel.userWeight.collectAsState()
    val physique by viewModel.targetPhysique.collectAsState()
    
    var editWeight by remember { mutableStateOf(weight.toString()) }
    var editPhysique by remember { mutableStateOf(physique) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "🏋️‍♂️ GYM & ANIME PHYSIQUE COACH",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Profiles details editor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = editWeight,
                onValueChange = { editWeight = it },
                label = { Text("Weight (kg)", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.width(90.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            OutlinedTextField(
                value = editPhysique,
                onValueChange = { editPhysique = it },
                label = { Text("Target Physique", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = {
                    val doubleWeight = editWeight.toDoubleOrNull() ?: 87.0
                    viewModel.updateWeight(doubleWeight)
                    viewModel.updateTargetPhysique(editPhysique)
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape).size(40.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Save profile settings", modifier = Modifier.size(18.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider()

        if (proUnlocked) {
            // Unlocked 3D Gym video interface
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // light green
                border = BoxBorderDefaults(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔄 [3D INTERACTIVE VIDEO INTERFACE - ENGAGED]",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = "[CAMERA ACTIVE]: Body posture assessment 360-degree Swipe Mode ON.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.PlayCircleFilled, contentDescription = "Video playing", tint = Color.White, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Perfect Push-up Form for Chest and Triceps",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💡 Tips: अपने हाथों को कंधों की चौड़ाई पर रखें, कोर को टाइट रखें और नीचे जाते समय सांस लें।",
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        } else {
            // Locked 3D video indicator & Free Tip
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().blur(if (false) 4.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "🔒 3D Interactive Video & Posture Detector is PRO ONLY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Use Promo Code MXE200 to unlock for Free!",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    Text(
                        text = "🏋️ FREE DIET & WORKOUT TIP:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• कैलोरी डेफिसिट (Calorie Deficit) में रहें ताकि फैट लॉस आसान हो।\n• रोज़ 50 पुश-अप्स और 50 स्क्वाट्स (Squats) करें।\n• पर्याप्त प्रोटीन लें और मीठी चीजें बिल्कुल छोड़ दें।",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MxeSportsModule() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "⚽ SPORTS PERFORMANCE & STAMINA TRACKER",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "🏃‍♂️ STAMINA DRILL (फुटबॉल/क्रिकेट के लिए):",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = "1. 400 मीटर स्प्रिंट (तेज़ दौड़) x 3 सेट्स। प्रत्येक सेट के बाद 2 मिनट आराम।\n2. हाई नीज़ (High Knees) और शटल रन—स्टैमिना और फुर्ती बढ़ाने के लिए बेस्ट है।",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        HorizontalDivider()
        
        Text(
            text = "⚠️ RUNNING POSTURE ANALYSIS (Wrong Run Alert):",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = "• गलत तरीका (Wrong Run): एड़ी (Heel) के बल ज़मीन पर भारी चोट के साथ पैर पटकना। इससे घुटनों में दर्द होता है।\n• सही तरीका (AI Fix): अपने शरीर को हल्का सा आगे झुकाएं और पंजे/मिड-फुट (Mid-foot) पर लैंड करें।",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MxeBusinessModule() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "💼 A TO Z BUSINESS & TRADING MASTERCLASS",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "📈 STOCK MARKET A-Z:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• जब आप शेयर खरीदते हैं, तो आप कंपनी के हिस्सेदार बनते हैं।\n• शुरुआती नियम: शुरुआत हमेशा निफ्टी 50 (Nifty 50) की टॉप कंपनियों से करें और 'Paper Trading' ऐप से सीखें।",
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
                
                HorizontalDivider()
                
                Text(
                    text = "💱 FOREX TRADING:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "• फॉरेक्स का मतलब है दो देशों की करेंसी (USD/INR) के उतार-चढ़ाव पर ट्रेड करना।\n• चेतावनी: यह अत्यधिक रिस्की है, बिना पूरी सीख के कदम न रखें।",
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
                
                HorizontalDivider()
                
                Text(
                    text = "👕 CLOTHING BRAND BLUEPRINT:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF0D9488)
                )
                Text(
                    text = "• स्टेप 1: अपनी नीश (Niche) चुनें (जैसे- एनीमे प्रिंटेड हुडीज़ या ओवरसाइज्ड टी-शर्ट)।\n• स्टेप 2: किसी अच्छे लोकल मैन्युफैक्चरर से कपड़े की क्वालिटी (GSM) चेक करके आर्डर करें।\n• स्टेप 3: इंस्टाग्राम रील्स और सोशल मीडिया मार्केटिंग के ज़रिए सीधे कस्टमर्स को बेचें।",
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun MxeLogsModule(logs: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "⏳ CENTRALISED APP HISTORY LOG",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (logs.isEmpty()) {
                    item {
                        Text(
                            text = "अभी तक कोई एक्टिविटी रिकॉर्ड नहीं हुई है।",
                            color = Color.Green,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    items(logs) { log ->
                        Text(
                            text = log,
                            color = Color(0xFF22C55E), // Terminal green
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MxeAdminModule(viewModel: MainViewModel, code: String, limit: Int) {
    val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsState()
    var passInput by remember { mutableStateOf("") }
    var errMessage by remember { mutableStateOf("") }
    
    var newCode by remember { mutableStateOf(code) }
    var newLimit by remember { mutableStateOf(limit.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "🛠️ ADMIN CONFIGURATION PANEL",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        if (!isAdminUnlocked) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "एडमिन कंट्रोल में प्रवेश करने के लिए पासवर्ड दर्ज करें। (Pass: MXEADMIN)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = passInput,
                    onValueChange = { passInput = it },
                    label = { Text("Admin Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_password_field")
                )
                
                if (errMessage.isNotEmpty()) {
                    Text(text = errMessage, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }
                
                Button(
                    onClick = {
                        val ok = viewModel.verifyAdminPassword(passInput)
                        if (!ok) {
                            errMessage = "गलत एडमिन पासवर्ड! एक्सेस ब्लॉक। ❌"
                        } else {
                            errMessage = ""
                        }
                        passInput = ""
                    },
                    modifier = Modifier.fillMaxWidth().testTag("admin_unlock_button")
                ) {
                    Text("Unlock Panel")
                }
            }
        } else {
            // Unlocked Admin Panel controls
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Status: Admin Unlocked ✅",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    fontSize = 12.sp
                )
                
                OutlinedTextField(
                    value = newCode,
                    onValueChange = { newCode = it },
                    label = { Text("Promo Code (Current: $code)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = newLimit,
                    onValueChange = { newLimit = it },
                    label = { Text("Code Usage Limit (Current: $limit)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Button(
                    onClick = {
                        val parsedLimit = newLimit.toIntOrNull() ?: limit
                        viewModel.updatePromoConfig(newCode, parsedLimit)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Update Configuration")
                }
            }
        }
    }
}

// Box borders defaults
@Composable
fun BoxBorderDefaults(): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = Color(0xFFBBF7D0) // light green border
    )
}
