package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.bo.AreaInfoBo;
import com.bugull.hithiumfarmweb.http.bo.AreaNetInNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.service.RealTimeDataService;
import com.bugull.hithiumfarmweb.http.service.StatisticsService;
import com.bugull.hithiumfarmweb.http.vo.*;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.springframework.util.CollectionUtils;
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
    @Resource
    private RealTimeDataService realTimeDataService;

    @PostMapping(value = "/netInNum")
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
        return statisticsService.statisticNetInNum(areaNetInNumBo, getUser());
    }

    @PostMapping(value = "/capacityNum")
    public ResHelper<CapacityNumVo> statisticCapacityNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.statisticCapacityNum(areaInfoBo, getUser());
    }

    @PostMapping(value = "/alarmNum")
    public ResHelper<AlarmNumVo> statisticAlarmNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.statisticAlarmNum(areaInfoBo, getUser());
    }

    /**
     * TODO 每节约1度电，就相应节约了0.4千克标准煤，
     * <p>
     * 减少污染排放0.272千克碳粉尘、
     * <p>
     * 0.997千克二氧化碳(CO2)、
     * <p>
     * 0.03千克二氧化硫(SO2)、
     * <p>
     * 0.015千克氮氧化物(NOX)。
     */
    @PostMapping(value = "/saveEnergy")
    public ResHelper<SaveEnergyVo> saveEnergyNum(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.saveEnergyNum(areaInfoBo);
    }

    @PostMapping(value = "/runNumStatistic")
    public ResHelper<RunNumVO> runNumStatistic(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.runNumStatistic(areaInfoBo, getUser());
    }

    /**
     * TODO 总收益要从incomeEntity实体类获取  收益分析代码还要重写
     *
     * @param areaInfoBo
     * @return
     */
    @PostMapping(value = "/incomeStatistic")
    public ResHelper<IncomeStatisticVo> incomeStatistic(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.incomeStatistic(areaInfoBo, getUser());
    }

    @PostMapping(value = "/incomeOfDayStatistic")
    public ResHelper<Map<String, IncomeStatisticVo>> incomeStatistics(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.incomeStatistics(areaInfoBo, getUser());
    }

    @PostMapping(value = "/incomeStatisticOfAll")
    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> incomeStatisticOfAll(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.incomeStatisticOfAll(areaInfoBo, getUser());
    }

    @GetMapping(value = "/deviceOfIncomeStatistic")
    public ResHelper<Map<String, List<IncomeStatisticOfDayVo>>> deviceOfIncomeStatistic(@RequestParam(value = "deviceName") String deviceName) {
        if (StringUtils.isEmpty(deviceName)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!realTimeDataService.verificationDeviceName(deviceName, getUser())) {
                return ResHelper.error(NO_QUERY_PERMISSION);
            }
        }
        return statisticsService.deviceOfIncomeStatistic(deviceName);
    }

    @PostMapping(value = "/capacityNumOfDate")
    public ResHelper<Map<String, List<CapacityVo>>> capacityNumOfDate(@RequestBody AreaInfoBo areaInfoBo) {
        return statisticsService.capacityNumOfDate(getUser(), areaInfoBo);
    }

    @GetMapping(value = "/capacityNumOfStation")
    public ResHelper<ChargeCapacityStationVo> capacityNumOfStation(@RequestParam(value = "stationId", required = false) String stationId) {
        if (StringUtils.isEmpty(stationId)) {
            return ResHelper.pamIll();
        }
        if (!CollectionUtils.isEmpty(getUser().getStationList()) && !getUser().getStationList().isEmpty()) {
            if (!getUser().getStationList().contains(stationId)) {
                return ResHelper.error(NO_QUERY_PERMISSION);
            }
        }
        return statisticsService.capacityNumOfStation(stationId);
    }


}
