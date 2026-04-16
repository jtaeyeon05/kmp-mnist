package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.jtaeyeon05.kmp_mnist.buildinfo.BuildInfo
import io.github.jtaeyeon05.kmp_mnist.ui.AppTheme
import io.github.jtaeyeon05.kmp_mnist.ui.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.rememberLayoutConstraints


@Composable
@Preview
fun App() {
    AppTheme {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val layoutConstraints = rememberLayoutConstraints(maxWidth, maxHeight)

            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                LocalLayoutConstraints provides layoutConstraints,
            ) {
                Screen()
            }
        }
    }
}

@Composable
fun Screen() {
    LocalLayoutConstraints.current.run {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(padding.large)
                    .size(component.cellBoard)
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = border.medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CellBoard",
                    fontSize = typography.medium.sp,
                    lineHeight = typography.medium.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier
                    .padding(padding.large)
                    .size(component.outputBoard)
                    .background(MaterialTheme.colorScheme.background)
                    .border(
                        width = border.medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    .align(if (screen.isVertical) Alignment.BottomCenter else Alignment.CenterEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "OutputBoard",
                    fontSize = typography.medium.sp,
                    lineHeight = typography.medium.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                modifier = Modifier
                    .padding(padding.small)
                    .align(Alignment.BottomEnd),
                text = "${BuildInfo.RELEASE_NAME} [${BuildInfo.RELEASE_CODE}]\n${BuildInfo.BUILD_NUMBER}",
                fontSize = typography.small.sp,
                lineHeight = typography.small.sp,
                textAlign = TextAlign.End,
            )
        }
    }
}
