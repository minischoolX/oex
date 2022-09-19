package org.edx.mobile.model.course

import com.google.gson.annotations.SerializedName

class BlocksCompletionBody(

    @SerializedName("username")
    var username: String,

    @SerializedName("course_key")
    var courseKey: String,

    blockIds: List<String>
) {
    var blocks = mutableMapOf<String, String>()

    init {
        blockIds.indices.forEach { index ->
            blocks[blockIds[index]] = "1"
        }
    }
}
