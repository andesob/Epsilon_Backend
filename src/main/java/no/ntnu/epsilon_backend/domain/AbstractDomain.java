/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * An abstract base class for domain objects with support for created and
 * version fields
 *
 * @author mikael
 */
@MappedSuperclass
public abstract class AbstractDomain implements Serializable {

    @Column(nullable = false)
    @Version
    Timestamp version;

    @Column(nullable = false, updatable = false)
    final private Timestamp created;

    /**
     *
     */
    public AbstractDomain() {
        this.created = new Timestamp(System.currentTimeMillis());
        this.version = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @Version allows the JPA engine to use optimistic locking in the database.
     * JPA will update the timestamp on insert and update requests
     * @return
     */
    public Timestamp getVersion() {
        return version;
    }

    /**
     *
     * @return
     */
    public Timestamp getCreated() {
        return created;
    }
}
