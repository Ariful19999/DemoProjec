package com.arif19.noticemanagement.modal;


public class add_item_for_group_admin {
    private String adminName;

    private String imageUrl;

    public add_item_for_group_admin(String adminName, String imageUrl) {
        this.adminName = adminName;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return adminName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}