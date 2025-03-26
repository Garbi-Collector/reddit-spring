package example.clon.reddit.services;

import example.clon.reddit.dtos.AuthenticationResponse;
import example.clon.reddit.dtos.LoginRequest;
import example.clon.reddit.dtos.RegisterRequest;
import example.clon.reddit.entities.NotificationEmail;
import example.clon.reddit.entities.User;
import example.clon.reddit.entities.VerificationToken;
import example.clon.reddit.exceptions.SpringRedditException;
import example.clon.reddit.repositories.UserRepository;
import example.clon.reddit.repositories.VerificationTokenRepository;
import example.clon.reddit.security.JwtProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
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
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    @Transactional
    public void signup(RegisterRequest registerRequest){

        // Verificar si el usuario o el email ya están registrados
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new SpringRedditException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new SpringRedditException("Email is already in use");
        }

        // generar entidad user desabilitado
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        //guardar entidad user
        userRepository.save(user);

        //generar token para habilitar al usuario
        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please activate your Account",
                user.getEmail(),
                "thank y    ou for signing up to Spring Reddit, " +
                "please click on the below url toactivate your Account: " +
                "http://186.125.31.60:8080/api/auth/accountVerification/"+ token));
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

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()-> new SpringRedditException("invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    protected void fetchUserAndEnable(VerificationToken verificationToken){
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new SpringRedditException("user not found: "+ username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                loginRequest.getPassword())
                );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }
}
