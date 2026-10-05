package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.CampusDatabase
import com.example.data.model.*
import com.example.data.repository.AttendanceScanResult
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class EventSortOption {
    UPCOMING,
    POPULAR,
    NEWEST
}

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CampusDatabase.getInstance(application)
    val repository = CampusRepository(db)

    // Current User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Current Role ("STUDENT" or "ADMIN")
    private val _currentRole = MutableStateFlow("STUDENT")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // All Events
    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All RSVPs (for Admin stats)
    val allRsvps: StateFlow<List<RsvpEntity>> = repository.allRsvps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("All")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedSort = MutableStateFlow(EventSortOption.UPCOMING)
    val selectedSort: StateFlow<EventSortOption> = _selectedSort.asStateFlow()

    // Saved Events for Current User
    private val _savedEventIds = MutableStateFlow<Set<String>>(emptySet())
    val savedEventIds: StateFlow<Set<String>> = _savedEventIds.asStateFlow()

    // User's RSVPs
    private val _userRsvps = MutableStateFlow<List<RsvpEntity>>(emptyList())
    val userRsvps: StateFlow<List<RsvpEntity>> = _userRsvps.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    // Selected Event for Details Screen
    private val _selectedEvent = MutableStateFlow<EventEntity?>(null)
    val selectedEvent: StateFlow<EventEntity?> = _selectedEvent.asStateFlow()

    // Newly confirmed RSVP (for celebration modal)
    private val _recentRsvp = MutableStateFlow<RsvpEntity?>(null)
    val recentRsvp: StateFlow<RsvpEntity?> = _recentRsvp.asStateFlow()

    // Attendance Scan Result
    private val _scanResult = MutableStateFlow<AttendanceScanResult?>(null)
    val scanResult: StateFlow<AttendanceScanResult?> = _scanResult.asStateFlow()

    // Feedback message (Snackbar/Toast)
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initSeedDataIfEmpty()
            // Set default user as Alex Chen (Student)
            val defaultStudent = repository.getUserByEmail("alex.chen@campus.edu")
            _currentUser.value = defaultStudent
            defaultStudent?.let { loadUserData(it.id) }
        }
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            repository.getSavedEventsForUser(userId).collect { list ->
                _savedEventIds.value = list.map { it.eventId }.toSet()
            }
        }
        viewModelScope.launch {
            repository.getRsvpsForUser(userId).collect { list ->
                _userRsvps.value = list
            }
        }
        viewModelScope.launch {
            repository.getNotificationsForUser(userId).collect { list ->
                _notifications.value = list
            }
        }
    }

    fun switchRole(newRole: String) {
        _currentRole.value = newRole
        viewModelScope.launch {
            if (newRole == "ADMIN") {
                val admin = repository.getUserByEmail("organizer@campus.edu")
                _currentUser.value = admin
                admin?.let { loadUserData(it.id) }
            } else {
                val student = repository.getUserByEmail("alex.chen@campus.edu")
                _currentUser.value = student
                student?.let { loadUserData(it.id) }
            }
        }
    }

    fun login(email: String, role: String): Boolean {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                _currentRole.value = user.role
                loadUserData(user.id)
                _toastMessage.value = "Welcome back, ${user.name}!"
            } else {
                _toastMessage.value = "User not found with this email"
            }
        }
        return true
    }

    fun registerStudent(
        name: String,
        email: String,
        studentId: String,
        department: String,
        year: String
    ) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = "usr_${UUID.randomUUID().toString().take(8)}",
                name = name,
                email = email,
                studentId = studentId,
                department = department,
                year = year,
                role = "STUDENT"
            )
            repository.registerUser(newUser)
            _currentUser.value = newUser
            _currentRole.value = "STUDENT"
            loadUserData(newUser.id)
            _toastMessage.value = "Account created! Welcome, $name"
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onDepartmentSelect(dept: String) {
        _selectedDepartment.value = dept
    }

    fun onSortSelect(sort: EventSortOption) {
        _selectedSort.value = sort
    }

    fun selectEvent(event: EventEntity) {
        _selectedEvent.value = event
    }

    fun toggleSave(eventId: String) {
        val user = _currentUser.value ?: return
        val isCurrentlySaved = _savedEventIds.value.contains(eventId)
        viewModelScope.launch {
            repository.toggleSaveEvent(user.id, eventId, isCurrentlySaved)
            _toastMessage.value = if (isCurrentlySaved) "Removed from Saved" else "Event Saved to Bookmarks"
        }
    }

    fun submitRsvp(
        event: EventEntity,
        phone: String,
        additionalInfo: String,
        onSuccess: (RsvpEntity) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val rsvp = repository.createRsvp(user, event, phone, additionalInfo)
            _recentRsvp.value = rsvp
            _toastMessage.value = "RSVP Confirmed! Ticket generated."
            onSuccess(rsvp)
        }
    }

    fun cancelRsvp(rsvpId: String, eventId: String) {
        viewModelScope.launch {
            repository.cancelRsvp(rsvpId, eventId)
            _toastMessage.value = "RSVP Cancelled."
        }
    }

    fun markNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
        }
    }

    fun clearRecentRsvp() {
        _recentRsvp.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Admin Operations
    fun saveEvent(
        title: String,
        description: String,
        category: String,
        date: String,
        startTime: String,
        endTime: String,
        venue: String,
        organizer: String,
        organizerEmail: String,
        contact: String,
        capacity: Int,
        eligibility: String,
        rules: String,
        requirements: String,
        speakers: String,
        schedule: String,
        existingId: String? = null
    ) {
        viewModelScope.launch {
            val event = EventEntity(
                id = existingId ?: "evt_${UUID.randomUUID().toString().take(8)}",
                title = title,
                description = description,
                category = category,
                date = date,
                startTime = startTime,
                endTime = endTime,
                venue = venue,
                organizer = organizer,
                organizerEmail = organizerEmail,
                contactNumber = contact,
                capacity = capacity,
                eligibility = eligibility,
                rules = rules,
                requirements = requirements,
                speakers = speakers,
                schedule = schedule,
                status = "REGISTRATION_OPEN"
            )
            if (existingId == null) {
                repository.saveEvent(event)
                _toastMessage.value = "Event created and published!"
            } else {
                repository.updateEvent(event)
                _toastMessage.value = "Event updated successfully!"
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
            _toastMessage.value = "Event removed from campus schedule."
        }
    }

    fun scanAttendance(code: String) {
        viewModelScope.launch {
            val result = repository.verifyAttendance(code)
            _scanResult.value = result
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
    }
}
