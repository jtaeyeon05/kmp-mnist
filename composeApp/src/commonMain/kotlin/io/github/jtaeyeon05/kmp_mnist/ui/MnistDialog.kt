package io.github.jtaeyeon05.kmp_mnist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.window.Popup
import io.github.jtaeyeon05.kmp_mnist.consumePointer
import io.github.jtaeyeon05.kmp_mnist.tappable
import io.github.jtaeyeon05.kmp_mnist.ui.component.HelpTip
import io.github.jtaeyeon05.kmp_mnist.ui.component.PixelImage
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleButton
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleSwitch
import io.github.jtaeyeon05.kmp_mnist.ui.component.RectangleTextButton
import io.github.jtaeyeon05.kmp_mnist.ui.theme.LocalLayoutConstraints
import io.github.jtaeyeon05.kmp_mnist.ui.theme.Scale
import kmp_mnist.composeapp.generated.resources.Res
import kmp_mnist.composeapp.generated.resources.ic_github_white


@Composable
fun MnistDialog(
    viewModel: MnistViewModel = remember { MnistViewModel() }
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
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .tappable { onDismissRequest() },
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

@Composable
private fun MnistDialogContent(
    onDismissRequest: () -> Unit,
    viewModel: MnistViewModel = remember { MnistViewModel() }
) {
    LocalLayoutConstraints.current.run {
        val uriHandler = LocalUriHandler.current

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
                .consumePointer()
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
                        fontSize = typography(Scale.LARGE).sp,
                        lineHeight = typography(Scale.LARGE).lineSp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                    ),
                )
                RectangleTextButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    scale = Scale.LARGE,
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
                verticalArrangement = Arrangement.spacedBy(padding(Scale.SMALL))
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = """
                        KMP MNIST는 Kotlin Multiplatform 기반의 온디바이스 손글씨 숫자 인식 프로그램입니다. 하나의 코드베이스로 모바일(Android, iOS), 데스크톱(Windows, MacOS, Linux), 웹(JS, WasmJS)을 모두 지원합니다.
                          - 작동 과정
                          1. 모델 학습: Python 환경에서 학습된 CNN 모델을 GGUF 포맷으로 저장
                          2. 로컬 추론: Kotlin 환경에서 SKaiNET 기반 추론 파이프라인으로 로컬에서 MNIST 분류 수행
                          3. 멀티플랫폼 UI: Compose Multiplatform을 사용하여 모든 플랫폼에서 일관된 사용자 경험을 제공
                    """.trimIndent(),
                    style = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Start,
                        fontSize = typography(Scale.MEDIUM).sp,
                        lineHeight = typography(Scale.MEDIUM).lineSp,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.None
                        ),
                        textIndent = TextIndent(firstLine = typography(Scale.MEDIUM).sp),
                        lineBreak = LineBreak.Paragraph,
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))
                MnistDialogRow(
                    textContent = {
                        Text(text = "CellMap Size")
                        HelpTip(
                            popupAlignment = Alignment.CenterEnd,
                            popupContent = { Text(text = "MNIST 분류는 28*28 크기의 흑백 이미지를 요구합니다. 입력 이미지가 20*20인 경우, 외각에 4 픽셀의 여백을 추가하여 분류합니다.") },
                        )
                    },
                    interactionContent = {
                        RectangleSwitch(
                            scale = Scale.MEDIUM,
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
                MnistDialogRow(
                    textContent = {
                        Text(text = "Realtime Computation")
                        HelpTip(
                            popupAlignment = Alignment.CenterEnd,
                            popupContent = {
                                Text(
                                    text = """
                                        Realtime Computation을 활성화 시 사용자가 획을 긋는 동안 실시간으로 분류를 시도합니다. 비활성화 시에는 사용자가 획 작성이 끝난 후 분류를 시도합니다.
                                        * 웹 환경 권장사항: Kotlin/JavaScript 및 Kotlin/Wasm에서는 아직 Coroutine의 Dispatcher가 실험적으로 제공되어, 웹에서는 Realtime Computation를 활성화하지 않는 것을 추천합니다.
                                    """.trimIndent(),
                                )
                            },
                        )
                    },
                    interactionContent = {
                        RectangleSwitch(
                            scale = Scale.MEDIUM,
                            checked = viewModel.realtimeMode,
                            onCheckedChange = { viewModel.toggleRealtimeMode() },
                        )
                    },
                )
                MnistDialogRow(
                    textContent = {
                        Text(text = "SKaiNET")
                        HelpTip(
                            popupAlignment = Alignment.CenterEnd,
                            popupContent = { Text(text = "SKaiNET은 Kotlin Multiplatform 기반 딥러닝 프레임워크입니다.") },
                        )
                    },
                    interactionContent = {
                        RectangleButton(
                            scale = Scale.MEDIUM,
                            onClick = { uriHandler.openUri("https://github.com/SKaiNET-developers/SKaiNET") },
                        ) {
                            PixelImage(
                                modifier = Modifier.size(component.icon(Scale.SMALL)),
                                resource = Res.drawable.ic_github_white,
                                contentDescription = "GitHub Icon",
                            )
                            Spacer(modifier = Modifier.width(padding.inner(Scale.MEDIUM)))
                            Text(text = "View")
                        }
                    },
                )
                MnistDialogRow(
                    textContent = {
                        Text(text = "Source Code")
                        HelpTip(
                            popupAlignment = Alignment.CenterEnd,
                            popupContent = { Text(text = "Kotlin 코드(UI, SKaiNET 모델)와 Python 코드(기반 모델) 모두 확인할 수 있습니다.") },
                        )
                    },
                    interactionContent = {
                        RectangleButton(
                            scale = Scale.MEDIUM,
                            onClick = { uriHandler.openUri("https://github.com/jtaeyeon05/kmp-mnist") },
                        ) {
                            PixelImage(
                                modifier = Modifier.size(component.icon(Scale.SMALL)),
                                resource = Res.drawable.ic_github_white,
                                contentDescription = "GitHub Icon",
                            )
                            Spacer(modifier = Modifier.width(padding.inner(Scale.MEDIUM)))
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
                    fontSize = typography(Scale.LARGE).sp,
                    lineHeight = typography(Scale.LARGE).lineSp,
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
