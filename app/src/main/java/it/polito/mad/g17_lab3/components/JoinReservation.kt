package it.polito.mad.g17_lab3.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.enums.Sport
import it.polito.mad.g17_lab3.viewmodels.*
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JoinReservationsComponent(
    viewModelReservation: ViewModelReservation,
    viewModelSport: ViewSport,
    navController: NavHostController,
    viewModelUsers:ViewModelUsers,
    viewCourt: ViewModelCourts,
    user: FirebaseUser?
) {
    val showLoader = remember { mutableStateOf(false) }
    var courts = remember { mutableStateListOf<Courts>() }
    viewCourt.getListaComponenti(showLoader = showLoader) {
        courts.clear()
        courts.addAll(it)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        var selectedDate by remember { //hook in react
            mutableStateOf(
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
            )
        }
        val db = FirebaseFirestore.getInstance()

        //val reservations by viewModelReservation.value.observeAsState(initial = emptyList())
        //val courts by viewModelReservation.courts.observeAsState(initial = emptyList())
        val showEditSlotReservation = remember { mutableStateOf(Reservation()) }
        val selectedSport = remember { mutableStateOf(Sport.ALL) }
        fun getDocumentQuerySnapshot(collectionName: String): Task<QuerySnapshot> {
            val collectionRef = db.collection(collectionName)
            return collectionRef.get()
        }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }


// Esempio di utilizzo della funzione
        val collectionName = "reservations" // Sostituisci con il nome della tua collezione
        val querySnapshotTask = getDocumentQuerySnapshot(collectionName)
        var reservations2 = remember { mutableStateListOf<Reservation>() }
        var (getReservation, setRefreshReservation) = remember {
            mutableStateOf(true)
        }

        fun getListaComponenti(callback: (List<Reservation>) -> Unit) {
            val database: FirebaseFirestore = FirebaseFirestore.getInstance()
            val collectionRef = db.collection("reservations")

            val listener = collectionRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Gestisci eventuali errori
                    return@addSnapshotListener
                }

                val componenti = mutableListOf<Reservation>()

                for (document in snapshot?.documents.orEmpty()) {
                    val componente = translateDocumentToReservation(document)
                    componenti.add(componente)
                }

                callback(componenti)
            }
        }

        getListaComponenti {
            reservations2.clear()
            reservations2.addAll(it)

        }
        if (getReservation) {
            showLoader.value = true;
            querySnapshotTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    // Esempio di iterazione sui documenti nel QuerySnapshot
                    for (document in querySnapshot) {

                        reservations2.add(translateDocumentToReservation(document))
                    }
                } else {
                    val exception = task.exception
                    // Gestisci l'errore
                    println("Errore durante l'ottenimento del QuerySnapshot: $exception")
                }
                showLoader.value = false
            }
            setRefreshReservation(false)
        }

        if (showLoader.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {

                Box(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize()) {
                        // Titolo
                        Text(
                            text = stringResource(id = R.string.reservations_for) + " " + selectedDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        SportSelect(selectedSport, setSelectedSport = { selectedSport.value = it })

                        // Calendario
                        CalendarView(
                            date = selectedDate,
                            activeDays = reservations2.filter {
                                it.sport == when (selectedSport.value) {
                                    Sport.BASKETBALL -> stringResource(R.string.basket_label)
                                    Sport.FOOTBALL -> stringResource(R.string.football_label)
                                    Sport.TENNIS -> stringResource(R.string.tennis_label)
                                    else -> it.sport
                                }
                                        && compareDataWithToday(it.date, it.time.split("-")[1]) > 0 && it.confirmed_participants.size < it.player_number &&
                                     it.author_id != user?.uid && !it.confirmed_participants.contains(user?.uid) && !it.participants.contains(user?.uid)}.map { it.date }.distinct(),
                            onDateSelected = { date ->
                                selectedDate = date
                            }
                        )

                        // Lista di slot

                        // Esegui la query per ottenere la lista di documenti da una collezione

                        JoinReservationList(
                            reservations2.filter {
                                it.date == selectedDate && it.sport == when (selectedSport.value) {
                                    Sport.BASKETBALL -> stringResource(R.string.basket_label)
                                    Sport.FOOTBALL -> stringResource(R.string.football_label)
                                    Sport.TENNIS -> stringResource(R.string.tennis_label)
                                    else -> it.sport
                                }
                            },
                            courts, //todo mettere la lista dei court presa da firebase
                            selectedDate,
                            viewModelSport,
                            viewModelCourts = viewCourt,
                            viewModelUsers = viewModelUsers,
                            showEditSlotReservation,
                            setShowLoader = { showLoader.value = it },
                            setRefreshReservation,
                            showLoader=showLoader,
                            navController = navController,
                            user=user
                        )
                    }

                }

        }
    }
}