package cn.edu.zju.bmi.service;


import cn.edu.zju.bmi.entity.DAO.User;
import cn.edu.zju.bmi.exception.ResourceNotFoundException;
import cn.edu.zju.bmi.repository.UserRepository;
import cn.edu.zju.bmi.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        User user = userRepository.findByUserName(name)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username : " + name)
                );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );

        return UserPrincipal.create(user);
    }
}