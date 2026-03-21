package com.eduplay.moblie.exceptions

import java.io.IOException

class NotAuthorisedException(override val message: String? = null) : IOException(message)