package it.polito.mad.g17_lab3.components

import android.net.Uri
import android.provider.SyncStateContract.Columns
import android.widget.ImageButton
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.g17_lab3.ExpandableCard
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.ShowProfile
import it.polito.mad.g17_lab3.viewmodels.ViewModelUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ImageProfiles(navController: NavController,users:List<String>,viewModelUsers: ViewModelUsers,reservation_id:String){
    var list_users= remember {
        mutableListOf<Users>()
    }
    viewModelUsers.getListaComponenti {
        list_users.clear()
        list_users.addAll(it.filter {
                users.contains(it.auth_id)
        })
    }
    val b=(list_users.size/2)-1
    val db=FirebaseFirestore.getInstance()
    if(list_users.size>0){
            Column(modifier = Modifier.height(100.dp)){
                LazyRow(modifier = Modifier.weight(1f).padding(horizontal = 10.dp, vertical = 15.dp)) {

                    items(list_users) { item ->
                        ItemProfile(navController,user = item,reservation_id)
                        Spacer(Modifier.width(20.dp))

                    }
                }

            }




    }

    }

@Composable
fun ItemProfile(navController: NavController,user:Users,reservation_id: String){
    val db=FirebaseFirestore.getInstance()

val res=reservation_id.split("/")

    if(user.profileImage==""){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        IconButton(onClick = {navController.navigate("ExternalProfile/${user.auth_id}/${res[1]}")}, modifier = Modifier.size(50.dp)){
            Image(
                painter = painterResource(id = R.drawable.profile_image_foreground),
                contentDescription = "profile image",
                Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.LightGray, CircleShape)
            )
        }
        Text(
            text = user.username,
            modifier = Modifier.padding(top = 4.dp),
            color = Color.Black,
            fontSize = 10.sp
        )}
    }else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = {navController.navigate("ExternalProfile/${user.auth_id}/${res[1]}")}, modifier = Modifier.size(50.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(model = user.profileImage),
                    contentDescription = "profile image",
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                )
            }

            Text(
                text = user.username,
                modifier = Modifier.padding(top = 4.dp),
                color = Color.Black,
                fontSize = 12.sp
            )
        }
    }

}
