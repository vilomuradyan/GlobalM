package com.globalm.platform.models;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("id")
    private int id;
    @SerializedName("metadata")
    private Metadata metadata;
    @SerializedName("user")
    private User user;
    @SerializedName("creator")
    private User creator;
    @SerializedName("target")
    private User target;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public static class Metadata {
        @SerializedName("is_favorite")
        private boolean isFavorite;
        @SerializedName("is_connected")
        private boolean isConnected;
        @SerializedName("is_requested")
        private boolean isRequested;
        @SerializedName("is_colleague")
        private boolean isColleague;
        @SerializedName("is_following")
        private boolean isFollowing;

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setFavorite(boolean favorite) {
            isFavorite = favorite;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void setConnected(boolean connected) {
            isConnected = connected;
        }

        public boolean isRequested() {
            return isRequested;
        }

        public void setRequested(boolean requested) {
            isRequested = requested;
        }

        public boolean isColleague() {
            return isColleague;
        }

        public void setColleague(boolean colleague) {
            isColleague = colleague;
        }

        public boolean isFollowing() {
            return isFollowing;
        }

        public void setFollowing(boolean following) {
            isFollowing = following;
        }
    }
}
