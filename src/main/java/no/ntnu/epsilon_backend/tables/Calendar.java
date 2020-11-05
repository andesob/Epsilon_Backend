/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import static no.ntnu.epsilon_backend.tables.Calendar.FIND_ALL_CALENDAR_ITEMS;

/**
 *
 * @author eskil
 */

@Entity
@Table(name = "CALENDAR_ITEMS")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_CALENDAR_ITEMS,query = " select c from Calendar c") //Order by day if problems putting it in order in app

public class Calendar implements Serializable{
        public static final String FIND_ALL_CALENDAR_ITEMS = "Calendar.findAllCalendarItems";

    @Id
    @Generated
    Long id;
    
    String date;
    String day;
    String month;
    
    String title;
    
    String description;
    String time;
    
    String adress;
   
    String latitude;
    String longitude;
    
    public Calendar(){
        
    }
    
    
}
