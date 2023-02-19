package com.thuan.logging.entities;

import com.thuan.logging.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@AllArgsConstructor
@ToString
@Data
public class ErrorLog {
    @Id
    private String requestId;
    private String className;
    private String methodName;
    private String args;
    private String exceptionClass;
    @Column(length = Constants.MSG_LIMIT)
    private String message;
    @Column(length = Constants.MSG_LIMIT)
    private String firstStack;

    @Transient
    private boolean hasThrown;

    public ErrorLog() {
        this.hasThrown = false;
    }
}
