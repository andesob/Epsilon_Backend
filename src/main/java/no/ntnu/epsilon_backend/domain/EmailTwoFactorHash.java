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
 * @author ander
 */
public class EmailTwoFactorHash implements Serializable {

    private String hash;
    private long timeGenerated;
    private long timeExpired;

    /**
     *
     * @param number
     */
    public EmailTwoFactorHash(String number) {
        hash = DigestUtils.md5Hex(number);
        timeGenerated = System.currentTimeMillis();
        timeExpired = timeGenerated + 900000;
    }

    /**
     *
     * @return
     */
    public String getHash() {
        return hash;
    }

    /**
     *
     * @return
     */
    public boolean isExpired() {
        return timeExpired <= System.currentTimeMillis();
    }
}
