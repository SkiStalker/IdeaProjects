import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public class Main {
    static BigDecimal CalcDirectoryByteSize(String path) {
        BigDecimal byteSize = BigDecimal.ZERO;
        for (File file : Objects.requireNonNull(new File(path).listFiles())) {
            if (file.isDirectory()) {
                byteSize = byteSize.add(CalcDirectoryByteSize(file.getAbsolutePath()));
            } else {
                byteSize = byteSize.add(BigDecimal.valueOf(file.length()));
            }
        }
        return byteSize;
    }

    static public void main(String[] args) {
        try {
            BigDecimal byteSize = BigDecimal.ZERO.add(CalcDirectoryByteSize(args[0]));

            BigDecimal kbSize = byteSize.divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP);
            BigDecimal mbSize = kbSize.divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP);
            BigDecimal gbSize = mbSize.divide(BigDecimal.valueOf(1024), 3, RoundingMode.HALF_UP);
            System.out.printf("%s ---- %s bytes / %s Kb / %s Mb / %s Gb\n", args[0], byteSize,
                    kbSize,
                    mbSize,
                    gbSize
                    )
                    ;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Wrong input args");
        }
        catch (NullPointerException ex) {
            System.out.println("Can not find specified directory");
        }
    }
}
