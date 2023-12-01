package it.polito.mad.g17_lab3.components


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.mad.g17_lab3.*
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.*
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import CourtRating
import androidx.compose.ui.res.stringResource
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import it.polito.mad.g17_lab3.viewmodels.ViewModelUsers


@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JoinReservationList(
    reservations: List<Reservation>,
    courts: List<Courts>,
    selectedDate: String,
    viewModelSport: ViewSport,
    viewModelCourts: ViewModelCourts,
    viewModelUsers: ViewModelUsers,
    showEditSlotReservation: MutableState<Reservation>,
    setShowLoader: (Boolean) -> Unit,
    refreshReservation: (Boolean) -> Unit,
    showLoader: MutableState<Boolean>,
    navController: NavHostController,
    user: FirebaseUser?
) {

    val showDialogJoin = remember { mutableStateOf(false) }
    val showLoaderInner = remember { mutableStateOf(false) }
    val hasLoaded = remember { mutableStateOf(false) }

    val currentReservation = remember { mutableStateOf(Reservation()) }
    val pastReservation= remember {
        mutableStateOf(false)
    }
    lateinit var reservationtmp:Reservation

    val (showAnimationChecked,setshowAnimationChecked) = remember {
        mutableStateOf(0)
    }
    val loadSlot = remember {
        mutableStateOf(false)
    }
    var courts = remember { mutableStateListOf<Courts>() }
    viewModelCourts.getListaComponenti(showLoader = showLoader) {
        courts.clear()
        courts.addAll(it)
        loadSlot.value = true
    }

    Column(

        modifier = Modifier
            .padding(16.dp)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .heightIn(max = 500.dp)
    ) {
        Text(
            text = if (reservations.isEmpty()) stringResource(R.string.no_reservations_for)+" "+selectedDate else stringResource(R.string.reservations_for)+" "+selectedDate,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(reservations) { reservation ->
                currentReservation.value = reservation
                pastReservation.value =
                    compareDataWithToday(reservation.date, reservation.time.split("-")[1]) < 0
                if (!pastReservation.value && currentReservation.value.confirmed_participants.size < currentReservation.value.player_number
                    && currentReservation.value.author_id != user?.uid && !currentReservation.value.participants.contains(user?.uid) && !currentReservation.value.confirmed_participants.contains(user?.uid)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp).wrapContentHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    )
                    {
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {


                            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                                Row {
                                    when (reservation.sport.uppercase(Locale.getDefault())) {
                                        "FOOTBALL" -> {
                                            Image(
                                                painterResource(R.drawable.football2),
                                                contentDescription = "football icon"
                                            )
                                        }

                                        "BASKET" -> Image(
                                            painterResource(R.drawable.basket2),
                                            contentDescription = "basket icon"
                                        )

                                        "TENNIS" -> Image(
                                            painterResource(R.drawable.tennis2),
                                            contentDescription = "tennis icon"
                                        )
                                    }
                                    Text(
                                        text = reservation.court_name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(horizontal = 5.dp)

                                    )
                                }
                                Text(
                                    text = if (courts.size > 0) courts.first { it.getId() == reservation.court_id!! }
                                        .getAddress() else "",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = reservation.time,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                            }
                                Column(
                                    modifier = Modifier
                                        .padding(20.dp)
                                ) {
                                    Image(
                                        painterResource(R.drawable.players2),
                                        contentDescription = "players icon"
                                    )
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = reservation.confirmed_participants.size.toString() + "/" + reservation.player_number.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,

                                        )
                                }
                                Column() {

                                    Button(
                                        onClick = {
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .weight(1F)

                                            showDialogJoin.value = true
                                            currentReservation.value = reservation
                                        },

                                        ) {
                                        Column() {
                                            Icon(Icons.Default.Add, contentDescription = "Join")
                                            Text(
                                                text = "Join",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        if (showDialogJoin.value) {
                                            CustomDialogJoin(
                                                reservation = currentReservation.value,
                                                courtSlotInfo = CourtSlotInfo(),
                                                setShowDialog = {
                                                    showDialogJoin.value = it
                                                },
                                                setShowLoader = setShowLoader,
                                                setShowAnimation = setshowAnimationChecked,
                                                setRefreshReservation = refreshReservation,
                                                navController = navController,
                                                user = user
                                            )
                                        }

                                    }

                                }
                        }
                        if(reservation.confirmed_participants.size>0){
                            reservation.id?.let {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "Participants confirmed",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                ImageProfiles(
                                    navController = navController,
                                    users = reservation.confirmed_participants,
                                    viewModelUsers = viewModelUsers,
                                    reservation_id = it.path,
                                )

                            }
                        }

                    }
                }
            }
        }
    }
    }



