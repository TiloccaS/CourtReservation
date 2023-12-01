package it.polito.mad.g17_lab3.viewmodels

import Notification
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.g17_lab3.MainActivity
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.components.Users
import it.polito.mad.g17_lab3.components.translateDocumentToReservation
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.data.ReservationRepository
import it.polito.mad.g17_lab3.data.User

class ViewModelReservation(application: Application) : AndroidViewModel(application) {
    class ViewModelReservationFactory(
        private val application: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ViewModelReservation(application) as T
    }
    /*
    private val db = ReservationDatabase.getDatabase(application)
    private val repo = ReservationRepository(application)
    private val _reservations = db.reservationDao().getAll()
    private val _courts = db.courtDao().getAll()
    var courts: LiveData<List<Court>> = _courts
    var value: LiveData<List<Reservation>> = _reservations

     */
    private val db = FirebaseFirestore.getInstance()
    fun getReservation( reservationId: String,callback: (Reservation?) -> Unit) {
        val collectionRef = db.collection("reservations")
        //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            var componenti :Reservation?=null

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToReservation(document)
                if(reservationId==componente.id!!.path){
                    componenti=componente
                    break
                }
                //courts.value!!.add(componente)
            }

            callback(componenti)
            //showLoader.value = false
        }
    }
    fun getReservations( callback: (MutableList<Reservation>) -> Unit) {
        val collectionRef = db.collection("reservations")
        //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti :MutableList<Reservation> = mutableListOf()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToReservation(document)
                componenti.add(componente)


                //courts.value!!.add(componente)
            }
            callback(componenti)
            //showLoader.value = false
        }

    }
    fun confirm_user(auth_user:String,reservation: MutableState<Reservation?>,choiche:MutableState<Boolean?>,showLoader:MutableState<Boolean>,user:Users,context:Context){
        val main=MainActivity()
        val cont=context
        if(reservation.value!=null){
            val arraytmp:ArrayList<String> = arrayListOf()
            reservation.value!!.participants.forEach{
                if(it!=auth_user){
                    arraytmp.add(
                        it
                    )
                }
            }
            val confermati=reservation.value!!.confirmed_participants
            confermati.add(auth_user)
            val db=FirebaseFirestore.getInstance()
            db.document(reservation!!.value!!.id!!.path).update("participants" , arraytmp).addOnSuccessListener {
                db.document(reservation!!.value!!.id!!.path).update("confirmed_participants",confermati).addOnSuccessListener {
                    db.collection("notifications").add(
                    Notification(
                        message = "${main.getStringFromId(context=cont,id= R.string.accepted_reservation)}" + reservation.value!!.date + " " + reservation.value!!.time + " ",
                        recipient = auth_user,
                        is_read = false,
                        timestamp = Timestamp.now()
                    )).addOnSuccessListener {
                        choiche.value=true

                        showLoader.value=false
                    }

                }

            }

        }



    }
    fun delete_user(auth_user: String?,reservation: Reservation,user:Users?=null,cont: Context,setShowLoader:(Boolean)->Unit) {
        val main = MainActivity()
        if (auth_user != null) {
            var arraytmp: ArrayList<String> = arrayListOf()
            val current = reservation.current_number
            if (reservation.confirmed_participants.contains(auth_user)) {
                reservation.confirmed_participants.forEach {
                    if (it != auth_user) {
                        arraytmp.add(it)
                    }
                }
                if (reservation.id != null) {
                    db.document(reservation!!.id!!.path).update("confirmed_participants", arraytmp)
                        .addOnSuccessListener {
                            db.document(reservation!!.id!!.path)
                                .update("current_number", current - 1).addOnSuccessListener {
                                if (user != null) {
                                    db.collection("notifications").add(
                                    Notification(
                                        message = main.getStringFromId(
                                            context = cont,
                                            id = R.string.the_user
                                        ) + user.username + main.getStringFromId(
                                            context = cont,
                                            id = R.string.decline_reservation
                                        ) + reservation.date + " " + reservation.time + " ",
                                        recipient = auth_user,
                                        is_read = false,
                                        timestamp = Timestamp.now()
                                    )).addOnSuccessListener { setShowLoader(false)
                                    }
                                } else {
                                    //showLoader.value = true
                                    val collectionRef = db.collection("users")
                                    val listener =
                                        collectionRef.addSnapshotListener { snapshot, error ->
                                            if (error != null) {
                                                // Gestisci eventuali errori
                                                return@addSnapshotListener
                                            }

                                            var usr: Users? = null

                                            for (document in snapshot?.documents.orEmpty()) {
                                                val componente = translateDocumentToUser(document)
                                                if (componente.auth_id == auth_user) {
                                                    usr = componente
                                                    db.collection("notifications").add(
                                                    Notification(
                                                        message = main.getStringFromId(
                                                            context = cont,
                                                            id = R.string.the_user
                                                        ) + usr.username + main.getStringFromId(
                                                            context = cont,
                                                            id = R.string.cancel_reservation
                                                        ) + reservation.date + " " + reservation.time + " ",
                                                        recipient = auth_user,
                                                        is_read = false,
                                                        timestamp = Timestamp.now()
                                                    )).addOnSuccessListener{ setShowLoader(false)
                                                    }
                                                    break
                                                }
                                                //courts.value!!.add(componente)
                                            }

                                        }
                                }
                            }
                        }
                }
            }

                arraytmp = arrayListOf()
                if (reservation.participants.contains(auth_user)) {
                    reservation.participants.forEach {
                        if (it != auth_user) {
                            arraytmp.add(it)
                        }
                    }
                    db.document(reservation.id!!.path).update("participants", arraytmp)
                        .addOnSuccessListener {
                            if(FirebaseAuth.getInstance().currentUser?.uid == reservation.author_id){
                                db.collection("notifications").add(
                                    Notification(
                                        message = main.getStringFromId(
                                            context = cont,
                                            id = R.string.the_user
                                        ) + user?.username + main.getStringFromId(
                                            context = cont,
                                            id = R.string.decline_reservation
                                        ) + reservation.date + " " + reservation.time + " ",
                                        recipient = auth_user,
                                        is_read = false,
                                        timestamp = Timestamp.now()
                                    )).addOnSuccessListener { setShowLoader(false)
                                }
                            }
                           setShowLoader(false)

                        }
                }
            }
        }


}
fun translateDocumentToUser(document: DocumentSnapshot): Users{
    val usr= Users()
    usr.bio=document.getString("bio")?:""
    usr.auth_id=document.getString("auth_id")?:""
    usr.basket_rating=document.getLong("basket_rating")?:0L
    usr.birthday=document.getString("birthday")?:""
    usr.football_rating=document.getLong("football_rating")?:0L
    usr.gender=document.getString("gender")?:""
    usr.email=document.getString("email")?:""
    usr.id=document.getString("id")?:""
    usr.phone=document.getString("phone")?:""
    usr.name=document.getString("name")?:""
    usr.surname=document.getString("surname")?:""
    usr.tennis_rating=document.getLong("tennis_rating")?:0L
    usr.username=document.getString("username")?:""
    usr.profileImage=document.getString("profileImage")?:""
    usr.visibleBirthday=document.getBoolean("visibleBirthday")?:false
    usr.visibleMail=document.getBoolean("visibleMail")?:false
    usr.visibleName=document.getBoolean("visibleName")?:false
    usr.visiblePhone=document.getBoolean("visiblePhone")?:false
    usr.visibleSports=document.getBoolean("visibleSports")?:false
    return usr




}