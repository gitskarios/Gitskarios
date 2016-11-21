package core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearchRequest {
  private List<SearchItem> items = new ArrayList<>();

  public SearchRequest() {

  }

  public String build() {
    StringBuilder builder = new StringBuilder();

    for (SearchItem item : items) {
      builder.append(" ");
      builder.append(item.toString());
      builder.append(" ");
    }

    return builder.toString().trim();
  }

  public void add(SearchItem item) {
    this.items.add(item);
  }

  public void add(Collection<SearchItem> items) {
    this.items.addAll(items);
  }
}
