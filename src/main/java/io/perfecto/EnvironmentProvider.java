package io.perfecto;

public interface EnvironmentProvider {
  public String getEnv(String envName);
  public String getProp(String propName);
}
