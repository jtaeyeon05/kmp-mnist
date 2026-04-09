package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP MNIST",
    ) {
        App()
    }
}
