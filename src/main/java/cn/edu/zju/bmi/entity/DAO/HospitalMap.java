package cn.edu.zju.bmi.entity.DAO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="hospital_code_dict")
@AllArgsConstructor
@NoArgsConstructor
public class HospitalMap {
    @Id
    @Column(name = "hospital_code")
    private String hospitalCode;
    @Column(name = "hospitalName")
    private String hospitalName;
}
