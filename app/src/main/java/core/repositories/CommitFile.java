package core.repositories;

public class CommitFile extends GitChangeStatus {

  public String filename;
  public String status;
  public String raw_url;
  public String blob_url;
  public String patch;
  public String sha;

  public CommitFile() {
  }

}
