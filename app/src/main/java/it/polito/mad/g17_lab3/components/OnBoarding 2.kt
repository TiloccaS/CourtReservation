package it.polito.mad.g17_lab3.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import it.polito.mad.g17_lab3.R

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun OnboardingUI(
    onGettingStartedClick:()->Unit,
    onSkipClicked:()->Unit) {
    val pagerState = rememberPagerState(pageCount = 6)

    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(shape = RoundedCornerShape(20.dp) ,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 8.dp),onClick = onSkipClicked,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = colorResource(R.color.purple_500),
                contentColor = Color.White)) {
            Text(text = stringResource(id = R.string.skip_tutorial))
        }

        HorizontalPager(state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) ) { page ->

            PageUI(page = onboardPages[page])
        }

        HorizontalPagerIndicator(pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = colorResource(R.color.purple_500)
        )

        AnimatedVisibility(visible = pagerState.currentPage == 5 ) {
            OutlinedButton(shape = RoundedCornerShape(20.dp) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),onClick = onGettingStartedClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colorResource(R.color.purple_500),
                    contentColor = Color.White)) {
                Text(text = stringResource(id = R.string.join_teamfinder))
            }
        }

    }
}

@Composable
fun PageUI(page: Page) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier.size(page.imageSize.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = page.title),
            fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = page.description),
            textAlign = TextAlign.Center,fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

    }
}


val onboardPages = listOf(
    Page(
        R.string.step_1_title,
        R.string.step_1_body,
        R.drawable.onboarding_1,
        400
    ),
    Page(
        R.string.step_2_title,
        R.string.step_2_body,
        R.drawable.onboarding_2,
        400
    ),
    Page(
        R.string.step_3_title,
        R.string.step_3_body,
        R.drawable.onboarding_3,
        400
    ),
    Page(
        R.string.step_4_title,
        R.string.step_4_body,
        R.drawable.onboarding_4,
        400
    ),
    Page(
        R.string.step_5_title,
        R.string.step_5_body,
        R.drawable.onboarding_5,
        400
    ),
    Page(
        R.string.step_6_title,
        R.string.step_6_body,
        R.drawable.onboarding_6,
        400
    )
)

data class Page(val title: Int,
                val description: Int,
                @DrawableRes val image:Int,
                val imageSize: Int)