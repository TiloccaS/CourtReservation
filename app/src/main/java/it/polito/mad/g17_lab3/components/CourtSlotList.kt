package it.polito.mad.g17_lab3.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.enums.Sport
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import kotlinx.coroutines.tasks.await

@Composable
fun CourtSlotList(
    courtSlots: List<CourtSlot>,
    selectedDate: String,
    showButton: Boolean = true,
    courts: List<Courts>,
    onButtonClicked: (selectedSlot: CourtSlot) -> Unit,
    showArrow: Boolean = true,
    selectedSport: Sport = Sport.ALL,
    viewModelCourts: ViewModelCourts,
    showLoader: MutableState<Boolean>
) {


    val db=FirebaseFirestore.getInstance()
    val coll1=db.collection("courts")
    lateinit var  x:DocumentSnapshot


    val docs:MutableList<DocumentSnapshot> = mutableListOf()
    val loadSlot= remember {
        mutableStateOf(false)
    }
    var courts = remember { mutableStateListOf<Courts>() }
    viewModelCourts.getListaComponenti(showLoader = showLoader) {
        courts.clear()
        courts.addAll(it)
        loadSlot.value=true
    }
    var courtNamesWithSlots= remember {
        mutableStateListOf<Pair<String, List<CourtSlot>>>()
    }


    if(loadSlot.value){
        courtNamesWithSlots.clear()
        courtNamesWithSlots.addAll(courtSlots
            .groupBy { courtSlot -> courtSlot.court_id }
            .mapNotNull { (courtId, courtSlots) ->
                var name:String?=null
                val court= courts.first { it.getId() == courtId }

                name = court.getName()



                court.getAddress()?.let { courtAddress -> "$name||$courtAddress" to courtSlots }


            }
        )
        loadSlot.value=false
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
            text = if (courtSlots.isEmpty()) stringResource(R.string.no_available_slots_for)+" "+ selectedDate else stringResource(R.string.slots_for)+" "+selectedDate,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(courtNamesWithSlots) { court ->
                Text(
                    text = "${court.first.split("||")[0]}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                court.second.forEach { slot ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${slot.sport} - ${slot.time_start} - ${slot.time_finish}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                if (showButton) {
                                    Button(
                                        onClick = { onButtonClicked(slot); },
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .padding(end = 8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if(showArrow){
                                                Icon(painter = painterResource(R.drawable.baseline_swap_horiz_24),
                                                contentDescription = stringResource(R.string.change_date))
                                            }
                                            else {
                                             Icon(
                                                Icons.Filled.Add,
                                                contentDescription = stringResource(R.string.choose_reservation_date)
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
    }
}

