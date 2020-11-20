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

    public EmailTwoFactorHash() {
        Random theRandom = new Random();
        theRandom.nextInt(662370);
        this.hash = DigestUtils.md5Hex("" + theRandom);

    }

}
