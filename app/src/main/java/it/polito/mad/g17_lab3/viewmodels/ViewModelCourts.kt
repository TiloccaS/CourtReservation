package it.polito.mad.g17_lab3.viewmodels

import CourtRating
import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.components.translateDocumentToReservation
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.data.ReservationRepository

class ViewModelCourts(application: Application) : AndroidViewModel(application) {
    class ViewModelCourtsFactory(
        private val application: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ViewModelCourts(application) as T
    }
    /*
    private val db = ReservationDatabase.getDatabase(application)
    private val repo = ReservationRepository(application)
    private val _reservations = db.reservationDao().getAll()
    private val _courts = db.courtDao().getAll()
    var courts: LiveData<List<Court>> = _courts
    var value: LiveData<List<Reservation>> = _reservations

     */
    val courts:MutableLiveData<MutableList<Courts>> = MutableLiveData()

     val db = FirebaseFirestore.getInstance()
    val courtsDoc=db.collection("courts")
     fun getListaComponenti(showLoader: MutableState<Boolean>, callback: (List<Courts>) -> Unit) {
        val collectionRef = db.collection("courts")
         //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti = mutableListOf<Courts>()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToCourts(document)
                componenti.add(componente)
                //courts.value!!.add(componente)
            }

            callback(componenti)
            //showLoader.value = false
        }
    }
    fun getListaReviews( callback: (List<CourtRating>) -> Unit) {
        val collectionRef = db.collection("court_ratings")
        //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti = mutableListOf<CourtRating>()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToCourtRating(document)
                componenti.add(componente)


                //courts.value!!.add(componente)
            }

            callback(componenti)
            //showLoader.value = false
        }
    }
    fun translateDocumentToCourts(document:DocumentSnapshot):Courts{
        val court=Courts()
        court.setName(document.getString("name")!!)
        court.setAddress(document.getString("address")!!)
        court.setOpening(document.getString("opening")!!)
        court.setClosing(document.getString("closing")!!)
        court.setId(document.getDocumentReference("id")?:null)
        return court
    }

    fun translateDocumentToCourtRating(document:DocumentSnapshot):CourtRating{
       val rat=CourtRating()
        rat.court_id=document.getDocumentReference("court_id")!!
        rat.rating=document.getLong("rating")!!
        rat.review=document.getString("review").toString()
        rat.reservation_id= document.getDocumentReference("reservation_id")!!
        rat.user_id=document.getDocumentReference("user_id")!!
        rat.username= document.getString("username").toString()
        return rat
    }

}