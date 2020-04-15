package com.akadimia.util;

// Java program to send email

import com.sun.mail.smtp.SMTPTransport;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;
import javax.mail.Transport;


public class SendEmail
{
    /*Session mailSession;

    public static void main(String args[]) throws AddressException, MessagingException
    {
        SendEmail javaEmail = new SendEmail();
        javaEmail.setMailServerProperties();
        javaEmail.draftEmailMessage();
        javaEmail.sendEmail();
    }

    private void setMailServerProperties()
    {
        Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        mailSession = Session.getDefaultInstance(emailProperties, null);
    }

    private MimeMessage draftEmailMessage() throws AddressException, MessagingException
    {
        String[] toEmails = { "2019ht12471@wilp.bits-pilani.ac.in" };
        String emailSubject = "Test email subject";
        String emailBody = "This is an email sent by <b>//howtodoinjava.com</b>.";
        MimeMessage emailMessage = new MimeMessage(mailSession);
        *//**
         * Set the mail recipients
         * *//*
        for (int i = 0; i < toEmails.length; i++)
        {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails[i]));
        }
        emailMessage.setSubject(emailSubject);
        *//**
         * If sending HTML mail
         * *//*
        emailMessage.setContent(emailBody, "text/html");
        *//**
         * If sending only text mail
         * *//*
        //emailMessage.setText(emailBody);// for a text email
        return emailMessage;
    }

    private void sendEmail() throws AddressException, MessagingException
    {
        *//**
         * Sender's credentials
         * *//*
        String fromUser = "kunalkumar.chowdhury@gmail.com";
        String fromUserEmailPassword = "Gopa2slping#*";

        String emailHost = "smtp.gmail.com";
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        *//**
         * Draft the message
         * *//*
        MimeMessage emailMessage = draftEmailMessage();
        *//**
         * Send the mail
         * *//*
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }*/
}
