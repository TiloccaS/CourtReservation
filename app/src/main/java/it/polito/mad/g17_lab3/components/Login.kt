import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.viewmodels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onNavigateToRegistration: () -> Unit, onLogin: (FirebaseUser?) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        // Aggiungi una variabile di stato per memorizzare il messaggio di errore
        val errorMessage = viewModel.error

        OutlinedTextField(
            value = viewModel.emailState.value,
            singleLine = true,
            onValueChange = { viewModel.setEmail(it) },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = viewModel.passwordState.value,
            singleLine = true,
            onValueChange = { viewModel.setPassword(it) },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.padding(16.dp)
        )

        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = { viewModel.login(onLogin) },
                modifier = Modifier
                    .weight(1f)
                    .width(IntrinsicSize.Max)
            ) {
                Text(stringResource(R.string.login))
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = { onNavigateToRegistration() },
                modifier = Modifier
                    .weight(1f)
                    .width(IntrinsicSize.Max)
            ) {
                Text(stringResource(R.string.register))
            }
        }
    }
}



