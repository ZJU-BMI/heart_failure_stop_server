package cn.edu.zju.bmi.entity.DAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User {
    @Id
    @Column(name = "uid")
    private Long id;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;
}
