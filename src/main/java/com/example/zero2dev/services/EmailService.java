package com.example.zero2dev.services;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    public void sendVerificationEmail(String recipientEmail, String verificationUrl) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("Email Verification");
        helper.setText(buildVerificationEmail(verificationUrl), true);

        mailSender.send(message);
    }

    private String buildVerificationEmail(String verificationUrl) {
        return "<div style='font-family: Arial, sans-serif; background-color: #f4f7fa; padding: 30px 0;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);'>" +

                "<div style='text-align: center;'>" +
                "<img src='https://i.imgur.com/USuFnG4.png' alt='Zero2Dev Logo' style='width: 120px; height: auto; margin-bottom: 20px;'>" +
                "<h2 style='color: #0d6efd;'>Welcome to Zero2Dev!</h2>" +
                "<p style='color: #555; font-size: 16px;'>Dear User,</p>" +
                "</div>" +

                "<p style='color: #555; font-size: 16px;'>Thank you for registering with us. We are excited to have you join the Zero2Dev community, where you can enhance your coding skills with challenges and judge your code.</p>" +

                "<p style='color: #555; font-size: 16px;'>To complete your registration and start exploring, please verify your email address by clicking the button below:</p>" +

                "<div style='text-align: center; margin: 20px 0;'>" +
                "<a href='" + verificationUrl + "' style='display: inline-block; padding: 12px 30px; color: #fff; background-color: #0d6efd; text-decoration: none; font-size: 18px; border-radius: 5px; text-transform: uppercase;'>Verify Your Email</a>" +
                "</div>" +

                "<p style='color: #555; font-size: 16px;'>If you did not create an account, please ignore this email. Your account will not be activated.</p>" +

                "<hr style='border: 1px solid #f1f1f1; margin: 30px 0;'>" +

                "<p style='color: #555; font-size: 14px;'>Thank you,<br><strong style='color: #0d6efd;'>Hải Code Dạo</strong><br><i style='color: #777;'>Admin at Zero2Dev</i></p>" +

                "<p style='font-size: 12px; color: #aaa;'>This is an automated message, please do not reply to this email.</p>" +
                "</div>" +
                "</div>";
    }
}
