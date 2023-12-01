import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.polito.mad.g17_lab3.R


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SexSelect(gender:String?,onGenderSelected: (String?) -> Unit) {
    val sexOptions = listOf(stringResource(R.string.man), stringResource(R.string.woman),stringResource(R.string.other))
    var selectedSex by remember { mutableStateOf("") }
    var expanded = remember { mutableStateOf(false) }


    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value },
    ) {
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            sexOptions.forEach { selectionOption ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = { selectedSex=selectionOption;
                        onGenderSelected(selectionOption);
                        expanded.value=false},
                    contentPadding = androidx.compose.material3.ExposedDropdownMenuDefaults.ItemContentPadding,

                    )

            }
        }
        OutlinedTextField(
            modifier=Modifier.padding(5.dp),
            singleLine = true,
            value = gender.toString(),
            onValueChange = { onGenderSelected(it) },
            colors =  TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                unfocusedBorderColor = Color.DarkGray
            ),
            readOnly = true,
            trailingIcon = {
                androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            shape = RoundedCornerShape(5.dp),
            label = {
                Text(
                    text = stringResource(R.string.gender)
                )
            })
    }
}