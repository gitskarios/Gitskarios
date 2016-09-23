package core.repositories;

import java.io.Serializable;

public class Permissions implements Serializable {

  public boolean admin;
  public boolean push;
  public boolean pull;

  public Permissions() {
    this.admin = false;
    this.push = false;
    this.pull = false;
  }
}
