package com.example.marketplace.Utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
public class MailHelper {
    static final String senderEmail = "berqarat.tsaghik@gmail.com";
    static final String senderPassword = "rnid agqo vxvr lpvj";
    static String subject = "[Berqarat] Please Verify Your Account";

    public static void sendEmail(final String clientEmail, String OtpCode) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(clientEmail));
            message.setSubject(subject);
            message.setText("Hey!\nThank you for registering with Berqarat!" +
                    "To ensure the security of your account and to stay connected with us,"  +
                    "we kindly request you to verify your email address.\n\nVerification code: "+ OtpCode +
                    "\n\nThanks,\nThe Berqarat Team");

            new Thread(() -> {
                try {
                    Transport.send(message);
                    Log.d("MailSender", "Email sent successfully.");
                } catch (MessagingException e) {
                    Log.e("MailSender", "Error sending email: " + e.getMessage());
                }
            }).start();            Log.d("MailHelper", "Email sent successfully.");
        } catch (MessagingException e) {
            Log.e("MailSender", "Error creating MimeMessage: " + e.getMessage());
            FirebaseAuth.getInstance().signOut();
        }
    }
}

