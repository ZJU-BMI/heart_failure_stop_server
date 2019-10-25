package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.LabTestPrimaryKey;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="lab_test_result")
public class LabTest {
    @EmbeddedId
    private LabTestPrimaryKey key;

    @Column(name = "lab_test_item_code")
    private String labTestItemCode;

    @Column(name = "lab_test_item_name")
    private String labTestItemName;

    @Column(name = "execute_date")
    private Date executeDate;

    @Column(name = "units")
    private String units;

    @Column(name = "result")
    private String result;
}
