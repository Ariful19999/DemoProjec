package com.arif19.noticemanagement.modal;


public class AddGroupMemberItem {
    private String memberName;
    private boolean isAdded; // New flag to track whether the member has been added

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    private String memberId;
    private String memberImage; // Add a field for holding video URL

    public String getAvatarImage() {
        return memberImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.memberImage = avatarImage;
    }


    public String getReporterName() {
        return memberName;
    }

    public void setReporterName(String reporterName) {
        this.memberName = reporterName;
    }



    // Other existing methods

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

}
