package com.yourname.portal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTaskNotification(String toEmail, String taskTitle, String location) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_admin_email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("New Task Assigned: " + taskTitle);
        message.setText("Hello,\n\nYou have been assigned a new task: " + taskTitle +
                "\nLocation: " + location +
                "\n\nPlease log in to the portal to view details and update your status.");

        mailSender.send(message);
    }
}