package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.time.Duration;
import java.time.Instant;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.repository.UserRepository;
import ingsoftware.gatinder.dto.AuthenticatedUserDto;
import ingsoftware.gatinder.dto.LoginDto;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.UserDto;

@Service
public class UserService {
    private static final Duration REMEMBER_TOKEN_DURATION = Duration.ofDays(2);

    @Autowired private UserRepository userRepository;
    @Autowired private ZoneService zoneService;
    @Autowired private PictureService pictureService;

    @Transactional public UserDto register(RegisterDto request) throws ErrorService {
        create(null, request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPassword(), request.getRepeatPassword(), request.getZoneId());
        return toDto(userRepository.findByEmail(request.getEmail()));
    }

    @Transactional public AuthenticatedUserDto authenticate(LoginDto request) throws ErrorService {
        User user = authenticate(request.getEmail(), request.getPassword());
        user.setRememberToken(UUID.randomUUID().toString());
        user.setRememberTokenExpiresAt(Instant.now().plus(REMEMBER_TOKEN_DURATION));
        userRepository.save(user);
        return new AuthenticatedUserDto(toDto(user), user.getRememberToken());
    }

    public UserDto findByRememberToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Optional<User> response = userRepository.findByRememberToken(token);
        if (response.isEmpty()) {
            return null;
        }
        User user = response.get();
        if (user.isDeleted() || user.getRememberTokenExpiresAt() == null
                || user.getRememberTokenExpiresAt().isBefore(Instant.now())) {
            return null;
        }
        return toDto(user);
    }

    @Transactional public void clearRememberToken(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        userRepository.findByRememberToken(token).ifPresent(user -> {
            user.setRememberToken(null);
            user.setRememberTokenExpiresAt(null);
            userRepository.save(user);
        });
    }

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        String pictureUrl = user.getPicture() == null ? null : "/pictures/user/" + user.getId();
        String zoneId = user.getZone() == null ? null : user.getZone().getId();
        return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                zoneId, pictureUrl, user.isDeleted());
    }

    public UserDto findDtoById(String userId) throws ErrorService {
        return toDto(findById(userId));
    }

    @Transactional public void create(MultipartFile file, String firstName, String lastName, String email, String password, String repeatedPassword, String zoneId) throws ErrorService {
        try {
            validate(firstName, lastName, email, password, repeatedPassword, zoneId);
            Zone zone = zoneService.findById(zoneId);
            try {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    throw new ErrorService("El correo electrónico ya está registrado");
                }
            } catch (NoResultException e) {
                throw new ErrorService("Error al verificar el correo electrónico");
                
            }
            User user = new User();
            user.setId(UUID.randomUUID().toString());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.setZone(zone);
            if (file != null && !file.isEmpty()) {
                Picture picture = pictureService.create(file);
                user.setPicture(picture);
            }
            userRepository.save(user);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al registrar el usuario");
        }
    }

    @Transactional public void update(MultipartFile file, String userId, String firstName, String lastName, String email, String password, String repeatedPassword, String zoneId) throws ErrorService {
        try {
            validate(firstName, lastName, email, password, repeatedPassword, zoneId);
            User user = findById(userId);
            Zone zone = zoneService.findById(zoneId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password);
            user.setZone(zone);
            if (file != null) {
                Picture picture = pictureService.update(user.getPicture().getId(), file);
                user.setPicture(picture);
            }
            userRepository.save(user);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al modificar el usuario");
        }
    }

    @Transactional public void deactivate(String userId) throws ErrorService {
        try {
            User user = findById(userId);
            user.setDeleted(true);
            userRepository.save(user);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al desactivar el usuario");
        }
    }

    @Transactional public void activate(String userId) throws ErrorService {
        try {
            User user = findById(userId);
            user.setDeleted(false);
            userRepository.save(user);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al activar el usuario");
        }
    }

    @Transactional public User authenticate(String email, String password) throws ErrorService {
        try {
            if (email == null || email.isEmpty()) {
                throw new ErrorService("El correo electrónico no puede ser nulo o vacío");
            }
            if (password == null || password.isEmpty()) {
                throw new ErrorService("La contraseña no puede ser nula o vacía");
            }
            User user = userRepository.findByEmailAndPassword(email, password);
            if (user == null) {
                throw new ErrorService("Correo electrónico o contraseña incorrectos");
            }
            if (user.isDeleted()) {
                throw new ErrorService("El usuario está desactivado");
            }
            return user;
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al iniciar sesión");
        }
    }

    public List<User> findAll() throws ErrorService {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar los usuarios");
        }
    }

    public User findById(String userId) throws ErrorService {
        try {
            if (userId == null || userId.isEmpty()) {
                throw new ErrorService("El ID del usuario no puede ser nulo o vacío");
            }
            Optional<User> response = userRepository.findById(userId);
            if (response.isPresent()) {
                return response.get();
            } else {
                throw new ErrorService("No se encontró el usuario con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al buscar el usuario");
        }
    }

    public void validate(String firstName, String lastName, String email, String password, String repeatedPassword, String zoneId) throws ErrorService {
        if (firstName == null || firstName.isEmpty()) {
            throw new ErrorService("El nombre no puede ser nulo o vacío");
        }
        if (lastName == null || lastName.isEmpty()) {
            throw new ErrorService("El apellido no puede ser nulo o vacío");
        }
        if (email == null || email.isEmpty()) {
            throw new ErrorService("El correo electrónico no puede ser nulo o vacío");
        }
        if (password == null || password.isEmpty()) {
            throw new ErrorService("La contraseña no puede ser nula o vacía");
        }
        if (!password.equals(repeatedPassword)) {
            throw new ErrorService("Las contraseñas no coinciden");
        }
        if (zoneId == null || zoneId.isEmpty()) {
            throw new ErrorService("La zona no puede ser nula o vacía");
        }
    }

}
