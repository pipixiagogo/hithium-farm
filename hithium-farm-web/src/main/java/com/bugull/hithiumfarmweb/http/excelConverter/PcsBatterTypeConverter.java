package com.bugull.hithiumfarmweb.http.excelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.poi.xwpf.usermodel.BreakType;
import sun.awt.geom.AreaOp;

public class PcsBatterTypeConverter implements Converter<Short> {
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
               return new CellData("未知类型");
           case 1:
               return new CellData("钛酸锂电池");
           case 2:
               return new CellData("磷酸铁锂电池");
           default:
               return new CellData("未知类型");
       }
    }
}
