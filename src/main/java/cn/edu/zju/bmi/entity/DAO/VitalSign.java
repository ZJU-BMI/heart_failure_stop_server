package cn.edu.zju.bmi.entity.DAO;
import cn.edu.zju.bmi.entity.DAO.key.VitalSignPrimaryKey;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="vital_sign")
public class VitalSign {
    @EmbeddedId
    private VitalSignPrimaryKey key;

    @Column(name = "record_time")
    private Date recordTime;

    @Column(name = "result")
    private double result;

    @Column(name = "unit")
    private String unit;
}
