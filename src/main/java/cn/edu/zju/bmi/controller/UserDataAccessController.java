package cn.edu.zju.bmi.controller;

import cn.edu.zju.bmi.entity.DAO.User;
import cn.edu.zju.bmi.service.GetUserDataService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(path= PathName.USER_INFO_DATA)
@Secured("ROLE_USER")
public class UserDataAccessController {
    private GetUserDataService userDataService;

    @Autowired
    public UserDataAccessController(GetUserDataService userDataService){
        this.userDataService = userDataService;
    }

    @GetMapping(value = PathName.USER_INFO)
    public User getUserInfo(@RequestParam(ParameterName.USER_NAME) String userName){
        User user = userDataService.getUserData(userName);
        user.setPassword("");
        return user;
    }
}
