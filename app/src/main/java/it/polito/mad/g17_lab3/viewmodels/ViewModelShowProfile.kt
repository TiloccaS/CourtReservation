package it.polito.mad.g17_lab3.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.components.Users
import it.polito.mad.g17_lab3.data.User

class ViewModelShowProfile(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    fun setParams(
        name: String, surname: String, user: String,
        gender: String, mail: String, bio: String, phone: String,
        birthday: String, football: Long, basket: Long, tennis: Long, image: String,
        visibleName: Boolean, visibleMail: Boolean, visiblePhone: Boolean,
        visibleBirthday: Boolean, visibleSports: Boolean, authId: String,setShowLoader:(Boolean)->Unit
    ) {

        db.document("/users/"+this.id).set(User(
            id = this.id,
            name = name,
            surname = surname,
            username = user,
            gender = gender,
            email = mail,
            bio = bio,
            birthday = birthday,
            phone = phone,
            profileImage = image,
            football_rating = football,
            basket_rating = basket,
            tennis_rating = tennis,
            visibleBirthday = visibleBirthday,
            visibleMail = visibleMail,
            visibleName = visibleName,
            visiblePhone = visiblePhone,
            visibleSports = visibleSports,
            auth_id = authId
        )).addOnSuccessListener {
            setShowLoader(false)

        }
    }
    fun upgradeImage(image:String){
        val aggiornamenti :HashMap<String,Any> = hashMapOf(
            "profileImage" to image
        )
        db.document("users/"+this.id).update(aggiornamenti)
    }
    fun load(showLoader: MutableState<Boolean>, hasLoaded: MutableState<Boolean>) {
        showLoader.value = true
       // hasLoaded.value = true
        val collection = "users"
        val attributeName = "auth_id"
        val attributeValue = FirebaseAuth.getInstance().currentUser?.uid


        db.collection(collection)
            .whereEqualTo(attributeName, attributeValue)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (document in querySnapshot) {
                    // Access each document that matches the filter


                    this.id = document.id
                    this.user = (document.getString("username") ?: "")

                    this.name = (document.getString("name") ?: "")
                    this.surname = (document.getString("surname") ?: "")
                    fullname = name + " " + surname
                    this.gender = (document.getString("gender") ?: "")
                    this.bio = (document.getString("bio") ?: "")
                    this.mail = (document.getString("email") ?: "")
                    this.phone = (document.getString("phone") ?: "")
                    this.birthday = (document.getString("birthday") ?: "")
                    this.photo = (document.getString("profileImage") ?: "")
                    this.football =
                        arrayListOf(
                            "Football",
                            (document.getLong("football_rating") ?: 0)
                        )
                    this.basket =
                        arrayListOf("Basket", (document.getLong("basket_rating") ?: 0))
                    this.tennis =
                        arrayListOf("Tennis", (document.getLong("tennis_rating") ?: 0))
                    this.sports = arrayListOf(this.football, this.basket, this.tennis)
                    this.showname = (document.getBoolean("visibleName") ?: false)
                    this.showmail = (document.getBoolean("visibleMail") ?: false)
                    this.showsports = (document.getBoolean("visibleSports") ?: false)
                    this.showphone = (document.getBoolean("visiblePhone") ?: false)
                    this.showbirthday = (document.getBoolean("visibleBirthday") ?: false)
                    this.auth_id = (document.getString("auth_id") ?: "")

                }
                showLoader.value = false
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the query
                showLoader.value = false
            }

    }
    fun getUser(authId:String, callback: (Users?) -> Unit) {
        val collectionRef = db.collection("users")
        //showLoader.value = true

        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Gestisci eventuali errori
                return@addSnapshotListener
            }

            var usr :Users?=null

            for (document in snapshot?.documents.orEmpty()) {
                val componente = translateDocumentToUser(document)
                if(componente.auth_id==authId){
                    usr=componente
                    break
                }
                //courts.value!!.add(componente)
            }

            callback(usr)
            //showLoader.value = false
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

    var id = ""
    var user = ""
    var name = ""
    var surname = ""
    var fullname = name + " " + surname
    var gender = ""
    var bio = ""
    var mail = ""
    var phone = "2"
    var birthday = ""
    var photo = ""
    var football = arrayListOf("Football", 0L)
    var basket = arrayListOf("Basket", 0L)
    var tennis = arrayListOf("Tennis", 0L)
    var sports = arrayListOf(football, basket, tennis)
    var showname = false
    var showmail = false
    var showsports = false
    var showphone = false
    var showbirthday = false
    var auth_id = ""

}