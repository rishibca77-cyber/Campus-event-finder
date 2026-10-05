package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Auth : Screen("auth")
    object StudentHome : Screen("student_home")
    object EventFinder : Screen("event_finder")
    object EventDetails : Screen("event_details")
    object DigitalTicket : Screen("digital_ticket")
    object MyEvents : Screen("my_events")
    object Notifications : Screen("notifications")
    object StudentProfile : Screen("student_profile")
    object AdminDashboard : Screen("admin_dashboard")
    object CreateEditEvent : Screen("create_edit_event")
    object ManageEvents : Screen("manage_events")
    object EventRsvps : Screen("event_rsvps")
    object AttendanceScanner : Screen("attendance_scanner")
}
