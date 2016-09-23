package core.issues;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IssueSearchResponse {
  @SerializedName("total_count") private int totalCount;
  @SerializedName("incomplete_results") private boolean incompleteResults;
  @SerializedName("items") private List<Issue> issues;

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }

  public boolean isIncompleteResults() {
    return incompleteResults;
  }

  public void setIncompleteResults(boolean incompleteResults) {
    this.incompleteResults = incompleteResults;
  }

  public List<Issue> getIssues() {
    return issues;
  }

  public void setIssues(List<Issue> issues) {
    this.issues = issues;
  }
}
