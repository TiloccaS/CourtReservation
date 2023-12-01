package it.polito.mad.g17_lab3

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.Date
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.g17_lab3.components.AuthWrapper
import it.polito.mad.g17_lab3.ui.theme.G17lab3Theme
import it.polito.mad.g17_lab3.viewmodels.*

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    fun getStringFromId(context: Context,id:Int): String {
        return context.getString(id)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showvm by viewModels<ViewModelShowProfile>()
        val viewModelSport by viewModels<ViewSport> {
            ViewSport.ViewSportFactory(application, -1)
        }
        val viewModelReservation by viewModels<ViewModelReservation> {
            ViewModelReservation.ViewModelReservationFactory(application)
        }
        val viewModelCourtFreeSlot by viewModels<ViewModelCourtFreeSlot> {
            ViewModelCourtFreeSlot.ViewModelCourtFreeSlotFactory(application)
        }
        val viewModelCourts by viewModels<ViewModelCourts>{
            ViewModelCourts.ViewModelCourtsFactory(application)
        }
        val viewModelUsers by viewModels<ViewModelUsers>{
            ViewModelUsers.ViewModelUsersFactory(application)
        }
        val loginvm by viewModels<LoginViewModel>()
        val registrationvm by viewModels<RegistrationViewModel>()
        setContent {
            G17lab3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AuthWrapper(
                        showvm,
                        viewModelSport,
                        viewModelReservation,
                        viewModelCourtFreeSlot,
                       viewModelCourts = viewModelCourts,
                        viewModelUsers,
                        loginvm,
                        registrationvm

                    )
                }
            }
        }
    }
}