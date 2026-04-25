package io.github.jtaeyeon05.kmp_mnist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.stylusHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import io.github.jtaeyeon05.kmp_mnist.applyIf
import io.github.jtaeyeon05.kmp_mnist.tappable
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.imageResource
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun PixelImage(
    modifier: Modifier = Modifier,
    resource: DrawableResource,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = 1f,
    onClick: (() -> Unit)? = null,
) {
    Image(
        modifier = modifier.applyIf(
            condition = onClick != null,
            onTrue = {
                this
                    .pointerHoverIcon(PointerIcon.Hand)
                    .stylusHoverIcon(PointerIcon.Hand)
                    .tappable { onClick!!() }
            }
        ),
        bitmap = imageResource(resource),
        contentDescription = contentDescription,
        contentScale = contentScale,
        alpha = alpha,
        filterQuality = FilterQuality.None,
    )
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
