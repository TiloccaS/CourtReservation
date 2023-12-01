package it.polito.mad.g17_lab3.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    val error = mutableStateOf("")

    val emailState = mutableStateOf("")
    val passwordState = mutableStateOf("")


    fun clear() {
        emailState.value = ""
        passwordState.value = ""
    }
    fun setEmail(email: String) {
        emailState.value = email
    }

    fun setPassword(password: String) {
        passwordState.value = password
    }

    fun login(onLogin: (user: FirebaseUser?) -> Unit) {
        val email = emailState.value
        val password = passwordState.value

        if(email == ""){
            error.value = "The email is mandatory"
            return
        }
        if(password == ""){
            error.value = "The password is mandatory"
            return
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login effettuato con successo
                    val user = firebaseAuth.currentUser
                    // Esegui le operazioni desiderate con l'utente loggato
                    onLogin(user)
                    error.value = ""
                } else {
                    // Errore durante il login
                    error.value = task.exception?.message.toString()
                    // Gestisci l'errore di autenticazione
                }
            }
    }
}