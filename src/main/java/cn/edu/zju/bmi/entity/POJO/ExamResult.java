package cn.edu.zju.bmi.entity.POJO;

import lombok.Data;

import java.util.Date;

@Data
public class ExamResult {
    private String examName;
    private Date examTime;
    private String examPara;
    private String impression;
    private String description;
    public ExamResult(String examName, Date examTime, String exam_para, String impression, String description){
        this.examPara = exam_para;
        this.examName = examName;
        this.examTime = examTime;
        this.impression = impression;
        this.description = description;
    }
}
