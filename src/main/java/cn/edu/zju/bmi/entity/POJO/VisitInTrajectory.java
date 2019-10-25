package cn.edu.zju.bmi.entity.POJO;
import lombok.Data;

@Data
public class VisitInTrajectory {
    private String visitNo;
    private String admissionTime;
    private String hospitalCode;
    private String visitType;
    private String visitID;

    public VisitInTrajectory(String visitNo, String admissionTime, String hospitalCode, String visitType,
                             String visitID){
        this.visitNo = visitNo;
        this.admissionTime = admissionTime;
        this.hospitalCode = hospitalCode;
        this.visitType = visitType;
        this.visitID = visitID;
    }
}
