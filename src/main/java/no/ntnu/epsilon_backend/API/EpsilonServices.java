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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import no.ntnu.epsilon_backend.tables.User;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.BeanParam;
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
import no.ntnu.epsilon_backend.domain.LatLng;
import no.ntnu.epsilon_backend.domain.Time;
import no.ntnu.epsilon_backend.tables.Calendar;
import no.ntnu.epsilon_backend.domain.ImageSend;
import no.ntnu.epsilon_backend.setup.MailService;
import no.ntnu.epsilon_backend.tables.AboutUsObject;
import no.ntnu.epsilon_backend.tables.Faq;
import no.ntnu.epsilon_backend.tables.Image;
import no.ntnu.epsilon_backend.tables.NewsfeedObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author rojahno
 */
@Path("/web")
@Stateless
//@DeclareRoles(Group.USER)
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
    //@RolesAllowed({Group.USER})
    public List<User> getAllUsers() {
        return em.createNamedQuery(User.FIND_ALL_USERS, User.class).getResultList();
    }

    private User getCurrentUser() {
        User user = em.find(User.class, sc.getUserPrincipal().getName());
        return user;
    }

    @GET
    @Path("getcalendar")
    public List<Calendar> getCalendars() {
        return em.createNamedQuery(Calendar.FIND_ALL_CALENDAR_ITEMS, Calendar.class).getResultList();
    }

    @GET
    @Path("newsfeed")
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({Group.USER})
    public List<NewsfeedObject> getAllNewsfeedObjects() {
        return em.createNamedQuery(NewsfeedObject.FIND_ALL_NEWSFEEDOBJECTS, NewsfeedObject.class).getResultList();
    }

    @PUT
    @Path("postNews")
    public Response postNewsfeedObject(@FormParam("title") String title,
            @FormParam("content") String content) {
        NewsfeedObject news = new NewsfeedObject(title, content, LocalDateTime.now(), LocalDateTime.now());
        em.persist(news);
        return Response.ok(news).build();
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
    @return alter a faq
     */
    @POST
    @Path("edit_faq")
    //@RolesAllowed({Group.Admin})
    public Response editFaqs(@FormParam("question") String question, @FormParam("answer") String answer, @FormParam("questionId") long id) {

        Faq faq = em.find(Faq.class, id);
        faq.setQuestion(question);
        faq.setAnswer(answer);

        return Response.ok().build();
    }


    /*
    @return all faqs
     */
    @PUT
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

    @PUT
    @Path("add_calendar_item")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCalendarItem(@FormParam("title") String title,
            @FormParam("description") String description,
            @BeanParam LatLng latLng,
            @BeanParam Time startTime,
            @BeanParam Time endTime,
            @FormParam("address") String address) {

        Calendar calendar = new Calendar(title, description, latLng, startTime, endTime, address);
        em.persist(calendar);
        return Response.ok(calendar).build();
    }

    @POST
    @Path("addAboutUsObject")
    public Response addAboutUsObject(@FormParam("position") String position, @FormParam("userid") String userid) {
        User user = em.find(User.class, userid);

        AboutUsObject aboutUsObject = new AboutUsObject(user, position);
        em.persist(aboutUsObject);

        return Response.ok(aboutUsObject).build();
    }

    @GET
    @Path("getAboutUsObjects")
    public Response getAboutUsObjects() {
        return Response.ok(em.createNamedQuery(AboutUsObject.FIND_ALL_ABOUT_US_OBJECTS).getResultList()).build();
    }

    //TODO: Change filepath so it matches an ubuntu server instead of windows specific filesystem.
    @POST
    @Path("uploadPictureAsString")
    public Response uploadPictureAsString(@FormParam("base64String") String base64String, @FormParam("userId") String userId, @FormParam("filename") String filename) {

        //byte[] decodedString = android.util.Base64.decode(imageString, android.util.Base64.DEFAULT);
        //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        byte[] decodedString = Base64.getMimeDecoder().decode(base64String);
        String filepath = "/C:/Dataingenior/" + filename;

        try {
            File file = new File(filepath);
            FileOutputStream imageOutputStream = new FileOutputStream(file);
            imageOutputStream.write(decodedString);
            imageOutputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
        }

        User user = em.find(User.class, userId);
        Image image = new Image(filepath, user);
        em.persist(image);
        ImageSend imageSend = new ImageSend(image.getImageId(), base64String, image.getUser());
        return Response.ok(imageSend).build();
    }

    @POST
    @Path("deletePicture")
    public Response deletePicture(@FormParam("pictureId") String pictureId) {
        long imageId = Long.valueOf(pictureId);
        Image image = null;
        image = em.find(Image.class, imageId);
        if (image != null) {
            em.remove(image);
        }
        return Response.ok(image).build();
    }

    @GET
    @Path("getUserPictures")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserPictures() {

        List<Image> imageList = em.createNamedQuery(Image.FIND_ALL_IMAGES).getResultList();
        List<ImageSend> imageSendList = new ArrayList<>();
        for (Image i : imageList) {
            String base64String;
            try {
                base64String = encodeBase64(i);
                ImageSend imageSend = new ImageSend(i.getImageId(), base64String, i.getUser());
                imageSendList.add(imageSend);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EpsilonServices.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.ok(imageSendList).build();
    }

    private String encodeBase64(Image i) throws FileNotFoundException {
        String base64String = "";
        File file = new File(i.getFilepath());
        try {
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
