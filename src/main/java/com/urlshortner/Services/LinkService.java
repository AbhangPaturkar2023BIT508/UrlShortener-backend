package com.urlshortner.Services;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.urlshortner.Entity.Link;
import com.urlshortner.Entity.User;
import com.urlshortner.Repository.LinkRepository;
import com.urlshortner.Repository.UserRepository;
import com.urlshortner.Utitlities.MessageData;
import com.urlshortner.Utitlities.Utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service("linkService")
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public Link createLink(Link link) {
        // System.out.println(link);

        // Generate custom code if not provided
        if (link.getCustomCode() == null || link.getCustomCode().isEmpty()) {
            String generatedCode;
            do {
                generatedCode = Utils.generateRandomCode(8);
            } while (linkRepository.existsByCustomCode(generatedCode));

            link.setCustomCode(generatedCode);
        }

        // Set creation timestamp
        link.setCreatedAt(LocalDateTime.now());

        // Set activation status
        if (link.getActivateAt() == null || !link.getActivateAt().isAfter(LocalDateTime.now())) {
            link.setActive(true);
        } else {
            link.setActive(false); // Activation is scheduled
        }

        link.setClicks(0);
        return linkRepository.save(link);
    }

    public boolean isCustomCodeExists(String customCode) {
        boolean exists = linkRepository.existsByCustomCode(customCode);
        // System.out.println(exists);
        return exists;
    }

    // Scheduled: Activate links when activateAt time is reached
    @Scheduled(fixedRate = 60_000) // every 1 minute
    public void activateScheduledLinks() {
        LocalDateTime now = LocalDateTime.now();
        List<Link> linksToActivate = linkRepository.findByActiveFalseAndActivateAtBefore(now);

        for (Link link : linksToActivate) {
            link.setActive(true);
            linkRepository.save(link);
            // System.out.println("Activated scheduled link: " + link.getCustomCode());
        }
    }

    // Scheduled: Deactivate links when expired
    @Scheduled(fixedRate = 60_000) // every 1 minute
    public void deactivateExpiredLinks() {
        LocalDateTime now = LocalDateTime.now();
        List<Link> linksToDeactivate = linkRepository.findByActiveTrueAndExpiresAtBefore(now);

        for (Link link : linksToDeactivate) {
            link.setActive(false);
            linkRepository.save(link);
            // System.out.println("Deactivated expired link: " + link.getCustomCode());
        }
    }

    // Resolve redirection based on link status
    public URI getRedirectUri(String code, String frontendBaseUrl) {
        Optional<Link> optionalLink = linkRepository.findByCustomCode(code);

        if (optionalLink.isEmpty()) {
            return URI.create(frontendBaseUrl + "/invalid?reason=not_found");
        }

        Link link = optionalLink.get();

        if (!link.isActive()) {
            return URI.create(frontendBaseUrl + "/invalid?reason=inactive");
        }

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            return URI.create(frontendBaseUrl + "/invalid?reason=expired");
        }

        // Increment click count and redirect
        link.setClicks(link.getClicks() + 1);
        linkRepository.save(link);

        return URI.create(link.getOriginalUrl());
    }

    public List<Link> getAllLinks(String userId) {
        return linkRepository.findByUserId(userId);
    }

    public Optional<Link> getLinkById(String id) {
        return linkRepository.findById(id);
    }

    public void deleteLink(String id) {
        linkRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 8 * * *") // Runs daily at 8 AM
    // @Scheduled(cron = "0 * * * * *")
    public void notifyUsersBeforeExpiry() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayFromNow = now.plusDays(1);

        List<Link> expiringLinks = linkRepository.findByExpiresAtBetween(now, oneDayFromNow);

        for (Link link : expiringLinks) {
            if (link.isNotifyOn()) {
                Optional<User> userOpt = userRepository.findById(link.getUserId());

                if (userOpt.isPresent()) {
                    User user = userOpt.get();

                    String formattedExpiry = link.getExpiresAt()
                            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));

                    String htmlMessage = MessageData.getExpiryNotificationMessageBody(link.getCustomCode(),
                            formattedExpiry);

                    try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true);

                        helper.setTo(user.getEmail());
                        helper.setSubject("Your short link will expire soon");
                        helper.setText(htmlMessage, true); // true enables HTML

                        mailSender.send(message);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
