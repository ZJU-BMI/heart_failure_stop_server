package cn.edu.zju.bmi.entity.DAO;
import cn.edu.zju.bmi.entity.DAO.key.PatientVisitPrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="patient_visit")
@AllArgsConstructor
@NoArgsConstructor
public class PatientVisit {
    @EmbeddedId
    private PatientVisitPrimaryKey key;

    @Column(name = "admission_date_time")
    private Date admissionDateTime;

    @Column(name = "discharge_date_time")
    private Date dischargeDateTime;

    @Column(name = "dept_name_admission_to")
    private String deptNameAdmissionTo;

    @Column(name = "dept_name_discharge_from")
    private String deptNameDischargeFrom;

    @Column(name = "death_flag")
    private String deathFlag;
}
