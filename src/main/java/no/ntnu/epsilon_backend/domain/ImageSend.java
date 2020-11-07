/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import no.ntnu.epsilon_backend.tables.User;

/**
 *
 * @author ander
 */
public class ImageSend {

    long imageId;
    String base64String;
    User user;

    public ImageSend(long imageId, String base64String, User user) {
        this.imageId = imageId;
        this.base64String = base64String;
        this.user = user;
    }

    public String getBase64String() {
        return base64String;
    }

    public User getUser() {
        return user;
    }

    public long getImageId() {
        return imageId;
    }
}
