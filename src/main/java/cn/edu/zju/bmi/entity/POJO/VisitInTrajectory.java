package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitInTrajectory {
    private String admissionTime;
    private String hospitalCode;
    private String visitType;
    private String visitID;
    private String hospitalName;
}
