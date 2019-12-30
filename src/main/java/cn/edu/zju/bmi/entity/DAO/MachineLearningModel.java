package cn.edu.zju.bmi.entity.DAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="machine_learning_model")
public class MachineLearningModel {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "main_category")
    private String mainCategory;

    @Column(name = "model_chinese_name")
    private String modelChineseName;

    @Column(name = "model_english_name")
    private String modelEnglishName;

    @Column(name = "model_chinese_function_name")
    private String modelChineseFunctionName;

    @Column(name = "model_english_function_name")
    private String modelEnglishFunctionName;

    @Column(name = "platform")
    private String platform;

    @Column(name = "access_control")
    private String accessControl;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    public MachineLearningModel(String user, String modelCategory, String modelChineseName, String modelEnglishName,
                                String modelFunctionChinese, String modelFunctionEnglish, String platform,
                                String accessControl, Date createTime, Date lastUpdateTime){
        this.createUser=user;
        this.mainCategory=modelCategory;
        this.modelChineseFunctionName=modelFunctionChinese;
        this.modelChineseName=modelChineseName;
        this.modelEnglishFunctionName=modelFunctionEnglish;
        this.platform=platform;
        this.accessControl=accessControl;
        this.createTime=createTime;
        this.lastUpdateTime=lastUpdateTime;
        this.modelEnglishName=modelEnglishName;
    }
}
