package it.polito.mad.g17_lab3.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.DocumentReference
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.CourtSlotInfo
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.enums.Choice
import it.polito.mad.g17_lab3.enums.Sport
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import org.w3c.dom.Document
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditSlotComponent(
    viewModel: ViewSport,
    viewCourt:ViewModelCourts,
    reservation: Reservation,
    showEditSlotReservation: MutableState<Reservation>,setRefresh:(Boolean)->Unit,
    showLoader: MutableState<Boolean>,
    onBack: () -> Unit = {}
) {






    Column(modifier = Modifier.fillMaxSize()) {
        var slots = remember { mutableStateListOf<CourtSlot>() }
        viewModel.getListaComponenti {
            slots.clear()
            slots.addAll(it)
        }
        var courts = remember { mutableStateListOf<Courts>() }
        viewCourt.getListaComponenti(showLoader) {
            courts.clear()
            courts.addAll(it)
        }


        var selectedDate by remember {
            mutableStateOf(
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date()).toString()
            )
        }
        var courtSlotInfo by remember { mutableStateOf(CourtSlotInfo()) }
        var court_id :DocumentReference? by remember { mutableStateOf(null) }
        val showDialog = remember { mutableStateOf(false) }
        val selectedSport = remember { mutableStateOf(Sport.ALL) }

        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.ArrowBack, contentDescription="")
                    }
                }


                // Titolo
                Text(
                    text = stringResource(R.string.edit_reservation_slot),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            SportSelect(selectedSport, setSelectedSport = { selectedSport.value = it })
            // Calendario
            CalendarView(
                date = selectedDate,
                activeDays = slots.filter {
                    compareDataWithToday(it.date,it.time_finish)>=0 && it.sport == when (selectedSport.value) {
                        Sport.BASKETBALL -> "Basket"
                        Sport.FOOTBALL -> "Football"
                        Sport.TENNIS -> "Tennis"
                        else -> it.sport
                    }
                }.map { it.date }.distinct(),
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            // Lista di slot
            CourtSlotList(
                courtSlots = slots.filter {
                    compareDataWithToday(it.date,it.time_finish)>=0 && it.date == selectedDate && it.sport == when (selectedSport.value) {
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
                        Name = courts.filter { it.getId()==court_id!! }.first().getName(),
                        selected = false,
                        reservation = null
                    )
                    courtSlotInfo.setDate(courtSlot.date)
                    showDialog.value = true
                },
                viewModelCourts = viewCourt,
                showLoader = showLoader
            )
        }

        if (showDialog.value) {
            reservation.time =
                courtSlotInfo.getCourtOpening() + "-" + courtSlotInfo.getCourtClosing()
            reservation.court_name = courtSlotInfo.getCourtName()
            reservation.court_id = courtSlotInfo.getId()
            reservation.date = courtSlotInfo.getDate()
            reservation.sport = courtSlotInfo.getCourtSport()
            reservation.author_id = courtSlotInfo.getAuthorId()
            CustomDialog(
                vm = viewModel,
                choice = Choice.EDIT,
                courtSlotInfo = courtSlotInfo,
                reservation = reservation,
                setShowDialog = {
                    showDialog.value = it
                },
                setEditSlot = {
                    showEditSlotReservation.value = reservation
                },
                setRefreshReservation = setRefresh,

                setShowAnimation = {
                    showEditSlotReservation.value = Reservation()
                    selectedDate =
                        SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date()).toString()
                }
            )
        }

    }


}