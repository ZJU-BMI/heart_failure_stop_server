package cn.edu.zju.bmi.support;

import cn.edu.zju.bmi.entity.DAO.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SingleVisitInfo{
    PatientVisit visitInfo;
    List<Diagnosis> diagnosisList;
    Map<String, List<LabTest>> labTestList;
    List<Operation> operationList;
    Map<String, List<VitalSign>> vitalSignList;
    Map<String, List<Orders>> orderList1;
    Map<String, List<Orders>> orderList2;
    List<EchoCardioElements> echoCardioElementsList;
    List<Exam> examList;

    public SingleVisitInfo(PatientVisit visitInfo, List<Diagnosis> diagnosisList, List<Operation> operationList,
                           Map<String, List<VitalSign>> vitalSignList, Map<String, List<LabTest>> labTestList,
                           Map<String, List<Orders>> orderList1, Map<String, List<Orders>> orderList2,
                           List<EchoCardioElements> echoCardioElementsList, List<Exam> examList){
        this.visitInfo = visitInfo;
        this.diagnosisList = diagnosisList;
        this.labTestList = labTestList;
        this.operationList = operationList;
        this.vitalSignList = vitalSignList;
        this.orderList1 = orderList1;
        this.orderList2 = orderList2;
        this.echoCardioElementsList = echoCardioElementsList;
        this.examList = examList;
    }
}
