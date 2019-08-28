package com.globalm.platform.models.assingments;

import com.google.gson.annotations.SerializedName;

public enum  AssignmentStatus {
    @SerializedName("pending")
    STATUS_PENDING,
    @SerializedName("accepted")
    STATUS_ACCEPTED,
    @SerializedName("rejected")
    STATUS_REJECTED,
    @SerializedName("active")
    STATUS_ACTIVE
}
