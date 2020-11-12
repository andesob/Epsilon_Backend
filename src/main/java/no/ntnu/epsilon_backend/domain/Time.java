/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author eskil
 */
@NoArgsConstructor
@AllArgsConstructor
public class Time implements Serializable {
    private String year;
    private String month;
    private String date;
    private String hour;
    private String minute;
       
}
