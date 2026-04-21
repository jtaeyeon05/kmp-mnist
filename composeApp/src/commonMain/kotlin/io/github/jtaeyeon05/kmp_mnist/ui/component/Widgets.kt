package io.github.jtaeyeon05.kmp_mnist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.times
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import io.github.jtaeyeon05.kmp_mnist.ui.theme.TypographySize.Companion.times
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun RectangleButton(
    modifier: Modifier = Modifier,
    scale: Scale = Scale.MEDIUM,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    LocalLayoutConstraints.current.run {
        Row(
            modifier = modifier
                .height(component.height(scale))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    color = MaterialTheme.colorScheme.onBackground,
                    width = border(Scale.MEDIUM),
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                    )
                }
                .padding(padding(Scale.SMALL)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start,
                    fontSize = typography(scale).sp,
                    lineHeight = typography(scale).lineSp,
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
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    LocalLayoutConstraints.current.run {
        RectangleButton(
            modifier = modifier.size(component.height(scale)),
            scale = scale,
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
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    offContent: @Composable (BoxScope.() -> Unit)? = null,
    onContent: @Composable (BoxScope.() -> Unit)? = null,
) {
    // Hmm... I don't like this.
    LocalLayoutConstraints.current.run {
        val typographySize = 0.75f * typography(scale)
        Row(
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(component.height(scale))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    color = MaterialTheme.colorScheme.onBackground,
                    width = border(Scale.MEDIUM),
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onCheckedChange(!checked) },
                    )
                }
                .padding(padding.inner(scale)),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = typographySize.sp,
                    lineHeight = typographySize.lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                    textIndent = TextIndent(
                        firstLine = 1f / 12f * typographySize.sp,
                        restLine =  1f / 12f * typographySize.sp,
                    ),
                ),
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = (if (checked) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground).copy(alpha = 0.75f),
                    contentColor = if (checked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(min = 2.4f * typographySize.dp)
                            .padding(horizontal = padding.inner(scale))
                            .padding(start = 1f / 12f * typographySize.dp),  // Mona12 폰트의 오른쪽 여백에 대한 대응
                        contentAlignment = Alignment.Center,
                    ) {
                        if (offContent != null) offContent()
                    }
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = (if (checked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background).copy(alpha = 0.75f),
                    contentColor = if (checked) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(min = 2.4f * typographySize.lineDp)
                            .padding(horizontal = padding.inner(scale))
                            .padding(start = 1f / 12f * typographySize.dp),  // Mona12 폰트의 오른쪽 여백에 대한 대응
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
fun LoadingBox(
    modifier: Modifier = Modifier,
    scale: Scale = Scale.MEDIUM,
) {
    LocalLayoutConstraints.current.run {
        var rotateDegree by rememberSaveable { mutableStateOf(0f) }
        LaunchedEffect(Unit) {
            while (true) {
                rotateDegree = (rotateDegree + 90f) % 360f
                delay(500.milliseconds)
            }
        }

        Box(
            modifier = modifier
                .size(component.height(scale))
                .background(
                    color = MaterialTheme.colorScheme.background,
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 1f / 12f * typography(scale).dp) // Mona12 폰트의 오른쪽 여백에 대한 대응
                    .rotate(rotateDegree),
                text = "⟳",
                style = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = typography(scale).sp,
                    lineHeight = typography(scale).lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                ),
            )
        }
    }
}
