import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import it.polito.mad.g17_lab3.R

data class Notification(
    val id: String = "",
    val message: String,
    var is_read: Boolean,
    val recipient: String,
    val timestamp: Timestamp
)

@Composable
fun NotificationList(notifications: List<Notification>, notificationsToRead: MutableState<Int>) {
    var showUnreadOnly by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = showUnreadOnly,
                    onCheckedChange = { showUnreadOnly = it },
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.show_only_not_read))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                for (notification in notifications) {
                    if (showUnreadOnly && notification.is_read) {
                        continue
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notification.message,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Codice per contrassegnare la notifica come letta
                                        val firestore = FirebaseFirestore.getInstance()
                                        val notificationsCollection =
                                            firestore.collection("notifications")
                                        notificationsCollection.document(notification.id)
                                            .update(
                                                hashMapOf(
                                                    "is_read" to true // Nuovo valore del campo "is_read"
                                                ) as Map<String, Any>
                                            )
                                            .addOnSuccessListener {
                                                // L'aggiornamento è stato eseguito con successo
                                            }
                                            .addOnFailureListener { exception ->
                                                // Si è verificato un errore durante l'aggiornamento
                                                // Gestisci l'errore di conseguenza
                                            }
                                    },
                                    enabled = !notification.is_read
                                ) {
                                    Text(
                                        style = MaterialTheme.typography.bodySmall, text =  stringResource(R.string.mark_as_read))
                                }
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timestamp = formatTimestamp(notification.timestamp)
                            Text(
                                text = "Timestamp: $timestamp",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun PreviewNotificationList(
    notifications: List<Notification>,
    notificationsToRead: MutableState<Int>
) {
    NotificationList(notifications = notifications, notificationsToRead = notificationsToRead)
}


@RequiresApi(Build.VERSION_CODES.O)
fun translateDocumentToNotification(document: DocumentSnapshot): Notification {
    return Notification(
        id = document.id,
        message = document.getString("message") ?: "",
        recipient = document.getString("recipient") ?: "",
        is_read = document.getBoolean("is_read") ?: false,
        timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
    )
}

private fun formatTimestamp(timestamp: com.google.firebase.Timestamp): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val date = timestamp.toDate()
    return dateFormat.format(date)
}