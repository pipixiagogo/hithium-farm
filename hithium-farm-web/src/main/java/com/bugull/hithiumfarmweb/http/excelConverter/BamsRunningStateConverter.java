package com.bugull.hithiumfarmweb.http.excelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class BamsRunningStateConverter implements Converter<Integer> {
    @Override
    public Class supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        switch (value){
            case -1:
                return new CellData("断开");
            case 0:
                return new CellData("充满");
            case 1:
                return new CellData("放空");
            case 2:
                return new CellData("告警");
            case 3:
                return new CellData("故障");
            case 4:
                return new CellData("正常");
            case 5:
                return new CellData("预警");
            case 6:
                return new CellData("跳机");
            case 7:
                return new CellData("待机");
            default:
                return new CellData("未知状态");
        }

    }
}
