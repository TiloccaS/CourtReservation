package it.polito.mad.g17_lab3


import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import it.polito.mad.g17_lab3.components.Users
import it.polito.mad.g17_lab3.data.Reservation
import it.polito.mad.g17_lab3.ui.theme.G17lab3Theme
import it.polito.mad.g17_lab3.viewmodels.ViewModelReservation
import it.polito.mad.g17_lab3.viewmodels.ViewModelShowProfile
import kotlin.math.roundToInt
fun getImageUrlFromStorage(filePath: String, image_uri:MutableState<Uri?>) {
    val storageRef = FirebaseStorage.getInstance().reference
    val db=FirebaseFirestore.getInstance()
    var ref:String?=null

    val doc=db.document(filePath).get().addOnSuccessListener {
         ref=it.getString("image_url")
        //image_uri.value=ref!!.toUri()
        if(ref!=null){
            val imageRef = storageRef.child(ref!!)
            image_uri.value=Uri.parse(ref!!)

        }



    }


}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowProfile(navController: NavHostController,authid:String?=null, showvm: ViewModelShowProfile,viewModelReservation: ViewModelReservation?=null,reservaionId:String?=null, onLogout: () -> Unit) {
    val cont= LocalContext.current
    val scrollState = rememberScrollState()
    var showLoader = remember { mutableStateOf(false) }
    var showLoader2 = remember { mutableStateOf(false) }

    var hasLoaded = remember { mutableStateOf(false) }
    val choiche= remember {
        mutableStateOf<Boolean?>(null)
    }
    val user= remember {
        mutableStateOf<Users?>(null)
    }

    val reservation= remember {
        mutableStateOf<Reservation?>(null)
    }

    if(reservaionId!=null){
        if(viewModelReservation!=null){
            viewModelReservation.getReservation(reservaionId){
                if(it!=null){
                    reservation.value=it

                }
            }
        }

    }

    if(authid!=null ){
        showvm.getUser(authid!!){
            if(it!=null){
                user.value=it
                hasLoaded.value=true
                showLoader.value=false
            }
        }
    }
    else if(authid==null &&  FirebaseAuth.getInstance().currentUser!=null){
        showvm.getUser(FirebaseAuth.getInstance().currentUser!!.uid){
            if(it!=null){
                user.value=it
                hasLoaded.value=true
                showLoader.value=false
            }
        }
    }

    val image_uri= remember {
        mutableStateOf<Uri?>(null)
    }
    val db=FirebaseFirestore.getInstance()
    if(FirebaseAuth.getInstance().currentUser!=null){

        getImageUrlFromStorage("images/${FirebaseAuth.getInstance().currentUser!!.uid}",image_uri)
    }
    if (showLoader.value || showLoader2.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else if(user.value!=null) {

        Box(Modifier.fillMaxSize()) {
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            if(authid==null ){
                Button(
                    shape = CircleShape,
                    onClick = { navController.navigate("EditProfile") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                        .zIndex(1F)
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                            }
                        },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.padding( 5.dp)
                    )
                }
            }

            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .fillMaxHeight()
            ) {

                    if(reservation.value!=null && reservation.value!!.author_id==FirebaseAuth.getInstance().currentUser?.uid) {
                        if(reservation.value!!.confirmed_participants.contains(authid)){
                            choiche.value=true
                        }
                        if (choiche.value == null) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .wrapContentHeight(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.this_user_want_join)+" "+ reservation.value!!.date+ " " +reservation.value!!.time ,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(onClick = {
                                            showLoader2.value=true;
                                            navController.popBackStack()
                                            choiche.value = true;viewModelReservation?.let {
                                            it.confirm_user(authid!!, reservation, choiche,showLoader2, user = user.value!!,
                                                cont)
                                        }
                                        }) {
                                            Text(text = "Accetta")
                                        }
                                        Button(onClick = {
                                            showLoader2.value=true
                                            viewModelReservation!!.delete_user(authid!!,reservation.value!!,user=user.value, cont = cont
                                        ){
                                            showLoader.value=it
                                        }; navController.popBackStack();choiche.value=false;}) {
                                            Text(text = "Rifiuta")
                                        }
                                    }
                                }
                            }
                        } else if (choiche.value == true) {
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .wrapContentHeight(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.this_user_is_confirmed) + " "+ reservation.value!!.date + " " +reservation.value!!.time ,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 16.dp),
                                        color = colorResource(id = R.color.success)
                                    )

                                }
                            }
                        }
                    }

                
                
                ElevatedCard(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                            if (user.value!!.profileImage==""){
                                Image(

                                    painter = painterResource(id = R.drawable.profile_image_foreground),
                                    contentDescription = "profile image",
                                    Modifier
                                        .size(200.dp)
                                        .align(
                                            Alignment.CenterHorizontally
                                        )
                                        .padding(10.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape)
                                )
                            } else{
                                Image(

                                    painter = rememberAsyncImagePainter(model = Uri.parse(user.value!!.profileImage)),
                                    contentDescription = "profile image",
                                    Modifier
                                        .size(200.dp)
                                        .align(
                                            Alignment.CenterHorizontally
                                        )
                                        .padding(10.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color.Gray, CircleShape)
                                )}


                        Text(
                            user.value!!.username.toString(),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        if (user.value!!.visibleName) {
                            Text(
                                showvm.fullname,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .wrapContentHeight(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.profile_detail),
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        if (user.value!!.visibleMail && user.value!!.email != "") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.mail),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(28.dp)
                                )
                                Text(
                                    text = user.value!!.email,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        if (user.value!!.visiblePhone && user.value!!.phone != "") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.telephone),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(28.dp)
                                )
                                Text(
                                    text = user.value!!.phone.toString(),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        if (user.value!!.visibleBirthday && user.value!!.birthday != "") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.cake),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(28.dp)
                                )
                                Text(
                                    text = user.value!!.birthday.toString(),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        if (user.value!!.gender != "") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.gender),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(28.dp)
                                )
                                Text(
                                    text = user.value!!.gender.toString(),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = user.value!!.bio.toString(),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                if (user.value!!.visibleSports) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .height(170.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        val scrollSkill = rememberScrollState()

                        Column(
                            Modifier
                                .padding(10.dp)
                                .verticalScroll(scrollSkill)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Skills",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )


                            Row {
                                Text(
                                    text = "Football",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                StaticRatingBar(
                                    rating = user.value!!.football_rating,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                            Row {
                                Text(
                                    text = "Basket",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                StaticRatingBar(
                                    rating = user.value!!.basket_rating,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                            Row {
                                Text(
                                    text = "Tennis",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                StaticRatingBar(
                                    rating = user.value!!.tennis_rating,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }

                        }

                    }
                }

                if(authid==null){
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = onLogout
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "LogOut",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "Logout")
                            }
                        }
                    }
                }


            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    G17lab3Theme {
        Greeting("Android")
    }
}