package com.thuan.logging.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ErrorLog {
    @Id
    private String requestId;
    private String methodName;
    private String exceptionClass;
    private String args;
    private String message;
    private String firstStack;

    @Transient
    private boolean hasThrown;
}