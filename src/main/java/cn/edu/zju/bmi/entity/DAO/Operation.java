package cn.edu.zju.bmi.entity.DAO;

import cn.edu.zju.bmi.entity.DAO.key.OperationPrimaryKey;
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
@AllArgsConstructor
@NoArgsConstructor
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
