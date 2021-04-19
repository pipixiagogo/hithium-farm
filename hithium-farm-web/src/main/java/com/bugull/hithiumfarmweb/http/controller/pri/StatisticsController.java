package com.bugull.hithiumfarmweb.http.controller.pri;

import com.bugull.hithiumfarmweb.http.bo.CapacityNumBo;
import com.bugull.hithiumfarmweb.http.bo.StatisticBo;
import com.bugull.hithiumfarmweb.http.service.StatisticsService;
import com.bugull.hithiumfarmweb.utils.ResHelper;
import com.bugull.mongo.utils.StringUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.bugull.hithiumfarmweb.common.Const.*;

/**
 * 首页统计模块
 */
@RestController
@RequestMapping(value = "/statistics")
public class StatisticsController extends AbstractController{


    @Resource
    private StatisticsService statisticsService;
    /**
     * 根据时间获取注册的设备数量
     */
    @RequestMapping(value = "/statisticNetInNum", method = RequestMethod.POST)
    @ApiOperation("统计入网数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "月 ：MONTH  季度： QUARTER"),
            @ApiImplicitParam(name = "year",value = "年份")
    })
    public ResHelper<List<StatisticBo>> statisticNetInNum(@RequestParam(value = "type", required = false) String type,
                                                          @RequestParam(value = "year", required = false) Integer year) {
        if (StringUtil.isEmpty(type) || year == null) {
            return ResHelper.pamIll();
        }
        if (!MONTH.equals(type) && !QUARTER.equals(type)) {
            return ResHelper.pamIll();
        }
        if (year < MIN_YEAR || year > MAX_YEAR) {
            return ResHelper.pamIll();
        }
        return statisticsService.statisticNetInNum(type, year);
    }

    @RequestMapping(value = "/statisticCapacityNum",method = RequestMethod.POST)
    @ApiOperation("储能充放电量统计")
    public ResHelper<CapacityNumBo>statisticCapacityNum(){
        return statisticsService.statisticCapacityNum();
    }
}
