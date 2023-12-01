package it.polito.mad.g17_lab3.components

import Notification
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.CourtSlotInfo
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.Reservation

import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalComposeUiApi
@Composable
fun CustomDialogJoin(
    courtSlotInfo: CourtSlotInfo,
    reservation: Reservation,
    setShowDialog: (Boolean) -> Unit,
    setShowLoader: (Boolean) -> Unit = {},
    setShowAnimation: (Int) -> Unit,
    setRefreshReservation: (Boolean) -> Unit,
    navController: NavHostController? = null,
    user: FirebaseUser?
) {

    var showRes by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var initialize_slot_info = remember {
        mutableStateOf(false)
    }
    var newRes: Reservation = Reservation(
        id = reservation.id,
        author_id = reservation.author_id,
        court_id = reservation.court_id,
        court_name = reservation.court_name,
        date = reservation.date,
        sport = reservation.sport,
        player_number = reservation.player_number,
        rented_equipment = reservation.rented_equipment,
        time = reservation.time,
        current_number = reservation.current_number,
        participants = reservation.participants,
        confirmed_participants = reservation.confirmed_participants
    )


    val confirm = context.getString(R.string.are_you_sure_you_want_to)+" "+ context.getString(R.string.to_join)+" "+context.getString(R.string.this_reservation)+"?"
    val db = FirebaseFirestore.getInstance()


    var equip by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {


        Surface(
            //color = Color.White,
            shape = MaterialTheme.shapes.extraLarge
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,


                        ) {
                    Text(
                        text = reservation.court_name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )}
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,


                        ) {
                    Text(
                        text = reservation.sport,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.wrapContentSize(Alignment.Center)
                    )}
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Text(text = reservation.date, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))

                        Text(
                            text = reservation.time,
                            style = MaterialTheme.typography.bodyLarge
                        )


                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,


                        ) {
                        Text(
                            text = context.getString(R.string.currently)+" "+"${reservation.confirmed_participants.size}"+" "+context.getString(R.string.out_of) +" "+"${reservation.player_number}"+" "+context.getString(R.string.participants),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.wrapContentSize(
                                Alignment.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Checkbox(checked = equip, onCheckedChange = {equip= !equip} )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = context.getString(R.string.request)+" "+context.getString(R.string.equipment),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.wrapContentSize(
                                Alignment.Center
                            ).padding(top=12.dp)
                        )
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
                    Text(text = context.getString(R.string.cancel))
                }

                Button(
                    onClick = {

                        newRes.current_number = newRes.current_number+1
                        newRes.participants.add(user?.uid.toString())
                        if(equip){
                            newRes.rented_equipment++
                        }

                        setShowLoader(true)
                        db.collection("users").get().addOnSuccessListener {
                                coll ->

                            var tmpname = ""
                            for (document in coll.documents.orEmpty()){
                                if(document.getString("auth_id")== user?.uid){
                                    tmpname=document.getString("username").toString()
                                }

                            }
                            db.document(reservation.id!!.path).set(newRes)
                                .addOnSuccessListener{
                                    setShowAnimation(1)
                                    setShowLoader(false)
                                    val notifications =
                                        db.collection("notifications")
                                    notifications.add(
                                        Notification(
                                            message = context.getString(R.string.user) +" "+ tmpname + " "+context.getString(
                                                R.string.wants_to_join_your_group_for_the_game_in)+" "+ newRes.court_name+" "+context.getString(
                                                R.string.on__date)+" " + newRes.date+ " " + newRes.time,
                                            recipient = reservation.author_id,
                                            is_read = false,
                                            timestamp = Timestamp.now()
                                        )
                                    )
                                    navController?.navigate("Reservations")

                                }
                                .addOnFailureListener {
                                    setShowAnimation(2)
                                    setShowLoader(false)
                                    val notifications = db.collection("notifications")
                                    notifications.add(
                                        Notification(
                                            message = context.getString(R.string.you_cant_join_this_reservation_due_to_an_error),
                                            recipient = user?.uid.toString(),
                                            is_read = false,
                                            timestamp = Timestamp.now()
                                        )
                                    )
                                }
                        }



                        showRes = true
                        setShowDialog(false)
                        return@Button },
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .weight(1F)
                        ) {
                            Text(text = stringResource(R.string.confirm))
                        }
            }
                    }
                }
        }

            }
        }


