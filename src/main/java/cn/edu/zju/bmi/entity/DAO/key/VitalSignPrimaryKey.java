package cn.edu.zju.bmi.entity.DAO.key;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class VitalSignPrimaryKey implements Serializable {
    @Column(name = "unified_patient_id")
    private String unifiedPatientID;

    @Column(name = "hospital_code")
    private String hospitalCode;

    @Column(name = "visit_type")
    private String visitType;

    @Column(name = "visit_id")
    private String visitID;

    @Column(name = "vital_sign")
    private String vitalSign;

    @Column(name = "vital_sign_no")
    private int vitalSignNo;
}