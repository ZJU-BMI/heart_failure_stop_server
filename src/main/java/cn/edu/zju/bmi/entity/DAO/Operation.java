package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.OperationPrimaryKey;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name="operation")
public class Operation {
    @EmbeddedId
    private OperationPrimaryKey key;

    @Column(name = "operation_desc")
    private String operationDesc;

    @Column(name = "operation_code")
    private String operationCode;

    @Column(name = "operating_date")
    private Date operationDate;
}
