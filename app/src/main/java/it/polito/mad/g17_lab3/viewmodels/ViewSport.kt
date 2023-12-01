package it.polito.mad.g17_lab3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.data.*

class ViewSport(application: Application, id: Int) : AndroidViewModel(application) {
    class ViewSportFactory(
        private val application: Application,
        private val key: Int
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ViewSport(application, key) as T
    }



    val db=FirebaseFirestore.getInstance()
    var freeSlot:MutableLiveData<MutableList<CourtSlot>> = MutableLiveData()
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
    val courtsDoc=db.collection("court_slots")

    fun getListaComponenti(callback: (List<CourtSlot>) -> Unit) {
        val collectionRef = db.collection("court_slots")

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti = mutableListOf<CourtSlot>()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToSlot(document)
                if(componente.reservation_id==null){
                   // freeSlot.value!!.add(componente)
                    componenti.add(componente)

                }

            }

            callback(componenti)
        }
    }

/*
    val repo = ReservationRepository(application)
    private var _sportvalue = repo.getAll()
    var value: LiveData<List<Court>> = repo.getAll()
    var value_reservation: LiveData<List<Reservation>> = repo.getReservation()
    var freeCourtSlot: LiveData<List<CourtSlot>> = repo.getFreeSlot()
    var value_rating: LiveData<List<RatingCourt>> = repo.getAllRating()
    var id_res= "0"

    fun add(slot: CourtSlot) {
        thread {
            repo.addCourtSlot(slot)
        }

    }

    fun add(rating: RatingCourt) {
        thread {
            repo.addRating(rating)
        }
    }

    fun add(reservation: Reservation, CourtSlot: CourtSlot, setShowLoader: (Boolean) -> Unit = {}) {
        thread {
             repo.addReservation(reservation)
            CourtSlot.reservation_id = id_res
            repo.update(CourtSlot)
            setShowLoader(false)
        }
    }


    fun update(reservation: Reservation, CourtSlot: CourtSlot, setShowLoader: (Boolean) -> Unit = {}) {
        thread {
            repo.update(reservation)
            repo.freeSlotWithId(reservation.id)
            CourtSlot.reservation_id = reservation.id
            repo.update(CourtSlot)
            setShowLoader(false)
        }
    }
/*
    fun delete(reservation: Reservation, setShowLoader: (Boolean) -> Unit = {}) {
        thread {
            repo.delete(reservation)
            repo.freeSlot(reservation.courtId, reservation.time.split(" -")[0], reservation.date)
            setShowLoader(false)
        }
    }

 */

    fun add(court: Court) {
        thread {
            repo.addCourt(court)
        }

    }

    fun clear() {
        repo.clear()
    }
    */

}


