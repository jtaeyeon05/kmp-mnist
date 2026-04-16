package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.ComposeViewport
import kmp_mnist.composeapp.generated.resources.Mona12
import kmp_mnist.composeapp.generated.resources.Mona12_Bold
import kmp_mnist.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.configureWebResources
import org.jetbrains.compose.resources.preloadFont


@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() {
    configureWebResources {
        resourcePathMapping { path -> "./$path" }
    }

    ComposeViewport(viewportContainerId = "compose-root") {
        val mona12Regular by preloadFont(
            resource = Res.font.Mona12,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        )
        val mona12Bold by preloadFont(
            resource = Res.font.Mona12_Bold,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
        )

        if (mona12Regular != null && mona12Bold != null) {
            LaunchedEffect(Unit) {
                showCompose()
                stopLoader()
            }
        }

        App()
    }
}
