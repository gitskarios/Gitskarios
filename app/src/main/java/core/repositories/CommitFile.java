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

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRaw_url() {
    return raw_url;
  }

  public void setRaw_url(String raw_url) {
    this.raw_url = raw_url;
  }

  public String getBlob_url() {
    return blob_url;
  }

  public void setBlob_url(String blob_url) {
    this.blob_url = blob_url;
  }

  public String getPatch() {
    return patch;
  }

  public void setPatch(String patch) {
    this.patch = patch;
  }

  public String getSha() {
    return sha;
  }

  public void setSha(String sha) {
    this.sha = sha;
  }
}
