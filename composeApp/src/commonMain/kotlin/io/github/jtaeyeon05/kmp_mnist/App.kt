package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.jtaeyeon05.kmp_mnist.ui.theme.AppTheme
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.MnistScreen
import io.github.jtaeyeon05.kmp_mnist.ui.theme.rememberLayoutConstraints


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
                MnistScreen()
            }
        }
    }
}
