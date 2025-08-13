import org.apache.commons.cli.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import java.security.*;
import java.util.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.text.*;
import java.io.*;

public class App {

  // functions stolen from the pack updater

  private static final byte[] HEADER_MAGIC = "ZBXNMB10".getBytes(StandardCharsets.UTF_8);

  public static boolean isEncrypted(FileInputStream fis) throws IOException {
        fis.getChannel().position(0);
        boolean result = Arrays.equals(fis.readNBytes(HEADER_MAGIC.length), HEADER_MAGIC);
        if (!result) fis.getChannel().position(0);
        return result;
  }

  public static InputStream wrapInputStream(FileInputStream fis) throws IOException {
        if (isEncrypted(fis)) {
            try (DataInputStream dis = new DataInputStream(fis)) {
                int versionMajor = dis.readInt();
                int versionMinor = dis.readInt();
                byte[] dContent;
                MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                byte[] key = dis.readNBytes(32);
                SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
                byte[] iv = Arrays.copyOfRange(sha256.digest(key), 0, 16);
                IvParameterSpec aesIv = new IvParameterSpec(iv);

                int len = dis.readInt();
                byte[] eContent = dis.readNBytes(len);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, aesKey, aesIv);
                dContent = cipher.doFinal(eContent);
                return new ByteArrayInputStream(dContent);
            } catch (Exception ex) {
                throw new IOException(ex);
            }
        } else {
            return fis;
        }
  }

  // end of functions stolen from pack updater

  public static void main(String[] args) throws java.io.FileNotFoundException, java.io.IOException {
        // parameter handling code stolen from stackoverflow
        Options options = new Options();

        Option input = new Option("f", "file", true, "file to decrypt");
        input.setRequired(true);
        options.addOption(input);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose 

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }


        String FilePath = cmd.getOptionValue("file");
        Path FilePath2 = Paths.get(cmd.getOptionValue("file"));

        // end of code stolen from StackOverflow

        try {
            FileInputStream inputFile = new FileInputStream(FilePath);
            Files.copy(wrapInputStream(inputFile), FilePath2, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (java.io.FileNotFoundException ex) {
            System.out.println("Error: file to decrypt not found");
        }
        catch (java.io.IOException ex) {
            System.out.println("Error: an I/O exception occured");
        }

    }

}
