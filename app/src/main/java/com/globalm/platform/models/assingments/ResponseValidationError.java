package com.globalm.platform.models.assingments;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseValidationError {
    @SerializedName("assignment_id")
    @Nullable
    private List<String> assignment;

    public ResponseValidationError(@Nullable List<String> assignment) {
        this.assignment = assignment;
    }

    @Nullable
    public List<String> getAssignment() {
        return assignment;
    }
}
