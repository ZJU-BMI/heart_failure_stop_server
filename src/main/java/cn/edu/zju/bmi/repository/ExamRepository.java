package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, String> {
    List<Exam> findTop2ByOrderByKeyUnifiedPatientID();
    List<Exam> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                       String visitID,
                                                                                       String visitType,
                                                                                       String hospitalCode);
    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.Exam(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.examNo, u.examName, u.examDateTime, u.examPara, u.description," +
            "u.impression) from cn.edu.zju.bmi.entity.DAO.Exam u where u.examName=?1")
    List<Exam> findEchoCardioGram(String type);
}
