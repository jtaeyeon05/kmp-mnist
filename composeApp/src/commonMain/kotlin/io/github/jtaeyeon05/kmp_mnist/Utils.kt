package io.github.jtaeyeon05.kmp_mnist

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.pow
import kotlin.math.round


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

fun Double.toPrecision(digits: Int): String {
    val factor = 10.0.pow(digits)
    val roundedValue = round(this * factor).toLong()

    return if (digits == 0) {
        "$roundedValue"
    } else if (digits > 0) {
        val fullString = "$roundedValue".padStart(digits + 1, '0')
        "${fullString.dropLast(digits)}.${fullString.takeLast(digits)}"
    } else {
        "${roundedValue * 10.0.pow(-digits).toLong()}"
    }
}

fun Float.toPrecision(digits: Int): String {
    return this.toDouble().toPrecision(digits)
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
