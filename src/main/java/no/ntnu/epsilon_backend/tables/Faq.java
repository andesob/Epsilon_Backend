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
import static no.ntnu.epsilon_backend.tables.Faq.FIND_ALL_FAQS;

/**
 *
 * @author rojahno Represents an item to be sold on the webpage
 *
 */
@Entity
@Table(name = "FAQ")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_FAQS, query = "select u from Faq u")
public class Faq implements Serializable {

    /**
     *
     */
    public static final String FIND_ALL_FAQS = "faq.findAllFaqs";

    @Id
    @GeneratedValue
    long questionId;

    String question;

    String answer;

    /**
     *
     */
    public Faq() {

    }

}
