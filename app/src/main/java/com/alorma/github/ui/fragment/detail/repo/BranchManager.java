package com.alorma.github.ui.fragment.detail.repo;

import com.alorma.github.sdk.bean.dto.response.Branch;

/**
 * Created by Bernat on 09/08/2014.
 */
public interface BranchManager {
    void setCurrentBranch(Branch branch);
}
