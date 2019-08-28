package com.globalm.platform.models.assingments;

import android.support.annotation.Nullable;

import com.globalm.platform.models.GeoLocation;
import com.globalm.platform.models.Owner;
import com.globalm.platform.models.Tag;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Assignment {
    private Integer id;
    private AssignmentStatus status;
    private String title;
    @Nullable
    private String description;
    private Date date;
    @SerializedName("respond_by")
    private Date respondBy;
    @SerializedName("is_urgent")
    private Boolean isUrgent;
    @SerializedName("copyright_transfer")
    private Boolean copyrightTransfer;
    @SerializedName("rate_currency_code")
    private String rateCurrencyCode;
    @SerializedName("rate_value")
    private Double rateValue;
    @SerializedName("target_geo_data")
    private GeoLocation targetGeoData;
    private Owner user;
    private Organization organization;
    private List<Tag> skills;
    private List<Response> responses;
    @SerializedName("assigned_user")
    private Owner assignedUser;

    public Assignment(
            Integer id,
            AssignmentStatus status, String title,
            @Nullable String description, Date date,
            Date respondBy,
            Boolean isUrgent,
            Boolean copyrightTransfer,
            String rateCurrencyCode,
            Double rateValue, GeoLocation targetGeoData,
            Owner user,
            Organization organization,
            List<Tag> skills,
            List<Response> responses,
            Owner assignedUser) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.date = date;
        this.respondBy = respondBy;
        this.isUrgent = isUrgent;
        this.copyrightTransfer = copyrightTransfer;
        this.rateCurrencyCode = rateCurrencyCode;
        this.rateValue = rateValue;
        this.targetGeoData = targetGeoData;
        this.user = user;
        this.organization = organization;
        this.skills = skills;
        this.responses = responses;
        this.assignedUser = assignedUser;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public Date getRespondBy() {
        return respondBy;
    }

    public Boolean getUrgent() {
        return isUrgent;
    }

    public Boolean getCopyrightTransfer() {
        return copyrightTransfer;
    }

    public String getRateCurrencyCode() {
        return rateCurrencyCode;
    }

    public GeoLocation getTargetGeoData() {
        return targetGeoData;
    }

    public Owner getUser() {
        return user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public List<Tag> getSkills() {
        return skills;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public Owner getAssignedUser() {
        return assignedUser;
    }

    public Integer getId() {
        return id;
    }

    public Double getRateValue() {
        return rateValue;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    @Nullable
    public String getDescription() {
        return description;
    }
}
