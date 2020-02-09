package cn.edu.zju.bmi.entity.POJO;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
public class VisitIdentifier implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String unifiedPatientID;
    private String hospitalCode;
    private String visitID;
    private String visitType;
    public VisitIdentifier(String unifiedPatientID, String hospitalCode, String visitType, String visitID){
        this.unifiedPatientID = unifiedPatientID;
        this.hospitalCode = hospitalCode;
        this.visitID = visitID;
        this.visitType=visitType;
    }
}
