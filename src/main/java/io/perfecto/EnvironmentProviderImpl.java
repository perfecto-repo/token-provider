package io.perfecto;

public class EnvironmentProviderImpl implements EnvironmentProvider {

  @Override
  public String getEnv(String envName) {
    return System.getenv(envName);
  }

  @Override
  public String getProp(String propName) {
    return System.getProperty(propName);
  }
}
