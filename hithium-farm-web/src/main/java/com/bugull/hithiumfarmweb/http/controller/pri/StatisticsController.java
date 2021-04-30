package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.bo.AreaInfoBo;
import com.bugull.hithiumfarmweb.http.bo.AreaNetInNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.service.StatisticsService;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.bugull.hithiumfarmweb.common.Const.*;

/**
 * 首页统计模块
 */
@RestController
@RequestMapping(value = "/statistics")
public class StatisticsController extends AbstractController {


    @Resource
    private StatisticsService statisticsService;

    @RequestMapping(value = "/netInNum", method = RequestMethod.POST)
    @ApiOperation(value = "统计总功率")
    @ApiImplicitParam(name = "areaNetInNumBo", value = "地区实体类", required = true, paramType = "body", dataTypeClass = AreaNetInNumBo.class, dataType = "AreaNetInNumBo")
    public ResHelper<List<StatisticBo>> statisticNetInNum(@RequestBody AreaNetInNumBo areaNetInNumBo) {
        if (areaNetInNumBo == null || StringUtils.isEmpty(areaNetInNumBo.getType()) || areaNetInNumBo.getYear() == null) {
            return ResHelper.pamIll();
        }
        if (!MONTH.equals(areaNetInNumBo.getType()) && !QUARTER.equals(areaNetInNumBo.getType())) {
            return ResHelper.pamIll();
        }
        if (areaNetInNumBo.getYear() < MIN_YEAR || areaNetInNumBo.getYear() > MAX_YEAR) {
            return ResHelper.pamIll();
        }
        return statisticsService.statisticNetInNum(areaNetInNumBo);
    }

    @RequestMapping(value = "/capacityNum", method = RequestMethod.POST)
    @ApiOperation(value = "储能充放电量统计")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", required = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<CapacityNumVo> statisticCapacityNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.statisticCapacityNum(areaInfoBo);
    }

    @RequestMapping(value = "/alarmNum", method = RequestMethod.POST)
    @ApiOperation(value = "待办告警数量")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", required = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<AlarmNumVo> statisticAlarmNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.statisticAlarmNum(areaInfoBo);
    }

    /**
     * 每节约1度电，就相应节约了0.4千克标准煤，
     * <p>
     * 减少污染排放0.272千克碳粉尘、
     * <p>
     * 0.997千克二氧化碳(CO2)、
     * <p>
     * 0.03千克二氧化硫(SO2)、
     * <p>
     * 0.015千克氮氧化物(NOX)。
     */
    @RequestMapping(value = "/saveEnergy", method = RequestMethod.POST)
    @ApiOperation(value = "节能减排数据")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", readOnly = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<SaveEnergyVo> saveEnergyNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.saveEnergyNum(areaInfoBo);
    }

    @PostMapping(value = "/runNumStatistic")
    @ApiOperation(value = "设备运行非运行数量统计")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", readOnly = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<RunNumVO> runNumStatistic(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.runNumStatistic(areaInfoBo);
    }

    @RequestMapping(value = "/incomeStatistic", method = RequestMethod.POST)
    @ApiOperation(value = "获取总收益")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", readOnly = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<IncomeStatisticVo> incomeStatistic(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.incomeStatistic(areaInfoBo);
    }

    @RequestMapping(value = "/incomeOfDayStatistic", method = RequestMethod.POST)
    @ApiOperation(value = "获取天、月、年收益")
    @ApiImplicitParam(name = "areaInfoBo", value = "地区实体类", readOnly = true, paramType = "body", dataTypeClass = AreaInfoBo.class, dataType = "AreaInfoBo")
    public ResHelper<Map<String,IncomeStatisticVo>> incomeStatistics(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.incomeStatistics(areaInfoBo);
    }





}
