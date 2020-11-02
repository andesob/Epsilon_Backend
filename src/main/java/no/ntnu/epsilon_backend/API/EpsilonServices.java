/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.API;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import no.ntnu.epsilon_backend.tables.User;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.ws.rs.FormParam;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import no.ntnu.epsilon_backend.API.AuthenticationService;
import no.ntnu.epsilon_backend.setup.MailService;
import no.ntnu.epsilon_backend.tables.Faq;
import no.ntnu.epsilon_backend.tables.Group;
import no.ntnu.epsilon_backend.tables.NewsfeedObject;
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

    @Inject
    MailService mailService;

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

    @GET
    @Path("newsfeed")
    //@RolesAllowed({Group.USER})
    public List<NewsfeedObject> getAllNewsfeedObjects() {
        return em.createNamedQuery(NewsfeedObject.FIND_ALL_NEWSFEEDOBJECTS, NewsfeedObject.class).getResultList();
    }

    @PUT
    @Path("postNews")
    public NewsfeedObject postNewsfeedObject(@FormParam("title") String title,
            @FormParam("content") String content) {
        NewsfeedObject news = new NewsfeedObject(title, content, LocalDateTime.now(), LocalDateTime.now());

        return null;
    }

    /*
    @return all faqs
     */
    @GET
    @Path("get_faqs")
    //@RolesAllowed({Group.USER})
    public List<Faq> getAllFaqs() {
        return em.createNamedQuery(Faq.FIND_ALL_FAQS, Faq.class).getResultList();
    }

    /*
    @return all faqs
     */
    @POST
    @Path("add_faqs")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({Group.ADMIN})
    public Response addFaq(
            @FormParam("question") @NotBlank String question,
            @FormParam("answer") @NotBlank String answer) {
        Faq faq = new Faq();
        faq.setAnswer(answer);
        faq.setQuestion(question);
        return Response.ok(em.merge(faq)).build();
    }

    /*
    @return all faqs
     */
    @POST
    @Path("ask_question")
    //@RolesAllowed({Group.USER})
    public Response askQuestion(@FormParam("question")
            @NotBlank String question) {

        mailService.onAsyncMessage(question);
        return Response.ok(question, MediaType.APPLICATION_JSON).build();

    }
}
