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
import lombok.Generated;
import lombok.NoArgsConstructor;
import no.ntnu.epsilon_backend.domain.LatitudeLongitude;
import no.ntnu.epsilon_backend.domain.Time;
import static no.ntnu.epsilon_backend.tables.Calendar.FIND_ALL_CALENDAR_ITEMS;

/**
 *
 * @author eskil
 */

@Entity
@Table(name = "CALENDAR_ITEMS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name = FIND_ALL_CALENDAR_ITEMS,query = " select c from Calendar c") //Order by day if problems putting it in order in app

public class Calendar implements Serializable{

    /**
     *
     */
    public static final String FIND_ALL_CALENDAR_ITEMS = "Calendar.findAllCalendarItems";

    @Id
    @GeneratedValue
    long id;
    
    String title;
    String description;
    
    String latLng;
    
    String startTime;   
    String endTime;  
    
    String address;
    
    /**
     *
     * @param title
     * @param description
     * @param latLng
     * @param startTime
     * @param endTime
     * @param address
     */
    public Calendar(String title, String description, String latLng, String startTime, String endTime, String address) {
        this.title = title;
        this.description = description;
        this.latLng = latLng;
        this.startTime = startTime;
        this.endTime = endTime;
        this.address = address;
    }

    /**
     *
     * @param poistion
     * @return
     */
    public String getStartTimeParsed(int poistion) {
        String[] arr = getStartTime().split(",");
        return arr[poistion];
    }  
}
