package com.urlshortner.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.urlshortner.Dto.LoginDTO;
import com.urlshortner.Dto.ResponseDTO;
import com.urlshortner.Entity.OTP;
import com.urlshortner.Entity.User;
import com.urlshortner.Exception.UrlshortnerException;
import com.urlshortner.Repository.OTPRepository;
import com.urlshortner.Repository.UserRepository;
import com.urlshortner.Utitlities.MessageData;
import com.urlshortner.Utitlities.Utils;

import jakarta.mail.internet.MimeMessage;

@Service(value = "userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OTPRepository otpRepository;

    public User registerUser(User user) throws UrlshortnerException {
        Optional<User> optional = userRepository.findByEmail(user.getEmail());
        if (optional.isPresent())
            throw new UrlshortnerException("User has registered already.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // System.out.println(user);
        return userRepository.save(user);
    }

    public User loginUser(LoginDTO loginDto) throws UrlshortnerException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UrlshortnerException("User is not registered."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new UrlshortnerException("Invalid Credentials.");
        return user;

    }

    public Boolean sendOtp(String email) throws Exception {
        userRepository.findByEmail(email).orElseThrow(() -> new UrlshortnerException("User is not registered."));
        MimeMessage mm = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mm, true);
        message.setTo(email);
        message.setSubject("Your OTP Code");
        String genOtp = Utils.generateOtp();
        OTP otp = new OTP(email, genOtp, LocalDateTime.now());
        otpRepository.save(otp);
        message.setText(MessageData.getMessageBodyForOTP(genOtp), true);
        javaMailSender.send(mm);
        return true;
    }

    public Boolean verifyOtp(String email, String otp) throws UrlshortnerException {
        OTP otpEntity = otpRepository.findById(email).orElseThrow(() -> new UrlshortnerException("OTP is expired."));
        if (!otpEntity.getOtpCode().equals(otp))
            throw new UrlshortnerException("OTP is incorrect.");
        return true;
    }

    @Scheduled(fixedRate = 60000)
    public void removeExpiredOTPs() {
        LocalDateTime expiry = LocalDateTime.now().minusMinutes(5);
        List<OTP> expiredOtp = otpRepository.findByCreationTimeBefore(expiry);
        if (!expiredOtp.isEmpty()) {
            otpRepository.deleteAll(expiredOtp);
        }
    }

    public ResponseDTO changePassword(LoginDTO loginDto) throws UrlshortnerException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UrlshortnerException("User is not registered."));
        user.setPassword(passwordEncoder.encode(loginDto.getPassword()));
        userRepository.save(user);
        return new ResponseDTO("Password Changed Successfully");
    }
}
