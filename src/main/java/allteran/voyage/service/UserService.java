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
import java.util.HashSet;
import java.util.Set;

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
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.USER);
        user.setRoles(roles);
//        user.setRoles(Collections.singleton(Role.USER));
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
        boolean isPasswordsMatches = isPasswordsMatches(user.getPassword(), userFromDb.getPassword());
        if(user.getPassword() != null){
            if(isPasswordsMatches) {
                userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                throw new IncorrectPasswordException("Текущий введен неверно");
            }
        }
        BeanUtils.copyProperties(user, userFromDb, "id", "password", "passwordConfirm");
        return userRepo.save(userFromDb);
    }


}
