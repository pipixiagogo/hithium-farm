package com.bugull.hithiumfarmweb.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {

    private int page;
    private int pageSize;
    private long totalRecord;
    private int totalPage;
    private List<T> datas;

    public Page(int page, int pageSize, long totalRecord, List<T> datas) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
        this.datas = datas;
        this.totalPage = (int) Math.ceil((double) totalRecord / (double) pageSize);
    }

//    //总页数
//    private Long totalPage;
//
//    //总记录数
//    private Long totalCount;
//
//    //每页显示集合
//    private List<T> rows;

}
