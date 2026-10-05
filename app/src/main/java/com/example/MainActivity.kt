package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.EventEntity
import com.example.data.model.RsvpEntity
import com.example.ui.screens.*
import com.example.ui.theme.CampusPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CampusViewModel

sealed class AppScreen {
    object Landing : AppScreen()
    object Auth : AppScreen()
    object StudentHome : AppScreen()
    object EventFinder : AppScreen()
    object MyEvents : AppScreen()
    object Notifications : AppScreen()
    object Profile : AppScreen()
    data class EventDetail(val event: EventEntity) : AppScreen()
    data class DigitalTicket(val rsvp: RsvpEntity, val event: EventEntity) : AppScreen()
    object AdminDashboard : AppScreen()
    data class CreateEditEvent(val event: EventEntity? = null) : AppScreen()
    object ManageEvents : AppScreen()
    data class EventRsvps(val event: EventEntity) : AppScreen()
    object AttendanceScanner : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CampusConnectApp()
            }
        }
    }
}

@Composable
fun CampusConnectApp(viewModel: CampusViewModel = viewModel()) {
    var screenStack by remember { mutableStateOf(listOf<AppScreen>(AppScreen.Landing)) }
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.Landing

    fun navigateTo(screen: AppScreen) {
        screenStack = screenStack + screen
    }

    fun popBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun replaceWith(screen: AppScreen) {
        screenStack = listOf(screen)
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // RSVP Dialog State
    var eventForRsvp by remember { mutableStateOf<EventEntity?>(null) }
    var confirmedRsvpAndEvent by remember { mutableStateOf<Pair<RsvpEntity, EventEntity>?>(null) }

    // Intercept Back button
    BackHandler(enabled = screenStack.size > 1) {
        popBack()
    }

    val isStudentBottomNavVisible = currentScreen in listOf(
        AppScreen.StudentHome,
        AppScreen.EventFinder,
        AppScreen.MyEvents,
        AppScreen.Profile
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isStudentBottomNavVisible) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.StudentHome,
                        onClick = { replaceWith(AppScreen.StudentHome) },
                        icon = {
                            Icon(
                                if (currentScreen is AppScreen.StudentHome) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_home_btn")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.EventFinder,
                        onClick = { replaceWith(AppScreen.EventFinder) },
                        icon = {
                            Icon(
                                if (currentScreen is AppScreen.EventFinder) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = "Discover"
                            )
                        },
                        label = { Text("Discover", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_discover_btn")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.MyEvents,
                        onClick = { replaceWith(AppScreen.MyEvents) },
                        icon = {
                            Icon(
                                if (currentScreen is AppScreen.MyEvents) Icons.Filled.ConfirmationNumber else Icons.Outlined.ConfirmationNumber,
                                contentDescription = "My Events"
                            )
                        },
                        label = { Text("My Events", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_my_events_btn")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Profile,
                        onClick = { replaceWith(AppScreen.Profile) },
                        icon = {
                            Icon(
                                if (currentScreen is AppScreen.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_profile_btn")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Landing -> {
                    LandingScreen(
                        viewModel = viewModel,
                        onExploreEvents = { replaceWith(AppScreen.EventFinder) },
                        onGetStarted = { navigateTo(AppScreen.Auth) },
                        onEventClick = { navigateTo(AppScreen.EventDetail(it)) },
                        onAdminDashboard = { replaceWith(AppScreen.AdminDashboard) }
                    )
                }

                is AppScreen.Auth -> {
                    AuthScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { isStudent ->
                            if (isStudent) {
                                replaceWith(AppScreen.StudentHome)
                            } else {
                                replaceWith(AppScreen.AdminDashboard)
                            }
                        }
                    )
                }

                is AppScreen.StudentHome -> {
                    StudentHomeScreen(
                        viewModel = viewModel,
                        onNavigateToFinder = { category ->
                            category?.let { viewModel.onCategorySelect(it) }
                            replaceWith(AppScreen.EventFinder)
                        },
                        onEventClick = { navigateTo(AppScreen.EventDetail(it)) },
                        onNavigateToMyEvents = { replaceWith(AppScreen.MyEvents) },
                        onNavigateToNotifications = { navigateTo(AppScreen.Notifications) },
                        onNavigateToProfile = { replaceWith(AppScreen.Profile) }
                    )
                }

                is AppScreen.EventFinder -> {
                    EventFinderScreen(
                        viewModel = viewModel,
                        onEventClick = { navigateTo(AppScreen.EventDetail(it)) }
                    )
                }

                is AppScreen.MyEvents -> {
                    MyEventsScreen(
                        viewModel = viewModel,
                        onViewTicket = { rsvp, event ->
                            navigateTo(AppScreen.DigitalTicket(rsvp, event))
                        },
                        onEventClick = { navigateTo(AppScreen.EventDetail(it)) }
                    )
                }

                is AppScreen.Notifications -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { popBack() }
                    )
                }

                is AppScreen.Profile -> {
                    StudentProfileScreen(
                        viewModel = viewModel,
                        onSwitchToAdmin = { replaceWith(AppScreen.AdminDashboard) },
                        onLogout = { replaceWith(AppScreen.Auth) }
                    )
                }

                is AppScreen.EventDetail -> {
                    val allEvents by viewModel.allEvents.collectAsState()
                    val liveEvent = allEvents.find { it.id == screen.event.id } ?: screen.event
                    val userRsvps by viewModel.userRsvps.collectAsState()
                    val rsvp = userRsvps.find { it.eventId == liveEvent.id }

                    EventDetailScreen(
                        event = liveEvent,
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onOpenRsvpDialog = { eventForRsvp = liveEvent },
                        onViewTicket = {
                            rsvp?.let { navigateTo(AppScreen.DigitalTicket(it, liveEvent)) }
                        }
                    )
                }

                is AppScreen.DigitalTicket -> {
                    TicketScreen(
                        rsvp = screen.rsvp,
                        event = screen.event,
                        onBack = { popBack() }
                    )
                }

                is AppScreen.AdminDashboard -> {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onCreateEvent = { navigateTo(AppScreen.CreateEditEvent()) },
                        onManageEvents = { navigateTo(AppScreen.ManageEvents) },
                        onOpenScanner = { navigateTo(AppScreen.AttendanceScanner) },
                        onSwitchToStudent = { replaceWith(AppScreen.StudentHome) }
                    )
                }

                is AppScreen.CreateEditEvent -> {
                    CreateEditEventScreen(
                        viewModel = viewModel,
                        existingEvent = screen.event,
                        onBack = { popBack() },
                        onSaved = { popBack() }
                    )
                }

                is AppScreen.ManageEvents -> {
                    ManageEventsScreen(
                        viewModel = viewModel,
                        onBack = { popBack() },
                        onEditEvent = { navigateTo(AppScreen.CreateEditEvent(it)) },
                        onViewRsvps = { navigateTo(AppScreen.EventRsvps(it)) },
                        onCreateEvent = { navigateTo(AppScreen.CreateEditEvent()) }
                    )
                }

                is AppScreen.EventRsvps -> {
                    EventRsvpsScreen(
                        event = screen.event,
                        viewModel = viewModel,
                        onBack = { popBack() }
                    )
                }

                is AppScreen.AttendanceScanner -> {
                    AttendanceScannerScreen(
                        viewModel = viewModel,
                        onBack = { popBack() }
                    )
                }
            }

            // RSVP Registration Dialog
            if (eventForRsvp != null && currentUser != null) {
                RsvpRegistrationDialog(
                    event = eventForRsvp!!,
                    user = currentUser!!,
                    onDismiss = { eventForRsvp = null },
                    onSubmit = { phone, notes ->
                        val targetEvent = eventForRsvp!!
                        viewModel.submitRsvp(targetEvent, phone, notes) { rsvp ->
                            confirmedRsvpAndEvent = rsvp to targetEvent
                            eventForRsvp = null
                        }
                    }
                )
            }

            // RSVP Confirmed Dialog
            if (confirmedRsvpAndEvent != null) {
                val (rsvp, event) = confirmedRsvpAndEvent!!
                RsvpConfirmationDialog(
                    rsvp = rsvp,
                    event = event,
                    onViewTicket = {
                        confirmedRsvpAndEvent = null
                        navigateTo(AppScreen.DigitalTicket(rsvp, event))
                    },
                    onDismiss = {
                        confirmedRsvpAndEvent = null
                    }
                )
            }
        }
    }
}
