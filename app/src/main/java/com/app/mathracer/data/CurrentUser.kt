package com.app.mathracer.data

import com.app.mathracer.data.model.User

object CurrentUser {
    @Volatile
    var user: User? = null
}
