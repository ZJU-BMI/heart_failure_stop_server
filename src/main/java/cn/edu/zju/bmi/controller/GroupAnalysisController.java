package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.entity.POJO.SexInfo;
import cn.edu.zju.bmi.entity.POJO.VisitInfoForGroupAnalysis;
import cn.edu.zju.bmi.service.GroupAnalysisService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import cn.edu.zju.bmi.support.StringResponse;
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

    @PostMapping(value = PathName.GET_VISIT_INFO)
    public List<VisitInfoForGroupAnalysis> getVisitInfo(
            @RequestParam(ParameterName.FILTER) String filter,
            @RequestParam(ParameterName.START_INDEX) String startIdxStr,
            @RequestParam(ParameterName.END_INDEX) String endIdxStr,
            @RequestParam(ParameterName.QUERY_ID) String queryID,
            @RequestParam(ParameterName.USER_NAME) String userName
    ) throws Exception {
        Integer startIdx = Integer.valueOf(startIdxStr);
        Integer endIdx = Integer.valueOf(endIdxStr);
        return groupAnalysisService.getVisitInfoForGroupAnalysisList(filter, userName, queryID, startIdx,
                endIdx);
    }

    @PostMapping(value = PathName.QUERY_WITH_FILTER)
    public StringResponse queryWithFilter(
            @RequestParam(ParameterName.FILTER) String filter,
            @RequestParam(ParameterName.QUERY_ID) String queryID,
            @RequestParam(ParameterName.USER_NAME) String userName
    ) throws Exception {
        // 返回的是该查询对应的visit数量
        return groupAnalysisService.queryDataAccordingToFilter(filter, userName, queryID);
    }

    @PostMapping(value = PathName.QUERY_WITH_FATHER_QUERY_AND_NEW_CONDITION)
    public StringResponse queryWithFatherQueryAndNewCondition(
            @RequestParam(ParameterName.FILTER) String filter,
            @RequestParam(ParameterName.FATHER_QUERY_ID) String fatherQueryID,
            @RequestParam(ParameterName.NEW_CONDITION) String newCondition,
            @RequestParam(ParameterName.QUERY_ID) String queryID,
            @RequestParam(ParameterName.USER_NAME) String userName
    ) throws Exception {
        return groupAnalysisService.queryDataByFatherQueryAndNewCondition(filter, userName, fatherQueryID, queryID,
                newCondition);
    }

    @PostMapping(value = PathName.GET_SEX_INFO)
    public SexInfo sexInfo(
            @RequestParam(ParameterName.FILTER) String filter,
            @RequestParam(ParameterName.QUERY_ID) String queryID,
            @RequestParam(ParameterName.USER_NAME) String userName
    ) throws Exception {
        return groupAnalysisService.getSexInfo(filter, userName, queryID);
    }
}
