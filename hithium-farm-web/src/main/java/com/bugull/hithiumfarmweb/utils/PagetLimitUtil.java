package com.bugull.hithiumfarmweb.utils;

import com.bugull.hithiumfarmweb.common.BuguPageQuery;
import com.bugull.hithiumfarmweb.common.Const;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class PagetLimitUtil {

    public static boolean pageLimit(BuguPageQuery<?> query, Map<String, Object> params) {
        //分页参数
        if (params.get(Const.PAGE) == null || params.get(Const.PAGESIZE) == null) {
            return false;
        }
        Integer page = Integer.parseInt((String) params.get(Const.PAGE));
        Integer pageSize = Integer.parseInt((String) params.get(Const.PAGESIZE));
        query.pageSize(pageSize).pageNumber(page);
        return true;
    }

    public static boolean orderField(BuguPageQuery<?> query,Map<String, Object> params){
        if (!StringUtils.isBlank((String) params.get("orderField"))) {
            Integer order = Integer.valueOf((String) params.get("order"));
            String orderField = (String) params.get("orderField");
            if (!ResHelper.checkOrder(order, orderField, Const.BREAKDOWNLOG_TABLE)) {
                return false;
            }
            if (params.get("order") == null) {
                return false;
            }
            if (order == 1) {
                query.sortAsc((String) params.get("orderField"));
            } else {
                query.sortDesc((String) params.get("orderField"));
            }
        }
        return true;
    }
}
