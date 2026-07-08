package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database.studyCardDao(), database.syllabusTopicDao())

    // UI state for selected navigation tab: "chat", "syllabus", "quiz", "saved", "mxe"
    private val _selectedTab = MutableStateFlow("chat")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    // --- MXE Core AI State ---
    private val _isSecurityPassed = MutableStateFlow(false)
    val isSecurityPassed: StateFlow<Boolean> = _isSecurityPassed.asStateFlow()

    private val _securityPin = MutableStateFlow("1234")
    val securityPin: StateFlow<String> = _securityPin.asStateFlow()

    private val _isProUnlocked = MutableStateFlow(false)
    val isProUnlocked: StateFlow<Boolean> = _isProUnlocked.asStateFlow()

    private val _promoCode = MutableStateFlow("MXE200")
    val promoCode: StateFlow<String> = _promoCode.asStateFlow()

    private val _codeLimit = MutableStateFlow(200)
    val codeLimit: StateFlow<Int> = _codeLimit.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _userWeight = MutableStateFlow(87.0)
    val userWeight: StateFlow<Double> = _userWeight.asStateFlow()

    private val _targetPhysique = MutableStateFlow("Lean Anime Aesthetic")
    val targetPhysique: StateFlow<String> = _targetPhysique.asStateFlow()

    private val _historyLogs = MutableStateFlow<List<String>>(listOf("[${getCurrentTimestamp()}] MXE Core AI initialized."))
    val historyLogs: StateFlow<List<String>> = _historyLogs.asStateFlow()

    private val _currentStudyTip = MutableStateFlow("NCERT is gold: 90%+ exam questions in CBSE Class 8 are directly based on NCERT textbooks. Practice every exercise!")
    val currentStudyTip: StateFlow<String> = _currentStudyTip.asStateFlow()

    private fun getCurrentTimestamp(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    fun logActivity(activity: String) {
        val timestamp = getCurrentTimestamp()
        _historyLogs.update { logs ->
            listOf("[$timestamp] $activity") + logs
        }
    }

    fun verifyPin(pin: String): Boolean {
        if (pin == _securityPin.value) {
            _isSecurityPassed.value = true
            logActivity("सफलतापूर्वक लॉगिन किया गया (Security PIN Verified).")
            return true
        }
        logActivity("लॉगिन विफल: गलत पिन दर्ज किया गया।")
        return false
    }

    fun applyPromoCode(code: String): Boolean {
        if (code.trim() == _promoCode.value) {
            if (_codeLimit.value > 0) {
                _isProUnlocked.value = true
                _codeLimit.update { it - 1 }
                logActivity("${_promoCode.value} कोड का उपयोग करके प्रो वर्जन अनलॉक किया।")
                return true
            } else {
                logActivity("प्रो कोड विफल: लिमिट समाप्त हो चुकी है।")
            }
        } else {
            logActivity("प्रो कोड विफल: अमान्य कोड '$code' दर्ज किया गया।")
        }
        return false
    }

    fun verifyAdminPassword(password: String): Boolean {
        if (password == "MXEADMIN") {
            _isAdminUnlocked.value = true
            logActivity("एडमिन पैनल एक्सेस किया गया।")
            return true
        }
        logActivity("एडमिन एक्सेस विफल: गलत पासवर्ड।")
        return false
    }

    fun updatePromoConfig(newCode: String, newLimit: Int) {
        if (newCode.isNotBlank()) {
            _promoCode.value = newCode
        }
        _codeLimit.value = newLimit
        logActivity("एडमिन द्वारा प्रो कोड को '$newCode' और सीमा को $newLimit किया गया।")
    }

    fun updateWeight(weight: Double) {
        _userWeight.value = weight
        logActivity("वजन अपडेट किया गया: ${weight}kg")
    }

    fun updateTargetPhysique(physique: String) {
        _targetPhysique.value = physique
        logActivity("लक्ष्य शरीर अपडेट किया गया: $physique")
    }

    fun logOutMxe() {
        _isSecurityPassed.value = false
        _isAdminUnlocked.value = false
        logActivity("सिस्टम से सुरक्षित रूप से लॉग आउट किया गया।")
    }

    private val studyTips = listOf(
        "NCERT is gold: 90%+ exam questions in CBSE Class 8 are directly based on NCERT textbooks. Practice every exercise!",
        "Avoid rote learning: explain concepts to an imaginary friend or voice-record yourself explaining them to deeply understand.",
        "Use Pomodoro: study with full focus for 25 minutes, then take a short 5-minute break to refresh your brain.",
        "Active Recall: after reading a section, close the book and write down everything you remember in your own words.",
        "CBSE Mock Tests: practicing previous year question papers is the single best way to manage time and remove exam fear.",
        "Social Science tip: use flowcharts and timelines for History chapters to easily remember crucial dates and events.",
        "Math Practice: do not just practice math mentally. Write down the solution step-by-step; actual writing builds muscle memory."
    )

    fun nextStudyTip() {
        val current = _currentStudyTip.value
        val index = studyTips.indexOf(current)
        val nextIndex = if (index == -1 || index >= studyTips.size - 1) 0 else index + 1
        _currentStudyTip.value = studyTips[nextIndex]
        logActivity("नया दैनिक स्टडी टिप चेक किया।")
    }

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = Sender.AI,
                text = "Namaste! 🙏 I am your CBSE School AI Assistant. Choose your Class and Subject below, and ask me any questions or doubts from the NCERT syllabus!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatSubject = MutableStateFlow("Math")
    val chatSubject: StateFlow<String> = _chatSubject.asStateFlow()

    private val _chatGrade = MutableStateFlow("Class 8")
    val chatGrade: StateFlow<String> = _chatGrade.asStateFlow()

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading.asStateFlow()

    // --- Syllabus Progress State ---
    val syllabusTopics: StateFlow<List<SyllabusTopic>> = repository.allSyllabusTopics
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _syllabusFilterGrade = MutableStateFlow("Class 8")
    val syllabusFilterGrade: StateFlow<String> = _syllabusFilterGrade.asStateFlow()

    private val _syllabusFilterSubject = MutableStateFlow("All")
    val syllabusFilterSubject: StateFlow<String> = _syllabusFilterSubject.asStateFlow()

    // --- Bookmarked Study Cards State ---
    val savedCards: StateFlow<List<StudyCard>> = repository.allStudyCards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Quiz State ---
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _quizIndex = MutableStateFlow(0)
    val quizIndex: StateFlow<Int> = _quizIndex.asStateFlow()

    private val _quizSelectedOption = MutableStateFlow("")
    val quizSelectedOption: StateFlow<String> = _quizSelectedOption.asStateFlow()

    private val _quizAnswered = MutableStateFlow(false)
    val quizAnswered: StateFlow<Boolean> = _quizAnswered.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizLoading = MutableStateFlow(false)
    val quizLoading: StateFlow<Boolean> = _quizLoading.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    private val _quizError = MutableStateFlow("")
    val quizError: StateFlow<String> = _quizError.asStateFlow()

    private val _quizGrade = MutableStateFlow("Class 8")
    val quizGrade: StateFlow<String> = _quizGrade.asStateFlow()

    private val _quizSubject = MutableStateFlow("Math")
    val quizSubject: StateFlow<String> = _quizSubject.asStateFlow()

    private val _quizTopicInput = MutableStateFlow("Rational Numbers")
    val quizTopicInput: StateFlow<String> = _quizTopicInput.asStateFlow()

    init {
        // Pre-populate syllabus database
        viewModelScope.launch(Dispatchers.IO) {
            repository.prepopulateSyllabusIfEmpty()
        }
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    // --- Chat Functions ---
    fun setChatSubject(subject: String) {
        _chatSubject.value = subject
    }

    fun setChatGrade(grade: String) {
        _chatGrade.value = grade
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(sender = Sender.USER, text = text)
        _chatMessages.update { it + userMessage }
        logActivity("पूछा गया सवाल: '$text' (${_chatSubject.value}, ${_chatGrade.value})")

        viewModelScope.launch {
            _chatLoading.value = true
            val response = GeminiHelper.askCbseDoubt(
                prompt = text,
                subject = _chatSubject.value,
                grade = _chatGrade.value
            )
            val aiMessage = ChatMessage(sender = Sender.AI, text = response)
            _chatMessages.update { it + aiMessage }
            _chatLoading.value = false
            logActivity("CBSE Tutor ने जवाब दिया।")
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = Sender.AI,
                text = "Chat history cleared. Select your subject/class and ask me any doubts!"
            )
        )
        logActivity("चैट हिस्ट्री साफ़ की गई।")
    }

    // --- Syllabus Functions ---
    fun setSyllabusFilterGrade(grade: String) {
        _syllabusFilterGrade.value = grade
    }

    fun setSyllabusFilterSubject(subject: String) {
        _syllabusFilterSubject.value = subject
    }

    fun updateTopicStatus(topic: SyllabusTopic, nextStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSyllabusTopic(topic.copy(status = nextStatus))
            logActivity("सिलेबस ट्रैक अपडेट किया गया: '${topic.title}' -> $nextStatus")
        }
    }

    // --- Study Card Functions ---
    fun bookmarkStudyCard(title: String, content: String, subject: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveStudyCard(
                StudyCard(
                    title = title,
                    content = content,
                    subject = subject
                )
            )
            logActivity("नोटबुक कार्ड सहेजा गया: '$title'")
        }
    }

    fun deleteStudyCard(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteStudyCardById(id)
            logActivity("नोटबुक कार्ड हटाया गया।")
        }
    }

    // --- Quiz Functions ---
    fun setQuizGrade(grade: String) {
        _quizGrade.value = grade
    }

    fun setQuizSubject(subject: String) {
        _quizSubject.value = subject
    }

    fun setQuizTopicInput(topic: String) {
        _quizTopicInput.value = topic
    }

    fun startNewQuiz() {
        viewModelScope.launch {
            _quizLoading.value = true
            _quizError.value = ""
            _quizFinished.value = false
            _quizIndex.value = 0
            _quizScore.value = 0
            _quizSelectedOption.value = ""
            _quizAnswered.value = false
            logActivity("क्विज़ शुरू की गई: Topic '${_quizTopicInput.value}' (${_quizSubject.value}, ${_quizGrade.value})")
            _quizAnswered.value = false

            val rawJson = GeminiHelper.generateQuiz(
                subject = _quizSubject.value,
                grade = _quizGrade.value,
                topic = _quizTopicInput.value
            )

            if (rawJson == "API_KEY_MISSING") {
                _quizError.value = "Please configure your GEMINI_API_KEY in the Secrets panel."
                _quizLoading.value = false
                return@launch
            }

            if (rawJson.startsWith("ERROR:")) {
                _quizError.value = "Failed to generate quiz: ${rawJson.removePrefix("ERROR:")}"
                _quizLoading.value = false
                return@launch
            }

            try {
                // Find potential outer JSON formatting in case of backticks
                val cleanedJson = cleanJsonString(rawJson)
                val format = Json { ignoreUnknownKeys = true }
                val questions = format.decodeFromString<List<QuizQuestion>>(cleanedJson)
                
                if (questions.isNotEmpty()) {
                    _quizQuestions.value = questions
                } else {
                    _quizError.value = "Gemini returned an empty quiz. Please try again."
                }
            } catch (e: Exception) {
                _quizError.value = "Error parsing CBSE quiz: ${e.localizedMessage}. Please try again."
            } finally {
                _quizLoading.value = false
            }
        }
    }

    private fun cleanJsonString(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    fun selectQuizOption(optionLetter: String) {
        if (_quizAnswered.value) return
        _quizSelectedOption.value = optionLetter
    }

    fun submitQuizAnswer() {
        if (_quizSelectedOption.value.isEmpty() || _quizAnswered.value) return
        _quizAnswered.value = true
        val currentQuestion = _quizQuestions.value.getOrNull(_quizIndex.value)
        if (currentQuestion != null) {
            // Check if answer matches (can handle case-insensitive or trimmed letter matching)
            val correctAnswerLetter = currentQuestion.answer.trim().firstOrNull()?.toString()?.uppercase() ?: ""
            val selectedLetter = _quizSelectedOption.value.trim().firstOrNull()?.toString()?.uppercase() ?: ""
            
            if (correctAnswerLetter == selectedLetter) {
                _quizScore.update { it + 1 }
            }
        }
    }

    fun advanceQuiz() {
        val nextIndex = _quizIndex.value + 1
        if (nextIndex >= _quizQuestions.value.size) {
            _quizFinished.value = true
        } else {
            _quizIndex.value = nextIndex
            _quizSelectedOption.value = ""
            _quizAnswered.value = false
        }
    }

    fun resetQuizModule() {
        _quizQuestions.value = emptyList()
        _quizFinished.value = false
        _quizLoading.value = false
        _quizError.value = ""
    }
}
