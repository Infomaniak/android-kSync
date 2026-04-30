package at.bitfire.davdroid.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.bitfire.davdroid.R

@Composable
fun KSyncTitle(
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.infomaniak_intro_welcome_logo_title),
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily(Font(R.font.arial_black)),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.alignByBaseline(),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.infomaniak_intro_welcome_logo_subtitle),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.alignByBaseline(),
        )
    }
}
