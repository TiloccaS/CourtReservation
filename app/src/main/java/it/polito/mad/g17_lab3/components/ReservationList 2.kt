package it.polito.mad.g17_lab3.components

import CourtRating
import CustomDialogResevation
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.g17_lab3.*
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.*
import it.polito.mad.g17_lab3.enums.Choice
import it.polito.mad.g17_lab3.viewmodels.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


fun translateDocumentToReservation(document: DocumentSnapshot): Reservation {
    val res = Reservation(
        time = document.getString("time") ?: "",
        date = document.getString("date") ?: "",
        author_id = (document.getString("author_id") ?: 0).toString(),
        court_id = (document.getDocumentReference("court_id")?:null),
        court_name = document.getString("court_name") ?: "",
        player_number = (document.getLong("player_number") ?: 0).toInt(),
        rented_equipment = (document.getLong("rented_equipment") ?: 0).toInt(),
        sport = document.getString("sport") ?: "",
        id = (document.getDocumentReference("id") ?: null),
        current_number = (document.getLong("current_number") ?: 0).toInt(),
        participants = (document.get("participants")?:arrayListOf<String>()) as ArrayList<String>,
        confirmed_participants = (document.get("confirmed_participants")?: arrayListOf<String>()) as ArrayList<String>
    )
    return res
}

fun translateDocumentToRatingReservation(document: DocumentSnapshot): CourtRating {
    val res = CourtRating()
    res.reservation_id = document.getDocumentReference("reservation_id")!!
    res.rating = document.getLong("rating")!!
    res.review = document.getString("reviews") ?: ""
    res.user_id = document.getDocumentReference("user_id")!!
    res.court_id = document.getDocumentReference("court_id")!!
    return res
}

@RequiresApi(Build.VERSION_CODES.O)
fun compareDataWithToday(stringaData: String, stringaOra: String): Int {
    val formatoDataOra = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val dataOraDaConfrontare = LocalDateTime.parse("$stringaData $stringaOra", formatoDataOra)
    val dataOraOggi = LocalDateTime.now()

    // Utilizza il metodo compareTo() per confrontare le date e le ore
    return dataOraDaConfrontare.compareTo(dataOraOggi)
}

fun getDocumentQuerySnapshot(collectionName: String): Task<QuerySnapshot> {
    val db = FirebaseFirestore.getInstance()

    val collectionRef = db.collection(collectionName)
    return collectionRef.get()
}


fun checkRated(
    reservation: Reservation,
    user_id: DocumentReference,
    setShowStar: (Boolean) -> Unit,
    setCheckedRate: (Boolean) -> Unit,
    showLoaderInner: MutableState<Boolean>,
    hasLoaded: MutableState<Boolean>
): Boolean {
    val querySnapshotTask = getDocumentQuerySnapshot("court_ratings")
    //showLoaderInner.value = true
    hasLoaded.value = true
    var ret = false
    querySnapshotTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val querySnapshot = task.result
            // Esempio di iterazione sui documenti nel QuerySnapshot

            for (document in querySnapshot) {
                var id_res = document.getDocumentReference("reservation_id")!!.path
                var utente = document.getDocumentReference("user_id")!!.path
                if (utente == user_id.path && id_res == reservation.id!!.path) {
                    setCheckedRate(true)
                }


            }
        }
        //showLoaderInner.value = false
    }
        .addOnFailureListener {
            //showLoaderInner.value = false
        }


    return ret
}

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationList(
    reservations: List<Reservation>,
    courts: List<Courts>,
    selectedDate: String,
    viewModelSport: ViewSport,
    viewModelCourts: ViewModelCourts,
    navController: NavController,
    viewModelUsers:ViewModelUsers,
    viewModelReservation: ViewModelReservation,
    showEditSlotReservation: MutableState<Reservation>,
    setShowLoader: (Boolean) -> Unit,
    refreshReservation: (Boolean) -> Unit,
    showLoader: MutableState<Boolean>

) {

    val showLoaderInner = remember { mutableStateOf(false) }
    val hasLoaded = remember { mutableStateOf(false) }

    val showDialogDelete = remember { mutableStateOf(false) }
    val showDialogEdit = remember { mutableStateOf(false) }
    val showDialogReview = remember { mutableStateOf(false) }

    val currentReservation = remember { mutableStateOf(Reservation()) }
    val pastReservation = remember {
        mutableStateOf(false)
    }
    lateinit var reservationtmp: Reservation

    val (showAnimationChecked, setshowAnimationChecked) = remember {
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
            text = if (reservations.isEmpty()) stringResource(R.string.no_reservations_for)+ " "+selectedDate else stringResource(R.string.reservations_for)+" "+selectedDate,
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        if (showLoaderInner.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(reservations) { reservation ->
                    pastReservation.value =
                        compareDataWithToday(reservation.date, reservation.time.split("-")[1]) < 0


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                            val (checkedRate, setCheckedRate) = remember {
                                mutableStateOf(false)
                            }
                            val (showStar, setShowStar) = remember {
                                mutableStateOf(false)
                            }
                            val db = FirebaseFirestore.getInstance()
                            val collectionRef = db.collection("users")
                            //showLoader.value = true
                            var userid = remember {
                                mutableStateOf<DocumentReference?>(null)
                            }
                            collectionRef.addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    // Gestisci eventuali errori
                                    return@addSnapshotListener
                                }

                                val componenti = mutableListOf<CourtRating>()

                                for (document in snapshot?.documents.orEmpty()) {
                                    val auth_id = document.getString("auth_id")
                                    if (auth_id == FirebaseAuth.getInstance().currentUser!!.uid) {
                                        userid.value = document.reference

                                    }

                                    //courts.value!!.add(componente)
                                }

                                //showLoader.value = false
                            }
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
                                        .getAddress() else "",//if (courts.any { it.id == reservation.courtId }) courts.filter { it.id == reservation.courtId }[0].address else "",
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
                                if(reservation.confirmed_participants.size>0 && reservation.player_number>0){
                                    Image(
                                        painterResource(R.drawable.players2),
                                        contentDescription = "players icon"
                                    )
                                    Text(
                                        textAlign = TextAlign.Center,
                                        text = "${reservation.confirmed_participants.size.toString()}/${reservation.player_number.toString()}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,

                                        )
                                }


                            }
                            Column() {
                                Row() {

                                    if (!pastReservation.value) {
                                        if(reservation.author_id==FirebaseAuth.getInstance().currentUser?.uid){
                                            IconButton(onClick = {
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .weight(1F)
                                                showDialogEdit.value = true
                                                currentReservation.value = reservation

                                            }) {
                                                Icon(
                                                    Icons.Outlined.Edit,
                                                    contentDescription = "edit Reservation"
                                                )
                                            }
                                        }

                                        IconButton(onClick = {
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .weight(1F)

                                            showDialogDelete.value = true
                                            currentReservation.value = reservation
                                            currentReservation.value.id = reservation.id
                                            reservationtmp = reservation
                                        }) {
                                            Icon(
                                                Icons.Outlined.Delete,
                                                contentDescription = "delete Reservation"
                                            )
                                        }
                                    } else {
                                        if (userid.value != null && currentReservation.value.id != null) {
                                            val doc = userid.value
                                            //if(!hasLoaded.value){
                                                checkRated(
                                                    currentReservation.value,
                                                    user_id = doc!!,
                                                    setShowStar,
                                                    setCheckedRate,
                                                    showLoaderInner,
                                                    hasLoaded
                                                )
                                            //}

                                        }

                                        if ((!checkedRate) && reservation.confirmed_participants.contains(FirebaseAuth.getInstance().currentUser!!.uid)) {
                                            IconButton(onClick = {
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp)
                                                    .weight(1F)

                                                showDialogReview.value = true
                                                currentReservation.value = reservation

                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.outline_star_half_24),
                                                    contentDescription = "rating now"
                                                )
                                            }
                                        }
                                    }
                                    if(currentReservation.value.id != null){
                                        if (showDialogReview.value) {
                                            CustomDialogResevation(
                                                value = "prova",
                                                reservation = currentReservation.value,
                                                viewModel = viewModelSport,
                                                setShowDialog = {
                                                    showDialogReview.value = it
                                                }
                                            )
                                        }
                                        if (showDialogDelete.value) {
                                            if(reservation.author_id!=FirebaseAuth.getInstance().currentUser?.uid){
                                                CustomDialog(
                                                    vm = viewModelSport,
                                                    choice = Choice.DELETE,
                                                    courtSlotInfo = CourtSlotInfo(),
                                                    reservation = currentReservation.value,
                                                    setShowDialog = {
                                                        showDialogDelete.value = it
                                                    },
                                                    viewModelReservation =viewModelReservation ,
                                                    setShowLoader = setShowLoader,
                                                    setShowAnimation = setshowAnimationChecked,
                                                    setRefreshReservation = refreshReservation
                                                )
                                            }
                                            else{

                                                CustomDialog(
                                                    vm = viewModelSport,
                                                    choice = Choice.DELETE,
                                                    courtSlotInfo = CourtSlotInfo(),
                                                    reservation = currentReservation.value,
                                                    setShowDialog = {
                                                        showDialogDelete.value = it
                                                    },
                                                    setShowLoader = setShowLoader,
                                                    setShowAnimation = setshowAnimationChecked,
                                                    setRefreshReservation = refreshReservation
                                                )
                                            }

                                        }
                                        if (showDialogEdit.value) {
                                            CustomDialog(
                                                vm = viewModelSport,
                                                choice = Choice.EDIT,
                                                courtSlotInfo = CourtSlotInfo(),
                                                reservation = currentReservation.value,
                                                setShowDialog = {
                                                    showDialogEdit.value = it
                                                },
                                                setEditSlot = {
                                                    showEditSlotReservation.value =
                                                        currentReservation.value
                                                },
                                                handleConfirm = {
                                                    currentReservation.value = Reservation()
                                                },
                                                setShowLoader = setShowLoader,
                                                setShowAnimation = setshowAnimationChecked,
                                                setRefreshReservation = refreshReservation

                                            )
                                        }
                                    }
                                }
                            }


                        }
                        if(reservation.player_number>0){
                            Column(){
                                if(reservation.participants.size>0){
                                    reservation.id?.let {
                                        Text(
                                            textAlign = TextAlign.Center,
                                            text = stringResource(id = R.string.partecipants_waiting),
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        ImageProfiles(
                                            navController = navController,
                                            users = reservation.participants,
                                            viewModelUsers = viewModelUsers,
                                            reservation_id = it.path,
                                        )

                                    }
                                }
                                if(reservation.confirmed_participants.size>0){
                                    reservation.id?.let {
                                        Text(
                                            textAlign = TextAlign.Center,
                                            text = stringResource(id = R.string.partecipants_confirmed),
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
    }
}

