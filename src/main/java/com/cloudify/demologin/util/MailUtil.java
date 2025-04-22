package com.cloudify.demologin.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public MailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Password Reset OTP");
        
        String emailContent = "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 5px;'>"
                + "<h2 style='color: #333;'>Password Reset Request</h2>"
                + "<p>You have requested to reset your password. Please use the following One-Time Password (OTP) to complete the process:</p>"
                + "<h3 style='background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 24px; letter-spacing: 5px;'>" + otp + "</h3>"
                + "<p>This OTP is valid for 30 minutes only.</p>"
                + "<p>If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>"
                + "<p>Regards,<br>Cloudify Team</p>"
                + "</div>";
        
        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}
