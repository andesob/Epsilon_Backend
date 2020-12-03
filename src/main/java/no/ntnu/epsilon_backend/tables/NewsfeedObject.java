/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.AllArgsConstructor;
import lombok.Data;
import static no.ntnu.epsilon_backend.tables.NewsfeedObject.FIND_ALL_NEWSFEEDOBJECTS;

/**
 *
 * @author andesob
 */
@Entity
@Table(name = "NewsfeedObject")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_NEWSFEEDOBJECTS, query = "select n from NewsfeedObject n order by n.newsfeedObjectId DESC")
public class NewsfeedObject implements Serializable {

    /**
     *
     */
    public static final String FIND_ALL_NEWSFEEDOBJECTS = "NewsfeedObject.findAllNewsfeedObjects";

    @Id
    @GeneratedValue
    long newsfeedObjectId;

    String title;

    String newsContent;

    String timeWritten;

    String lastUpdated;

    /**
     *
     */
    public NewsfeedObject() {

    }

    /**
     *
     * @param title
     * @param contents
     * @param timeWritten
     * @param lastUpdated
     */
    public NewsfeedObject(String title, String contents, String timeWritten, String lastUpdated) {
        this.title = title;
        this.newsContent = contents;
        this.timeWritten = timeWritten;
        this.lastUpdated = lastUpdated;
    }
}
