/*
 * Copyright (c) 2015, Haiyang Li. All rights reserved.
 */

package com.landawn.abacus.impl;

/**
 * 
 * @since 0.8
 * 
 * @author Haiyang Li
 */
public final class MySqlDef {
    public static final String insertAccount = "INSERT INTO account (first_name, last_name, gui, last_update_time, create_time) VALUES ( ?,  ?,  ?,  ?,  ?) ";
    public static final String selectAccountById = "select * from account WHERE account.id = ?";
    public static final String selectFirstNameFromAccountById = "select first_name from account WHERE account.id = ?";
    public static final String updateAccountFirstNameById = "UPDATE account SET first_name = ? WHERE account.id = ?";
    public static final String deleteAccountById = "DELETE FROM account WHERE account.id = ?";
    public static final String deleteAllAccount = "DELETE FROM account";
}
