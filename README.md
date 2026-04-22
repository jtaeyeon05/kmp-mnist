# kmp-mnist

<div align=right>
        jtaeyeon05 (2026/04)
</div>

> Kotlin, Kotlin Multiplatform


KMP MNIST는 Kotlin Multiplatform 기반의 온디바이스 손글씨 숫자 인식 프로그램입니다. 하나의 코드베이스로 모바일(Android, iOS), 데스크톱(Windows, MacOS, Linux), 웹(JS, WasmJS)을 모두 지원합니다.

- 작동 과정
  1. 학습: Python 환경에서 학습한 CNN 모델을 GGUF 포멧으로 저장
  2. 로컬 추론: Kotlin 환경에서 [SKaiNET](https://github.com/SKaiNET-developers/SKaiNET) 기반 추론 파이프라인으로 로컬에서 MNIST 분류 수행
  3. 멀티플랫폼 UI: Compose Multiplatform을 사용하여 모든 플랫폼에서 일관된 사용자 경험을 제공

[kmp-mnist.xodus.lol](https://kmp-mnist.xodus.lol)
