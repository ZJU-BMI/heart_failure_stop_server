package cn.edu.zju.bmi.controller;

import cn.edu.zju.bmi.service.StateAccessService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import cn.edu.zju.bmi.support.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(path=PathName.STATE_MANAGEMENT)
@RolesAllowed("ROLE_USER")
public class StateAccessController {
    // 主管用户上次访问状态的存储和获取，由于这本质上并不是一种数据，只是一个缓存，所以我就不把这个数据存在数据库里了
    private StateAccessService stateAccessService;

    @Autowired
    public StateAccessController(StateAccessService stateAccessService){
        this.stateAccessService = stateAccessService;
    }

    @PostMapping(value = PathName.UPDATE_STATE)
    public StringResponse updateState(
            @RequestParam(ParameterName.USER_ID) String userID,
            @RequestParam(ParameterName.STATE_CONTENT) String stateContent){
        return stateAccessService.updateState(userID, stateContent);
    }

    @GetMapping(value = PathName.DOWNLOAD_STATE)
    public StringResponse downloadState(
            @RequestParam(ParameterName.USER_ID) String userID){
        return stateAccessService.downloadState(userID);
    }
}
