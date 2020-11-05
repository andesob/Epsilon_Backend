package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static no.ntnu.epsilon_backend.tables.User.FIND_ALL_USERS;
import static no.ntnu.epsilon_backend.tables.User.FIND_USER_BY_IDS;

/**
 * A user of the system. Bound to the authentication system
 *
 * @author Rojahno
 */
@Entity
@Table(name = "AUSER")
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_USERS, query = "select u from User u order by u.firstName")
@NamedQuery(name = FIND_USER_BY_IDS, query = "select u from User u where u.userid in :ids")
@NoArgsConstructor
public class User implements Serializable {

    public static final String FIND_USER_BY_IDS = "User.findUserByIds";
    public static final String FIND_ALL_USERS = "User.findAllUsers";

    public enum State {
        ACTIVE, INACTIVE
    }

    @Id
    String userid;

    @JsonbTransient
    String password;

    @Version
    Timestamp version;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date created;

    @Enumerated(EnumType.STRING)
    State currentState = State.ACTIVE;

    @ManyToMany
    @JoinTable(name = "AUSERGROUP",
            joinColumns = @JoinColumn(name = "userid", referencedColumnName = "userid"),
            inverseJoinColumns = @JoinColumn(name = "name", referencedColumnName = "name"))
    List<Group> groups;

    String firstName;
    String middleName;
    String lastName;

    @Email
    String email;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "auser_properties", joinColumns = @JoinColumn(name = "uid"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    Map<String, String> properties = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    public List<Group> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }

}
