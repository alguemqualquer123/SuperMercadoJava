package com.supermercado.util;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {

    private static final Logger logger = LoggerFactory.getLogger(SystemUtil.class);
    private static final String APP_NAME = "SuperMercadoPDV";
    private static final String RUN_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";

    public static boolean setStartupOnBoot(boolean enable) {
        try {
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "javaw.exe";
            String classpath = System.getProperty("java.class.path");
            // Assuming the main class is com.supermercado.SuperMercadoApp
            // In a real packaged jar, we would point to the jar or exe wrapper.
            // For now, valid for the current runtime context roughly.

            // However, registry commands are specific.
            // Using "reg add"

            if (enable) {
                // Determine current executable or standard startup command
                // Note: Getting the exact command to run the jar files robustly is complex.
                // We will try to register the currently running command line if possible, or
                // construct a generic java command.

                // For simplicity and robustness in this specific request context:
                String val;
                // If running from class files (IDE), this might point to a classes directory,
                // which won't work with -jar.
                // Handling IDE vs JAR environment:

                if (SystemUtil.class.getResource("SystemUtil.class").toString().startsWith("jar:")) {
                    // We are in a JAR
                    // val is already set roughly correctly above, but let's refine
                    String jarPath = new File(
                            SystemUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getAbsolutePath();
                    val = "\"" + javaBin + "\" -jar \"" + jarPath + "\"";
                } else {
                    // We are in an IDE / valid classpath
                    // It's hard to persist "IDE run" to startup.
                    // We'll log a warning and use a dummy or skip.
                    logger.warn(
                            "Ambiente de desenvolvimento detectado (não é um JAR). Funcionalidade 'Iniciar com o Windows' pode estar limitada.");
                    // Constructing the classpath based command
                    val = "\"" + javaBin + "\" -cp \"" + classpath + "\" com.supermercado.SuperMercadoApp";
                }

                // Use ProcessBuilder for better argument handling
                ProcessBuilder pb = new ProcessBuilder("reg", "add", RUN_KEY, "/v", APP_NAME, "/t", "REG_SZ", "/d", val,
                        "/f");
                Process p = pb.start();
                int result = p.waitFor();
                return result == 0;
            } else {
                ProcessBuilder pb = new ProcessBuilder("reg", "delete", RUN_KEY, "/v", APP_NAME, "/f");
                Process p = pb.start();
                int result = p.waitFor();
                return result == 0;
            }
        } catch (Exception e) {
            logger.error("Failed to update startup registry", e);
            return false;
        }
    }

    public static boolean isStartupOnBootEnabled() {
        try {
            ProcessBuilder pb = new ProcessBuilder("reg", "query", RUN_KEY, "/v", APP_NAME);
            Process p = pb.start();
            int result = p.waitFor();
            return result == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
