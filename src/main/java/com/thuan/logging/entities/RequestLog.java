package com.thuan.logging.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private String uri;
    private String status;
    private String methodName;
    private String args;
    private Long completionTime;
}
