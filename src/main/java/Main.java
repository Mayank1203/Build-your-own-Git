import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("No command provided. Use 'init' or 'cat-file'.");
      return;
    }

    final String command = args[0];
    final File gitDir = new File(".git");

    switch (command) {
      case "init" -> initializeGitRepo(gitDir);
      case "cat-file" -> {
        if (args.length < 3) {
          System.err.println("Usage: cat-file <option> <hash>");
        } else {
          readBlob(gitDir, args[1], args[2]);
        }
      }
      default -> System.out.println("Unknown command: " + command);
    }
  }

  private static void initializeGitRepo(File gitDir) {
    if (gitDir.exists()) {
      System.out.println("Git repository already initialized.");
      return;
    }

    try {
      new File(gitDir, "objects").mkdirs();
      new File(gitDir, "refs/heads").mkdirs();

      File headFile = new File(gitDir, "HEAD");
      headFile.createNewFile();
      Files.write(headFile.toPath(), "ref: refs/heads/main\n".getBytes());

      System.out.println("Initialized git directory");
    } catch (IOException e) {
      System.err.println("Failed to initialize git repository: " + e.getMessage());
    }
  }

  private static void readBlob(File gitDir, String option, String hash) {
    if (!gitDir.exists()) {
      System.err.println("Not a git repository. Please initialize with 'init'.");
      return;
    }

    if (!"-p".equals(option)) {
      System.err.println("Unsupported option: " + option + ". Only '-p' is supported.");
      return;
    }

    try {
      // Locate the object file in the .git/objects directory
      File objectFile = new File(gitDir, "objects/" + hash.substring(0, 2) + "/" + hash.substring(2));
      if (!objectFile.exists()) {
        System.err.println("Object not found: " + hash);
        return;
      }

      // Read and print the blob content
      byte[] content = Files.readAllBytes(objectFile.toPath());
      System.out.println(new String(content)); // Print blob content to stdout
    } catch (IOException e) {
      System.err.println("Error reading blob: " + e.getMessage());
    }
  }
}

