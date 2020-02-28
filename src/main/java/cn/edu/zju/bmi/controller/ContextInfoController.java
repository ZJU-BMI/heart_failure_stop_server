package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.service.CodingServices;
import cn.edu.zju.bmi.support.PathName;
import cn.edu.zju.bmi.support.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping(path= PathName.CONTEXT_INFO)
@RolesAllowed("ROLE_USER")
public class ContextInfoController {
    // 本模型中存在诸多编码（ICD-9，10,药物编码等）
    // 本controller负责向前端发送相关编码信息
    private CodingServices codingServices;
    @Autowired
    public ContextInfoController(CodingServices codingServices){
        this.codingServices = codingServices;
    }
    @GetMapping(value = PathName.DIAGNOSIS_CODE)
    public StringResponse getDiagnosisCode(){
        return new StringResponse(codingServices.getDiagnosisCodingListJson());
    }

    @GetMapping(value = PathName.OPERATION_CODE)
    public StringResponse getOperationCode(){
        return new StringResponse(codingServices.getOperationCodingListJson());
    }

    @GetMapping(value = PathName.MEDICINE_CODE)
    public StringResponse getMedicineCode(){
        return new StringResponse(codingServices.getMedicineCodingListJson());
    }

    @GetMapping(value = PathName.LAB_TEST_CODE)
    public StringResponse getLabTestCode(){
        return new StringResponse(codingServices.getLabTestCodingListJson());
    }
}
