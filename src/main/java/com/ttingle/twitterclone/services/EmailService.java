package com.ttingle.twitterclone.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordUpdatedEmail(String recipientEmail, String recipientName){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Your Password Has Been Updated!");
        message.setText("Dear " + recipientName + ": \nYou are receiving this email as your password has been updated. If this wasn't you please click the link bellow to reset your password. \n(TBC not functional in this version)");

        javaMailSender.send(message);
    }

}
