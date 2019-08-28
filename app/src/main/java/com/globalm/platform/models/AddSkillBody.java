package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

public class AddSkillBody {
    @SerializedName("skill_id")
    private String skillId;

    public AddSkillBody(String skillId) {
        this.skillId = skillId;
    }

    public String getSkillId() {
        return skillId;
    }
}
