package com.example.data

import kotlinx.coroutines.flow.Flow

class Repository(
    private val studyCardDao: StudyCardDao,
    private val syllabusTopicDao: SyllabusTopicDao
) {
    val allStudyCards: Flow<List<StudyCard>> = studyCardDao.getAllStudyCards()
    val allSyllabusTopics: Flow<List<SyllabusTopic>> = syllabusTopicDao.getAllSyllabusTopics()

    suspend fun saveStudyCard(card: StudyCard) {
        studyCardDao.insertStudyCard(card)
    }

    suspend fun deleteStudyCardById(id: Int) {
        studyCardDao.deleteStudyCardById(id)
    }

    suspend fun updateSyllabusTopic(topic: SyllabusTopic) {
        syllabusTopicDao.updateSyllabusTopic(topic)
    }

    suspend fun prepopulateSyllabusIfEmpty() {
        if (syllabusTopicDao.getCount() < 25) {
            syllabusTopicDao.deleteAll()
            val defaultTopics = listOf(
                // Class 8 Mathematics
                SyllabusTopic(title = "Rational Numbers", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Linear Equations in One Variable", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Understanding Quadrilaterals", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Data Handling", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Squares and Square Roots", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Cubes and Cube Roots", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Comparing Quantities", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Algebraic Expressions and Identities", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Mensuration", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Exponents and Powers", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Direct and Inverse Proportions", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Factorisation", subject = "Math", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Introduction to Graphs", subject = "Math", grade = "Class 8", status = "PENDING"),

                // Class 8 Science
                SyllabusTopic(title = "Crop Production and Management", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Microorganisms: Friend and Foe", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Coal and Petroleum", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Combustion and Flame", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Conservation of Plants and Animals", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Reproduction in Animals", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Reaching the Age of Adolescence", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Force and Pressure", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Friction", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Sound", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Chemical Effects of Electric Current", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Some Natural Phenomena", subject = "Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Light", subject = "Science", grade = "Class 8", status = "PENDING"),

                // Class 8 Social Science
                SyllabusTopic(title = "How, When and Where (History)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "From Trade to Territory (History)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Ruling the Countryside (History)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Tribals, Dikus and Golden Age", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "When People Rebel 1857 & After", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Civilising the Native, Educating Nation", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Women, Caste and Reform", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Resources & Land, Water (Geography)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Agriculture (Geography)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Industries & Human Resources (Geo)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "The Indian Constitution (Civics)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Understanding Secularism & Parliament", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Judiciary & Criminal Justice (Civics)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Confronting Marginalisation (Civics)", subject = "Social Science", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Law and Social Justice (Civics)", subject = "Social Science", grade = "Class 8", status = "PENDING"),

                // Class 8 English
                SyllabusTopic(title = "The Best Christmas Present in the World", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "The Tsunami", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Glimpses of the Past", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "Bepin Choudhury's Lapse of Memory", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "The Summit Within", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "This is Jody's Fawn", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "A Visit to Cambridge", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "A Short Monsoon Diary", subject = "English", grade = "Class 8", status = "PENDING"),
                SyllabusTopic(title = "The Great Stone Face", subject = "English", grade = "Class 8", status = "PENDING"),

                // Class 10 Mathematics
                SyllabusTopic(title = "Real Numbers", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Polynomials", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Quadratic Equations", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Arithmetic Progressions", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Trigonometry Basics", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Some Applications of Trigonometry", subject = "Math", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Triangles & Coordinate Geometry", subject = "Math", grade = "Class 10", status = "PENDING"),
                
                // Class 10 Science
                SyllabusTopic(title = "Chemical Reactions and Equations", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Acids, Bases and Salts", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Metals and Non-metals", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Carbon and its Compounds", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Life Processes", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Light - Reflection & Refraction", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Electricity", subject = "Science", grade = "Class 10", status = "PENDING"),
                SyllabusTopic(title = "Magnetic Effects of Electric Current", subject = "Science", grade = "Class 10", status = "PENDING"),

                // Class 12 Physics
                SyllabusTopic(title = "Electrostatic Potential & Capacitance", subject = "Physics", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Current Electricity", subject = "Physics", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Moving Charges & Magnetism", subject = "Physics", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Electromagnetic Induction", subject = "Physics", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Ray Optics and Optical Instruments", subject = "Physics", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Wave Optics", subject = "Physics", grade = "Class 12", status = "PENDING"),

                // Class 12 Chemistry
                SyllabusTopic(title = "Solutions", subject = "Chemistry", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Electrochemistry", subject = "Chemistry", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Chemical Kinetics", subject = "Chemistry", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Coordination Compounds", subject = "Chemistry", grade = "Class 12", status = "PENDING"),
                SyllabusTopic(title = "Haloalkanes and Haloarenes", subject = "Chemistry", grade = "Class 12", status = "PENDING")
            )
            syllabusTopicDao.insertSyllabusTopics(defaultTopics)
        }
    }
}
