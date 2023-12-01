package it.polito.mad.g17_lab3.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.components.Users

class ViewModelUsers(application: Application) : AndroidViewModel(application) {
    class ViewModelUsersFactory(
        private val application: Application
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ViewModelUsers(application) as T
    }
    /*
    private val db = ReservationDatabase.getDatabase(application)
    private val repo = ReservationRepository(application)
    private val _reservations = db.reservationDao().getAll()
    private val _courts = db.courtDao().getAll()
    var courts: LiveData<List<Court>> = _courts
    var value: LiveData<List<Reservation>> = _reservations

     */
    val courts:MutableLiveData<MutableList<Users>> = MutableLiveData()

     val db = FirebaseFirestore.getInstance()
     fun getListaComponenti( callback: (List<Users>) -> Unit) {
        val collectionRef = db.collection("users")
         //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            val componenti = mutableListOf<Users>()

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToUser(document)
                componenti.add(componente)
                //courts.value!!.add(componente)
            }

            callback(componenti)
            //showLoader.value = false
        }
    }

    fun translateDocumentToUser(document:DocumentSnapshot):Users{
        val usr=Users()
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


}