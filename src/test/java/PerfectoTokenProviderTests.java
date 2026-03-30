import io.perfecto.EnvironmentProvider;
import io.perfecto.PerfectoTokenProvider;
import io.perfecto.TokenNotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class PerfectoTokenProviderTests {


  private static final Path TEMP_JSON_PATH = Path.of(System.getProperty("java.io.tmpdir"), "tokens.json");
  private static final EnvironmentProvider mockedProvider = Mockito.mock(EnvironmentProvider.class);

  private void createDummyFile(Path tokensFilePath) throws IOException {
    var jsonObject = new JSONObject();
    jsonObject.put("cloud1", "token123");
    jsonObject.put("cloud2", "token456");

    try (var file = new FileWriter(tokensFilePath.toString())) {
      file.write(jsonObject.toString());
    }
  }

  private void deleteDummyFile(Path tokensFilePath) throws IOException {
    Files.deleteIfExists(tokensFilePath);
  }


  @BeforeEach
  void setUp() throws IOException {
    Mockito.when(mockedProvider.getEnv("PERFECTO_TOKEN_STORAGE")).thenReturn(TEMP_JSON_PATH.toString());
    PerfectoTokenProvider.setEnvironmentProvider(mockedProvider);
    createDummyFile(TEMP_JSON_PATH);
  }

  @AfterEach
  void tearDown() throws IOException {
    deleteDummyFile(TEMP_JSON_PATH);
  }

  @Test
  void testGetTokenForCloud_Returns_Valid_Token_From_Storage_From_PERFECTO_TOKEN_STORAGE() {
    var token = PerfectoTokenProvider.getTokenForCloud("cloud1");
    assertEquals("token123", token);
    token = PerfectoTokenProvider.getTokenForCloud("cloud2");
    assertEquals("token456", token);
  }


  @Test
  void testGetTokenForCloud_Returns_Valid_Key_From_Storage_From_USER_HOME_When_PERFECTO_TOKEN_STORAGE_Is_Not_Set() throws IOException {

    var newTokensFile = Path.of(System.getProperty("java.io.tmpdir"), "tokens2.json");
    createDummyFile(newTokensFile);
    Mockito.when(mockedProvider.getEnv("PERFECTO_TOKEN_STORAGE")).thenReturn(null);
    Mockito.when(mockedProvider.getProp("user.home")).thenReturn(newTokensFile.toString());

    var token = PerfectoTokenProvider.getTokenForCloud("cloud1");
    assertEquals("token123", token);
    token = PerfectoTokenProvider.getTokenForCloud("cloud2");
    assertEquals("token456", token);
  }

  @Test
  void testGetTokenForCloud_Returns_Invalid_Key() {
    assertThrows(TokenNotFoundException.class, () -> PerfectoTokenProvider.getTokenForCloud("cloudX"));
  }


  @Test
  void testGetTokenForCloud_Returns_Valid_Key_From_New_Location() throws IOException {
    var newTokensFile = Path.of(System.getProperty("java.io.tmpdir"), "tokens2.json");
    createDummyFile(newTokensFile);
    var token = PerfectoTokenProvider.getTokenFromFile(newTokensFile.toString(), "cloud2");
    assertEquals("token456", token);
    deleteDummyFile(newTokensFile);
 }

  @Test
  void testGetTokenForCloud_Returns_FileNotFound_Exception() throws IOException {
    deleteDummyFile(TEMP_JSON_PATH);
    assertThrows(RuntimeException.class, () -> PerfectoTokenProvider.getTokenFromFile("missingFile", "cloud1"));
  }
}