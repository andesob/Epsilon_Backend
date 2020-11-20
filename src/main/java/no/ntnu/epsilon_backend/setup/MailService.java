package no.ntnu.epsilon_backend.setup;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

            String reciever = "andrersu@stud.ntnu.no";
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
                System.out.println(hash);

                String reciever = verificationStrings.get(0);
                if (reciever != null && reciever.length() > 0) {
                    mimeMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(reciever));
                    mimeMessage.setFrom(new InternetAddress(smtpUser));
                    mimeMessage.setSubject("Epsilon Email Verification Link");
                    mimeMessage.setText("This link is valid for 30 minutes. Click this link to confirm your email address and complete setup for your account."
                            + "\n\nVerification Link: " + "http://localhost:8080/Epsilon_Backend/api/auth/activateAccount?key1=" + hash);
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
}
