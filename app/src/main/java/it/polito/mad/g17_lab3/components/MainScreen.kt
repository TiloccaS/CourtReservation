package it.polito.mad.g17_lab3

import Notification
import PreviewNotificationList
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.navigation.animation.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.components.AddReservationComponent
import it.polito.mad.g17_lab3.components.EditProfile
import it.polito.mad.g17_lab3.components.JoinReservationsComponent
import it.polito.mad.g17_lab3.components.ImageProfiles
import it.polito.mad.g17_lab3.components.ReservationsComponent
import it.polito.mad.g17_lab3.viewmodels.*
import translateDocumentToNotification


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    showvm: ViewModelShowProfile,
    viewModelSport: ViewSport,
    viewModelReservation: ViewModelReservation,
    viewModelCourtFreeSlot: ViewModelCourtFreeSlot,
    viewModelCourts: ViewModelCourts,
    viewModelUsers: ViewModelUsers,
    onLogout: () -> Unit,
    user: FirebaseUser?,
    startTutorial: () -> Unit
) {
    val navController = rememberAnimatedNavController()
    val currentRoute  = remember {
        mutableStateOf("Reservation")
    }
    if (user != null) {

        Log.d("UID", user!!.uid)
    }
    val notificationsToRead = remember { mutableStateOf(0) }
    val notifications = remember { mutableStateListOf<Notification>() }

    fun getListaComponenti(callback: (List<Notification>) -> Unit) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userLogged = FirebaseAuth.getInstance().currentUser
        val collectionRef = db.collection("notifications")


        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti = mutableListOf<Notification>()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToNotification(document)
                componenti.add(componente)
            }

            callback(componenti)
        }
    }

    getListaComponenti { it ->
        notifications.clear()
        notifications.addAll(it.filter {
            it.recipient == (FirebaseAuth.getInstance().currentUser?.uid ?: "")
        }.sortedByDescending { it.timestamp })

        notificationsToRead.value = it.filter { it.recipient == (FirebaseAuth.getInstance().currentUser?.uid ?: "") && !it.is_read }.size
    }

    Scaffold(bottomBar = {
        BottomAppBar(containerColor = MaterialTheme.colorScheme.primary) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = { navController.navigate("Reservations") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    colors = if(currentRoute.value=="Reservations"){
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer )} else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_calendar_month_24),
                            contentDescription = "Reservations"
                        )
                        Text(text = stringResource(id = R.string.reservations), fontSize = 13.sp)
                    }
                }
                Button(
                    onClick = { navController.navigate("Join") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    colors = if(currentRoute.value=="Join"){
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer )} else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_people_24),
                            contentDescription = "Join"
                        )
                        Text(text = stringResource(id = R.string.join), fontSize = 13.sp)
                    }
                }
                Button(
                    onClick = { navController.navigate("Courts") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    colors = if(currentRoute.value=="Courts"){
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer )} else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.football_field),
                            contentDescription = "Courts"
                        )
                        Text(text = stringResource(id = R.string.courts), fontSize = 13.sp)
                    }
                }
                Button(
                    onClick = { navController.navigate("Profile") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp),
                    colors = if(currentRoute.value=="Profile"){
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer )} else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_person_24),
                            contentDescription = "Profile"
                        )
                        Text(text =stringResource(id = R.string.profile), fontSize = 13.sp)
                    }
                }
            }
        }

    },
        topBar = {
            TopAppBar(
                title = { Text(text = "Team Finder", color = Color.White)  },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(
                        onClick = { startTutorial() }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.info),
                            contentDescription = "Tutorial",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate("Notifications") }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.bell),
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    if (notificationsToRead.value > 0) {
                        Badge(
                            content = { Text(text = notificationsToRead.value.toString(), color = Color.White) },
                            modifier = Modifier.align(Alignment.Top)
                        )
                    }
                }
            )
        }

    ) {
        Box(modifier = Modifier.padding(it)) {
            AnimatedNavHost(navController = navController, startDestination = "Reservations") {
                composable("Reservations",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "Reservations" -> slideInVertically(initialOffsetY = { 0 })

                            else -> slideInHorizontally(initialOffsetX = { -1000 })
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            
                            "Reservations" -> slideOutVertically(targetOffsetY = { 0 })

                            else -> slideOutHorizontally(targetOffsetX = { -1000 })
                        }
                    }) {
                    currentRoute.value="Reservations"
                    ReservationsComponent(
                        viewModelReservation,
                        viewModelSport,
                        navController = navController, viewCourt = viewModelCourts,
                        user = user, viewModelUsers = viewModelUsers
                    )
                }
                composable("Add",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "Reservations" -> slideInHorizontally(initialOffsetX = { 1000 })
                            else -> slideInHorizontally(initialOffsetX = { -1000 })
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "Reservations" -> slideOutHorizontally(targetOffsetX = { 1000 })
                            else -> slideOutHorizontally(targetOffsetX = { -1000 })
                        }
                    }) {
                    currentRoute.value="Add"
                    AddReservationComponent(
                        viewModelSport, viewModelCourts = viewModelCourts, user,
                        navController = navController, onBack = {navController.popBackStack()}
                    )
                }
                composable("EditProfile",
                    enterTransition = { slideInVertically(initialOffsetY = { 2000 }) },
                    exitTransition = { slideOutVertically(targetOffsetY = { 2000 }) })
                {
                    currentRoute.value="EditProfile"
                    EditProfile(navController, showvm) }
                composable("Profile",
                    enterTransition = {

                        when (initialState.destination.route) {
                            "EditProfile" -> slideInVertically(initialOffsetY = { -2000 })
                            "Profile"-> slideInVertically(initialOffsetY = { 0 })
                            else -> slideInHorizontally(initialOffsetX = { 1000 })
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "EditProfile" -> slideOutVertically(targetOffsetY = { -2000 })
                            "Profile" -> slideOutVertically(targetOffsetY = { 0 })

                            else -> slideOutHorizontally(targetOffsetX = { 1000 })
                        }
                    }) {
                    currentRoute.value="Profile"
                    ShowProfile(
                        navController = navController,
                        showvm = showvm,
                        onLogout = onLogout
                    )
                }
                composable("Notifications",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "Courts" -> slideInVertically(initialOffsetY = { -2000 })
                            "Notifications" -> slideInVertically(initialOffsetY = { 0 })

                            else -> slideInHorizontally(initialOffsetX = { 1000 })
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {
                            "Profile" -> slideOutVertically(targetOffsetY = { -2000 })
                            "Notifications" -> slideOutVertically(targetOffsetY = { 0 })

                            else -> slideOutHorizontally(targetOffsetX = { 1000 })
                        }
                    }) {
                    currentRoute.value="Notifications"
                    PreviewNotificationList(notifications = notifications, notificationsToRead = notificationsToRead)
                }
                composable("Courts",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "Courts" -> slideInHorizontally(initialOffsetX = { 0 })
                            "Profile" -> slideInHorizontally(initialOffsetX = { -1000 })
                            else -> slideInHorizontally(initialOffsetX = { 1000 })
                        }
                    },
                    exitTransition = {
                        when (initialState.destination.route) {
                            "Courts" -> slideOutHorizontally(targetOffsetX = { 0 })

                            else ->slideOutHorizontally(targetOffsetX = { 1000 })

                        }

                    }) {
                    currentRoute.value="Courts"
                    showList(
                        viewModelCourtFreeSlot = viewModelCourtFreeSlot,
                        viewModel = viewModelSport,
                        viewModelCourt = viewModelCourts,
                        user = user,
                        navController = navController
                    )

                }

                composable("ExternalProfile/{item}/{reservation}"){
                    var item = it.arguments!!.getString("item")
                    var reservation= it.arguments!!.getString("reservation")
                    reservation="reservations/"+reservation
                    print(item)
                    ShowProfile(navController = navController, showvm = showvm, authid = item, reservaionId = reservation, viewModelReservation = viewModelReservation) {

                    }

                }
                composable("Join",
                    enterTransition = {
                        when (initialState.destination.route) {
                            "Join" -> slideInVertically(initialOffsetY = { 0 })
                            "Courts" -> slideInHorizontally(initialOffsetX = { -1000 })
                            "Profile" -> slideInHorizontally(initialOffsetX = { -1000 })
                            else -> slideInHorizontally(initialOffsetX = { 1000 })
                        }
                    },
                    exitTransition = {
                        when (targetState.destination.route) {

                            "Join" -> slideOutVertically(targetOffsetY = { 0 })

                            else -> slideOutHorizontally(targetOffsetX = { 1000 })
                        }
                    }) {
                    currentRoute.value="Join"
                    JoinReservationsComponent(
                        viewModelReservation = viewModelReservation,
                        viewModelSport = viewModelSport,
                        navController = navController,
                        viewModelUsers = viewModelUsers,
                        viewCourt = viewModelCourts,
                        user=user

                    )
                }
            }
        }
    }
}
