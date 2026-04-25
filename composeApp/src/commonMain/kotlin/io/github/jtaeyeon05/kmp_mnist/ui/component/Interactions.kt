package io.github.jtaeyeon05.kmp_mnist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import io.github.jtaeyeon05.kmp_mnist.applyIf
import io.github.jtaeyeon05.kmp_mnist.consumePointer
import io.github.jtaeyeon05.kmp_mnist.horizontal
import io.github.jtaeyeon05.kmp_mnist.tappable
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import io.github.jtaeyeon05.kmp_mnist.vertical
import kmp_mnist.composeapp.generated.resources.Res
import kmp_mnist.composeapp.generated.resources.ic_question_circle_white
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.min


@Composable
fun RectangleButton(
    modifier: Modifier = Modifier,
    scale: Scale = Scale.MEDIUM,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    LocalLayoutConstraints.current.run {
        val enabledColor = MaterialTheme.colorScheme.onBackground
        val disabledColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f).compositeOver(MaterialTheme.colorScheme.background)
        Row(
            modifier = modifier
                .height(component.height(scale))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    color = if (enabled) enabledColor else disabledColor,
                    width = border(Scale.MEDIUM),
                )
                .applyIf(
                    condition = enabled,
                    onTrue = {
                        this
                            .pointerHoverIcon(PointerIcon.Hand)
                            .stylusHoverIcon(PointerIcon.Hand)
                            .tappable { onClick() }
                    },
                )
                .padding(padding(Scale.SMALL)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (enabled) enabledColor else disabledColor,
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start,
                    fontSize = 0.75f * typography(scale).sp,
                    lineHeight = 0.75f * typography(scale).lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                ),
            ) {
                content()
            }
        }
    }
}

@Composable
fun RectangleTextButton(
    modifier: Modifier = Modifier,
    scale: Scale = Scale.MEDIUM,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    LocalLayoutConstraints.current.run {
        RectangleButton(
            modifier = modifier.size(component.height(scale)),
            scale = scale,
            enabled = enabled,
            onClick = onClick,
        ) {
            Spacer(modifier = Modifier.width(1f / 12f * typography(scale).dp))   // Mona12 폰트의 오른쪽 여백에 대한 대응
            content()
        }
    }
}

@Composable
fun RectangleSwitch(
    modifier: Modifier = Modifier,
    scale: Scale = Scale.MEDIUM,
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    offContent: @Composable (BoxScope.() -> Unit)? = null,
    onContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    LocalLayoutConstraints.current.run {
        val enabledColor = MaterialTheme.colorScheme.onBackground
        val disabledColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f).compositeOver(MaterialTheme.colorScheme.background)
        val checkedColor = MaterialTheme.colorScheme.onBackground
        val uncheckedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.50f).compositeOver(MaterialTheme.colorScheme.background)
        Row(
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(component.height(scale))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    color = if (enabled) enabledColor else disabledColor,
                    width = border(Scale.MEDIUM),
                )
                .applyIf(
                    condition = enabled,
                    onTrue = {
                        this
                            .pointerHoverIcon(PointerIcon.Hand)
                            .stylusHoverIcon(PointerIcon.Hand)
                            .tappable { onCheckedChange(!checked) }
                    },
                )
                .padding(padding.inner(scale)),
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = typography(scale).sp,
                    lineHeight = typography(scale).lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                ),
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = if (checked) MaterialTheme.colorScheme.background else (if (enabled) uncheckedColor else disabledColor),
                    contentColor = if (checked) (if (enabled) enabledColor else disabledColor) else MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(min = 2f * typography(scale).dp)
                            .padding(horizontal = padding.inner(scale))
                            .padding(start = 1f / 12f * typography(scale).dp),  // Mona12 폰트의 오른쪽 여백에 대한 대응
                        contentAlignment = Alignment.Center,
                    ) {
                        if (offContent != null) offContent()
                    }
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = if (checked) (if (enabled) checkedColor else disabledColor) else MaterialTheme.colorScheme.background,
                    contentColor = if (checked) MaterialTheme.colorScheme.background else (if (enabled) enabledColor else disabledColor),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(min = 2f * typography(scale).dp)
                            .padding(horizontal = padding.inner(scale))
                            .padding(start = 1f / 12f * typography(scale).dp),  // Mona12 폰트의 오른쪽 여백에 대한 대응
                        contentAlignment = Alignment.Center
                    ) {
                        if (onContent != null) onContent()
                    }
                }
            }
        }
    }
}

@Composable
fun HelpTip(
    tipModifier: Modifier = Modifier,
    tipResource: DrawableResource = Res.drawable.ic_question_circle_white,
    tipContentDescription: String? = "HelpTip",
    tipAlpha: Float = 0.5f,

    popupModifier: Modifier = Modifier,
    popupAlignment: Alignment = Alignment.TopCenter,
    popupWidthLimits: Boolean = true,
    popupMargin: Dp = LocalLayoutConstraints.current.run { padding(Scale.SMALL) },
    popupContent: @Composable () -> Unit,
) {
    LocalLayoutConstraints.current.run {
        var showPopup by rememberSaveable { mutableStateOf(false) }

        val density = LocalDensity.current
        var windowSize by remember { mutableStateOf(IntSize.Zero) }
        var popupSize by remember { mutableStateOf(IntSize.Zero) }
        var imageSize by remember { mutableStateOf(IntSize.Zero) }
        var imagePosition by remember { mutableStateOf(IntOffset.Zero) }

        PixelImage(
            modifier = tipModifier
                .size(component.icon(Scale.SMALL))
                .onGloballyPositioned {
                    imageSize = it.size
                    imagePosition = IntOffset(
                        x = it.positionInWindow().x.toInt(),
                        y = it.positionInWindow().y.toInt()
                    )
                },
            resource = tipResource,
            contentDescription = tipContentDescription,
            alpha = tipAlpha,
            onClick = { showPopup = true },
        )

        if (showPopup) {
            Popup(
                onDismissRequest = { showPopup = false },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { windowSize = it }
                        .tappable { showPopup = false }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == PointerEventType.Scroll) {
                                        showPopup = false
                                    }
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { _, _ ->
                                showPopup = false
                            }
                        },
                ) {
                    Box(
                        modifier = popupModifier
                            .consumePointer()
                            .onSizeChanged { popupSize = it }
                            .offset {
                                val x = when (popupAlignment.horizontal) {
                                    Alignment.Start -> imagePosition.x - popupSize.width  // Parent.Start == Popup.End
                                    Alignment.End -> imagePosition.x + imageSize.width  // Parent.End == Popup.Start
                                    else -> imagePosition.x + imageSize.width / 2 - popupSize.width / 2
                                }
                                val y = when (popupAlignment.vertical) {
                                    Alignment.Top -> imagePosition.y - popupSize.height - popupSize.height  // Parent.Top == Popup.Bottom
                                    Alignment.Bottom -> imagePosition.y + imageSize.height  // Parent.Bottom == Popup.Top
                                    else -> imagePosition.y + imageSize.height / 2 - popupSize.height / 2
                                }

                                IntOffset(
                                    x = x.coerceIn(0 .. windowSize.width - popupSize.width),
                                    y = y.coerceIn(0 .. windowSize.height - popupSize.height),
                                )
                            }
                            .applyIf(
                                condition = popupWidthLimits,
                                onTrue = {
                                    val maxWidth = when (popupAlignment.horizontal) {
                                        Alignment.Start -> imagePosition.x
                                        Alignment.End -> windowSize.width - imagePosition.x - imageSize.width
                                        else -> {
                                            val imageCenter = imagePosition.x + imageSize.width / 2
                                            2 * min(imageCenter, windowSize.width - imageCenter)
                                        }
                                    }

                                    this
                                        .alpha(if (maxWidth > 0f) 1f else 0f)
                                        .widthIn(max = density.run { maxWidth.toDp() })
                                }
                            )
                            .padding(popupMargin)
                            .background(MaterialTheme.colorScheme.surfaceBright)
                            .padding(padding(Scale.SMALL))
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            LocalTextStyle provides LocalTextStyle.current.copy(
                                textAlign = when (popupAlignment.horizontal) {
                                    Alignment.Start -> TextAlign.End
                                    Alignment.End -> TextAlign.Start
                                    else -> TextAlign.Center
                                },
                                fontSize = typography(Scale.SMALL).sp,
                                lineHeight = typography(Scale.SMALL).lineSp,
                                lineHeightStyle = LineHeightStyle(
                                    alignment = LineHeightStyle.Alignment.Center,
                                    trim = LineHeightStyle.Trim.None
                                ),
                            ),
                        ) {
                            popupContent()
                        }
                    }
                }
            }
        }
    }
}
