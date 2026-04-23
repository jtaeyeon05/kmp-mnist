package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kmp_mnist.composeapp.generated.resources.Res
import kmp_mnist.composeapp.generated.resources.ic_kmp_mnist
import org.jetbrains.compose.resources.painterResource


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP MNIST",
        icon = painterResource(Res.drawable.ic_kmp_mnist),
    ) {
        App()
    }
}
