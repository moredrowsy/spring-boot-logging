package com.thuan.logging.entities;

import com.thuan.logging.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class RequestLog {
    @Id
    private String requestId;
    @Column(length = Constants.HEADER_LIMIT)
    private String headers;
    private String uri;
    private String className;
    private String methodName;
    @Column(length = Constants.ARGS_LIMIT)
    private String args;
    private String status;
    private Long completionTime;
}
