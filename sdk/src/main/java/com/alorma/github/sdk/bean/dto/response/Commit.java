package com.alorma.github.sdk.bean.dto.response;

public class Commit extends ShaUrl{

    private Commit commit;
    public User author;
    public ListShaUrl parents;
    public User committer;
	public String message;

	@Override
	public String toString() {
		return commit != null ? commit.message : "No message";
	}
}