package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import java.io.Serializable;
import java.util.List;

public class Owner implements Serializable {
    @SerializedName("id")
    private Integer id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("full_name")
    private String fullName;
    private String email;
    @Nullable
    @SerializedName("profile")
    private Profile profile;

    public Owner(Integer id,
                 String firstName,
                 String lastName,
                 String fullName,
                 String email,
                 @Nullable Profile profile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.profile = profile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    @Nullable
    public Profile getProfile() {
        return profile;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static class Profile implements Serializable {
        @SerializedName("thumb")
        private String thumb;
        @SerializedName("title")
        private String title;
        @SerializedName("country")
        private String country;
        private String bio;
        private List<Tag> skills;
        @Nullable
        private Cover cover;

        public Profile(String thumb,
                       String title,
                       String country,
                       String bio,
                       List<Tag> skills,
                       Cover cover) {
            this.thumb = thumb;
            this.title = title;
            this.country = country;
            this.bio = bio;
            this.skills = skills;
            this.cover = cover;
        }

        public String getThumb() {
            return thumb;
        }

        public String getTitle() {
            return title;
        }

        public String getCountry() {
            return country;
        }

        public String getBio() {
            return bio;
        }

        public List<Tag> getSkills() {
            return skills;
        }

        @Nullable
        public Cover getCover() {
            return cover;
        }
    }
}