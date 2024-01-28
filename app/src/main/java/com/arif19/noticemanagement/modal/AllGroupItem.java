package com.arif19.noticemanagement.modal;


public class AllGroupItem {
    private String groupName;
    private boolean isAdded; // New flag to track whether the group has been added

    public String getgroupId() {
        return groupId;
    }

    public void setgroupId(String groupId) {
        this.groupId = groupId;
    }

    private String groupId;
    private String groupImage; // Add a field for holding video URL

    public String getAvatarImage() {
        return groupImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.groupImage = avatarImage;
    }


    public String getReporterName() {
        return groupName;
    }

    public void setReporterName(String reporterName) {
        this.groupName = reporterName;
    }



    // Other existing methods

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

}

