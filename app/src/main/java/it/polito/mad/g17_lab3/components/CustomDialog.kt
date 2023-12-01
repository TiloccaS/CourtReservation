package it.polito.mad.g17_lab3.components

//import androidx.compose.material.*

import Notification
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.CourtSlotInfo
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.enums.Choice
import it.polito.mad.g17_lab3.viewmodels.ViewModelReservation
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import java.util.*

import it.polito.mad.g17_lab3.MainActivity
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalComposeUiApi
@Composable
fun CustomDialog(
    vm: ViewSport,
    choice: Enum<Choice>,
    courtSlotInfo: CourtSlotInfo,
    reservation: Reservation,
    setShowDialog: (Boolean) -> Unit,
    setEditSlot: () -> Unit = {},
    handleConfirm: () -> Unit = {},
    setShowLoader: (Boolean) -> Unit = {},
    setShowAnimation: (Int) -> Unit,
    viewModelReservation: ViewModelReservation?=null,
    setRefreshReservation: (Boolean) -> Unit,
    navController: NavHostController? = null
) {
    val txtFieldErrorRented = remember { mutableStateOf("") }
    val txtFieldErrorMember = remember { mutableStateOf("") }
    val current_user_id = remember { mutableStateOf("") }
    val open_reservation= remember {
        mutableStateOf(false)
    }
    val txtFieldMember = remember { mutableStateOf("") }
    val txtFieldRented = remember { mutableStateOf("") }
    var shoeRes by remember { mutableStateOf(false) }
    var initialize_slot_info = remember {
        mutableStateOf(false)
    }
    val main=MainActivity()
    val cont=LocalContext.current


    var newRes: Reservation = Reservation(
        id = reservation.id,
        author_id = reservation.author_id,
        court_id = reservation.court_id,
        court_name = reservation.court_name,
        date = reservation.date,
        sport = reservation.sport,
        player_number = reservation.player_number,
        rented_equipment = reservation.rented_equipment,
        time = reservation.time
    )
    if (!initialize_slot_info.value) {
        courtSlotInfo.setPeople(reservation.player_number)
        courtSlotInfo.setEquipment(reservation.rented_equipment)

        initialize_slot_info.value = true


    }
    val confirm =
stringResource(Choice.valueOf(choice.name).choice )+ " "+stringResource(R.string.this_reservation)
    val db = FirebaseFirestore.getInstance()
    val coll1 = db.collection("reservations")
    fun getDocumentQuerySnapshot(collectionName: String): Task<QuerySnapshot> {
        val collectionRef = db.collection(collectionName)
        return collectionRef.get()
    }



    Dialog(onDismissRequest = { setShowDialog(false) }) {


        Surface(
            color = Color.White,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .wrapContentSize(Alignment.Center)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,


                        ) {
                        Text(
                            text = confirm,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.wrapContentSize(Alignment.Center)

                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = reservation.court_name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = reservation.sport,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Text(text = reservation.date, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = reservation.time,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        when (choice) {
                            Choice.EDIT -> {
                                IconButton(onClick = {
                                    setEditSlot()

                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_edit_24),
                                        contentDescription = "edit Date"
                                    )
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(20.dp))


                    when (choice) {
                        Choice.ADD -> {
                            reservation.current_number=1
                            reservation.confirmed_participants.add(FirebaseAuth.getInstance().currentUser!!.uid)
                            Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(
                                    text = "${stringResource(id = R.string.reservation_open)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                Switch(
                                    checked = open_reservation.value,
                                    onCheckedChange = { newValue ->
                                        open_reservation.value = newValue
                                        if(newValue==false){
                                            reservation.player_number=0
                                            txtFieldMember.value=""
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))

                            if(open_reservation.value==true){
                                OutlinedTextField(
                                    singleLine = true,
                                    maxLines = 1,
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        errorIndicatorColor = Color.Red
                                    ),
                                    isError = (txtFieldMember.value.toIntOrNull() == null && txtFieldMember.value!=""),
                                    placeholder = { Text(text = stringResource(R.string.max_people_joinable)) },
                                    label = { Text(text = stringResource(R.string.people_joinable)) },
                                    value = txtFieldMember.value,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = {
                                        txtFieldMember.value = it
                                        if (it.toIntOrNull() != null) {
                                            courtSlotInfo.setPeople(Integer.parseInt(it))
                                            reservation.player_number = Integer.parseInt(it)

                                            txtFieldErrorMember.value = ""

                                        }
                                    })
                                Spacer(modifier = Modifier.height(20.dp))

                            }


                            OutlinedTextField(
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    errorIndicatorColor = Color.Red
                                ),
                                isError = (txtFieldRented.value.toIntOrNull() == null && txtFieldRented.value!="" ), maxLines = 1,
                                placeholder = { Text(text = stringResource(R.string.enter_number_of_rented_equipment)) },
                                label = { Text(text = stringResource(R.string.equipment)) },
                                value = txtFieldRented.value,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                onValueChange = {
                                    txtFieldRented.value = it
                                    if (it.toIntOrNull() != null) {
                                        courtSlotInfo.setEquipment(Integer.parseInt(it))
                                        reservation.rented_equipment = Integer.parseInt(it)
                                        txtFieldErrorRented.value = ""
                                    }
                                })
                        }
                        Choice.EDIT -> {
                            OutlinedTextField(
                                maxLines = 1,
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    errorIndicatorColor = Color.Red
                                ),
                                isError = (txtFieldMember.value.toIntOrNull() == null && txtFieldMember.value != "") || txtFieldErrorMember.value!="" ,
                                placeholder = {
                                    if(txtFieldErrorMember.value!="") {
                                        Text(text = "${txtFieldErrorMember.value}" , color = Color.Red)

                                    }else {
                                        Text(text =  stringResource(R.string.people_joinable) )

                                    }
                                    },
                                label = { Text(text = stringResource(R.string.people_joinable) )},
                                value = txtFieldMember.value,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                onValueChange = {
                                    txtFieldMember.value = it

                                    if (it.toIntOrNull() != null) {
                                        courtSlotInfo.setPeople(Integer.parseInt(it))
                                        reservation.player_number = Integer.parseInt(it)
                                        newRes.player_number=Integer.parseInt(it)
                                        if(newRes.player_number>=reservation.confirmed_participants.size){
                                            txtFieldErrorMember.value = ""

                                        }

                                    }
                                })
                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent,
                                    errorIndicatorColor = Color.Red
                                ),
                                //isError = txtFieldRented.value.toIntOrNull() == null && txtFieldRented.value != "",
                                maxLines = 1,
                                placeholder = { Text(text = "${reservation.rented_equipment}"+" "+stringResource(R.string.equipments_requested)) },
                                label = { Text(text = "${reservation.rented_equipment}"+" "+stringResource(R.string.equipments_requested) )},
                                value = txtFieldRented.value,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                onValueChange = {
                                    txtFieldRented.value = it

                                    if (it.toIntOrNull() != null) {
                                        courtSlotInfo.setEquipment(Integer.parseInt(it))
                                        reservation.rented_equipment = Integer.parseInt(it)
                                        txtFieldErrorRented.value = ""
                                        newRes.rented_equipment=Integer.parseInt(it)



                                    }
                                })

                        }
                        Choice.DELETE -> {
                            Text(
                                text = "${reservation.player_number}"+" "+ stringResource(R.string.participants),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.wrapContentSize(
                                    Alignment.Center
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = " ${reservation.rented_equipment}"+" "+stringResource(R.string.equipments_requested),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.wrapContentSize(
                                    Alignment.Center
                                )
                            )

                        }
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    Row(Modifier.padding(top = 10.dp)) {

                        OutlinedButton(
                            onClick = { setShowDialog(false) },
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .weight(1F)
                        ) {
                            Text(text =stringResource(R.string.cancel))
                        }
                        Button(
                            onClick = {
                                setShowLoader(true)

                                val nplayers = txtFieldMember.value.toIntOrNull()
                                val rented = txtFieldRented.value.toIntOrNull()

                                if (nplayers != null) {
                                    newRes.player_number = nplayers
                                }
                                if (rented != null) {
                                    newRes.rented_equipment = rented
                                }
                                var courtSlot = CourtSlot(
                                    id = courtSlotInfo.getSlotId(),
                                    date = courtSlotInfo.getDate(),
                                    sport = courtSlotInfo.getCourtSport(),
                                    time_start = courtSlotInfo.getCourtOpening(),
                                    time_finish = courtSlotInfo.getCourtClosing(),
                                    reservation_id = reservation.id,
                                    court_id = reservation.court_id!!
                                )
                                when (choice) {

                                    Choice.ADD -> {
                                        //setShowLoader(true)
                                        newRes.player_number+=1
                                        if (txtFieldMember.value.isEmpty()) {
                                            txtFieldErrorMember.value = "${main.getStringFromId(context=cont,id=R.string.field_cannot_be_empty)}"

                                        } else if (txtFieldRented.value.isEmpty()) {
                                            txtFieldErrorRented.value = "${main.getStringFromId(context=cont,id=R.string.field_cannot_be_empty)}"

                                        }


                                        //vm.add(newRes, courtSlot, setShowLoader)
                                        newRes.current_number=1
                                        newRes.confirmed_participants.add(FirebaseAuth.getInstance().currentUser!!.uid)

                                        val reservations = db.collection("reservations")
                                        reservations.add(newRes).addOnSuccessListener {
                                            newRes.id = it
                                            db.document(it!!.path).set(newRes)
                                                .addOnSuccessListener {
                                                    courtSlot.reservation_id = newRes.id
                                                    db.document(courtSlot.id!!.path).set(courtSlot).addOnSuccessListener {
                                                        val notifications =
                                                            db.collection("notifications")
                                                        notifications.add(
                                                            Notification(
                                                                message = "${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " ${main.getStringFromId(context=cont,id=R.string.on__date)} " + newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_been_added_successfully)}",
                                                                recipient = reservation.author_id,
                                                                is_read = false,
                                                                timestamp = Timestamp.now()
                                                            )
                                                        ).addOnSuccessListener {
                                                            setShowDialog(false)

                                                            setShowLoader(false)
                                                            setShowAnimation(1)

                                                            navController?.navigate("Reservations")


                                                        }



                                                    }





                                                }.addOnFailureListener {
                                                    setShowAnimation(2)
                                                    setShowDialog(false)

                                                    setShowLoader(false)
                                                }
                                        }.addOnFailureListener {
                                            setShowAnimation(2)
                                            setShowLoader(false)
                                            val notifications = db.collection("notifications")
                                            notifications.add(
                                                Notification(
                                                    message = "${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " ${main.getStringFromId(context=cont,id=R.string.on__date)} " + newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_not_been_added_due_to_an_error)}",
                                                    recipient = reservation.author_id,
                                                    is_read = false,
                                                    timestamp = Timestamp.now()
                                                )
                                            ).addOnSuccessListener {
                                                setShowLoader(false)

                                            }
                                        }


                                    }
                                    Choice.EDIT -> {
                                        newRes.confirmed_participants=reservation.confirmed_participants
                                        newRes.participants=reservation.participants
                                        if(newRes.player_number<reservation.confirmed_participants.size){
                                            txtFieldErrorMember.value= main.getStringFromId(cont,id = R.string.currently_there_are)+ " "+newRes.confirmed_participants.size.toString()+" "+ main.getStringFromId(cont,id =R.string.not_have_less)

                                            txtFieldMember.value=""
                                          //  setShowDialog(false)

                                            setShowLoader(false)

                                            //txtFieldMember.value=txtFieldErrorMember.value
                                        }else
                                        {
                                            newRes.author_id =
                                                FirebaseAuth.getInstance().currentUser!!.uid
                                            //setShowLoader(true)

                                            //vm.update(newRes, courtSlot, setShowLoader)
                                            val aggiornamenti= mapOf(
                                                "player_number" to newRes.player_number,
                                                "rented_equipment" to newRes.rented_equipment,
                                                "court_id" to newRes.court_id,
                                                "date" to newRes.date,
                                                "time" to newRes.time,
                                                "sport" to newRes.sport

                                            )
                                            if (courtSlotInfo.getId() == null) {
                                                db.document(newRes.id!!.path).update(aggiornamenti)
                                                    .addOnSuccessListener {
                                                        setShowAnimation(1)
                                                        setShowDialog(false)

                                                        setShowLoader(false)
                                                    }.addOnFailureListener { setShowAnimation(2) }
                                                //setShowLoader(false)
                                            } else {
                                                val updates = HashMap<String, Any>()
                                                updates["reservation_id"] = FieldValue.delete()
                                                var collectionName = "court_slots"
                                                val querySnapshotTask =
                                                    getDocumentQuerySnapshot(collectionName)
                                                querySnapshotTask.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        val querySnapshot = task.result
                                                        // Esempio di iterazione sui documenti nel QuerySnapshot
                                                        for (document in querySnapshot) {
                                                            if (document.getDocumentReference("reservation_id") == newRes.id!!) {
                                                                db.document(document.reference!!.path)
                                                                    .update(updates).addOnSuccessListener {
                                                                        db.document(courtSlot.reservation_id!!.path)
                                                                            .update(aggiornamenti)
                                                                            .addOnSuccessListener {
                                                                                db.document(courtSlot.id!!.path)
                                                                                    .set(courtSlot)
                                                                                    .addOnSuccessListener {
                                                                                        setShowAnimation(1)
                                                                                        db.collection("notifications").add(
                                                                                            Notification(
                                                                                                message = "${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " ${main.getStringFromId(context=cont,id=R.string.on__date)} " + newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_been_edited_successfully)}",
                                                                                                recipient = newRes.author_id,
                                                                                                is_read = false,
                                                                                                timestamp = Timestamp.now()
                                                                                            )
                                                                                        ).addOnSuccessListener {
                                                                                            setShowDialog(false)

                                                                                            setShowLoader(false)

                                                                                        }
                                                                                    }.addOnFailureListener {
                                                                                        setShowAnimation(2)
                                                                                        setShowDialog(false)

                                                                                        setShowLoader(false)
                                                                                    }


                                                                            }.addOnFailureListener {
                                                                                setShowAnimation(2)
                                                                                setShowDialog(false)

                                                                                setShowLoader(false)
                                                                            }
                                                                    }
                                                            }

                                                        }



                                                    } else {
                                                        val exception = task.exception
                                                        // Gestisci l'errore
                                                        println("Errore durante l'ottenimento del QuerySnapshot: $exception")
                                                    }
                                                }
                                            }
                                        }


                                    }
                                    Choice.DELETE -> {
                                     //   setShowLoader(true)
                                        if(viewModelReservation!=null){
                                            viewModelReservation.delete_user(auth_user = FirebaseAuth.getInstance().currentUser?.uid,reservation, setShowLoader = setShowLoader, cont = cont)
                                        }
                                        else{
                                            db.document(reservation.id!!.path).delete()
                                                .addOnSuccessListener {
                                                    var collectionName = "court_slots"

                                                val querySnapshotTask =
                                                    getDocumentQuerySnapshot(collectionName)
                                                val updates = HashMap<String, Any>()
                                                updates["reservation_id"] = FieldValue.delete()
                                                querySnapshotTask.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        val querySnapshot = task.result
                                                        // Esempio di iterazione sui documenti nel QuerySnapshot
                                                        for (document in querySnapshot) {
                                                            var date =
                                                                document.getString("date") ?: ""
                                                            var id =
                                                                (document.getDocumentReference("reservation_id")
                                                                    ?: null)
                                                            var time =
                                                                document.getString("time_start")
                                                                    ?: "" + "-" + document.getString(
                                                                        "time_finish"
                                                                    ) ?: ""
                                                            if (id == reservation.id) {
                                                                db.document(document.reference!!.path)
                                                                    .update(
                                                                        updates
                                                                    ).addOnSuccessListener {
                                                                        setShowDialog(false)

                                                                        setShowLoader(false)
                                                                        setShowAnimation(1)
                                                                        val notifications =
                                                                            db.collection("notifications")
                                                                        notifications.add(
                                                                            Notification(
                                                                                message = "${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " ${main.getStringFromId(context=cont,id=R.string.on__date)} " + newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_been_deleted_successfully)}",
                                                                                recipient = reservation.author_id,
                                                                                is_read = false,
                                                                                timestamp = Timestamp.now()
                                                                            )
                                                                        ).addOnSuccessListener {
                                                                            for(tmp in reservation.confirmed_participants){
                                                                                if(tmp!=reservation.author_id){
                                                                                    db.collection("notifications")
                                                                                    notifications.add(
                                                                                        Notification(
                                                                                            message = "${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " "+"${main.getStringFromId(context=cont,id=R.string.on__date)}" +" "+ newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_been_deleted)}",
                                                                                            recipient = tmp,
                                                                                            is_read = false,
                                                                                            timestamp = Timestamp.now()
                                                                                        ))
                                                                                }

                                                                            }
                                                                        }
                                                                    }.addOnFailureListener {
                                                                        setShowAnimation(2)
                                                                        setShowDialog(false)

                                                                        setShowLoader(false)
                                                                        val notifications =
                                                                            db.collection("notifications")
                                                                        notifications.add(
                                                                            Notification(
                                                                                message ="${main.getStringFromId(context=cont,id=R.string.the_reservation_for_the_court)}" +" "+ newRes.court_name + " ${main.getStringFromId(context=cont,id=R.string.on__date)} " + newRes.date + " " + newRes.time + " "+ "${main.getStringFromId(context=cont,id=R.string.has_not_been_deleted_due_to_an_error)}",
                                                                                recipient = reservation.author_id,
                                                                                is_read = false,
                                                                                timestamp = Timestamp.now()
                                                                            )
                                                                        ).addOnSuccessListener {

                                                                            }
                                                                        }
                                                                }

                                                            }
                                                        } else {
                                                            val exception = task.exception
                                                            // Gestisci l'errore
                                                            println("Errore durante l'ottenimento del QuerySnapshot: $exception")
                                                        }
                                                    }
                                                }.addOnFailureListener { setShowAnimation(2) }

                                        }
                                        }


                                }
                                if(txtFieldErrorMember.value==""){
                                    shoeRes = true
                                    handleConfirm()
                                  //  setShowLoader(false)
                                 //   setShowDialog(false)
                                }




                                return@Button
                            },
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .weight(1F)
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }

                    }
                }

            }
        }
    }


}
