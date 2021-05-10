package com.bugull.hithiumfarmweb.http.excelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.awt.image.ShortLookupTable;

public class PcsChargeDischargeShortConverter implements Converter<Short> {
    @Override
    public Class supportJavaTypeKey() {
        return Short.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Short convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return null;
    }

    @Override
    public CellData convertToExcelData(Short value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        switch (value){
            case 0:
                return new CellData("待机");
            case 1:
                return new CellData("充电");
            case 2:
                return new CellData("放电");
            case 3:
                return new CellData("停机");
            default:
                return new CellData("未知数据");
        }
    }
}
