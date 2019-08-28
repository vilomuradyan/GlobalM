package com.globalm.platform.models.assingments;

import android.support.annotation.Nullable;

import com.globalm.platform.models.Owner;
import com.google.gson.annotations.SerializedName;

public class Response {
    private Integer id;
    @SerializedName("user_id")
    @Nullable
    private Integer userId;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("assignment_id")
    @Nullable
    private Integer assignmentId;
    @Nullable
    private Status status;
    @Nullable
    private String message;
    @Nullable
    private Owner user;

    public Response(Integer id,
                    @Nullable Integer userId,
                    String createdAt,
                    @Nullable Integer assignmentId,
                    @Nullable Status status,
                    @Nullable String message,
                    @Nullable Owner user) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.assignmentId = assignmentId;
        this.status = status;
        this.message = message;
        this.user = user;
    }

    @Nullable
    public Integer getAssignmentId() {
        return assignmentId;
    }

    @Nullable
    public Status getStatus() {
        return status;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    @Nullable
    public Owner getUser() {
        return user;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public Integer getUserId() {
        return userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public enum Status {
        @SerializedName("accepted")
        ACCEPTED,
        @SerializedName("pending")
        PENDING
    }
}
