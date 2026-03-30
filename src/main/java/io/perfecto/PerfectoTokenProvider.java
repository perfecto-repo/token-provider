package io.perfecto;

import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.IOException;

/**
 * Provides Perfecto tokens for a given cloud.
 * <p>
 * Tokens are read from a JSON file stored locally. This provider can be
 * extended to handle token regeneration if the token has expired.
 * </p>
 */
public class PerfectoTokenProvider {

  private static final String FILE_NAME = "tokens.json";
  private static String absoluteFilePath;
  private static EnvironmentProvider environmentProvider = new EnvironmentProviderImpl();
  /**
   * Default constructor.
   * <p>
   * Initializes the PerfectoTokenProvider without loading tokens.
   * Tokens can be loaded later via a custom method if needed.
   * </p>
   */
  public PerfectoTokenProvider() {
  }

  public static void setEnvironmentProvider(EnvironmentProvider environmentProvider) {
    PerfectoTokenProvider.environmentProvider = environmentProvider;
  }
  /**
   * Returns the token associated with the given cloud name from the given file path.
   *
   * @param absoluteFilePath the absolute file path to the tokens.json file
   * @param cloudName        the name of the cloud
   * @return the token as a string
   * @throws TokenNotFoundException if no token is found for the given cloud
   */
  public static String getTokenFromFile(String absoluteFilePath, String cloudName) {
    PerfectoTokenProvider.absoluteFilePath = absoluteFilePath;
    return getTokenForCloud(cloudName);
  }

  /**
   * Returns the token associated with the given cloud name.
   * <p>
   * The lookup order for the JSON file is:
   * <ol>
   *   <li>Environment variable {@code PERFECTO_TOKEN_STORAGE}, if set</li>
   *   <li>Provided file location via {@link #getTokenFromFile(String, String)}</li>
   *   <li>User home directory + {@code tokens.json}</li>
   * </ol>
   *
   * @param cloudName the name of the cloud
   * @return the token as a string
   * @throws TokenNotFoundException if no token is found for the given cloud
   */
  public static String getTokenForCloud(String cloudName) {
    absoluteFilePath = environmentProvider.getEnv("PERFECTO_TOKEN_STORAGE");
    if (absoluteFilePath == null || absoluteFilePath.isEmpty())
      absoluteFilePath = environmentProvider.getProp("user.home");

    try (var reader = new FileReader(absoluteFilePath)) {
      var tokenizer = new JSONTokener(reader);
      var jsonObject = new JSONObject(tokenizer);

      var token = jsonObject.optString(cloudName, null);
      if (token == null) {
        throw new TokenNotFoundException(cloudName, absoluteFilePath);
      }
      return token;

    } catch (IOException e) {
      throw new RuntimeException("Error reading JSON file from location: " + absoluteFilePath, e);
    }
  }
}