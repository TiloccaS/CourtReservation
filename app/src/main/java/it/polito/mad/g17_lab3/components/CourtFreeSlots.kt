package it.polito.mad.g17_lab3

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.DocumentReference
import it.polito.mad.g17_lab3.components.CalendarView
import it.polito.mad.g17_lab3.components.CourtSlotList
import it.polito.mad.g17_lab3.components.compareDataWithToday
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourtFreeSlot
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CourtFreeSlotsComponent(viewModel: ViewModelCourtFreeSlot, courtId: DocumentReference?,viewModelCourts:ViewModelCourts, showLoader: MutableState<Boolean>) {

    var showText=remember {
        mutableStateOf(false)
    }
     val court = remember {
         mutableStateOf<Courts?>(null)
     }
    Column(modifier = Modifier.fillMaxSize()) {
    val courts by viewModelCourts.courts.observeAsState(initial = emptyList())
        var selectedDate by remember { //hook in react
            mutableStateOf(
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
            )
        }

        var slot = remember { mutableStateListOf<CourtSlot>() }
        viewModel.getListaComponenti {
            slot.clear()
            slot.addAll(it)
        }

        if (courtId != null) {
            viewModelCourts.db.document(courtId.path).get().addOnSuccessListener {
                court.value=viewModelCourts.translateDocumentToCourts(it)
                showText.value=true
            }
        }
        if (showText.value && court.value!=null) {
            Text(
                text = stringResource(R.string.available_slots_for) +" "+ (court.value!!.getName()),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        if( court.value!=null){

            if (courtId != null) {
                CalendarView(
                    activeDays = slot.filter { compareDataWithToday(it.date,it.time_start)>=0 && it.court_id.path == courtId.path && it.reservation_id==null}.map { it.date }.distinct(),
                    onDateSelected = { date ->
                        selectedDate = date
                    }
                )
            }

            // Lista di slot

            if (courtId != null) {
                CourtSlotList(
                    courtSlots = slot.filter { compareDataWithToday(it.date,it.time_start)>=0 &&  (it.court_id.path == courtId.path && it.date == selectedDate) && it.reservation_id==null },
                    selectedDate = selectedDate,
                    showButton = false,
                    courts = listOf(court.value!!),
                    onButtonClicked = {},
                    viewModelCourts = viewModelCourts,
                    showLoader = showLoader
                )
            }
        }



        /*todo decommentare tutto e collegarlo a firebase

            val courts by viewModel.courts.observeAsState(initial = emptyList())


            var selectedDate by remember { //hook in react
                mutableStateOf(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()).toString()
                )
            }
            val slot by viewModel.slots.observeAsState(initial = emptyList())

            // Titolo
            if (courts.filter { it.id == courtId }.size > 0) {
                Text(
                    text = "Available slots for " + (courts.filter { it.id == courtId }.first().name),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }


            // Calendario
            CalendarView(
                activeDays = slot.filter { it.court_id == courtId }.map { it.date }.distinct(),
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            // Lista di slot
            CourtSlotList(
                courtSlots = slot.filter { (it.court_id == courtId && it.date == selectedDate) },
                selectedDate = selectedDate,
                showButton = false,
                courts = courts.filter { it.id == courtId },
                onButtonClicked = {}
            )

             */

    }
}