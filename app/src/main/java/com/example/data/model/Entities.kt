package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val password: String = "password123",
    val studentId: String = "",
    val department: String = "",
    val year: String = "",
    val role: String = "STUDENT", // "STUDENT" or "ADMIN"
    val profileImage: String = ""
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val category: String, // Technical, Cultural, Sports, Workshop, Seminar, Hackathon, Symposium, Club Event, Placement, Competition, Fest
    val date: String, // YYYY-MM-DD or readable
    val startTime: String,
    val endTime: String,
    val venue: String,
    val organizer: String,
    val organizerEmail: String,
    val contactNumber: String,
    val capacity: Int,
    val registrationCount: Int = 0,
    val status: String = "REGISTRATION_OPEN", // DRAFT, PUBLISHED, REGISTRATION_OPEN, REGISTRATION_FULL, COMPLETED, CANCELLED
    val eligibility: String = "Open to all students",
    val rules: String = "Standard college code of conduct applies",
    val requirements: String = "College ID card is mandatory",
    val speakers: String = "",
    val schedule: String = "",
    val department: String = "All Departments",
    val eventType: String = "On-Campus",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rsvps")
data class RsvpEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val eventId: String,
    val rsvpId: String, // e.g. "RSVP-2026-00125"
    val studentName: String,
    val studentId: String,
    val collegeEmail: String,
    val department: String,
    val year: String,
    val phone: String = "",
    val additionalInfo: String = "",
    val status: String = "CONFIRMED", // CONFIRMED, CANCELLED
    val registeredAt: Long = System.currentTimeMillis(),
    val attendanceStatus: String = "NOT_ATTENDED", // NOT_ATTENDED, VERIFIED
    val verifiedAt: Long? = null
)

@Entity(tableName = "saved_events", primaryKeys = ["userId", "eventId"])
data class SavedEventEntity(
    val userId: String,
    val eventId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String = "ALERT", // RSVP, REMINDER, VENUE_CHANGE, TIME_CHANGE, NEW_EVENT, ALERT
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
