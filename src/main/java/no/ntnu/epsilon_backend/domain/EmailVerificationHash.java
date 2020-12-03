/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import java.io.Serializable;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author rojahno
 */
public class EmailVerificationHash implements Serializable {

    private String hash;
    private long timeWhenGenerated;
    private long timeWhenExpired;

    /**
     *
     */
    public EmailVerificationHash() {
        setNewHash();
    }

    private void setNewHash() {
        // Generate Hash Code which helps in creating Activation Link
        Random theRandom = new Random();
        theRandom.nextInt(999999);
        this.hash = DigestUtils.md5Hex("" + theRandom);
        this.timeWhenGenerated = System.currentTimeMillis();
        this.timeWhenExpired = (timeWhenGenerated + 1800000);
    }

    /**
     *
     * @return
     */
    public String getHash() {
        return this.hash;
    }

    /**
     *
     * @return
     */
    public Long getTimeWhenGenerated() {
        return this.timeWhenGenerated;
    }

    /**
     *
     * @return
     */
    public long getTimeWhenExpired() {
        return this.timeWhenExpired;
    }
}
