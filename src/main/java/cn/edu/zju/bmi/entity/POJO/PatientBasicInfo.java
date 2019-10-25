package cn.edu.zju.bmi.entity.POJO;
import lombok.Data;

@Data
public class PatientBasicInfo {
    private String patientName;
    private String birthday;
    private String sex;
    private String ethnicGroup;
    public PatientBasicInfo(String patientName, String birthday, String sex, String ethnicGroup){
        this.patientName = patientName;
        this.birthday = birthday;
        this.sex = sex;
        this.ethnicGroup = ethnicGroup;
    }
}
