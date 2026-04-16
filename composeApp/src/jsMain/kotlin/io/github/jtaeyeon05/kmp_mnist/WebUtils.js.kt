package io.github.jtaeyeon05.kmp_mnist

import kotlinx.browser.document
import org.w3c.dom.HTMLElement


actual fun showCompose() {
    (document.getElementById("compose-root") as? HTMLElement)?.run {
        style.opacity = "1"
        style.zIndex = "2"
    }
}

actual external fun stopLoader()
