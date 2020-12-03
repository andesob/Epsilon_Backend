/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.API;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import no.ntnu.epsilon_backend.tables.User;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
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
import no.ntnu.epsilon_backend.tables.Calendar;
import no.ntnu.epsilon_backend.domain.ImageSend;
import no.ntnu.epsilon_backend.setup.MailService;
import no.ntnu.epsilon_backend.tables.AboutUsObject;
import no.ntnu.epsilon_backend.tables.Faq;
import no.ntnu.epsilon_backend.tables.Group;
import no.ntnu.epsilon_backend.tables.Image;
import no.ntnu.epsilon_backend.tables.NewsfeedObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author rojahno
 */
@Path("web")
@Stateless
@DeclareRoles(Group.USER)
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

    /**
     * Returns list of all users
     *
     * @return all users
     */
    @GET
    @Path("users")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public List<User> getAllUsers() {
        return em.createNamedQuery(User.FIND_ALL_USERS, User.class).getResultList();
    }

    /**
     *
     * @return List of calendar items
     */
    @GET
    @Path("getcalendar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public List<Calendar> getCalendars() {

        List<Calendar> calendarList = em.createNamedQuery(Calendar.FIND_ALL_CALENDAR_ITEMS, Calendar.class).getResultList();
        List<Calendar> resultList = new ArrayList<>();
        for (Calendar c : calendarList) {
            if (LocalDate.now().minusDays(10).isBefore(LocalDate.of(Integer.parseInt(c.getStartTimeParsed(0)), Integer.parseInt(c.getStartTimeParsed(1)) + 1, Integer.parseInt(c.getStartTimeParsed(2))))) {
                resultList.add(c);
            }
        }
        return resultList;
    }

    /**
     *
     * @return List of newsfeedobjects
     */
    @GET
    @Path("newsfeed")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public List<NewsfeedObject> getAllNewsfeedObjects() {
        List<NewsfeedObject> newsList = null;
        try {
            newsList = em.createNamedQuery(NewsfeedObject.FIND_ALL_NEWSFEEDOBJECTS, NewsfeedObject.class).getResultList();
        } catch (Exception e) {
        }
        return newsList;
    }

    /**
     *
     * @param title
     * @param content
     * @return Response ok with newsfeedobject created
     */
    @PUT
    @Path("postNews")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response postNewsfeedObject(@FormParam("title") String title,
            @FormParam("content") String content) {
        String time = LocalDateTime.now().toString();
        NewsfeedObject news = new NewsfeedObject(title, content, time, time);
        em.persist(news);
        return Response.ok(news).build();
    }

    /**
     *
     * @return List of faqobjects
     */
    @GET
    @Path("get_faqs")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public List<Faq> getAllFaqs() {
        return em.createNamedQuery(Faq.FIND_ALL_FAQS, Faq.class).getResultList();
    }

    /*
    @return alter a faq
     */
    /**
     *
     * @param question
     * @param answer
     * @param id
     * @return Response
     */
    @POST
    @Path("edit_faq")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response editFaqs(@FormParam("question") String question, @FormParam("answer") String answer, @FormParam("questionId") long id) {

        Faq faq = em.find(Faq.class, id);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        em.persist(faq);
        em.flush();

        return Response.ok().build();
    }


    /*
    @return
     */
    /**
     *
     * @param question
     * @param answer
     * @return Response
     */
    @PUT
    @Path("add_faqs")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response addFaq(
            @FormParam("question") @NotBlank String question,
            @FormParam("answer") @NotBlank String answer) {
        Faq faq = new Faq();
        faq.setAnswer(answer);
        faq.setQuestion(question);
        em.persist(faq);

        //Returns BAD REQUEST if the faq is not created correctly
        if (em.find(Faq.class, faq.getQuestionId()) != null) {
            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     *
     * @param faqId
     * @return Response
     */
    @PUT
    @Path("delete_faq")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response deleteFaq(
            @FormParam("id") long faqId) {

        Faq faq = em.find(Faq.class, faqId);

        //Returns BAD REQUEST if faq does not exist
        if (faq != null) {
            em.remove(faq);
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    /*
    @return all faqs
     */
    /**
     *
     * @param question
     * @return Response ACCEPTED
     */
    @POST
    @Path("ask_question")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public Response askQuestion(@FormParam("question")
            @NotBlank String question) {
        //Sends mail with the question
        mailService.onAsyncMessage(question);
        return Response.status(Response.Status.ACCEPTED).entity(question).build();
    }

    /**
     *
     * @param title
     * @param description
     * @param latLng
     * @param startTime
     * @param endTime
     * @param address
     * @return Response ok with calendar object
     */
    @PUT
    @Path("add_calendar_item")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response addCalendarItem(@FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("latlng") String latLng,
            @FormParam("starttime") String startTime,
            @FormParam("endtime") String endTime,
            @FormParam("address") String address) {

        Calendar calendar = new Calendar(title, description, latLng, startTime, endTime, address);
        em.merge(calendar);
        return Response.ok(calendar).build();
    }

    /**
     *
     * @param id
     * @return Response
     */
    @PUT
    @Path("delete_calendar_item")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response deleteCalendar(@FormParam("id") long id) {
        Calendar calendar = em.find(Calendar.class, id);

        //OK if calendar item exists and is removed
        if (calendar != null) {
            em.remove(calendar);
            return Response.status(Response.Status.OK).build();
        }

        //BAD REQUEST  if item doesnt exist
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     *
     * @param position
     * @param userid
     * @return Response OK with aboutusobject
     */
    @POST
    @Path("addAboutUsObject")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response addAboutUsObject(@FormParam("position") String position, @FormParam("userid") String userid) {
        User user = em.find(User.class, userid);

        AboutUsObject aboutUsObject = new AboutUsObject(user, position);
        em.persist(aboutUsObject);

        return Response.ok(aboutUsObject).build();
    }

    /**
     *
     * @return Response with list of aboutusobjects
     */
    @GET
    @Path("getAboutUsObjects")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public Response getAboutUsObjects() {
        return Response.ok(em.createNamedQuery(AboutUsObject.FIND_ALL_ABOUT_US_OBJECTS).getResultList()).build();
    }

    /**
     *
     * @param base64String
     * @param userId
     * @param filename
     * @return Response OK with image object
     */
    @POST
    @Path("uploadPictureAsString")
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response uploadPictureAsString(@FormParam("base64String") String base64String, @FormParam("userId") String userId, @FormParam("filename") String filename) {
        byte[] decodedString = Base64.getMimeDecoder().decode(base64String);
        String filepath = "/opt/epsilon/pictures/" + filename;
        try {
            File file = new File(filepath);
            FileOutputStream imageOutputStream = new FileOutputStream(file);
            imageOutputStream.write(decodedString);
            imageOutputStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        }

        User user = em.find(User.class, userId);
        deletePictureIfExists(userId);
        Image image = new Image(filepath, user);
        em.persist(image);
        ImageSend imageSend = new ImageSend(image.getImageId(), base64String, image.getUser());
        return Response.ok(imageSend).build();
    }

    private void deletePictureIfExists(String userid) {
        Image image = null;
        try {
            image = em.createNamedQuery(Image.FIND_IMAGE_BY_USERID, Image.class).setParameter("uid", userid).getSingleResult();
        } catch (Exception e) {
        }

        if (image != null) {
            em.remove(image);
        }
    }

    /**
     *
     * @return Response with a list of images
     */
    @GET
    @Path("getUserPictures")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public Response getUserPictures() {

        List<Image> imageList = em.createNamedQuery(Image.FIND_ALL_IMAGES).getResultList();
        List<ImageSend> imageSendList = new ArrayList<>();
        for (Image i : imageList) {
            String base64String;
            try {
                //Converts to base64 string
                base64String = encodeBase64(i);
                ImageSend imageSend = new ImageSend(i.getImageId(), base64String, i.getUser());
                imageSendList.add(imageSend);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);

                //BAD REQUEST if file is not found
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.ok(imageSendList).build();
    }

    /**
     *
     * @param i
     * @return base64 string of image
     * @throws FileNotFoundException
     */
    private String encodeBase64(Image i) throws FileNotFoundException {
        String base64String = "";

        try {
            File file = new File(i.getFilepath());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fileInputStream.read(imageData);

            base64String = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return base64String;
    }
}
