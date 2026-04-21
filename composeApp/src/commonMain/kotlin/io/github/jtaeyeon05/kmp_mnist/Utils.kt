package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput


val Alignment.horizontal: Alignment.Horizontal
    get() = when (this) {
        Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> Alignment.Start
        Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

val Alignment.vertical: Alignment.Vertical
    get() = when (this) {
        Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> Alignment.Top
        Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> Alignment.Bottom
        else -> Alignment.CenterVertically
    }

inline fun Modifier.applyIf(condition: Boolean, crossinline block: Modifier.() -> Modifier) = if (condition) this.block() else this

fun Modifier.tappable(onTap: PointerInputScope.(Offset) -> Unit) = this.pointerInput(Unit) {
    detectTapGestures(
        onTap = { onTap(it) }
    )
}

fun Modifier.consumePointer() = this.pointerInput(Unit) {
    detectTapGestures()
}
