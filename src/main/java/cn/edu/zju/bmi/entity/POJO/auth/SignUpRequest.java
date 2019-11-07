package cn.edu.zju.bmi.entity.POJO.auth;

import lombok.Data;

@Data
public class SignUpRequest {
    private long uid;
    private String userName;
    private String password;
    private String role;
}
