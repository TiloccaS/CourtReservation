package it.polito.mad.g17_lab3.viewmodels

import Notification
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.MainActivity
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.data.User
import it.polito.mad.g17_lab3.enums.LoginState

class RegistrationViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val nameState = mutableStateOf("")
    val surnameState = mutableStateOf("")
    val emailState = mutableStateOf("")
    val usernameState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val confirmPasswordState = mutableStateOf("")
    val error = mutableStateOf("")
    val firebaseDb = FirebaseFirestore.getInstance()

    fun clear() {
        nameState.value = ""
        surnameState.value = ""
        emailState.value = ""
        usernameState.value = ""
        passwordState.value = ""
        confirmPasswordState.value = ""
    }

    fun setName(name: String) {
        nameState.value = name
    }
    fun setSurname(surname: String) {
        surnameState.value = surname
    }
    fun setEmail(email: String) {
        emailState.value = email
    }
    fun setUsername(username: String) {
        usernameState.value = username
    }

    fun setPassword(password: String) {
        passwordState.value = password
    }
    fun setConfirmPassword(confirmPassword: String) {
        confirmPasswordState.value = confirmPassword
    }

    fun register(cont: Context, onRegister: () -> Unit) {
        val email = emailState.value
        val password = passwordState.value
        val confirmPassword = confirmPasswordState.value
        if(password != confirmPassword){
            error.value = "${cont.getString(R.string.passwords_dont_correspond)}"
            return
        }
        if(email == ""){
            error.value = "${cont.getString(R.string.the_email_is_mandatory)}"
            return
        }
        if(password == ""){
            error.value = "${cont.getString(R.string.the_password_is_mandatory)}"
            return
        }
        //val main=MainActivity()
        //val cont= LocalContext.current

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registrazione avvenuta con successo
                    // Puoi gestire qui l'azione successiva come il redirect alla schermata di login

                    var authId = firebaseAuth.currentUser?.uid
                    if(authId == null){
                        authId = ""
                    }

                    val users = firebaseDb.collection("users")
                    users.add(
                        User(
                            name = nameState.value,
                            surname = surnameState.value,
                            email = emailState.value,
                            username = usernameState.value,
                            auth_id = authId,
                        )
                    ).addOnSuccessListener {
                        error.value = "";
                        FirebaseFirestore.getInstance().collection("notifications").add(
                            Notification(
                                message = "${cont.getString(R.string.welcome)} "+ nameState.value + " " + surnameState.value + " "+"${cont.getString(R.string.and_lets_get_started)}",
                                recipient = authId,
                                is_read = false,
                                timestamp = Timestamp.now()
                            )
                        ).addOnSuccessListener {

                        }
                    }.addOnFailureListener { error.value = it.message.toString()  }
                    onRegister()
                } else {
                    // Gestione dell'errore durante la registrazione
                    error.value = task.exception?.message.toString();
                    // Puoi gestire l'errore qui mostrando un messaggio all'utente o effettuando altre azioni appropriate
                }
            }
    }
}
