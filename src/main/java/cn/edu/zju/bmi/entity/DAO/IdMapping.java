package cn.edu.zju.bmi.entity.DAO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="id_mapping")
public class IdMapping {
    @Id
    @Column(name = "unified_patient_id")
    private String unifiedPatientID;

    @Column(name = "hospital_code")
    private String hospitalCode;

    @Column(name = "hospital_pat_id")
    private String hospitalPatID;
}
