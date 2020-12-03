package no.ntnu.epsilon_backend.setup;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author mikael
 */
@Log
@Singleton
public class MailService {

    @Inject
    @ConfigProperty(name = "mail.smtp.host")
    String smtpHost;

    @Inject
    @ConfigProperty(name = "mail.smtp.username")
    String smtpUser;

    @Inject
    @ConfigProperty(name = "mail.smtp.password")
    String smtpPassword;

    /**
     * Send an email
     *
     * @param questionAsked received event
     */
    public void onAsyncMessage(@ObservesAsync String questionAsked) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", "587");
            Session mailSession = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setSubject("New Message");

            String reciever = "mobilefant@hotmail.com";
            if (reciever != null && reciever.length() > 0) {
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(reciever));
                mimeMessage.setFrom(new InternetAddress(smtpUser));
                mimeMessage.setText(questionAsked);
                Transport.send(mimeMessage);
            } else {
                log.log(Level.INFO, "Failed to find email for user {0}", reciever);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param verificationStrings
     */
    public void onAsyncVerificationEmail(@ObservesAsync List<String> verificationStrings) {
        try {

            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", "587");
                Session mailSession = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpUser, smtpPassword);
                    }
                });

                MimeMessage mimeMessage = new MimeMessage(mailSession);

                String hash = verificationStrings.get(1);

                String reciever = verificationStrings.get(0);
                if (reciever != null && reciever.length() > 0) {
                    mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(reciever));
                    mimeMessage.setFrom(new InternetAddress(smtpUser));
                    mimeMessage.setSubject("Epsilon Email Verification Link");
                    mimeMessage.setText("This link is valid for 30 minutes. Click this link to confirm your email address and complete setup for your account."
                            + "\n\nVerification Link: " + "https://epsilonbackend.uials.no/Epsilon_Backend/api/auth/activateAccount?key1=" + hash);
                    Transport.send(mimeMessage);
                } else {
                    log.log(Level.INFO, "Failed to find email for user {0}", reciever);
                }
            } catch (MessagingException ex) {
                Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
        }
    }

    /**
     *
     * @param verificationList
     */
    @Asynchronous
    public void onAsyncTwoFactorEmail(@ObservesAsync List<String> verificationList) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", "587");
            Session mailSession = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setSubject("Epsilon App security code");

            String reciever = verificationList.get(1);
            if (reciever != null && reciever.length() > 0) {
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(reciever));
                mimeMessage.setFrom(new InternetAddress(smtpUser));
                mimeMessage.setText("Hello, to complete login please enter the following verification code: \n" + verificationList.get(0));
                Transport.send(mimeMessage);
            } else {
                log.log(Level.INFO, "Failed to find email for user {0}", reciever);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param verificationList
     */
    @Asynchronous
    public void onAsyncForgotPassword(@ObservesAsync List<String> verificationList) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", "587");
            Session mailSession = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpUser, smtpPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setSubject("Epsilon App forgot password");

            String reciever = verificationList.get(1);
            if (reciever != null && reciever.length() > 0) {
                mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(reciever));
                mimeMessage.setFrom(new InternetAddress(smtpUser));
                mimeMessage.setText("Hello, your new password is: \n" + verificationList.get(0) + "\nYou may change your password after you have logged in.");
                Transport.send(mimeMessage);
            } else {
                log.log(Level.INFO, "Failed to find email for user {0}", reciever);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
