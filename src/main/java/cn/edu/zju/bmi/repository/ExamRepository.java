package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, String> {
    List<Exam> findTop2ByOrderByKeyUnifiedPatientID();
    List<Exam> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                       String visitID,
                                                                                       String visitType,
                                                                                       String hospitalCode);
}
