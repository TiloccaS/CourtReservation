import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.ui.res.stringResource
import it.polito.mad.g17_lab3.R


@Composable
fun DataPicker(birthday: String, setBirthDay: (String?) -> Unit) {

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf(birthday) }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            setBirthDay(mDate.value)
        }, mYear, mMonth, mDay
    )

    OutlinedTextField(
        singleLine = true,
        modifier = Modifier
            .padding(5.dp)
            .clickable { },

        value = mDate.value,
        onValueChange = { setBirthDay(it) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,

        ),
        trailingIcon = {
            IconButton(onClick = { mDatePickerDialog.show() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        },
        shape = RoundedCornerShape(5.dp),
        label = {
            Text(
stringResource(id = R.string.birthday)            )
        }

    )
}
