/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.API;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import no.ntnu.epsilon_backend.tables.User;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import no.ntnu.epsilon_backend.API.AuthenticationService;
import no.ntnu.epsilon_backend.tables.Group;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author rojahno
 */
@Path("/web")
@Stateless
public class EpsilonServices {

    DataSource ds;

    @PersistenceContext
    EntityManager em;

    @Inject
    AuthenticationService autenticationService;

    @Context
    SecurityContext sc;
    /*
    @Inject
    MailService mailService;
     */
    /**
     * path to store photos
     */
    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "chatphotos")
    String photoPath;

    private String getPhotoPath() {
        return photoPath;
    }

    /**
     * Returns list of all users
     *
     * @return all users
     */
    @GET
    @Path("users")
    @RolesAllowed({Group.USER})
    public List<User> getAllUsers() {
        return em.createNamedQuery(User.FIND_ALL_USERS, User.class).getResultList();
    }

    private User getCurrentUser() {
        User user = em.find(User.class, sc.getUserPrincipal().getName());
        return user;
    }

}
