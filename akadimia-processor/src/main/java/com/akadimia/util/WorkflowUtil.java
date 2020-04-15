package com.akadimia.util;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class WorkflowUtil {


    public static void main(String[] args) {

    }
    public static void sendMail(String emailId, String subject, String content) throws Exception {

        Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        Session mailSession = Session.getDefaultInstance(emailProperties, null);

        String[] toEmails = { "2019ht12471@wilp.bits-pilani.ac.in" , emailId};
        MimeMessage emailMessage = new MimeMessage(mailSession);
        /**
         * Set the mail recipients
         * */
        for (int i = 0; i < toEmails.length; i++)
        {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
        }
        /**
         * If sending HTML mail
         * */
        emailMessage.setSubject(subject);
        emailMessage.setContent(content, "text/html");

        /**
         * Sender's credentials
         * */
        String fromUser = "kunalkumar.chowdhury@gmail.com";
        String fromUserEmailPassword = "Gopa2slping#*";

        String emailHost = "smtp.gmail.com";
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserEmailPassword);

        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }

    public static String getReplyMsg(String id, String status){
        return id +","+status;
    }

}
