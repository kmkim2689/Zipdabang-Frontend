package com.UMC.zipdabang.src.my.data

import java.io.Serializable

data class ItemRecipeChallengeData(
    val userId : Int?,
    val likes: Int?,
    val image: String?,
    val name: String?
) : Serializable