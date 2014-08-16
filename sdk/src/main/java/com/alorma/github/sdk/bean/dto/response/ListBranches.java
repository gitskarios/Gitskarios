package com.alorma.github.sdk.bean.dto.response;

import java.util.ArrayList;

/**
 * Created by Bernat on 20/07/2014.
 */
public class ListBranches extends ArrayList<Branch>{
    public ListBranches(int capacity) {
        super(capacity);
    }

    public ListBranches() {
    }
}
