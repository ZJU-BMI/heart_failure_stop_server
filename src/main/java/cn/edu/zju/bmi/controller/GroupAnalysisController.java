package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.entity.POJO.VisitInfoForGroupAnalysis;
import cn.edu.zju.bmi.service.GroupAnalysisService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(path= PathName.GROUP_ANALYSIS_DATA)
@RolesAllowed("ROLE_USER")
public class GroupAnalysisController {
    private GroupAnalysisService groupAnalysisService;

    @Autowired
    public GroupAnalysisController(GroupAnalysisService groupAnalysisService){
        this.groupAnalysisService = groupAnalysisService;
    }

    @PostMapping(value = PathName.QUERY_WITH_FILTER)
    public List<VisitInfoForGroupAnalysis> updatePreprocess(
            @RequestParam(ParameterName.FILTER) String filter,
            @RequestParam(ParameterName.START_INDEX) String startIdxStr,
            @RequestParam(ParameterName.END_INDEX) String endIdxStr,
            @RequestParam(ParameterName.TIME_STAMP) String timeStampStr,
            @RequestParam(ParameterName.USER_NAME) String userName
    ) throws Exception {
        Long timeStamp = Long.valueOf(timeStampStr);
        Integer startIdx = Integer.valueOf(startIdxStr);
        Integer endIdx = Integer.valueOf(endIdxStr);
        return groupAnalysisService.getVisitInfoForGroupAnalysisList(filter, userName, timeStamp, startIdx, endIdx);
    }
}
