package com.cattery.presentation.util

import com.cattery.domain.models.SyncOutcome

fun SyncOutcome.uiError(): String? = when (this) {
    is SyncOutcome.Failed -> message.takeIf { !hasCache }
    else -> null
}
