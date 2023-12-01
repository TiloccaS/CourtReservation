package it.polito.mad.g17_lab3.components

import DataPicker
import SexSelect
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.viewmodels.ViewModelShowProfile
import java.io.File
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditProfile(navController: NavHostController, showvm: ViewModelShowProfile) {
    val scrollState = rememberScrollState()
    var hasLoaded = remember { mutableStateOf(false) }
    var showLoader = remember { mutableStateOf(false) }
    var showLoader2 = remember { mutableStateOf(false) }

    val user= remember {
        mutableStateOf<Users?>(null)
    }

    var nameState by remember { mutableStateOf(showvm.name) }
    var surnameState by remember { mutableStateOf(showvm.surname) }
    var userState by remember { mutableStateOf(showvm.user) }
    var mailState by remember { mutableStateOf(showvm.mail) }
    var phoneState by remember { mutableStateOf(showvm.phone) }
    val (genderState, setGenderState) = remember { mutableStateOf(showvm.gender) }
    val (birthdayState, setBirthDayState) = remember {
        mutableStateOf(showvm.birthday)
    }

    if (birthdayState == null) {
        setBirthDayState(
            java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
        )
    }

    var bioState by remember { mutableStateOf(showvm.bio) }

    val (imageState,setIsState) = remember { mutableStateOf(showvm.photo) }
    val (image_uri,set_uri)= remember {
        mutableStateOf<Uri?>(null)
    }
    var expanded by remember {
        mutableStateOf(false)
    }


    var footballSkill by remember { mutableStateOf(showvm.football[1]) }
    var basketSkill by remember { mutableStateOf(showvm.basket[1]) }
    var tennisSkill by remember { mutableStateOf(showvm.tennis[1]) }
    val (visibleName, setVisibleName) = remember { mutableStateOf(showvm.showname) }
    val (visibleMail, setVisibleMail) = remember { mutableStateOf(showvm.showmail) }
    val (visibleSports, setVisibleSport) = remember { mutableStateOf(showvm.showsports) }
    val (visiblePhone, setVisiblePhone) = remember { mutableStateOf(showvm.showphone) }
    val (visibleBirthday, setVisibleBirthday) = remember { mutableStateOf(showvm.showbirthday) }
    if (!hasLoaded.value) {
        showvm.load(showLoader, hasLoaded)
        showvm.getUser(FirebaseAuth.getInstance().currentUser!!.uid){
            if(it!=null){

                user.value=it


                nameState=user.value!!.name
                surnameState=user.value!!.surname
                userState=user.value!!.username
                mailState=user.value!!.email
                phoneState=user.value!!.phone
                setGenderState(user.value!!.gender)
                setBirthDayState(user.value!!.birthday)
                footballSkill=user.value!!.football_rating
                basketSkill=user.value!!.basket_rating
                tennisSkill=user.value!!.tennis_rating
                setVisibleName(user.value!!.visibleName)
                setVisiblePhone(user.value!!.visiblePhone)
                setVisibleSport(user.value!!.visibleSports)
                setVisibleMail(user.value!!.visibleMail)
                setVisibleBirthday(user.value!!.visibleBirthday)
                setIsState(user.value!!.profileImage)
                hasLoaded.value=true

                showLoader.value=false
            }
        }
    }

    if (showLoader.value || showLoader2.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        ) {
            Column {
                Column(
                    Modifier
                        .verticalScroll(scrollState)
                        .background(color = colorResource(id = R.color.off_white))
                ) {

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .height(300.dp), elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)

                    ) {


                        ImagePicker(imageState,
                            setIsState = setIsState,
                            user,
                            Modifier.align(Alignment.CenterHorizontally),
                            set_uri
                        )

                    }

                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .wrapContentHeight(), elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column {
                            Row {
                                OutlinedTextField(
                                    modifier = Modifier.padding(5.dp),
                                    singleLine = true,
                                    value = nameState.toString(),
                                    onValueChange = { nameState = it },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(5.dp),
                                    label = {
                                        Text(
                                            text = stringResource(R.string.name)                                        )
                                    })
                                CustomSwitch(
                                    height = 50.dp,
                                    visible = visibleName,
                                    setVisible = setVisibleName
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))


                            OutlinedTextField(
                                modifier = Modifier.padding(5.dp),
                                singleLine = true,
                                value = surnameState.toString(),
                                onValueChange = { surnameState = it },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(5.dp),
                                label = {
                                    Text(
                                        text = stringResource(R.string.surname)
                                    )
                                })
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                modifier = Modifier.padding(5.dp),
                                singleLine = true,
                                value = userState.toString(),
                                onValueChange = { userState = it },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(5.dp),
                                label = {
                                    Text(
                                        text = stringResource(R.string.username)                                    )
                                })
                            Spacer(modifier = Modifier.height(10.dp))

                            Row {
                                OutlinedTextField(
                                    modifier = Modifier.padding(5.dp),
                                    singleLine = true,
                                    value = mailState.toString(),
                                    onValueChange = { mailState = it },
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(5.dp),
                                    label = {
                                        Text(
                                            text = stringResource(R.string.email)
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                )
                                CustomSwitch(
                                    height = 50.dp,
                                    visible = visibleMail,
                                    setVisible = setVisibleMail
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row {
                                OutlinedTextField(
                                    modifier = Modifier.padding(5.dp),
                                    singleLine = true,
                                    value = phoneState.toString(),
                                    onValueChange = { phoneState = it },
                                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent),
                                    shape = RoundedCornerShape(5.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    label = {
                                        Text(
                                            text = stringResource(R.string.phone_number)
                                        )
                                    })
                                CustomSwitch(
                                    height = 50.dp,
                                    visible = visiblePhone,
                                    setVisible = setVisiblePhone
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            SexSelect(
                                gender = genderState,
                                onGenderSelected = setGenderState as (String?) -> Unit
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row {

                                DataPicker(birthdayState!!, setBirthDayState as (String?) -> Unit)

                                CustomSwitch(
                                    height = 50.dp,
                                    visible = visibleBirthday,
                                    setVisible = setVisibleBirthday
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .clickable { },

                                value = bioState.toString(),
                                singleLine = true,
                                onValueChange = { bioState = it },
                                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent),
                                shape = RoundedCornerShape(5.dp),
                                label = {
                                    Text(
                                        text = stringResource(R.string.bio)
                                    )
                                })
                            Spacer(modifier = Modifier.height(10.dp))


                            Box {
                                Row {
                                    Button(onClick = { expanded = true }) {
                                        Row {
                                            Text(
                                                text = stringResource(R.string.select_sports),
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = stringResource(R.string.add_sport)
                                            )
                                        }
                                    }
                                    CustomSwitch(
                                        height = 50.dp,
                                        visible = visibleSports,
                                        setVisible = setVisibleSport
                                    )
                                }


                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(onClick = { }) {
                                        Text(
                                            text = stringResource(R.string.football_label),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        footballSkill =
                                            it.polito.mad.g17_lab3.RatingBar(
                                                rating = footballSkill as Long,
                                                size = 25.dp
                                            )
                                    }
                                    DropdownMenuItem(onClick = { }) {
                                        Text(
                                            text = stringResource(R.string.basket_label),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        basketSkill =
                                            it.polito.mad.g17_lab3.RatingBar(
                                                rating = basketSkill as Long,
                                                size = 25.dp
                                            )
                                    }
                                    DropdownMenuItem(onClick = { }) {
                                        Text(
                                            text = stringResource(R.string.tennis_label),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        tennisSkill =
                                            it.polito.mad.g17_lab3.RatingBar(
                                                rating = tennisSkill as Long,
                                                size = 25.dp
                                            )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.Center) {

                                OutlinedButton(
                                    onClick = { navController.navigate("Profile") },
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1F)
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(text = stringResource(R.string.cancel))
                                }


                                Button(
                                    onClick = {
                                        showLoader2.value=true
                                        if(image_uri!=null){
                                            uploadImageToFirebase(imageUri = image_uri,showvm)

                                        }
                                        showvm.setParams(
                                            nameState.toString(),
                                            surnameState.toString(),
                                            userState.toString(),
                                            genderState.toString(),
                                            mailState.toString(),
                                            bioState.toString(),
                                            phoneState.toString(),
                                            birthdayState.toString(),
                                            footballSkill as Long,
                                            basketSkill as Long,
                                            tennisSkill as Long,
                                            imageState,
                                            visibleName,
                                            visibleMail,
                                            visiblePhone,
                                            visibleBirthday,
                                            visibleSports,
                                            showvm.auth_id
                                        ){
                                            showLoader2.value=it
                                        }; navController.navigate("Profile")
                                    },

                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1F)
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(text = stringResource(R.string.confirm))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class ComposeFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file
            )
        }
    }
}
fun uploadImageToFirebase(imageUri: Uri?,viewModelShowProfile: ViewModelShowProfile) {
    if (imageUri == null) {
        // Image is not selected
        return
    }

    val storageRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("${FirebaseAuth.getInstance().currentUser!!.uid}").child("my_image.jpg")


    val uploadTask: UploadTask = storageRef.putFile(imageUri)
    uploadTask.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Image uploaded successfully
            val downloadUrl = task.result?.storage?.downloadUrl?.addOnSuccessListener {
                val data = hashMapOf(
                    "image_url" to it.toString()
                )

                viewModelShowProfile.upgradeImage(it.toString())
                val db=FirebaseFirestore.getInstance()

                db.document("images/${FirebaseAuth.getInstance().currentUser!!.uid}").set(data)
            }

            // Get the download URL of the uploaded image
        } else {
            // Error uploading image
            val exception = task.exception
            // Handle the exception
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    imageState:String,
    setIsState:(String)->Unit,
    user: MutableState<Users?>,
    modifier: Modifier = Modifier,
    set_uri:(Uri?)->Unit
) {
    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null

            if(uri!=null){
                imageUri = uri
                setIsState(imageUri.toString())
                set_uri(imageUri)
                user.value!!.profileImage=imageUri.toString()
        }



        }
    )


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
                hasImage = success

        }
    )

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )


    val context = LocalContext.current
    Box {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {

            IconButton(
                onClick = {  set_uri(null)
                    setIsState("")
                    user.value!!.profileImage=""
                          imageUri=null},
                modifier = Modifier
                    .offset(x = 270.dp, y = (10).dp), // Opzionale: per spostare il pulsante in alto a destra
                colors = IconButtonDefaults.iconButtonColors(Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            if (hasImage && imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "image",
                    modifier = Modifier
                        .size(180.dp)
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            } else {
                if (imageState=="") {
                    Image(

                        painter = painterResource(id = R.drawable.profile_image_foreground),
                        contentDescription = "profile image",
                        Modifier
                            .size(180.dp)
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(model = user.value!!.profileImage),
                        contentDescription = "image",
                        modifier = Modifier
                            .size(180.dp)
                            .align(
                                Alignment.CenterHorizontally
                            )
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        imagePicker.launch("image/*")
                    }, modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = stringResource(R.string.select_image),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))

                Button(
                    onClick = {

                        val uri = ComposeFileProvider.getImageUri(context)
                        //imageUri = uri
                        cameraPermissionState.launchPermissionRequest()
                        if (cameraPermissionState.hasPermission)
                            cameraLauncher.launch(uri)

                        if(uri!=null){
                            setIsState(imageUri.toString())
                            user.value!!.profileImage=imageUri.toString()
                            imageUri=uri
                            set_uri(imageUri)
                        }
                        },Modifier.wrapContentSize()

                ) {
                    Text(
                        text = stringResource(R.string.take_photo),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

            }
        }
    }
}

@Composable
fun CustomSwitch(
    width: Dp = 72.dp,
    height: Dp = 40.dp,
    borderWidth: Dp = 4.dp,
    iconInnerPadding: Dp = 4.dp,
    visible: Boolean,
    setVisible: (Boolean) -> Unit
) {

    // this is to disable the ripple effect
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // state of the switch
    var switchOn by remember {
        mutableStateOf(visible)
    }

    // for moving the thumb
    //val alignment by animateAlignmentAsState(if (switchOn) 1f else -1f)

    // outer rectangle with border
    Box(
        modifier = Modifier
            .size(width = width, height = height)
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                switchOn = !switchOn;
                setVisible(!visible)
            }
            .border(
                width = borderWidth,
                color = Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {


        // thumb with icon
        Icon(
            painter = if (switchOn) painterResource(id = R.drawable.visibility) else painterResource(
                id = R.drawable.not_visibility
            ),
            contentDescription = if (switchOn) "Enabled" else "Disabled",
            modifier = Modifier
                .size(size = 35.dp)
                .padding(all = iconInnerPadding)
                .align(BiasAlignment(horizontalBias = 0f, verticalBias = 0f)),

            )

    }


}