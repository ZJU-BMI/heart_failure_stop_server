package cn.edu.zju.bmi.repository;
import cn.edu.zju.bmi.entity.DAO.LabTest;
import cn.edu.zju.bmi.entity.DAO.key.LabTestPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
