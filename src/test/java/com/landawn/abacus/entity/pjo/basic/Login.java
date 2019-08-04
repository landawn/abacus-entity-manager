/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.pjo.basic;

import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class Login {
    private long id;
    private long accountId;
    private String loginId;
    private String loginPassword;
    private int status;
    private Timestamp lastUpdateTime;
    private Timestamp createTime;

    public Login() {
    }

    public Login(long id) {
        this();

        setId(id);
    }

    public Login(long accountId, String loginId, String loginPassword, int status, 
        Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setAccountId(accountId);
        setLoginId(loginId);
        setLoginPassword(loginPassword);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public Login(long id, long accountId, String loginId, String loginPassword, 
        int status, Timestamp lastUpdateTime, Timestamp createTime) {
        this();

        setId(id);
        setAccountId(accountId);
        setLoginId(loginId);
        setLoginPassword(loginPassword);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    @Type("long")
    public long getId() {
        return id;
    }

    public Login setId(long id) {
        this.id = id;

        return this;
    }

    @Type("long")
    public long getAccountId() {
        return accountId;
    }

    public Login setAccountId(long accountId) {
        this.accountId = accountId;

        return this;
    }

    @Type("String")
    public String getLoginId() {
        return loginId;
    }

    public Login setLoginId(String loginId) {
        this.loginId = loginId;

        return this;
    }

    @Type("String")
    public String getLoginPassword() {
        return loginPassword;
    }

    public Login setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;

        return this;
    }

    @Type("int")
    public int getStatus() {
        return status;
    }

    public Login setStatus(int status) {
        this.status = status;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Login setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    @Type("Timestamp")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public Login setCreateTime(Timestamp createTime) {
        this.createTime = createTime;

        return this;
    }

    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(accountId);
        h = 31 * h + Objects.hashCode(loginId);
        h = 31 * h + Objects.hashCode(loginPassword);
        h = 31 * h + Objects.hashCode(status);
        h = 31 * h + Objects.hashCode(lastUpdateTime);
        h = 31 * h + Objects.hashCode(createTime);

        return h;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Login) {
            final Login other = (Login) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(accountId, other.accountId)
                && Objects.equals(loginId, other.loginId)
                && Objects.equals(loginPassword, other.loginPassword)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", accountId=" + Objects.toString(accountId)
                 + ", loginId=" + Objects.toString(loginId)
                 + ", loginPassword=" + Objects.toString(loginPassword)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}
