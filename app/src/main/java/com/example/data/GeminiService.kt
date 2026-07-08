package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Common Data Classes ---

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val tools: List<JsonObject>? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String
)

@Serializable
data class ResponseFormat(
    val text: ResponseFormatText? = null
)

@Serializable
data class ResponseFormatText(
    val mimeType: String,
    val schema: JsonObject? = null
)

@Serializable
data class GenerationConfig(
    val responseFormat: ResponseFormat? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)

// --- Retrofit Setup ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

// --- CBSE AI Helper Functions ---

object GeminiHelper {

    suspend fun askCbseDoubt(prompt: String, subject: String, grade: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Error: Please configure your GEMINI_API_KEY in the Secrets panel."
        }

        val systemPrompt = """
            You are a highly encouraging, friendly CBSE (Central Board of Secondary Education) School AI Assistant. 
            You must act as an expert tutor for Indian school pupils. 
            Adhere strictly to the NCERT syllabus for ${grade} in the subject of ${subject}.
            Provide clear step-by-step explanations, definition of formulas, and real-life examples relevant to Indian students.
            Use neat layout, bullet points, and highlight key terms.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No explanation received. Please try again."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Failed to reach Gemini server. Check network connection."}"
        }
    }

    suspend fun generateQuiz(subject: String, grade: String, topic: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API_KEY_MISSING"
        }

        val prompt = "Generate a CBSE style quiz consisting of exactly 5 multiple choice questions for $grade, subject: $subject, specifically about: $topic."
        
        val systemPrompt = """
            You are a CBSE board examiner. Create exactly 5 premium multiple choice questions (MCQs) covering the requested topic.
            Follow the NCERT syllabus. Ensure questions vary from conceptual to analytical.
            The response MUST be a JSON array matching the required JSON schema, containing questions, a list of exactly 4 options, the correct answer letter, and an explanation.
        """.trimIndent()

        // Build the strict JSON schema
        val schema = buildJsonObject {
            put("type", "ARRAY")
            putJsonObject("items") {
                put("type", "OBJECT")
                putJsonObject("properties") {
                    putJsonObject("question") {
                        put("type", "STRING")
                        put("description", "The question text, clear and concise")
                    }
                    putJsonObject("options") {
                        put("type", "ARRAY")
                        putJsonObject("items") { put("type", "STRING") }
                        put("description", "Exactly 4 options, prefix each option with a letter like 'A) Option text'")
                    }
                    putJsonObject("answer") {
                        put("type", "STRING")
                        put("description", "The correct answer option letter, must be exactly 'A', 'B', 'C', or 'D'")
                    }
                    putJsonObject("explanation") {
                        put("type", "STRING")
                        put("description", "Explanation of why this answer is correct")
                    }
                }
                putJsonArray("required") {
                    add(buildJsonObject { put("name", "question") })
                    add(buildJsonObject { put("name", "options") })
                    add(buildJsonObject { put("name", "answer") })
                    add(buildJsonObject { put("name", "explanation") })
                }
            }
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(
                temperature = 0.5f,
                responseFormat = ResponseFormat(
                    text = ResponseFormatText(
                        mimeType = "application/json",
                        schema = schema
                    )
                )
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            "ERROR: ${e.localizedMessage}"
        }
    }
}
