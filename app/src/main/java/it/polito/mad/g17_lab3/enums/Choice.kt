package it.polito.mad.g17_lab3.enums

import androidx.compose.ui.res.stringResource
import it.polito.mad.g17_lab3.R

enum class Choice(val choice: Int) {
    ADD(R.string.add), EDIT(R.string.edit), DELETE(R.string.delete)
}