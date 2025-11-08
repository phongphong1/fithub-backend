package fa.training.fithub.service.impl;

import fa.training.fithub.entity.Notification;
import fa.training.fithub.entity.User;
import fa.training.fithub.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private final String WARN_MESS = "Email notification service not support this method";

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @Override
    public void sendNotification(User recipient, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(recipient.getEmail());
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            logger.info("Email notification sent successfully to {}", recipient.getEmail());

        } catch (Exception e) {
            logger.error("Email notification sent failed", e);
        }
    }

    /**
     * Gửi email sử dụng template Thymeleaf
     *
     * @param recipient Người nhận
     * @param subject Chủ đề
     * @param templateName Tên file template (ví dụ: "welcome-email")
     * @param templateVariables Một Map chứa các biến (key) và giá trị (value) để chèn vào template
     */
    public void sendEmailWithTemplate(User recipient, String subject, String templateName, Map<String, Object> templateVariables) {
        Context context = new Context();
        context.setVariables(templateVariables);

        try {
            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipient.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            logger.info("Email notification sent successfully to {}", recipient.getEmail());

        } catch (Exception e) {
            logger.error("Email notification sent failed", e);
        }
    }

    @Override
    public void sendPushNotification(User recipient, String title, String body, Map<String, String> customData) {
        logger.warn(WARN_MESS);
    }

    @Override
    public List<Notification> getUnreadNotifications(User user) {
        logger.warn(WARN_MESS);
        return List.of();
    }

    @Override
    public void markAsRead(String notificationId) {
        logger.warn(WARN_MESS);
    }

    @Override
    public void markAllAsRead(User user) {
        logger.warn(WARN_MESS);
    }
}
