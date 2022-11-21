import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.ui.MessageType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;

/**
 * @author: zx
 * @date: 2022/10/11
 */
public class JavaExecutePythonUtils {
    public static Process proc;
    private static String pythonFileName = "post-detect.py";
    private static String pluginP = "";

    /**
     * 解压jar包
     *
     * @param jarpath
     * @param targetDir
     */
    public static void UnAllFile(String jarpath, String targetDir) {
        if (jarpath == null || targetDir == null) {
            try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                fileWriter.append("jarpath参数为空");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw new NullPointerException("参数为空");
        }
        try {
            File file = new File(jarpath);
            JarFile jar = new JarFile(file);
            // fist get all directories,
            // then make those directory on the destination Path
            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) enums.nextElement();
                String fileName = targetDir + File.separator + entry.getName();
                File f = new File(fileName);
                if (fileName.endsWith("/")) {
                    f.mkdirs();
                }
            }
            //now create all files
            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) enums.nextElement();
                String fileName = targetDir + File.separator + entry.getName();
                File f = new File(fileName);
                if (!fileName.endsWith("/")) {
                    InputStream is = jar.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(f);
                    // write contents of 'is' to 'fos'
                    while (is.available() > 0) {
                        fos.write(is.read());
                    }
                    fos.close();
                    is.close();
                }
            }
        } catch (JarException e) {
            e.printStackTrace();
            try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                fileWriter.append(e.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                fileWriter.append(e.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (IOException e) {
            try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                fileWriter.append(e.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void executePython(String path, String pluginP) {
        JavaExecutePythonUtils.pluginP = pluginP;
        try {
            String pluginPath = path.replace(".jar", "");
            File folder = new File(pluginPath);
            if (!folder.exists() && !folder.isDirectory()) {
                JavaExecutePythonUtils.UnAllFile(path, pluginPath);
            }
            PropertiesComponent applicationComponent = PropertiesComponent.getInstance();
            PostDetectParams.path = applicationComponent.getValue("com.zx.unique.plugin.id" + "_path");
            PostDetectParams.focalDistance = applicationComponent.getValue("com.zx.unique.plugin.id" + "_focalDistance");

            String[] args = new String[]{PostDetectParams.path, pluginPath + File.separator + JavaExecutePythonUtils.pythonFileName, PostDetectParams.focalDistance};
            if (proc != null) {
                proc.destroyForcibly();
            }

            proc = Runtime.getRuntime().exec(args);// 执行py文件

            System.out.println(proc.toString());
            new Thread(new OutputHandlerRunnable(proc.getInputStream())).start();
            new Thread(new OutputHandlerRunnable(proc.getErrorStream())).start();

            ProjectManager.getInstance().addProjectManagerListener(new VetoableProjectManagerListener() {
                @Override
                public boolean canClose(@NotNull Project project) {
                    proc.destroyForcibly();
                    return true;
                }
            });
        } catch (Exception execp) {
            System.out.println(execp);

            try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                fileWriter.append(execp.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw new RuntimeException("执行python脚本异常:" + execp.getMessage());
        }
    }

    private static class OutputHandlerRunnable implements Runnable {
        private InputStream in;

        public OutputHandlerRunnable(InputStream inputStream) {
            this.in = inputStream;
        }

        @Override
        public void run() {
            try (BufferedReader bufr = new BufferedReader(new InputStreamReader(this.in))) {
                String line = null;
                while ((line = bufr.readLine()) != null) {
                    System.out.println("来自python脚本：" + line);

                    try (FileWriter fileWriter = new FileWriter(JavaExecutePythonUtils.pluginP + "/post-detect.log", true)) {
                        fileWriter.append(line);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }

                    if (line.contains("raise Exception(\"填写缓存区传值\")")) {
                        NotificationGroup notificationGroup = new NotificationGroup("action_switch_id", NotificationDisplayType.TOOL_WINDOW, false);
                        Notification notification = notificationGroup.createNotification("请调整坐姿哦", MessageType.INFO);
                        Notifications.Bus.notify(notification);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
