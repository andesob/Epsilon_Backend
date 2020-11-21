/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.epsilon_backend.API;

import io.jsonwebtoken.Claims;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.sql.DataSource;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import lombok.extern.java.Log;
import javax.annotation.Resource;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.HttpHeaders;
import no.ntnu.epsilon_backend.setup.DatasourceProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import no.ntnu.epsilon_backend.domain.EmailTwoFactorHash;
import no.ntnu.epsilon_backend.domain.EmailVerificationHash;
import no.ntnu.epsilon_backend.setup.KeyService;
import no.ntnu.epsilon_backend.setup.MailService;
import no.ntnu.epsilon_backend.tables.AboutUsObject;
import no.ntnu.epsilon_backend.tables.Group;
import no.ntnu.epsilon_backend.tables.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * @Stateless makes this class into a transactional stateless EJB, which is a
 * requirement of using the JPA EntityManager to communicate with the database.
 *
 * @DeclareRoles({UserGroup.ADMIN,UserGroup.USER}) specifies the roles used in
 * this EJB.
 *
 * @author Rojahno
 */
@Path("auth")
@Stateless
@Log
public class AuthenticationService {

    private static final String INSERT_USERGROUP = "INSERT INTO AUSERGROUP(NAME,EMAIL) VALUES (?,?)";
    private static final String DELETE_USERGROUP = "DELETE FROM AUSERGROUP WHERE NAME = ? AND EMAIL = ?";

    @Inject
    KeyService keyService;

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    /**
     * The application server will inject a DataSource as a way to communicate
     * with the database.
     */
    @Resource(lookup = DatasourceProducer.JNDI_NAME)
    DataSource dataSource;

    /**
     * The application server will inject a EntityManager as a way to
     * communicate with the database via JPA.
     */
    @PersistenceContext
    EntityManager em;

    @Inject
    MailService mailService;

    @Inject
    PasswordHash hasher;

    @Inject
    JsonWebToken principal;

    /**
     *
     * @param email
     * @param pwd
     * @param request
     * @return
     */
    @GET
    @Path("login")
    public Response login(
            @QueryParam("email") @NotBlank String email,
            @QueryParam("pwd") @NotBlank String pwd) {
        User user = null;
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
        }

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (!user.getValidated()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(user.getUserid(), pwd));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            send2FactorKey(user);
            return Response
                    .ok(user)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("verify")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyJwt() {
        try {
            User user = em.createNamedQuery(User.FIND_USER_BY_ID, User.class).setParameter("id", principal.getName()).getSingleResult();
            if (user != null) {
                return Response.ok(user).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @GET
    @Path("activateAccount")
    public Response activateAccount(
            @Context HttpServletRequest request, @Context HttpServletResponse response) {

        User user = null;
        String emailHash = request.getParameter("key1");
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_HASH, User.class).setParameter("emailHash", emailHash).getSingleResult();

            EmailVerificationHash hash = user.getEmailVerificationHash();

            if (System.currentTimeMillis() > hash.getTimeWhenExpired()) {
                return Response.status(Response.Status.GONE).build();
            }

            if (user != null && !user.getValidated()) {
                user.setValidated(true);
                response.sendRedirect("../../Success.jsp");
            } else {
                response.sendRedirect("../../Failure.jsp");
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return Response.status(Response.Status.OK).build();
    }

    /**
     *
     * @param name
     * @param groups
     * @param request
     * @return
     */
    private String issueToken(String name, Set<String> groups, HttpServletRequest request) {
        try {
            Date now = new Date();
            Date expiration = Date.from(LocalDateTime.now().plusSeconds(15L).atZone(ZoneId.systemDefault()).toInstant());
            JwtBuilder jb = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("kid", "abc-1234567890")
                    .setSubject(name)
                    .setId("a-123")
                    //.setIssuer(issuer)
                    .claim("iss", issuer)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .claim("upn", name)
                    .claim("groups", groups)
                    .claim("aud", "aud")
                    .claim("auth_time", now)
                    .signWith(keyService.getPrivate());
            return jb.compact();
        } catch (InvalidKeyException t) {
            log.log(Level.SEVERE, "Failed to create token", t);
            throw new RuntimeException("Failed to create token", t);
        }
    }

    private String issueRefreshToken(String name, HttpServletRequest request) {
        Date now = new Date();
        Date expiration = Date.from(LocalDateTime.now().plusMinutes(90l).atZone(ZoneId.systemDefault()).toInstant());
        JwtBuilder jb = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("kid", "abc-1234567890")
                .setSubject(name)
                .setId("a-123")
                .claim("iss", issuer)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .claim("upn", name)
                .claim("aud", "aud")
                .claim("auth_time", now)
                .signWith(keyService.getPrivate());
        return jb.compact();
    }

    @Path("isTokenExpired")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response isTokenExpired(@Context HttpServletRequest request) {
        try {
            String[] arr = request.getHeader("refreshToken").split(" ");
            Claims claim = Jwts.parser().setSigningKey(keyService.getPrivate()).parseClaimsJws(arr[1]).getBody();
            String id = claim.getSubject();

            User user = em.createNamedQuery(User.FIND_USER_BY_ID, User.class).setParameter("id", id).getSingleResult();
            Set<String> groups = new HashSet<>();
            for (Group g : user.getGroups()) {
                groups.add(g.getName());
            }
            if (user != null && !(claim.getExpiration().getTime() - System.currentTimeMillis() <= 0)) {
                String accessToken = issueToken(id, groups, request);
                String refreshToken = issueRefreshToken(id, request);
                return Response.ok(user)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .header("refreshTokenHeader", "Bearer " + refreshToken)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    /**
     * Does an insert into the AUSER and AUSERGROUP tables.It creates a SHA-256
     * hash of the password and Base64 encodes it before the user is created in
     * the database.The authentication system will read the AUSER table when
     * doing an authentication.
     *
     * @param firstName
     * @param uid
     * @param email
     * @param lastName
     * @param pwd
     * @return
     */
    @POST
    @Path("create_user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("pwd") String pwd,
            @FormParam("email") String email) {
        User user = null;
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
        }

        if (user != null) {
            log.log(Level.INFO, "user already exists {0}", email);
            return Response.serverError().entity("Email already in use").build();
        } else {
            user = new User();
            user.setFirstName(firstName);
            user.setPassword(hasher.generate(pwd.toCharArray()));
            user.setEmail(email);
            user.setLastName(lastName);
            user.setValidated(Boolean.FALSE);
            Group usergroup = em.find(Group.class, Group.USER);
            user.getGroups().add(usergroup);
            user.setEmailVerificationHash(new EmailVerificationHash());
            user.setEmailHash(user.getEmailVerificationHash().getHash());

            ArrayList arrayList = new ArrayList<String>();
            arrayList.add(user.getEmail());
            arrayList.add(user.getEmailVerificationHash().getHash());

            mailService.onAsyncVerificationEmail(arrayList);

            return Response.ok(em.merge(user)).build();
        }
    }

    private void send2FactorKey(User user) {
        Random rand = new Random();
        String random = String.format("%04d%n", rand.nextInt(10000));

        List<String> list = new ArrayList<>();
        list.add("6969");
        list.add(user.getEmail());
        mailService.onAsyncTwoFactorEmail(list);
        EmailTwoFactorHash twofactorhash = new EmailTwoFactorHash("6969");
        user.setTwofactorHash(twofactorhash);
        em.merge(user);

    }

    @POST
    @Path("twofactor")
    public Response twofactor(
            @FormParam("email") @NotBlank String email,
            @FormParam("pwd") @NotBlank String pwd,
            @FormParam("2factorkey") @NotBlank String key,
            @Context HttpServletRequest request) {
        User user = null;
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
        }

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } else if (!user.getValidated()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(user.getUserid(), pwd));

        String hashedKey = DigestUtils.md5Hex("" + key);
        EmailTwoFactorHash userhash = user.getTwofactorHash();

        if (result.getStatus() == CredentialValidationResult.Status.VALID && hashedKey.equals(userhash.getHash()) && !userhash.isExpired()) {
            String token = issueToken(result.getCallerPrincipal().getName(),
                    result.getCallerGroups(), request);
            String refreshToken = issueRefreshToken(result.getCallerPrincipal().getName(),
                    request);
            return Response
                    .ok(user)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("refreshTokenHeader", "Bearer " + refreshToken)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Path("addboardmember")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.ADMIN, Group.BOARD})
    public Response addBoardMember(@FormParam("email") String email, @FormParam("position") String position
    ) {
        User user = null;
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null) {
            return Response.serverError().entity("Email doesn't exist").build();
        } else {
            Group boardGroup = em.find(Group.class, Group.BOARD);
            user.getGroups().add(boardGroup);
            AboutUsObject aboutUsObject = new AboutUsObject(user, position);
            em.persist(aboutUsObject);
        }
        return Response.ok(em.merge(user)).build();
    }

    @POST
    @Path("createadminuser")
    //@RolesAllowed({Group.ADMIN})
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdminUser(@FormParam("firstName") String firstName,
            @FormParam("pwd") String pwd,
            @FormParam("email") String email,
            @FormParam("lastName") String lastName
    ) {
        User user = null;
        try {
            user = em.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
        }

        if (user != null) {
            log.log(Level.INFO, "adminuser already exists {0}", email);
            return Response.serverError().entity("Email already in use").build();
        } else {
            user = new User();
            user.setFirstName(firstName);
            user.setPassword(hasher.generate(pwd.toCharArray()));
            user.setEmail(email);
            user.setLastName(lastName);
            Group admingroup = em.find(Group.class, Group.ADMIN);
            Group usergroup = em.find(Group.class, Group.USER);
            user.getGroups().add(usergroup);
            user.getGroups().add(admingroup);
            user.setValidated(Boolean.TRUE);

            String hashedKey = DigestUtils.md5Hex("0000");
            EmailTwoFactorHash hash = new EmailTwoFactorHash(hashedKey);
            user.setTwofactorHash(hash);

            return Response.ok(em.merge(user)).build();
        }
    }

    @GET
    @Path("currentuser")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser() {
        return em.find(User.class, principal.getName());
    }

    /**
     *
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("addrole")
    @RolesAllowed({Group.ADMIN})
    public Response addRole(@QueryParam("uid") String uid, @QueryParam("role") String role
    ) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try ( Connection c = dataSource.getConnection();  PreparedStatement psg = c.prepareStatement(INSERT_USERGROUP)) {
            psg.setString(1, role);
            psg.setString(2, uid);
            psg.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    /**
     *
     * @param role
     * @return
     */
    private boolean roleExists(String role) {
        boolean result = false;

        if (role != null) {
            switch (role) {
                case Group.ADMIN:
                case Group.USER:
                    result = true;
                    break;
            }
        }

        return result;
    }

    /**
     *
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("removerole")
    @RolesAllowed({Group.ADMIN})
    public Response removeRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        try ( Connection c = dataSource.getConnection();  PreparedStatement psg = c.prepareStatement(DELETE_USERGROUP)) {
            psg.setString(1, role);
            psg.setString(2, uid);
            psg.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().build();
    }

    /**
     *
     * @param uid
     * @param password
     * @param sc
     * @return
     */
    @PUT
    @Path("changepassword")
    @RolesAllowed({Group.USER, Group.ADMIN, Group.BOARD})
    public Response changePassword(@QueryParam("uid") String uid,
            @QueryParam("pwd") String password,
            @Context SecurityContext sc) {
        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;
        if (authuser == null || uid == null || (password == null || password.length() < 3)) {
            log.log(Level.SEVERE, "Failed to change password on user {0}", uid);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (authuser.compareToIgnoreCase(uid) != 0 && !sc.isUserInRole(Group.ADMIN)) {
            log.log(Level.SEVERE,
                    "No admin access for {0}. Failed to change password on user {1}",
                    new Object[]{authuser, uid});
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            User user = em.find(User.class, uid);
            user.setPassword(hasher.generate(password.toCharArray()));
            em.merge(user);
            return Response.ok().build();
        }
    }
}
