/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import static no.ntnu.epsilon_backend.tables.AboutUsObject.FIND_ALL_ABOUT_US_OBJECTS;

/**
 *
 * @author ander
 */
@Entity
@Table(name = "ABOUT_US_OBJECT")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_ABOUT_US_OBJECTS, query = "select o from AboutUsObject o order by o.aboutUsObjectId")
public class AboutUsObject implements Serializable {

    /**
     *
     */
    public static final String FIND_ALL_ABOUT_US_OBJECTS = "Item.findAllAboutUsObjects";

    @Id
    @GeneratedValue
    long aboutUsObjectId;

    User user;
    String position;

    /**
     *
     * @param user
     * @param position
     */
    public AboutUsObject(User user, String position) {
        this.user = user;
        this.position = position;
    }

    /**
     *
     */
    public AboutUsObject() {
    }

}
