package example.clon.reddit.controllers;

import example.clon.reddit.dtos.AuthenticationResponse;
import example.clon.reddit.dtos.LoginRequest;
import example.clon.reddit.dtos.RegisterRequest;
import example.clon.reddit.exceptions.SpringRedditException;
import example.clon.reddit.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la autenticación y registro de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Registra un nuevo usuario en la plataforma.
     *
     * @param registerRequest Objeto que contiene los datos de registro del usuario.
     * @return ResponseEntity con un mensaje de éxito o un mensaje de error en caso de conflicto.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be null or empty");
        }

        try {
            authService.signup(registerRequest);
            return ResponseEntity.ok("User registration successful. Please check your email to activate your account.");
        } catch (SpringRedditException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict si el usuario ya existe
        }
    }

    /**
     * Verifica la cuenta del usuario a partir del token de activación.
     *
     * @param token Token de verificación enviado al correo del usuario.
     * @return ResponseEntity con un mensaje de confirmación de activación de cuenta.
     */
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}
