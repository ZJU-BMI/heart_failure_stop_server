package cn.edu.zju.bmi.service;

import cn.edu.zju.bmi.entity.DAO.User;
import cn.edu.zju.bmi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserDataService {
    private UserRepository userRepository;

    @Autowired
    public GetUserDataService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserData(String userName){
        return userRepository.findByUserName(userName).orElseThrow();
    }
}
