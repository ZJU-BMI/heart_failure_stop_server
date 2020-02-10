package cn.edu.zju.bmi.entity.DAO;
import cn.edu.zju.bmi.entity.DAO.key.ExamPrimaryKey;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="exam")
public class Exam {
    @EmbeddedId
    private ExamPrimaryKey key;

    @Column(name = "exam_name")
    private String examName;

    @Column(name = "exam_date_time")
    private Date examDateTime;

    @Column(name = "exam_para")
    private String examPara;

    @Column(name = "description")
    private String description;

    @Column(name = "impression")
    private String impression;

    public Exam(String unifiedPatientID, String hospitalCode, String visitType, String visitID, String examNo,
                String examName, Date examDateTime, String examPara, String description, String impression){
        this.key = new ExamPrimaryKey(unifiedPatientID, hospitalCode, visitType, visitID,
                examNo);
        this.examDateTime = examDateTime;
        this.examName = examName;
        this.examPara = examPara;
        this.description = description;
        this.impression = impression;
    }
}
