package cn.edu.zju.bmi.entity.POJO.auth;

import lombok.Data;

@Data
public class SignUpRequest {
    private String realName;
    private String userName;
    private String password;
}
