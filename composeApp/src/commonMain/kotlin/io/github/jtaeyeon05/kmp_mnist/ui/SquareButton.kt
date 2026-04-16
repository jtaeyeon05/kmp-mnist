package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times


@Composable
fun SquareButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    LocalLayoutConstraints.current.run {
        Box(
            modifier = modifier
                .size(component.squareButton)
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
                .border(
                    color = MaterialTheme.colorScheme.onBackground,
                    width = border.medium,
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(start = 0.1f * typography.medium.dp),  // Mona12 폰트의 오른쪽 여백에 대한 대응
                text = text,
                style = LocalTextStyle.current.copy(
                    fontSize = typography.medium.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = typography.medium.lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                ),
            )
        }
    }
}
