/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.tables;

import no.ntnu.epsilon_backend.domain.MediaObject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import no.ntnu.epsilon_backend.domain.MediaObjectAdapter;
import static no.ntnu.epsilon_backend.tables.Item.FIND_ALL_ITEMS;

/**
 *
 * @author rojahno Represents an item to be sold on the webpage
 *
 */
@Entity
@Table(name = "ITEMS")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_ITEMS, query = "select u from Item u order by u.title")
//@NamedQuery(name = FIND_ITEMS_BY_USERID, query = "select u from Item u where u.userid in :ids")
public class Item implements Serializable {

    public static final String FIND_ALL_ITEMS = "Item.findAllUsers";
    //public static final String FIND_ITEMS_BY_USERID = "ITEM.findByUserId";

    @Id
    @GeneratedValue
    String itemId;

    String title;

    String description;

    @JoinColumn(nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    User owner;

    BigDecimal price;

    @JsonbTypeAdapter(MediaObjectAdapter.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<MediaObject> photos;

    public Item() {

    }

}
