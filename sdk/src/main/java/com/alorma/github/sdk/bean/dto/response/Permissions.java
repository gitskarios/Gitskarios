package com.alorma.github.sdk.bean.dto.response;

/**
 * Created by Bernat on 13/07/2014.
 */
public class Permissions {
    public boolean admin;
    public boolean push;
    public boolean pull;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Permissions{");
        sb.append("admin=").append(admin);
        sb.append(", push=").append(push);
        sb.append(", pull=").append(pull);
        sb.append('}');
        return sb.toString();
    }
}
