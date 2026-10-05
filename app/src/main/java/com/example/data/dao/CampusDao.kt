package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC, startTime ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun getEventById(id: String): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventByIdSync(id: String): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: String)
}

@Dao
interface RsvpDao {
    @Query("SELECT * FROM rsvps ORDER BY registeredAt DESC")
    fun getAllRsvps(): Flow<List<RsvpEntity>>

    @Query("SELECT * FROM rsvps WHERE userId = :userId ORDER BY registeredAt DESC")
    fun getRsvpsForUser(userId: String): Flow<List<RsvpEntity>>

    @Query("SELECT * FROM rsvps WHERE eventId = :eventId ORDER BY registeredAt DESC")
    fun getRsvpsForEvent(eventId: String): Flow<List<RsvpEntity>>

    @Query("SELECT * FROM rsvps WHERE rsvpId = :rsvpId LIMIT 1")
    suspend fun getRsvpByCode(rsvpId: String): RsvpEntity?

    @Query("SELECT * FROM rsvps WHERE userId = :userId AND eventId = :eventId LIMIT 1")
    fun getRsvpByUserAndEvent(userId: String, eventId: String): Flow<RsvpEntity?>

    @Query("SELECT * FROM rsvps WHERE userId = :userId AND eventId = :eventId LIMIT 1")
    suspend fun getRsvpByUserAndEventSync(userId: String, eventId: String): RsvpEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRsvp(rsvp: RsvpEntity)

    @Update
    suspend fun updateRsvp(rsvp: RsvpEntity)

    @Delete
    suspend fun deleteRsvp(rsvp: RsvpEntity)

    @Query("DELETE FROM rsvps WHERE id = :id")
    suspend fun deleteRsvpById(id: String)
}

@Dao
interface SavedEventDao {
    @Query("SELECT * FROM saved_events WHERE userId = :userId")
    fun getSavedEventsForUser(userId: String): Flow<List<SavedEventEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_events WHERE userId = :userId AND eventId = :eventId)")
    fun isEventSaved(userId: String, eventId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaved(saved: SavedEventEntity)

    @Query("DELETE FROM saved_events WHERE userId = :userId AND eventId = :eventId")
    suspend fun deleteSaved(userId: String, eventId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET `read` = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("UPDATE notifications SET `read` = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)
}
