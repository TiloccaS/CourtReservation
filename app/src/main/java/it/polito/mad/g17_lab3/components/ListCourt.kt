package it.polito.mad.g17_lab3

import CourtRating
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import it.polito.mad.g17_lab3.components.AddReservationComponent
import it.polito.mad.g17_lab3.data.RatingCourt
import it.polito.mad.g17_lab3.ui.theme.Shapes
import it.polito.mad.g17_lab3.viewmodels.Courts
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourtFreeSlot
import it.polito.mad.g17_lab3.viewmodels.ViewModelCourts
import it.polito.mad.g17_lab3.viewmodels.ViewSport
import androidx.compose.ui.Alignment.Companion as Alignment1


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun showList(
    viewModelCourtFreeSlot: ViewModelCourtFreeSlot,
    viewModel: ViewSport,
    viewModelCourt: ViewModelCourts, user: FirebaseUser?,
    navController: NavHostController,
) {

    val showLoader = remember { mutableStateOf(false) }
    val showFreeSlot = remember {
        mutableStateOf(false)
    }
    val courtId = remember {
        mutableStateOf<DocumentReference?>(null)
    }
    if (showLoader.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        if (!showFreeSlot.value) {
            Box {
                Column {
                    Row {
                        Text(
                            text = stringResource(id = R.string.our_courts),
                            fontWeight = FontWeight.Bold,
                            style = typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Row {
                        CourtList(
                            modifier = Modifier,
                            viewModel = viewModel,
                            viewModelCourtFreeSlot,
                            courtId = courtId,
                            showFreeSlot = showFreeSlot,
                            viewCourt = viewModelCourt,
                            body = {},
                            showLoader = showLoader
                        )
                    }
                }
            }
        } else {
            if(courtId.value!=null){
                AddReservationComponent(
                    viewModel, viewModelCourts = viewModelCourt, user,
                    navController = navController, showAvailabilityCourt = courtId.value, onBack = {showFreeSlot.value = false}
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CourtList(
    modifier: Modifier,
    viewModel: ViewSport,
    viewModelCourtFreeSlot: ViewModelCourtFreeSlot,
    viewCourt: ViewModelCourts,
    courtId: MutableState<DocumentReference?>,
    showFreeSlot: MutableState<Boolean>,
    body: @Composable () -> Unit,
    showLoader: MutableState<Boolean>
) {


    var courts = remember { mutableStateListOf<Courts>() }
    viewCourt.getListaComponenti(showLoader = showLoader) {
        courts.clear()
        courts.addAll(it)
    }


    Box(modifier) {
        LazyColumn {
            items(courts) { court ->
                ExpandableCard(
                    court = court,
                    viewModel = viewModel,
                    showFreeSlot,
                    courtId = courtId,
                    viewModelCourts = viewCourt
                )
                Divider()
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun StaticRatingBar(
    modifier: Modifier = Modifier, rating: Long
) {

    val size = 25.dp

    Row {
        for (i in 1..rating) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_star_24),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size),
                tint = Color(0xFFFFD700)
            )

        }
        if (rating < 5) {
            for (i in rating + 1..5) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.baseline_star_24
                    ),
                    contentDescription = "star",
                    modifier = modifier
                        .width(size)
                        .height(size),
                    tint = Color(0xFFA2ADB1)
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Long,
    size: Dp = 35.dp,
): Long {
    var ratingState by remember {
        mutableStateOf(rating)
    }

    var selected by remember {
        mutableStateOf(false)
    }
    val size by animateDpAsState(
        targetValue = if (selected) (size) else (size - 5.dp),
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        verticalAlignment = Alignment1.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1L..5L) {
            Icon(
                painter = painterResource(
                    id = R.drawable.baseline_star_24
                ),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                ratingState = i
                            }

                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
    return ratingState
}

@ExperimentalComposeUiApi
@Composable
fun CustomDialogReviewa(
    value: List<CourtRating>,
    modifier: Modifier,
    setShowDialog: (Boolean) -> Unit,
    setValue: (String) -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            modifier = Modifier
                .size(300.dp) // Imposta la dimensione desiderata
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(modifier) {

                LazyColumn(modifier= Modifier
                    .height(300.dp)
                    .padding(16.dp)) {

                    items(value) { review ->

                        itemReview(item = review)
                        Divider()
                    }
                }




            }

        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun itemReview(item: CourtRating) {
    Text(text = stringResource(id = R.string.author) + ": " + item.username)
    StaticRatingBar(rating = item.rating)
    Text(text = stringResource(id = R.string.review) + ": " + item.review)
}

@ExperimentalComposeUiApi
@Composable
fun CustomDialog(
    value: String,
    court: Courts,
    viewModel: ViewSport,
    setShowDialog: (Boolean) -> Unit,
    setValue: (String) -> Unit
) {

    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf(value) }
    val scrollState = rememberScrollState()


    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            color = Color.White,
        ) {
            var rat = 0L
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)

            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .wrapContentSize(Alignment.Center)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = "Set value",
                            style = typography.titleLarge,
                            modifier = Modifier.wrapContentSize(Alignment.Center)

                        )
                    }
                    Row {
                        rat = RatingBar(rating = rat)

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                color = colorResource(id = if (txtFieldError.value.isEmpty()) android.R.color.holo_green_light else android.R.color.holo_red_dark)
                            )
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text(text = stringResource(id = R.string.enter_value)) },
                        value = txtField.value,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            txtField.value = it
                        })

                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Button(
                            onClick = {
                                if (txtField.value.isEmpty()) {
                                    txtFieldError.value = "${R.string.field_cannot_be_empty}"


                                    return@Button
                                }

                                //viewModel.add(ratingCourt) TODO allineare ocn firebase

                                setValue(txtField.value)
                                setShowDialog(false)
                            }, shape = RoundedCornerShape(50.dp), modifier = Modifier.fillMaxWidth()

                        ) {
                            Text(text = stringResource(id = R.string.done))
                        }
                    }


                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpandableCard(
    court: Courts,
    viewModel: ViewSport,
    showFreeSlot: MutableState<Boolean>,
    courtId: MutableState<DocumentReference?>,
    viewModelCourts: ViewModelCourts
) {
    val showDialog = remember { mutableStateOf(false) }
    val showDialogReviews = remember {
        mutableStateOf(false)
    }
    var expendedState by remember {
        mutableStateOf(false)
    }
    var reviews= remember {
        mutableStateListOf<CourtRating>()
    }

    viewModelCourts.getListaReviews(){
        reviews.clear()
        it.forEach{
            if(it.court_id.path==court.getId()!!.path){
                reviews.add(it)
            }
        }
    }


    val rotationState by animateFloatAsState(targetValue = if (expendedState) 180f else 0f)
    Card(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize(
            animationSpec = tween(
                durationMillis = 300, easing = LinearOutSlowInEasing
            )
        ), shape = Shapes.medium, onClick = { expendedState = !expendedState }) {
        Row {

            Text(
                text = court.getName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(6f)
                    .padding(5.dp),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    expendedState = !expendedState
                }, modifier = Modifier
                    .alpha(ContentAlpha.medium)
                    .weight(1f)
                    .rotate(rotationState)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop Down Error"
                )
            }
        }

    }
    if (expendedState) {
        Text(
            text = court.getAddress(),
            fontSize = typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Normal,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(5.dp)
        )
        var avg = 0L

        val ratingCourt = reviews.map { it.rating }

        if (ratingCourt.isNotEmpty()) {
            avg =(ratingCourt.sum() / ratingCourt.size)
        }
        Row {
            StaticRatingBar(rating = avg)

            Text(text = "(${ratingCourt.size})")
            if (ratingCourt.isNotEmpty()) {
                IconButton(onClick = { showDialogReviews.value = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_right_24),
                        contentDescription = "show review"
                    )
                }
            }


        }
        if (showDialog.value) CustomDialog(
            value = "",
            court = court,
            viewModel = viewModel,
            setShowDialog = {
                showDialog.value = it
            }) {}
        TextButton(onClick = {
            showFreeSlot.value = true
            courtId.value = court.getId()

        }, border = BorderStroke(0.dp, Color.Transparent)) {
            Text(text = stringResource(id = R.string.show_availability), color = colorResource(id = R.color.green))
        }
        if (showDialogReviews.value) {
            CustomDialogReviewa(value = reviews, modifier = Modifier, setShowDialog = {
                showDialogReviews.value = it
            }) {

            }

        }



    }
}
