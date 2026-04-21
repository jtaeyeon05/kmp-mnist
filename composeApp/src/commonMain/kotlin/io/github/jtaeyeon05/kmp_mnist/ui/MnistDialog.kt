package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleButton
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleSwitch
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleTextButton
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import kmp_mnist.composeapp.generated.resources.Res
import kmp_mnist.composeapp.generated.resources.github_white
import org.jetbrains.compose.resources.imageResource


@Composable
fun MnistDialog(
    viewModel: MnistViewModel = rememberSaveable { MnistViewModel() }
) {
    MnistDialogProvider(
        onDismissRequest = { viewModel.dismissDialog() },
        content = {
            MnistDialogContent(
                onDismissRequest = { viewModel.dismissDialog() },
                viewModel = viewModel,
            )
        },
    )
}

@Composable
private fun MnistDialogProvider(
    onDismissRequest: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDismissRequest() },
                    )
                },
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

@Composable
private fun MnistDialogContent(
    onDismissRequest: () -> Unit,
    viewModel: MnistViewModel = rememberSaveable { MnistViewModel() }
) {
    LocalLayoutConstraints.current.run {
        Column(
            modifier = Modifier
                .size(component.dialog)
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
                .border(
                    width = border(Scale.LARGE),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                .padding(border(Scale.LARGE))
                .pointerInput(Unit) {
                    detectTapGestures()
                }  // Consume Touch Gestures
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding(Scale.SMALL))
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = component.height(Scale.MEDIUM) + padding(Scale.SMALL)),
                    text = "KMP MNIST",
                    style = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = typography(Scale.MEDIUM).sp,
                        lineHeight = typography(Scale.MEDIUM).lineSp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                    ),
                )
                RectangleTextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onDismissRequest,
                ) {
                    Text(text = "X")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(
                        bottom = padding(Scale.SMALL),
                        start = padding(Scale.SMALL),
                        end = padding(Scale.SMALL),
                    ),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = rememberSaveable { "TODO ".repeat(20).map { it }.shuffled().joinToString("") },
                    style = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        fontSize = typography(Scale.MEDIUM).sp,
                        lineHeight = typography(Scale.MEDIUM).lineSp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                    ),
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = padding.inner(Scale.MEDIUM)),
                )
                MnistDialogRow(
                    textContent = {
                        Text("CellMap Size")
                        Help()
                    },
                    interactionContent = {
                        RectangleSwitch(
                            scale = Scale.SMALL,
                            checked = viewModel.cellSize == 28,
                            onCheckedChange = {
                                viewModel.updateCellSize(
                                    if (viewModel.cellSize == 28) 20 else 28
                                )
                            },
                            offContent = { Text(text = "20") },
                            onContent = { Text(text = "28") },
                        )
                    },
                )
                Spacer(modifier = Modifier.height(padding.inner(Scale.MEDIUM)))
                MnistDialogRow(
                    textContent = {
                        Text("Realtime Computation")
                        Help()
                    },
                    interactionContent = {
                        RectangleSwitch(
                            scale = Scale.SMALL,
                            checked = viewModel.realtimeMode,
                            onCheckedChange = { viewModel.toggleRealtimeMode() },
                        )
                    },
                )
                Spacer(modifier = Modifier.height(padding.inner(Scale.MEDIUM)))
                MnistDialogRow(
                    textContent = {
                        Text("SKaiNET")
                        Help()
                    },
                    interactionContent = {
                        RectangleButton(
                            scale = Scale.SMALL,
                            onClick = { /* TODO */ },
                        ) {
                            Image(
                                modifier = Modifier.size(component.height(Scale.SMALL) - 2 * padding(Scale.SMALL)),
                                bitmap = imageResource(Res.drawable.github_white),
                                contentDescription = "GitHub Icon",
                                contentScale = ContentScale.Fit,
                                filterQuality = FilterQuality.None,
                            )
                            Spacer(modifier = Modifier.width(padding.inner(Scale.SMALL)))
                            Text(text = "View")
                        }
                    },
                )
                Spacer(modifier = Modifier.height(padding.inner(Scale.MEDIUM)))
                MnistDialogRow(
                    textContent = {
                        Text("Source Code")
                        Help()
                    },
                    interactionContent = {
                        RectangleButton(
                            scale = Scale.SMALL,
                            onClick = { /* TODO */ },
                        ) {
                            Image(
                                modifier = Modifier.size(component.height(Scale.SMALL) - 2 * padding(Scale.SMALL)),
                                bitmap = imageResource(Res.drawable.github_white),
                                contentDescription = "GitHub Icon",
                                contentScale = ContentScale.Fit,
                                filterQuality = FilterQuality.None,
                            )
                            Spacer(modifier = Modifier.width(padding.inner(Scale.SMALL)))
                            Text(text = "View")
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun MnistDialogRow(
    modifier: Modifier = Modifier,
    textContent: @Composable RowScope.() -> Unit,
    interactionContent: @Composable RowScope.() -> Unit,
) {
    LocalLayoutConstraints.current.run {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(padding(Scale.SMALL)),
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides LocalTextStyle.current.copy(
                    textAlign = TextAlign.Start,
                    fontSize = typography(Scale.MEDIUM).sp,
                    lineHeight = typography(Scale.MEDIUM).lineSp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    ),
                ),
            ) {
                textContent()
            }
            Spacer(Modifier.weight(1f))
            interactionContent()
        }
    }
}

@Composable
private fun Help() {}
