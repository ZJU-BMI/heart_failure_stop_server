package cn.edu.zju.bmi.entity.DAO;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="lab_test_item_name_dict")
public class LabTestNameDict {
    @Id
    @Column(name = "lab_test_item_name")
    private String itemName;
}
