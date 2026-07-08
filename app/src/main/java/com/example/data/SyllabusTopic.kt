package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "syllabus_topics")
data class SyllabusTopic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String,
    val grade: String, // "Class 10", "Class 12"
    val status: String // "PENDING", "IN_PROGRESS", "MASTERED"
)
