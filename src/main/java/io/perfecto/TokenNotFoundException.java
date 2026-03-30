package io.perfecto;

/**
 * Thrown to indicate that a token for a specified cloud could not be found.
 */
public class TokenNotFoundException extends RuntimeException {
  /**
   * Constructs a new exception with details about the missing token.
   *
   * @param cloudName   the cloud name for which the token was missing
   * @param fileLocation the JSON file that was searched
   */
  public TokenNotFoundException(String cloudName, String fileLocation) {
    super(String.format("Token for cloud %s not found in storage %s", cloudName, fileLocation));
  }
}
