/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import no.ntnu.epsilon_backend.domain.AbstractDomain;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.ntnu.epsilon_backend.tables.User;

/**
 *
 * @author mikael
 */
/**
 *
 * @author mikael
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class MediaObject extends AbstractDomain {

    @Id
    String id;

    String name;

    long filesize;
    String mimeType;

    @JoinColumn(nullable = false)
    @ManyToOne
    User owner;

    /**
     *
     */
    protected MediaObject() {
        super();
    }

    /**
     *
     * @param id
     * @param owner
     */
    public MediaObject(String id, User owner) {
        this();
        this.id = id;
        this.owner = owner;
    }

    /**
     *
     * @param id
     * @param owner
     * @param name
     * @param filesize
     * @param mimetype
     */
    public MediaObject(String id, User owner, String name, long filesize, String mimetype) {
        this();
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.filesize = filesize;
        this.mimeType = mimetype;
    }
}
