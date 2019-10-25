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
}
