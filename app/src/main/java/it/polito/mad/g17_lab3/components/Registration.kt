package it.polito.mad.g17_lab3.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.viewmodels.RegistrationViewModel


@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel, onNavigateToLogin: () -> Unit, showOnboarding: MutableState<Boolean>, onRegister: () -> Unit) {
val cont= LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = stringResource(R.string.registration),
                style = LocalTextStyle.current.copy(
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.Bold
                ),
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
                value = viewModel.usernameState.value,
                singleLine = true,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text(stringResource(R.string.username)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = viewModel.nameState.value,
                singleLine = true,
                onValueChange = { viewModel.setName(it) },
                label = { Text(stringResource(R.string.name)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = viewModel.surnameState.value,
                singleLine = true,
                onValueChange = { viewModel.setSurname(it) },
                label = { Text(stringResource(R.string.surname)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
            OutlinedTextField(
                value = viewModel.confirmPasswordState.value,
                singleLine = true,
                onValueChange = { viewModel.setConfirmPassword(it) },
                label = { Text(stringResource(R.string.confirmPassword))},
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
                    onClick = { viewModel.register (cont){
                        onRegister()
                        showOnboarding.value = true
                    }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .width(IntrinsicSize.Max)
                ) {
                    Text(stringResource(R.string.register))
                }

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = { onNavigateToLogin() },
                    modifier = Modifier
                        .weight(1f)
                        .width(IntrinsicSize.Max)
                ) {
                    Text(stringResource(R.string.login))
                }
            }
        }
    }

}