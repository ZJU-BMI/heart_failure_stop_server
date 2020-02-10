package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.DiagnosisPrimaryKey;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name="diagnosis")
@NoArgsConstructor
public class Diagnosis {
    @EmbeddedId
    private DiagnosisPrimaryKey key;

    @Column(name = "diagnosis_desc")
    private String diagnosisDesc;

    @Column(name = "diagnosis_code")
    private String diagnosisCode;

    @Column(name = "code_version")
    private String codeVersion;

    public Diagnosis(String unifiedPatientID, String hospitalCode, String visitType, String visitID,
                     String diagnosisType, String diagnosisNo, String diagnosisDesc, String diagnosisCode,
                     String codeVersion){
        this.key = new DiagnosisPrimaryKey(unifiedPatientID, hospitalCode,
                visitType, visitID, diagnosisType, diagnosisNo);
        this.diagnosisCode=diagnosisCode;
        this.codeVersion=codeVersion;
        this.diagnosisDesc=diagnosisDesc;
    }
}
