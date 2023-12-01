package it.polito.mad.g17_lab3.components

import LoginScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.g17_lab3.MainScreen
import it.polito.mad.g17_lab3.enums.LoginState
import it.polito.mad.g17_lab3.viewmodels.*
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthWrapper(
    showvm: ViewModelShowProfile,
    viewModelSport: ViewSport,
    viewModelReservation: ViewModelReservation,
    viewModelCourtFreeSlot: ViewModelCourtFreeSlot,
    viewModelCourts: ViewModelCourts,
    viewModelUsers: ViewModelUsers,
    loginvm: LoginViewModel,
    registrationvm: RegistrationViewModel
) {
    val userLogged = FirebaseAuth.getInstance().currentUser
    var loginState by remember {
        mutableStateOf(if (userLogged == null) LoginState.LOGIN else LoginState.LOGGED)
    }
    var user by remember {
        mutableStateOf(userLogged)
    }
    var showOnboarding = remember {
        mutableStateOf(false)
    }
    when (loginState) {
        LoginState.LOGIN -> {
            LoginScreen(viewModel = loginvm,
                onNavigateToRegistration = { loginState = LoginState.REGISTER; registrationvm.clear() },
                onLogin = {
                    user = it
                    if (user != null) {
                        loginState = LoginState.LOGGED
                    }
                }
            )
        }
        LoginState.REGISTER -> {
            RegistrationScreen(
                viewModel = registrationvm,
                onNavigateToLogin = {
                    loginState = LoginState.LOGIN
                },
                showOnboarding = showOnboarding,
                onRegister = {
                    if(FirebaseAuth.getInstance().currentUser != null){
                        loginState = LoginState.LOGGED
                    }
                }
            )
        }
        LoginState.LOGGED -> {
            if (showOnboarding.value) {
                OnboardingUI({ showOnboarding.value = false }, { showOnboarding.value = false })
            } else {
                MainScreen(
                    showvm = showvm,
                    viewModelSport = viewModelSport,
                    viewModelReservation = viewModelReservation,
                    viewModelCourtFreeSlot = viewModelCourtFreeSlot,
                    onLogout = {
                        loginState = LoginState.LOGIN
                        FirebaseAuth.getInstance().signOut()
                    },
                    user = user,
                    viewModelCourts = viewModelCourts,
                    viewModelUsers = viewModelUsers,
                    startTutorial = { showOnboarding.value = true }
                )
            }
        }
    }


}

