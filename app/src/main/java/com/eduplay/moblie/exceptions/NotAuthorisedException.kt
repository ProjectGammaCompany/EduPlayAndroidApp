package com.eduplay.moblie.exceptions

import java.lang.RuntimeException

class NotAuthorisedException(override val message: String? = null): RuntimeException(message) {
}