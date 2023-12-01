package it.polito.mad.g17_lab3.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.data.CourtSlot
import it.polito.mad.g17_lab3.data.ReservationRepository

class ViewModelCourtFreeSlot(application: Application) : AndroidViewModel(application) {
    class ViewModelCourtFreeSlotFactory(
        private val application: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ViewModelCourtFreeSlot(application) as T
    }

    val repo = ReservationRepository(application)
    val courtSlots: MutableLiveData<MutableList<CourtSlot>> = MutableLiveData()

    private val db = FirebaseFirestore.getInstance()
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
                componenti.add(componente)
                //courtSlots.value.add(componente)
            }

            callback(componenti)
        }
    }
    /*
    var courts: LiveData<List<Court>> = repo.getAll()
    var slots: LiveData<List<CourtSlot>> = repo.getFreeSlot()

     */
    fun translateDocumentToSlot(document: DocumentSnapshot): CourtSlot {
        return CourtSlot(
            court_id = (document.getDocumentReference("court_id")!!),
            date = document.getString("date") ?: "",
            time_start = document.getString("time_start") ?: "",
            time_finish = document.getString("time_finish") ?: "",
            sport = document.getString("sport") ?: "",
            id = (document.getDocumentReference("id") ?: null),
            reservation_id = document.getDocumentReference("reservation_id")?:null

        )
    }

}