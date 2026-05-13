import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class PythonRunner {
    private final String pythonCommand;
    private final String scriptPath;
    private final String workingDirectory;

    public PythonRunner(String pythonCommand, String scriptPath, String workingDirectory) {
        this.pythonCommand = pythonCommand;
        this.scriptPath = scriptPath;
        this.workingDirectory = workingDirectory;
    }

    public void run() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(pythonCommand, scriptPath);

        if (workingDirectory != null) {
            pb.directory(new File(workingDirectory));
        }

        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException(
                    "Python script failed with exit code: "
                            + exitCode
                            + "\n\nPython output:\n"
                            + output
            );
        }
    }
}