package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.OrdersPrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="orders")
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @EmbeddedId
    private OrdersPrimaryKey key;

    @Column(name = "repeat_indicator")
    private String repeatIndicator;

    @Column(name = "order_class")
    private String orderClass;

    @Column(name = "order_text")
    private String orderText;

    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "dosage_unit")
    private String dosageUnit;

    @Column(name = "administration")
    private String administration;

    @Column(name = "duration")
    private String duration;

    @Column(name = "duration_unit")
    private String durationUnits;

    @Column(name = "start_date_time")
    private Date startDateTime;

    @Column(name = "stop_date_time")
    private Date stopDateTime;

    @Column(name = "frequency_counter")
    private String freqCounter;

    @Column(name = "frequency_interval")
    private String freqInterval;

    @Column(name = "frequency_interval_unit")
    private String freqIntervalUnit;

    @Column(name = "frequency_detail")
    private String freqDetail;

    @Column(name = "frequency")
    private String frequency;
}
