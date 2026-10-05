package com.example.data.repository

import com.example.data.database.CampusDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CampusRepository(private val db: CampusDatabase) {

    private val userDao = db.userDao()
    private val eventDao = db.eventDao()
    private val rsvpDao = db.rsvpDao()
    private val savedEventDao = db.savedEventDao()
    private val notificationDao = db.notificationDao()

    val allEvents: Flow<List<EventEntity>> = eventDao.getAllEvents()
    val allRsvps: Flow<List<RsvpEntity>> = rsvpDao.getAllRsvps()

    suspend fun initSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getUserByEmail("alex.chen@campus.edu")
        if (existingUsers == null) {
            // Seed Default Users
            val studentAlex = UserEntity(
                id = "usr_student_alex",
                name = "Alex Chen",
                email = "alex.chen@campus.edu",
                password = "password123",
                studentId = "CS-2023-042",
                department = "Computer Science & Engineering",
                year = "3rd Year",
                role = "STUDENT"
            )

            val studentMaya = UserEntity(
                id = "usr_student_maya",
                name = "Maya Patel",
                email = "maya.patel@campus.edu",
                password = "password123",
                studentId = "EC-2024-118",
                department = "Electronics & Communication",
                year = "2nd Year",
                role = "STUDENT"
            )

            val adminSarah = UserEntity(
                id = "usr_admin_sarah",
                name = "Dr. Sarah Jenkins",
                email = "organizer@campus.edu",
                password = "admin123",
                studentId = "FAC-DEAN-09",
                department = "Student Activities Council",
                year = "Dean / Faculty",
                role = "ADMIN"
            )

            userDao.insertUser(studentAlex)
            userDao.insertUser(studentMaya)
            userDao.insertUser(adminSarah)

            // Seed Sample Events
            val seedEvents = listOf(
                EventEntity(
                    id = "evt_hack_01",
                    title = "HackCamp 2026: AI & Web3 Sprint",
                    description = "Join the 24-hour inter-college hackathon to build intelligent decentralized apps, automated AI agents, and innovative campus solutions. Free mentors, meals, and \$5,000 cash prizes!",
                    category = "Hackathon",
                    date = "2026-10-06",
                    startTime = "09:00 AM",
                    endTime = "09:00 PM",
                    venue = "Turing Innovation Center, Block C",
                    organizer = "Campus Developers Club",
                    organizerEmail = "hackcamp@campus.edu",
                    contactNumber = "+1 (555) 234-5678",
                    capacity = 120,
                    registrationCount = 98,
                    status = "REGISTRATION_OPEN",
                    eligibility = "All undergraduate and graduate students",
                    rules = "Teams of 2-4 members. Bring personal laptops and college ID card. Original code created during the hackathon only.",
                    requirements = "Laptops, chargers, college ID, active GitHub profile.",
                    speakers = "Elena Vance (AI Lead @ Vertex), Dev Sharma (Web3 Architect)",
                    schedule = "09:00 AM - Check-in & Breakfast\n10:00 AM - Opening Keynote & Team Matching\n11:00 AM - Hacking Begins\n02:00 PM - Mentor Pitch Checkpoints\n08:00 PM - Demos & Winner Ceremony",
                    department = "Computer Science & Engineering",
                    eventType = "Hackathon"
                ),
                EventEntity(
                    id = "evt_cult_02",
                    title = "Rhythm '26: Annual Inter-College Cultural Fest",
                    description = "The biggest cultural celebration of the academic year featuring live band concerts, street dance faceoffs, theatrical drama, and celebrity guest performances!",
                    category = "Fest",
                    date = "2026-10-12",
                    startTime = "05:00 PM",
                    endTime = "10:30 PM",
                    venue = "University Grand Amphitheatre",
                    organizer = "Campus Cultural Council",
                    organizerEmail = "rhythm@campus.edu",
                    contactNumber = "+1 (555) 345-6789",
                    capacity = 800,
                    registrationCount = 642,
                    status = "REGISTRATION_OPEN",
                    eligibility = "All registered students and alumni pass holders",
                    rules = "Entry strictly via digital ticket QR scan. Bags subject to security check. Respect campus decorum.",
                    requirements = "Digital RSVP Ticket QR, Valid Student ID.",
                    speakers = "DJ Kairo, The Echoes Indie Rock Band, Classical Fusion Troupe",
                    schedule = "05:00 PM - Gates Open & Food Stalls\n06:00 PM - Battle of the Bands\n07:30 PM - Street Dance Showcase\n08:45 PM - Headliner Musical Concert",
                    department = "All Departments",
                    eventType = "Cultural Fest"
                ),
                EventEntity(
                    id = "evt_work_03",
                    title = "Google Cloud & Generative AI Workshop",
                    description = "Hands-on masterclass on building LLM agents, prompt engineering, multi-modal Gemini API integration, and deploying scalable cloud microservices.",
                    category = "Workshop",
                    date = "2026-10-07",
                    startTime = "10:00 AM",
                    endTime = "01:30 PM",
                    venue = "Advanced Computing Lab 3, Faraday Hall",
                    organizer = "Google Developer Student Club (GDSC)",
                    organizerEmail = "gdsc@campus.edu",
                    contactNumber = "+1 (555) 456-7890",
                    capacity = 60,
                    registrationCount = 58,
                    status = "REGISTRATION_OPEN",
                    eligibility = "Students interested in Cloud & Artificial Intelligence",
                    rules = "Hands-on workshop. Prior basic Python or JavaScript knowledge recommended.",
                    requirements = "Personal laptop with Chrome browser. Google Cloud credits provided.",
                    speakers = "Arjun Rao (Cloud Solutions Architect)",
                    schedule = "10:00 AM - Intro to Generative Models\n11:00 AM - Hands-on Lab: Gemini API & Function Calling\n12:30 PM - Cloud Deployment & Q&A",
                    department = "Computer Science & Engineering",
                    eventType = "Technical Workshop"
                ),
                EventEntity(
                    id = "evt_symp_04",
                    title = "National Robotics & Autonomous Systems Symposium",
                    description = "Showcasing student drone prototypes, quadrupeds, autonomous rovers, and cutting-edge papers on computer vision and embedded systems.",
                    category = "Symposium",
                    date = "2026-10-15",
                    startTime = "09:30 AM",
                    endTime = "04:30 PM",
                    venue = "Mechatronics Research Complex, Hall B",
                    organizer = "Robotics & Automation Society",
                    organizerEmail = "robotics@campus.edu",
                    contactNumber = "+1 (555) 567-8901",
                    capacity = 150,
                    registrationCount = 112,
                    status = "REGISTRATION_OPEN",
                    eligibility = "Engineering & Physical Science students",
                    rules = "Live drone demonstrations only in designated indoor caged arena.",
                    requirements = "Symposium delegate pass / RSVP ticket.",
                    speakers = "Dr. Michael Chen (MIT Robotics Lab), Priya Nair (DroneTech Systems)",
                    schedule = "09:30 AM - Paper Presentations\n11:30 AM - Autonomous Rover Live Demo\n02:00 PM - Industrial Robotics Panel\n03:30 PM - Best Innovation Awards",
                    department = "Mechanical & Mechatronics",
                    eventType = "Symposium"
                ),
                EventEntity(
                    id = "evt_sport_05",
                    title = "Campus Premier League Cricket & Football Finals",
                    description = "Championship finals of the inter-department sports tournament! Come cheer for your department teams in high-energy cricket and football showdowns.",
                    category = "Sports",
                    date = "2026-10-08",
                    startTime = "08:30 AM",
                    endTime = "06:00 PM",
                    venue = "University Central Stadium & Sports Complex",
                    organizer = "Campus Sports Association",
                    organizerEmail = "sports@campus.edu",
                    contactNumber = "+1 (555) 678-9012",
                    capacity = 500,
                    registrationCount = 380,
                    status = "REGISTRATION_OPEN",
                    eligibility = "All students, staff, and faculty supporters",
                    rules = "Spectator code of conduct strictly enforced. Free team fan banners at entrance.",
                    requirements = "Student ID or faculty pass.",
                    speakers = "Coach Marcus Blake (Former National Athlete)",
                    schedule = "08:30 AM - Cricket Final: CS Knights vs Mech Titans\n02:00 PM - Football Final: ECE Strikers vs Business United\n05:15 PM - Trophy Presentation",
                    department = "Physical Education",
                    eventType = "Sports Tournament"
                ),
                EventEntity(
                    id = "evt_place_06",
                    title = "NextGen Campus Placement & Career Fair 2026",
                    description = "Meet hiring managers and tech recruiters from 40+ top tier companies including Microsoft, Google, Amazon, Deloitte, and innovative high-growth startups.",
                    category = "Placement",
                    date = "2026-10-20",
                    startTime = "09:00 AM",
                    endTime = "05:00 PM",
                    venue = "University Convention Center, 1st & 2nd Floors",
                    organizer = "University Placement & Training Cell",
                    organizerEmail = "placement@campus.edu",
                    contactNumber = "+1 (555) 789-0123",
                    capacity = 600,
                    registrationCount = 590,
                    status = "REGISTRATION_OPEN",
                    eligibility = "3rd and Final Year Undergraduates & Master's students",
                    rules = "Formal attire mandatory. Carry at least 5 printed copies of your resume.",
                    requirements = "Updated resume copies, College ID, RSVP QR Pass.",
                    speakers = "HR Leaders & Technical Recruiters from Fortune 500 Companies",
                    schedule = "09:00 AM - Company Keynotes & Overview\n10:30 AM - Booth Networking & Resume Submissions\n01:30 PM - On-spot Technical Screenings\n04:00 PM - Interview Shortlist Announcements",
                    department = "All Departments",
                    eventType = "Career Fair"
                ),
                EventEntity(
                    id = "evt_comp_07",
                    title = "TechSpark: Algorithmic Coding Arena",
                    description = "Fast-paced competitive programming contest on dynamic programming, graph theory, and algorithmic problem solving. Rank up on the live leaderboard!",
                    category = "Competition",
                    date = "2026-10-10",
                    startTime = "02:00 PM",
                    endTime = "05:00 PM",
                    venue = "Central Computing Lab, Turing Block",
                    organizer = "ACM Student Chapter",
                    organizerEmail = "acm@campus.edu",
                    contactNumber = "+1 (555) 890-1234",
                    capacity = 80,
                    registrationCount = 80,
                    status = "REGISTRATION_FULL",
                    eligibility = "Students enrolled in any STEM program",
                    rules = "Individual participation. Standard competitive programming rules apply. Plagiarism detection enabled.",
                    requirements = "College ID and competitive coding platform account.",
                    speakers = "Tanmay Shah (International Grandmaster on Codeforces)",
                    schedule = "02:00 PM - Contest Briefing & Environment Setup\n02:30 PM - 2.5hr Speed Coding Contest\n05:00 PM - Live Editorial & Awards",
                    department = "Computer Science & Engineering",
                    eventType = "Competition"
                ),
                EventEntity(
                    id = "evt_des_08",
                    title = "Modern UI/UX & Product Design Masterclass",
                    description = "Learn how to conduct user research, create design systems, wireframe in Figma, and build frictionless mobile experiences that delight users.",
                    category = "Workshop",
                    date = "2026-10-14",
                    startTime = "11:00 AM",
                    endTime = "03:00 PM",
                    venue = "Design & Media Studio, Room 204",
                    organizer = "Campus Design & Creative Media Guild",
                    organizerEmail = "design@campus.edu",
                    contactNumber = "+1 (555) 901-2345",
                    capacity = 45,
                    registrationCount = 28,
                    status = "REGISTRATION_OPEN",
                    eligibility = "Open to all design enthusiasts and developers",
                    rules = "Bring a laptop with Figma installed (free student account).",
                    requirements = "Laptop, mouse recommended, Figma account.",
                    speakers = "Sophia Lin (Product Designer @ Stripe)",
                    schedule = "11:00 AM - Foundations of Modern UI Design\n12:30 PM - Hands-on Figma Interactive Prototype\n02:00 PM - Critiques & Portfolio Review",
                    department = "Design & Creative Arts",
                    eventType = "Workshop"
                ),
                EventEntity(
                    id = "evt_sem_09",
                    title = "Cybersecurity & Ethical Hacking Summit",
                    description = "Deep dive into web application security, zero-day threat landscapes, network penetration testing, and ethical hacking careers.",
                    category = "Seminar",
                    date = "2026-10-18",
                    startTime = "10:00 AM",
                    endTime = "01:00 PM",
                    venue = "Newton Memorial Auditorium",
                    organizer = "Information Security Student Guild",
                    organizerEmail = "infosec@campus.edu",
                    contactNumber = "+1 (555) 012-3456",
                    capacity = 200,
                    registrationCount = 145,
                    status = "REGISTRATION_OPEN",
                    eligibility = "All students interested in Cyber Defense",
                    rules = "Live ethical demonstrations are for educational defense purposes only.",
                    requirements = "College ID card.",
                    speakers = "Kavita Reddy (Certified Ethical Hacker & CISO Advisor)",
                    schedule = "10:00 AM - Modern Threat Landscape 2026\n11:15 AM - Live CTF & Vulnerability Showcase\n12:15 PM - Cybersecurity Career Pathways",
                    department = "Information Technology",
                    eventType = "Seminar"
                ),
                EventEntity(
                    id = "evt_club_10",
                    title = "Campus Photography & Drone Filmmaking Meet",
                    description = "Golden hour photowalk, drone cinematography demo, and portfolio feedback session across the most scenic campus spots.",
                    category = "Club Event",
                    date = "2026-10-09",
                    startTime = "03:30 PM",
                    endTime = "06:30 PM",
                    venue = "Student Plaza & Open Air Amphitheatre",
                    organizer = "Campus Lens & Shutter Club",
                    organizerEmail = "lens@campus.edu",
                    contactNumber = "+1 (555) 123-4567",
                    capacity = 50,
                    registrationCount = 32,
                    status = "REGISTRATION_OPEN",
                    eligibility = "Open to any student with a camera or smartphone",
                    rules = "Respect privacy of fellow campus members during shooting.",
                    requirements = "DSLR, mirrorless camera, or smartphone camera.",
                    speakers = "Liam Rossi (National Geographic Contributor)",
                    schedule = "03:30 PM - Composition & Lighting Masterclass\n04:30 PM - Guided Campus Photowalk\n05:45 PM - Drone Flight Demo & Editing Review",
                    department = "Arts & Media",
                    eventType = "Club Meet"
                )
            )
            eventDao.insertEvents(seedEvents)

            // Seed Initial Confirmed RSVP for Alex
            val seedRsvp = RsvpEntity(
                id = "rsvp_seed_alex_hack",
                userId = "usr_student_alex",
                eventId = "evt_hack_01",
                rsvpId = "RSVP-2026-00125",
                studentName = "Alex Chen",
                studentId = "CS-2023-042",
                collegeEmail = "alex.chen@campus.edu",
                department = "Computer Science & Engineering",
                year = "3rd Year",
                phone = "+1 (555) 789-4321",
                additionalInfo = "Interested in AI Agents track. Bringing laptop & hardware kit.",
                status = "CONFIRMED",
                registeredAt = System.currentTimeMillis() - 86400000L,
                attendanceStatus = "NOT_ATTENDED"
            )
            rsvpDao.insertRsvp(seedRsvp)

            // Seed Initial Saved Event for Alex
            savedEventDao.insertSaved(SavedEventEntity("usr_student_alex", "evt_cult_02"))
            savedEventDao.insertSaved(SavedEventEntity("usr_student_alex", "evt_work_03"))

            // Seed Initial Notifications
            val seedNotifications = listOf(
                NotificationEntity(
                    id = "notif_01",
                    userId = "usr_student_alex",
                    title = "RSVP Confirmed: HackCamp 2026!",
                    message = "Your seat is confirmed! Your digital ticket is ready. RSVP ID: RSVP-2026-00125",
                    type = "RSVP",
                    read = false,
                    createdAt = System.currentTimeMillis() - 3600000L
                ),
                NotificationEntity(
                    id = "notif_02",
                    userId = "usr_student_alex",
                    title = "Event Reminder 🔔",
                    message = "HackCamp 2026 starts tomorrow at 9:00 AM at Turing Innovation Center.",
                    type = "REMINDER",
                    read = false,
                    createdAt = System.currentTimeMillis() - 7200000L
                ),
                NotificationEntity(
                    id = "notif_03",
                    userId = "usr_student_alex",
                    title = "New Workshop Announced",
                    message = "Google Cloud & Generative AI Workshop has opened for registrations.",
                    type = "NEW_EVENT",
                    read = true,
                    createdAt = System.currentTimeMillis() - 86400000L
                )
            )
            notificationDao.insertNotifications(seedNotifications)
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    fun getEventById(eventId: String): Flow<EventEntity?> = eventDao.getEventById(eventId)
    suspend fun getEventByIdSync(eventId: String): EventEntity? = withContext(Dispatchers.IO) {
        eventDao.getEventByIdSync(eventId)
    }

    suspend fun saveEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(eventId: String) = withContext(Dispatchers.IO) {
        eventDao.deleteEventById(eventId)
    }

    fun getRsvpsForUser(userId: String): Flow<List<RsvpEntity>> = rsvpDao.getRsvpsForUser(userId)
    fun getRsvpsForEvent(eventId: String): Flow<List<RsvpEntity>> = rsvpDao.getRsvpsForEvent(eventId)
    fun getRsvpByUserAndEvent(userId: String, eventId: String): Flow<RsvpEntity?> = rsvpDao.getRsvpByUserAndEvent(userId, eventId)

    suspend fun createRsvp(
        user: UserEntity,
        event: EventEntity,
        phone: String,
        additionalInfo: String
    ): RsvpEntity = withContext(Dispatchers.IO) {
        // Generate random sequence number
        val randomSeq = (100..999).random()
        val yearStr = SimpleDateFormat("yyyy", Locale.US).format(Date())
        val rsvpCode = "RSVP-$yearStr-${String.format(Locale.US, "%05d", randomSeq + event.registrationCount)}"

        val rsvp = RsvpEntity(
            id = "rsvp_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            eventId = event.id,
            rsvpId = rsvpCode,
            studentName = user.name,
            studentId = user.studentId,
            collegeEmail = user.email,
            department = user.department,
            year = user.year,
            phone = phone,
            additionalInfo = additionalInfo,
            status = "CONFIRMED",
            registeredAt = System.currentTimeMillis(),
            attendanceStatus = "NOT_ATTENDED"
        )
        rsvpDao.insertRsvp(rsvp)

        // Increment event registration count
        val updatedEvent = event.copy(
            registrationCount = event.registrationCount + 1,
            status = if (event.registrationCount + 1 >= event.capacity) "REGISTRATION_FULL" else event.status
        )
        eventDao.updateEvent(updatedEvent)

        // Add Notification
        val notif = NotificationEntity(
            id = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            title = "RSVP Confirmed: ${event.title}",
            message = "Your RSVP was successful! Ticket Code: $rsvpCode. See you at ${event.venue}.",
            type = "RSVP",
            read = false,
            createdAt = System.currentTimeMillis()
        )
        notificationDao.insertNotification(notif)

        rsvp
    }

    suspend fun cancelRsvp(rsvpId: String, eventId: String) = withContext(Dispatchers.IO) {
        rsvpDao.deleteRsvpById(rsvpId)
        val event = eventDao.getEventByIdSync(eventId)
        if (event != null && event.registrationCount > 0) {
            val updated = event.copy(
                registrationCount = event.registrationCount - 1,
                status = if (event.status == "REGISTRATION_FULL") "REGISTRATION_OPEN" else event.status
            )
            eventDao.updateEvent(updated)
        }
    }

    fun isEventSaved(userId: String, eventId: String): Flow<Boolean> = savedEventDao.isEventSaved(userId, eventId)
    fun getSavedEventsForUser(userId: String): Flow<List<SavedEventEntity>> = savedEventDao.getSavedEventsForUser(userId)

    suspend fun toggleSaveEvent(userId: String, eventId: String, isCurrentlySaved: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlySaved) {
            savedEventDao.deleteSaved(userId, eventId)
        } else {
            savedEventDao.insertSaved(SavedEventEntity(userId, eventId))
        }
    }

    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(userId)
    suspend fun markAllNotificationsAsRead(userId: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(userId)
    }

    suspend fun verifyAttendance(codeOrRsvpId: String): AttendanceScanResult = withContext(Dispatchers.IO) {
        val trimmed = codeOrRsvpId.trim()
        val rsvp = rsvpDao.getRsvpByCode(trimmed)
            ?: return@withContext AttendanceScanResult.NotFound("No registration ticket found for code '$trimmed'")

        if (rsvp.attendanceStatus == "VERIFIED") {
            return@withContext AttendanceScanResult.AlreadyVerified(
                rsvp = rsvp,
                message = "Already checked in at ${formatTime(rsvp.verifiedAt ?: rsvp.registeredAt)}"
            )
        }

        val updated = rsvp.copy(
            attendanceStatus = "VERIFIED",
            verifiedAt = System.currentTimeMillis()
        )
        rsvpDao.updateRsvp(updated)

        val event = eventDao.getEventByIdSync(rsvp.eventId)
        AttendanceScanResult.Success(
            rsvp = updated,
            eventTitle = event?.title ?: "Campus Event"
        )
    }

    private fun formatTime(timeMillis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timeMillis))
    }
}

sealed class AttendanceScanResult {
    data class Success(val rsvp: RsvpEntity, val eventTitle: String) : AttendanceScanResult()
    data class AlreadyVerified(val rsvp: RsvpEntity, val message: String) : AttendanceScanResult()
    data class NotFound(val error: String) : AttendanceScanResult()
}
