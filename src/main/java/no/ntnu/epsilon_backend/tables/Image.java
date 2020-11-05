/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import static no.ntnu.epsilon_backend.tables.Image.FIND_ALL_IMAGES;

/**
 *
 * @author ander
 */
@Entity
@Table(name = "Image")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_IMAGES, query = "select i from Image i order by i.imageId")
public class Image implements Serializable {

    public static final String FIND_ALL_IMAGES = "Image.findAllImages";

    @Id
    @GeneratedValue
    long imageId;

    String filepath;

    User user;

    public Image(String filepath, User user) {
        this.filepath = filepath;
        this.user = user;
    }

    public Image() {

    }

    public String getFilepath() {
        return filepath;
    }

    public long getImageId() {
        return imageId;
    }

    public User getUser() {
        return user;
    }
}
