package it.polito.mad.g17_lab3.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.g17_lab3.*
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.CourtSlotInfo
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.enums.Choice
import it.polito.mad.g17_lab3.enums.Sport
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

fun translateDocumentToSlot(document: DocumentSnapshot): CourtSlot {
    val reservation_id=if(document.contains("reservation_id")){document.getDocumentReference("reservation_id")}else{null}
    return CourtSlot(
        court_id = (document.getDocumentReference("court_id")!!),
        date = document.getString("date") ?: "",
        time_start = document.getString("time_start") ?: "",
        time_finish = document.getString("time_finish") ?: "",
        sport = document.getString("sport") ?: "",
        id = (document.getDocumentReference("id") ?: null),
        reservation_id = reservation_id
    )
}
fun translateDocumentToCourt(document: DocumentSnapshot): Courts {  val court=Courts()
    court.setName(document.getString("name")!!)
    court.setAddress(document.getString("address")!!)
    court.setOpening(document.getString("opening")!!)
    court.setClosing(document.getString("closing")!!)
    return court
}

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)//todo aggiugere add reservation ai campi
@Composable
fun AddReservationComponent(viewModel: ViewSport, viewModelCourts: ViewModelCourts, user: FirebaseUser?,
                            navController: NavHostController, showAvailabilityCourt: DocumentReference? = null, onBack: () -> Unit = {}) {
    val db = FirebaseFirestore.getInstance()
    fun getDocumentQuerySnapshot(collectionName: String): Task<QuerySnapshot> {
        val collectionRef = db.collection(collectionName)
        return collectionRef.get()
    }
    val showLoader = remember { mutableStateOf(false) }
    val coll1 = db.collection("reservations")
    var slots = remember {
        mutableStateListOf<CourtSlot>()
    }
    var courts = remember {
        mutableStateListOf<Courts>()
    }
    val collectionName = "court_slots" // Sostituisci con il nome della tua collezione
    val querySnapshotTask =getDocumentQuerySnapshot(collectionName)
    var (getSlots,setRefreshSlots) = remember {
        mutableStateOf(true)
    }
    var (getCourts,setRefreshCourts) = remember {
        mutableStateOf(true)
    }
    var courts2 = remember { mutableStateListOf<Courts>() }
    viewModelCourts.getListaComponenti(showLoader = showLoader) {
        courts2.clear()
        courts2.addAll(it)
    }
    viewModel.getListaComponenti { it ->
        slots.clear()
        if(showAvailabilityCourt != null){
            slots.addAll(it.filter { it.court_id == showAvailabilityCourt })
        }else{
            slots.addAll(it)
        }
    }

    val courtSnapshotTask =getDocumentQuerySnapshot("courts")
    if(getCourts){
        courtSnapshotTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                // Esempio di iterazione sui documenti nel QuerySnapshot
                for (document in querySnapshot) {
                    courts.add(translateDocumentToCourt(document))
                }
            } else {
                val exception = task.exception
                // Gestisci l'errore
                println("Errore durante l'ottenimento del QuerySnapshot: $exception")
            }
        }

        setRefreshCourts(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
       // val slots by viewModel.freeCourtSlot.observeAsState(initial = emptyList())
        //val courts by viewModel.value.observeAsState(initial = emptyList())
        var selectedDate by remember {
            mutableStateOf(
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date()).toString()
            )
        }
        var courtSlotInfo by remember { mutableStateOf(CourtSlotInfo()) }
        var court_id:DocumentReference? by remember { mutableStateOf(null)}
        val showDialog = remember { mutableStateOf(false) }
        val selectedSport = remember { mutableStateOf(Sport.ALL) }
        val (showAnimationChecked,setshowAnimationChecked) = remember {
            mutableStateOf(0)
        }
        if (showLoader.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            if(showAnimationChecked!=0) {
                if(showAnimationChecked==1){
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedCheckmark(setshowAnimationChecked)

                    }
                }
                else{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedError(setshowAnimationChecked)

                    }
                }



            }
            else{
                Column {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(end = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.ArrowBack, contentDescription="")
                            }
                        }

                        // Titolo
                        Text(
                            text = stringResource(R.string.create_reservation),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    SportSelect(selectedSport, setSelectedSport = { selectedSport.value = it })
                    // Calendario


                    CalendarView(
                        date = selectedDate,
                        activeDays = slots.toList().filter {
                           compareDataWithToday(it.date,it.time_finish)>=0 && it.reservation_id==null && it.sport == when (selectedSport.value) {
                                Sport.BASKETBALL -> "Basket"
                                Sport.FOOTBALL -> "Football"
                                Sport.TENNIS -> "Tennis"
                                else -> it.sport
                            }
                        }.map { it.date }.distinct(),
                        onDateSelected = { date ->
                            selectedDate = date
                        },
                        selectedSport = selectedSport.value
                    )

                    // Lista di slot
                    CourtSlotList(
                        courtSlots = slots.toList().filter {
                           compareDataWithToday(it.date,it.time_finish)>=0 && it.reservation_id==null && it.date == selectedDate && it.sport == when (selectedSport.value) {
                               Sport.BASKETBALL -> "Basket"
                               Sport.FOOTBALL -> "Football"
                               Sport.TENNIS -> "Tennis"
                                else -> it.sport
                            }
                        },
                        selectedDate = selectedDate,
                        showButton = true,
                        courts = courts,
                        onButtonClicked = {
                            val courtSlot = it
                            showDialog.value = true
                            court_id = courtSlot.court_id
                            courtSlotInfo = fromCourtSlotToCourtSlotInfo(
                                courtSlot = courtSlot,
                                Name =courts2.filter { it.getId()==court_id!! }.first().getName(),
                                selected = false,
                                reservation = null
                            )
                            courtSlotInfo.setDate(courtSlot.date)
                            //courtSlotInfo.setSlotId(courtSlot.id!!)
                            courtSlotInfo.setByCourtSlot(courtSlot)
                            showDialog.value = true
                        },
                        showArrow = false,
                        selectedSport = selectedSport.value,
                        viewModelCourts =viewModelCourts,
                        showLoader = showLoader
                    )
                }
            }


            if (showDialog.value) {
                CustomDialog(
                    vm = viewModel,
                    choice = Choice.ADD,
                    courtSlotInfo = courtSlotInfo,
                    reservation = Reservation(
                        author_id = user!!.uid,
                        sport = courtSlotInfo.getCourtSport(),
                        rented_equipment = 0,

                        court_name = courtSlotInfo.getCourtName(),
                        player_number = 0,
                        date = courtSlotInfo.getDate(),
                        court_id = court_id,
                        time = courtSlotInfo.getCourtOpening() + "-" + courtSlotInfo.getCourtClosing()
                    ),
                    setShowDialog = {
                        showDialog.value = it
                    },
                    setShowLoader = { showLoader.value = it },
                    setShowAnimation = setshowAnimationChecked,
                    setRefreshReservation = setRefreshSlots,
                    navController = navController

                )
            }
        }

    }
}


fun fromCourtSlotToCourtSlotInfo(
    courtSlot: CourtSlot,
    Name: String,
    selected: Boolean,
    reservation: Reservation?
): CourtSlotInfo {
    val csi: CourtSlotInfo = CourtSlotInfo()
    csi.setCourtName(Name)
    csi.setByCourtSlot(courtSlot)
    csi.setSelected(selected)
    if (reservation != null) {
        csi.setPeople(reservation.player_number)
    }
    if (reservation != null) {
        csi.setEquipment(reservation.rented_equipment)
    }
    if (reservation != null) {
        csi.setReservationId(reservation.id!!)
    }
    return csi
}
@Composable
fun AnimatedCheckmark(setShowAnimation:(Int)->Unit) {
    val transition = rememberInfiniteTransition()

    val checkmarkSize by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .size(checkmarkSize.dp)
            ) {
                val checkmarkPath = Path().apply {
                    moveTo(size.width * 0.25f, size.height * 0.5f)
                    lineTo(size.width * 0.4f, size.height * 0.65f)
                    lineTo(size.width * 0.75f, size.height * 0.35f)
                }

                drawPath(
                    path = checkmarkPath,
                    color = Color.Green,
                    alpha = 0.8f,
                    style = Stroke(width = 8.dp.toPx())
                )
            }

            Text(
                text = stringResource(R.string.success),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .border(width = 2.dp, color = Color.Green, shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
    LaunchedEffect(Unit){
        delay(500)
        setShowAnimation(0)
    }
    //
}

@Composable
fun AnimatedError(setShowAnimation: (Int) -> Unit) {
    val transition = rememberInfiniteTransition()
    val errorSize by transition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .size(errorSize.dp)
                    .padding(8.dp)
            ) {
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 8.dp.toPx()
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 8.dp.toPx()
                )
            }

            Text(
                text = stringResource(R.string.unexpected_error),
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .border(width = 2.dp, color = Color.Red, shape = MaterialTheme.shapes.small)
                    .padding(8.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
    LaunchedEffect(Unit){
        delay(1000)
        setShowAnimation(0)
    }
}

