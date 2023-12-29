package com.ding.coding.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shiding
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportDTO {

    @ExcelProperty(value = "id")
    private Integer id;

    @ExcelProperty(value = "名称")
    private String name;
}
