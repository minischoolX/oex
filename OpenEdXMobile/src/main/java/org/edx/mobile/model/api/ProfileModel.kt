package org.edx.mobile.model.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProfileModel(
    @JvmField
    @SerializedName("id")
    var id: Long,

    @JvmField
    @SerializedName("username")
    var username: String,

    @JvmField
    @SerializedName("email")
    var email: String,

    @SerializedName("name")
    var name: String? = null,
) : Serializable {

    @JvmField
    var hasLimitedProfile = false
}
