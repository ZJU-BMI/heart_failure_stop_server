package cn.edu.zju.bmi.entity.DAO.key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ExamPrimaryKey implements Serializable {
    @Column(name = "unified_patient_id")
    private String unifiedPatientID;

    @Column(name = "hospital_code")
    private String hospitalCode;

    @Column(name = "visit_type")
    private String visitType;

    @Column(name = "visit_id")
    private String visitID;

    @Column(name = "exam_no")
    private String examNo;
}
