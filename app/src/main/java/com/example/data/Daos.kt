package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyCardDao {
    @Query("SELECT * FROM study_cards ORDER BY timestamp DESC")
    fun getAllStudyCards(): Flow<List<StudyCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyCard(card: StudyCard)

    @Query("DELETE FROM study_cards WHERE id = :id")
    suspend fun deleteStudyCardById(id: Int)
}

@Dao
interface SyllabusTopicDao {
    @Query("SELECT * FROM syllabus_topics ORDER BY id ASC")
    fun getAllSyllabusTopics(): Flow<List<SyllabusTopic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusTopics(topics: List<SyllabusTopic>)

    @Update
    suspend fun updateSyllabusTopic(topic: SyllabusTopic)

    @Query("SELECT COUNT(*) FROM syllabus_topics")
    suspend fun getCount(): Int

    @Query("DELETE FROM syllabus_topics")
    suspend fun deleteAll()
}
