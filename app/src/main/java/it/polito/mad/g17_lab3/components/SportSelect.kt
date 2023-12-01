package it.polito.mad.g17_lab3.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.mad.g17_lab3.R
import it.polito.mad.g17_lab3.enums.Sport


@Composable
fun SportSelect(selectedSport: MutableState<Sport>, setSelectedSport: (Sport) -> Unit) {

    val buttons = Sport.values().map {
        when (it) {
            Sport.BASKETBALL -> Triple("Basket", R.drawable.basket, Sport.BASKETBALL)
            Sport.FOOTBALL -> Triple("Football", R.drawable.football, Sport.FOOTBALL)
            Sport.TENNIS -> Triple("Tennis", R.drawable.tennis, Sport.TENNIS)
            else -> Triple(stringResource(id = R.string.all), 0, Sport.ALL)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(3.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        buttons.forEachIndexed { _, button ->
            Button(
                onClick = {
                    setSelectedSport(button.third)
                },
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 8.dp),
                colors = if (selectedSport.value == button.third) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                },
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (button.second != 0) {
                            Icon(
                                painter = painterResource(id = button.second),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                button.first,
                                modifier = Modifier.padding(top = 2.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
