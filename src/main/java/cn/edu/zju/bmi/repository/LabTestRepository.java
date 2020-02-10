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
            "where (u.result <> null and u.result <> ?4) and u.labTestItemCode=?1 and " +
            "FUNCTION('CONVERT', u.result, FLOAT)<?3 and ?2<FUNCTION('CONVERT', u.result, FLOAT)")
    List<LabTest> findByItemAndValueBetween(String labTestCode, double lowValue, double highValue, String emptyString);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.LabTest(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.labTestNo, u.labTestItemCode, " +
            "u.labTestItemName, u.executeDate, u.units, u.result) from cn.edu.zju.bmi.entity.DAO.LabTest u " +
            "where (u.result <> null and u.result <> ?3) and u.labTestItemCode=?1 and " +
            "?2<FUNCTION('CONVERT', u.result, FLOAT)")
    List<LabTest> findByItemAndValueGreaterThan(String labTestCode, double lowValue, String emptyString);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.LabTest(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.labTestNo, u.labTestItemCode, " +
            "u.labTestItemName, u.executeDate, u.units, u.result) from cn.edu.zju.bmi.entity.DAO.LabTest u " +
            "where (u.result <> null and u.result <> ?3) and u.labTestItemCode=?1 and " +
            "FUNCTION('CONVERT', u.result, FLOAT)<?2")
    List<LabTest> findByItemAndValueLessThan(String labTestCode, double highValue, String emptyString);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.LabTest(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.labTestNo, u.labTestItemCode, " +
            "u.labTestItemName, u.executeDate, u.units, u.result) from cn.edu.zju.bmi.entity.DAO.LabTest u " +
            "where (u.result <> null and u.result <> ?3) and u.labTestItemCode=?1 and u.result=?2")
    List<LabTest> findByItemAndValueIs(String labTestCode, String value, String emptyString);
}
