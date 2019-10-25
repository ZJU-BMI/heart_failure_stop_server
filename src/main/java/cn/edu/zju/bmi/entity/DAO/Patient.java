package cn.edu.zju.bmi.entity.DAO;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="patient")
public class Patient {
    @Id
    @Column(name = "unified_patient_id")
    private String unifiedPatientID;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "ethnic_group")
    private String ethnicGroup;

}
