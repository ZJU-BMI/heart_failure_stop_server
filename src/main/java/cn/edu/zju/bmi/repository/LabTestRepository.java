package cn.edu.zju.bmi.repository;
import cn.edu.zju.bmi.entity.DAO.LabTest;
import cn.edu.zju.bmi.entity.DAO.key.LabTestPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabTestRepository extends JpaRepository<LabTest, LabTestPrimaryKey> {
    List<LabTest> findTop2ByOrderByKeyUnifiedPatientID();
    List<LabTest> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                          String visitID,
                                                                                          String visitType,
                                                                                          String hospitalCode);
    List<LabTest> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndLabTestItemName(
            String unifiedPatientID, String visitID, String visitType, String hospitalCode, String itemName);
    List<LabTest> findByKeyUnifiedPatientIDAndLabTestItemName(
            String unifiedPatientID, String itemName);

    List<LabTest> findByLabTestItemCode(String code);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.LabTest(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.labTestNo, u.labTestItemCode, " +
            "u.labTestItemName, u.executeDate, u.units, u.result) from cn.edu.zju.bmi.entity.DAO.LabTest u " +
            "where (u.result <> null and u.result <> ?2) and u.labTestItemCode=?1")
    List<LabTest> findByItem(String labTestCode, String emptyString);
}
