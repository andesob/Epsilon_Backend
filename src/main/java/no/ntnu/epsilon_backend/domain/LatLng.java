/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import java.io.Serializable;

/**
 *
 * @author eskil
 */
public class LatLng implements Serializable {
    
    private double latitude;
    private double longitude;
    
    public LatLng(double latitude,double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
}
