package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import cn.edu.zju.bmi.entity.DAO.User;
import cn.edu.zju.bmi.entity.POJO.auth.ApiResponse;
import cn.edu.zju.bmi.entity.POJO.auth.JwtAuthenticationResponse;
import cn.edu.zju.bmi.entity.POJO.auth.LoginRequest;
import cn.edu.zju.bmi.entity.POJO.auth.SignUpRequest;
import cn.edu.zju.bmi.repository.UserRepository;
import cn.edu.zju.bmi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(PathName.AUTH)
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping(PathName.SIGNUP_USER_EXIST_TEST)
    public String userExistTest(@RequestParam(ParameterName.USER_NAME) String userName) {
        boolean isExist = userRepository.existsByUserName(userName);
        if(isExist)
            return "true";
        else
            return "false";
    }

    @PostMapping(PathName.LOGIN)
    public ResponseEntity<?> userLogin(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(PathName.SIGNUP)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUserName(signUpRequest.getUserName())) {
            return new ResponseEntity<>(new ApiResponse(false, "account name is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        List<User> list = userRepository.findTop1ByOrderByIdDesc();
        Long uid = null;
        if (list.size()==0)
            uid = 1l;
        else
            uid = list.get(0).getId()+1;

        // Creating user's account
        User user = new User(uid, signUpRequest.getUserName(),signUpRequest.getRealName(),
                signUpRequest.getPassword(), "TBC");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
