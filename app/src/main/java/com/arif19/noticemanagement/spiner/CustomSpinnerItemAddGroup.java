package com.arif19.noticemanagement.spiner;


import static com.arif19.noticemanagement.public_url.PublicUrl.rootUrl;

public class CustomSpinnerItemAddGroup {
    private String text;
    private String imageUrl;

    public CustomSpinnerItemAddGroup(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return rootUrl+imageUrl;
    }
}
