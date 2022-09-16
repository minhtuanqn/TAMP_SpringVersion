package com.tamp_backend.constant;

/**
 * Information enum of user in system
 */
public class UserEnum {

    /**
     * Enum of all roles of user
     */
    public enum RoleEnum
    {
        SYSTEM_ADMIN,
        SUPPLIER,
        PARTNER,
        AFFILIATOR,
        SHIPPER,
    }

    /**
     * Enum of all wallet type of all roles
     */
    public enum WalletTypeEnum
    {
        SYSTEM,
        SUPPLIER,
        PARTNER,
        AFFILIATOR
    }
}
