package com.bugull.hithium.core.dto;

import lombok.Data;

/**
 * Pcc并网口
 */
@Data
public class PccsInfo {
    private Integer stationId;
    private String description;
    private String name;
    /**
     * ProjectId、StationUniqueId、IsCloud无用
     */
    private Integer projectId;
    private Integer stationUniqueId;
    private boolean isCloud;
    private Integer id;
}
