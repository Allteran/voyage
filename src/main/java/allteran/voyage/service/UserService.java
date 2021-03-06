package allteran.voyage.service;

import allteran.voyage.domain.Role;
import allteran.voyage.domain.User;
import allteran.voyage.exception.IncorrectPasswordException;
import allteran.voyage.repo.UserRepo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findByPhone(s);
        if (user == null) {
            throw new UsernameNotFoundException("User with phone number " + s + " not found");
        }
        return user;
    }

    public boolean addUser(User user) {
        User userFromDb = findByPhone(user.getPhone());
        if(userFromDb != null) {
            return false;
        }
        user.setRoles(Collections.singleton(Role.USER));
        user.setCreationDate(LocalDate.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        userRepo.save(user);
        return true;
    }

    public User findByPhone(String phone) {
        return userRepo.findByPhone(phone);
    }

    public User findById(Long id) {
        return userRepo.findById(id).get();
    }

    public boolean isPasswordsMatches(String enteredPassword, String correctPassword) {
        return passwordEncoder.matches(enteredPassword, correctPassword);
    }

    public User updateUser(User userFromDb, User user){
        if(user.getNewPassword() != null){
            boolean isPasswordsMatches = isPasswordsMatches(user.getPassword(), userFromDb.getPassword());
            if(isPasswordsMatches) {
                userFromDb.setPassword(passwordEncoder.encode(user.getNewPassword()));
            } else {
                throw new IncorrectPasswordException("?????????????? ???????????? ??????????????");
            }
        }
        BeanUtils.copyProperties(user, userFromDb, "id", "password", "passwordConfirm");
        return userRepo.save(userFromDb);
    }


}
