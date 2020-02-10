package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.LabTestPrimaryKey;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="lab_test_result")
@NoArgsConstructor
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

    public LabTest(String unifiedPatientID, String hospitalCode, String visitType, String visitID, String labTestNo,
                   String labTestCode, String labTestName, Date executeDate, String unit, String result){
        this.key = new LabTestPrimaryKey(unifiedPatientID, hospitalCode, visitType,
                visitID, labTestNo);
        this.labTestItemCode = labTestCode;
        this.labTestItemName = labTestName;
        this.executeDate = executeDate;
        this.units = unit;
        this.result = result;
    }
}
