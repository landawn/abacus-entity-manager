/*
 * Generated by Abacus.
 */
package com.landawn.abacus.entity.hbase;

import java.util.List;
import java.util.SortedSet;
import java.util.Set;
import java.util.LinkedHashSet;
import java.sql.Timestamp;
import java.util.Objects;
import com.landawn.abacus.util.HBaseColumn;
import com.landawn.abacus.util.N;
import com.landawn.abacus.annotation.Id;
import com.landawn.abacus.annotation.Column;
import com.landawn.abacus.entity.hbase.HbasePNL;
import com.landawn.abacus.annotation.Type;


/**
 * Generated by Abacus.
 * @version ${version}
 */
public class AccountContact implements HbasePNL.AccountContactPNL {

    @Id
    @Column("id")
    private HBaseColumn<Long> id;

    @Column("accountId")
    private List<HBaseColumn<Long>> accountId;

    @Column("telephone")
    private SortedSet<HBaseColumn<String>> telephone;

    @Column("city")
    private Set<HBaseColumn<String>> city;

    @Column("state")
    private LinkedHashSet<HBaseColumn<String>> state;

    @Column("zipCode")
    private List<HBaseColumn<String>> zipCode;

    @Column("status")
    private List<HBaseColumn<Integer>> status;

    @Column("lastUpdateTime")
    private List<HBaseColumn<Timestamp>> lastUpdateTime;

    @Column("createTime")
    private List<HBaseColumn<Timestamp>> createTime;

    public AccountContact() {
    }

    public AccountContact(HBaseColumn<Long> id) {
        this();

        setId(id);
    }

    public AccountContact(List<HBaseColumn<Long>> accountId, 
        SortedSet<HBaseColumn<String>> telephone, 
        Set<HBaseColumn<String>> city, 
        LinkedHashSet<HBaseColumn<String>> state, 
        List<HBaseColumn<String>> zipCode, List<HBaseColumn<Integer>> status, 
        List<HBaseColumn<Timestamp>> lastUpdateTime, 
        List<HBaseColumn<Timestamp>> createTime) {
        this();

        setAccountId(accountId);
        setTelephone(telephone);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    public AccountContact(HBaseColumn<Long> id, List<HBaseColumn<Long>> accountId, 
        SortedSet<HBaseColumn<String>> telephone, 
        Set<HBaseColumn<String>> city, 
        LinkedHashSet<HBaseColumn<String>> state, 
        List<HBaseColumn<String>> zipCode, List<HBaseColumn<Integer>> status, 
        List<HBaseColumn<Timestamp>> lastUpdateTime, 
        List<HBaseColumn<Timestamp>> createTime) {
        this();

        setId(id);
        setAccountId(accountId);
        setTelephone(telephone);
        setCity(city);
        setState(state);
        setZipCode(zipCode);
        setStatus(status);
        setLastUpdateTime(lastUpdateTime);
        setCreateTime(createTime);
    }

    @Type("HBaseColumn<Long>")
    public HBaseColumn<Long> getId() {
        return id;
    }

    public AccountContact setId(HBaseColumn<Long> id) {
        this.id = id;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<Long> id() {
        return (HBaseColumn<Long>) (this.id == null ? HBaseColumn.emptyOf(long.class) : id);
    }

    public AccountContact setId(long value) {
        setId(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setId(long value, long version) {
        setId(HBaseColumn.valueOf(value, version));

        return this;
    }

    @Type("List<HBaseColumn<Long>>")
    public List<HBaseColumn<Long>> getAccountId() {
        return accountId;
    }

    public AccountContact setAccountId(List<HBaseColumn<Long>> accountId) {
        this.accountId = accountId;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<Long> accountId() {
        return (HBaseColumn<Long>) (N.isNullOrEmpty(accountId) ? HBaseColumn.emptyOf(long.class) : accountId.iterator().next());
    }

    public AccountContact setAccountId(long value) {
        setAccountId(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setAccountId(long value, long version) {
        setAccountId(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setAccountId(HBaseColumn<Long> hbaseColumn) {
        if (accountId == null) {
            accountId = N.newInstance(java.util.List.class);
        } else {
            accountId.clear();
        }

        accountId.add(hbaseColumn);

        return this;
    }

    public AccountContact addAccountId(long value) {
        addAccountId(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addAccountId(long value, long version) {
        addAccountId(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addAccountId(HBaseColumn<Long> hbaseColumn) {
        if (accountId == null) {
            accountId = N.newInstance(java.util.List.class);
        }

        accountId.add(hbaseColumn);

        return this;
    }

    @Type("SortedSet<HBaseColumn<String>>")
    public SortedSet<HBaseColumn<String>> getTelephone() {
        return telephone;
    }

    public AccountContact setTelephone(SortedSet<HBaseColumn<String>> telephone) {
        this.telephone = telephone;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<String> telephone() {
        return (HBaseColumn<String>) (N.isNullOrEmpty(telephone) ? HBaseColumn.emptyOf(String.class) : telephone.iterator().next());
    }

    public AccountContact setTelephone(String value) {
        setTelephone(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setTelephone(String value, long version) {
        setTelephone(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setTelephone(HBaseColumn<String> hbaseColumn) {
        if (telephone == null) {
            telephone = new java.util.TreeSet<HBaseColumn<String>>(HBaseColumn.DESC_HBASE_COLUMN_COMPARATOR);
        } else {
            telephone.clear();
        }

        telephone.add(hbaseColumn);

        return this;
    }

    public AccountContact addTelephone(String value) {
        addTelephone(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addTelephone(String value, long version) {
        addTelephone(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addTelephone(HBaseColumn<String> hbaseColumn) {
        if (telephone == null) {
            telephone = new java.util.TreeSet<HBaseColumn<String>>(HBaseColumn.DESC_HBASE_COLUMN_COMPARATOR);
        }

        telephone.add(hbaseColumn);

        return this;
    }

    @Type("Set<HBaseColumn<String>>")
    public Set<HBaseColumn<String>> getCity() {
        return city;
    }

    public AccountContact setCity(Set<HBaseColumn<String>> city) {
        this.city = city;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<String> city() {
        return (HBaseColumn<String>) (N.isNullOrEmpty(city) ? HBaseColumn.emptyOf(String.class) : city.iterator().next());
    }

    public AccountContact setCity(String value) {
        setCity(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setCity(String value, long version) {
        setCity(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setCity(HBaseColumn<String> hbaseColumn) {
        if (city == null) {
            city = N.newInstance(java.util.Set.class);
        } else {
            city.clear();
        }

        city.add(hbaseColumn);

        return this;
    }

    public AccountContact addCity(String value) {
        addCity(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addCity(String value, long version) {
        addCity(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addCity(HBaseColumn<String> hbaseColumn) {
        if (city == null) {
            city = N.newInstance(java.util.Set.class);
        }

        city.add(hbaseColumn);

        return this;
    }

    @Type("LinkedHashSet<HBaseColumn<String>>")
    public LinkedHashSet<HBaseColumn<String>> getState() {
        return state;
    }

    public AccountContact setState(LinkedHashSet<HBaseColumn<String>> state) {
        this.state = state;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<String> state() {
        return (HBaseColumn<String>) (N.isNullOrEmpty(state) ? HBaseColumn.emptyOf(String.class) : state.iterator().next());
    }

    public AccountContact setState(String value) {
        setState(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setState(String value, long version) {
        setState(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setState(HBaseColumn<String> hbaseColumn) {
        if (state == null) {
            state = N.newInstance(java.util.LinkedHashSet.class);
        } else {
            state.clear();
        }

        state.add(hbaseColumn);

        return this;
    }

    public AccountContact addState(String value) {
        addState(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addState(String value, long version) {
        addState(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addState(HBaseColumn<String> hbaseColumn) {
        if (state == null) {
            state = N.newInstance(java.util.LinkedHashSet.class);
        }

        state.add(hbaseColumn);

        return this;
    }

    @Type("List<HBaseColumn<String>>")
    public List<HBaseColumn<String>> getZipCode() {
        return zipCode;
    }

    public AccountContact setZipCode(List<HBaseColumn<String>> zipCode) {
        this.zipCode = zipCode;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<String> zipCode() {
        return (HBaseColumn<String>) (N.isNullOrEmpty(zipCode) ? HBaseColumn.emptyOf(String.class) : zipCode.iterator().next());
    }

    public AccountContact setZipCode(String value) {
        setZipCode(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setZipCode(String value, long version) {
        setZipCode(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setZipCode(HBaseColumn<String> hbaseColumn) {
        if (zipCode == null) {
            zipCode = N.newInstance(java.util.List.class);
        } else {
            zipCode.clear();
        }

        zipCode.add(hbaseColumn);

        return this;
    }

    public AccountContact addZipCode(String value) {
        addZipCode(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addZipCode(String value, long version) {
        addZipCode(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addZipCode(HBaseColumn<String> hbaseColumn) {
        if (zipCode == null) {
            zipCode = N.newInstance(java.util.List.class);
        }

        zipCode.add(hbaseColumn);

        return this;
    }

    @Type("List<HBaseColumn<Integer>>")
    public List<HBaseColumn<Integer>> getStatus() {
        return status;
    }

    public AccountContact setStatus(List<HBaseColumn<Integer>> status) {
        this.status = status;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<Integer> status() {
        return (HBaseColumn<Integer>) (N.isNullOrEmpty(status) ? HBaseColumn.emptyOf(int.class) : status.iterator().next());
    }

    public AccountContact setStatus(int value) {
        setStatus(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setStatus(int value, long version) {
        setStatus(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setStatus(HBaseColumn<Integer> hbaseColumn) {
        if (status == null) {
            status = N.newInstance(java.util.List.class);
        } else {
            status.clear();
        }

        status.add(hbaseColumn);

        return this;
    }

    public AccountContact addStatus(int value) {
        addStatus(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addStatus(int value, long version) {
        addStatus(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addStatus(HBaseColumn<Integer> hbaseColumn) {
        if (status == null) {
            status = N.newInstance(java.util.List.class);
        }

        status.add(hbaseColumn);

        return this;
    }

    @Type("List<HBaseColumn<Timestamp>>")
    public List<HBaseColumn<Timestamp>> getLastUpdateTime() {
        return lastUpdateTime;
    }

    public AccountContact setLastUpdateTime(List<HBaseColumn<Timestamp>> lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<Timestamp> lastUpdateTime() {
        return (HBaseColumn<Timestamp>) (N.isNullOrEmpty(lastUpdateTime) ? HBaseColumn.emptyOf(Timestamp.class) : lastUpdateTime.iterator().next());
    }

    public AccountContact setLastUpdateTime(Timestamp value) {
        setLastUpdateTime(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setLastUpdateTime(Timestamp value, long version) {
        setLastUpdateTime(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setLastUpdateTime(HBaseColumn<Timestamp> hbaseColumn) {
        if (lastUpdateTime == null) {
            lastUpdateTime = N.newInstance(java.util.List.class);
        } else {
            lastUpdateTime.clear();
        }

        lastUpdateTime.add(hbaseColumn);

        return this;
    }

    public AccountContact addLastUpdateTime(Timestamp value) {
        addLastUpdateTime(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addLastUpdateTime(Timestamp value, long version) {
        addLastUpdateTime(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addLastUpdateTime(HBaseColumn<Timestamp> hbaseColumn) {
        if (lastUpdateTime == null) {
            lastUpdateTime = N.newInstance(java.util.List.class);
        }

        lastUpdateTime.add(hbaseColumn);

        return this;
    }

    @Type("List<HBaseColumn<Timestamp>>")
    public List<HBaseColumn<Timestamp>> getCreateTime() {
        return createTime;
    }

    public AccountContact setCreateTime(List<HBaseColumn<Timestamp>> createTime) {
        this.createTime = createTime;

        return this;
    }

    /**
     * Returns the (first) column or an empty column if it's null.
     */
    public HBaseColumn<Timestamp> createTime() {
        return (HBaseColumn<Timestamp>) (N.isNullOrEmpty(createTime) ? HBaseColumn.emptyOf(Timestamp.class) : createTime.iterator().next());
    }

    public AccountContact setCreateTime(Timestamp value) {
        setCreateTime(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact setCreateTime(Timestamp value, long version) {
        setCreateTime(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact setCreateTime(HBaseColumn<Timestamp> hbaseColumn) {
        if (createTime == null) {
            createTime = N.newInstance(java.util.List.class);
        } else {
            createTime.clear();
        }

        createTime.add(hbaseColumn);

        return this;
    }

    public AccountContact addCreateTime(Timestamp value) {
        addCreateTime(HBaseColumn.valueOf(value));

        return this;
    }

    public AccountContact addCreateTime(Timestamp value, long version) {
        addCreateTime(HBaseColumn.valueOf(value, version));

        return this;
    }

    public AccountContact addCreateTime(HBaseColumn<Timestamp> hbaseColumn) {
        if (createTime == null) {
            createTime = N.newInstance(java.util.List.class);
        }

        createTime.add(hbaseColumn);

        return this;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + Objects.hashCode(id);
        h = 31 * h + Objects.hashCode(accountId);
        h = 31 * h + Objects.hashCode(telephone);
        h = 31 * h + Objects.hashCode(city);
        h = 31 * h + Objects.hashCode(state);
        h = 31 * h + Objects.hashCode(zipCode);
        h = 31 * h + Objects.hashCode(status);
        h = 31 * h + Objects.hashCode(lastUpdateTime);
        h = 31 * h + Objects.hashCode(createTime);

        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AccountContact) {
            final AccountContact other = (AccountContact) obj;

            return Objects.equals(id, other.id)
                && Objects.equals(accountId, other.accountId)
                && Objects.equals(telephone, other.telephone)
                && Objects.equals(city, other.city)
                && Objects.equals(state, other.state)
                && Objects.equals(zipCode, other.zipCode)
                && Objects.equals(status, other.status)
                && Objects.equals(lastUpdateTime, other.lastUpdateTime)
                && Objects.equals(createTime, other.createTime);
        }

        return false;
    }

    @Override
    public String toString() {
         return "{id=" + Objects.toString(id)
                 + ", accountId=" + Objects.toString(accountId)
                 + ", telephone=" + Objects.toString(telephone)
                 + ", city=" + Objects.toString(city)
                 + ", state=" + Objects.toString(state)
                 + ", zipCode=" + Objects.toString(zipCode)
                 + ", status=" + Objects.toString(status)
                 + ", lastUpdateTime=" + Objects.toString(lastUpdateTime)
                 + ", createTime=" + Objects.toString(createTime)
                 + "}";
    }
}
