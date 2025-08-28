package musai.app.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from.name}")
    private String fromName;

    public void sendAdminNotification(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setFrom(String.format("%s <%s>", fromName, fromEmail));
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
