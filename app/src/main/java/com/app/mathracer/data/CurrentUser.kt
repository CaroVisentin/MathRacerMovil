package com.app.mathracer.data

import com.app.mathracer.data.model.User

object CurrentUser {
    @Volatile
    var user: User? = null
    @Volatile
    var cachedFriends: List<com.app.mathracer.data.model.Friend>? = null
    @Volatile
    var activeCharacterProductId: Int? = null
    @Volatile
    var activeBackgroundProductId: Int? = null
    @Volatile
    var activeVehicleProductId: Int? = null
}
