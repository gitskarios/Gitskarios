package com.alorma.github.sdk.bean.dto.response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class Branch {
    public String name;
    public Commit commit;
    public Links _links;

	@Override
	public String toString() {
		return name;
	}
}
