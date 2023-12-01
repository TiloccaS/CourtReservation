import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.polito.mad.g17_lab3.RatingBar
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.ui.res.stringResource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.R

class CourtRating {
    lateinit var court_id:DocumentReference
    var rating=0L
    lateinit var user_id:DocumentReference
    lateinit var username:String
    var review=""
    lateinit var reservation_id:DocumentReference

}
@ExperimentalComposeUiApi
@Composable
fun CustomDialogResevation(
    value: String,
    reservation: Reservation,
    viewModel: ViewSport,
    setShowDialog: (Boolean) -> Unit,
) {

    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf(value) }
    val scrollState = rememberScrollState()
    var text = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    val user: FirebaseUser? = auth.currentUser
    var id=""
    if(user!=null){
        id=user.uid
    }
    var courtid=reservation.court_id
    var rating=0L
    var review = remember{
        mutableStateOf("")
    }
    var id_doc_user:DocumentReference?=null

     val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("users")
    //showLoader.value = true
    var userid= remember {
        mutableStateOf<DocumentReference?>(null)
    }
    var username= remember {
        mutableStateOf<String?>(null)
    }
    collectionRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
            // Gestisci eventuali errori
            return@addSnapshotListener
        }

        val componenti = mutableListOf<CourtRating>()

        for (document in snapshot?.documents.orEmpty()) {
            val auth_id=document.getString("auth_id")
            if(auth_id==id){
                id_doc_user=document.reference
                userid.value=db.document(id_doc_user!!.path)
                username.value=document.getString("username");
            }


            //courts.value!!.add(componente)
        }

        //showLoader.value = false
    }
    var reservationid=db.document(reservation.id!!.path)
    var court_id=db.document(courtid!!.path)






    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            color = Color.White,
            shape = androidx.compose.material3.MaterialTheme.shapes.extraLarge
        ) {
            var rat = 0L
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .wrapContentSize(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        ) {
                        Text(
                            text = stringResource(R.string.rate_this_reservation),
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.wrapContentSize(Alignment.Center)

                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        rating = RatingBar(rating = rating)

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                    ) {
                        Text(
                            text = stringResource(R.string.review) + ":",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.wrapContentSize(Alignment.Center)

                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        value = review.value,
                        onValueChange = {review.value=it
                                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)

                            .padding(4.dp),
                        textStyle = TextStyle(color = Color.Black),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        OutlinedButton(
                            onClick = {
                                if (txtField.value.isEmpty()) {
                                    txtFieldError.value = "${R.string.field_cannot_be_empty}"
                                }
                                if(userid.value!=null){
                                    var rev=CourtRating()
                                    rev.user_id=userid.value!!
                                    rev.username=username.value!!
                                    rev.reservation_id=reservationid
                                    rev.rating=rating
                                    rev.review=review.value
                                    rev.court_id=court_id
                                    db.collection("court_ratings").add(rev)

                                }

                                //setValue(txtField.value)
                                setShowDialog(false)
                            }, shape = RoundedCornerShape(50.dp), modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(text = stringResource(R.string.done))
                        }
                    }


                }
            }
        }
    }
}
