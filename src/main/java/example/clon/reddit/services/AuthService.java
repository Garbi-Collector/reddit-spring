package example.clon.reddit.services;

import example.clon.reddit.dtos.RegisterRequest;
import example.clon.reddit.entities.NotificationEmail;
import example.clon.reddit.entities.User;
import example.clon.reddit.entities.VerificationToken;
import example.clon.reddit.repositories.UserRepository;
import example.clon.reddit.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private MailService mailService;

    public void signup(RegisterRequest registerRequest){
        // generar entidad user desabilitado
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        //guardar entidad user
        userRepository.save(user);

        //generar token para habilitar al usuario
        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please activate your Account",
                user.getEmail(),
                "thankyou for signing up to Spring Reddit, " +
                "please click on the below url toactivate your Account:" +
                "http://localhost:8080/api/auth/accountVerification/"+ token));
    }

    private String generateVerificationToken(User user) {
        //generacion de token para el verificador de tokens
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        //guardar entidad
        verificationTokenRepository.save(verificationToken);
        return token;

    }
}
