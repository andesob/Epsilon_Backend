package no.ntnu.epsilon_backend.tables;

import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author rojahno
 */
@Entity
@Table(name = "AGROUP")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "users")
public class Group implements Serializable {

    /**
     *
     */
    public static final String USER = "user";

    /**
     *
     */
    public static final String ADMIN = "admin";

    /**
     *
     */
    public static final String BOARD = "board";

    /**
     *
     */
    public static final String[] GROUPS = {USER, ADMIN, BOARD};

    @Id
    String name;

    String project;

    @JsonbTransient
    @ManyToMany
    @JoinTable(name = "AUSERGROUP",
            joinColumns = @JoinColumn(name = "name", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name = "userid", referencedColumnName = "userid"))
    List<User> users;

    /**
     *
     * @param name
     */
    public Group(String name) {
        this.name = name;
    }
}
