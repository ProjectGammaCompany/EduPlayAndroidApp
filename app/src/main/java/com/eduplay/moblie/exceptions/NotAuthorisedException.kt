package com.eduplay.moblie.exceptions

import java.io.IOException
import java.lang.RuntimeException

class NotAuthorisedException(override val message: String? = null): IOException(message) {
}