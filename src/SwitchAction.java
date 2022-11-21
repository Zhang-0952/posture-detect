import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.MessageType;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: zx
 * @date: 2022/10/20
 *
 */
public class SwitchAction extends AnAction {
    public static String pluginPath = "";

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            PluginId pluginId = PluginId.getId("com.zx.unique.plugin.id");
            IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
            File path = plugin.getPath();
            pluginPath = path.getAbsolutePath();
            JavaExecutePythonUtils.executePython(pluginPath, "taskId");
        } catch (Exception execp) {
            System.out.println(execp);
            try (FileWriter fileWriter = new FileWriter(pluginPath + "/post-detect.log", true)) {
                fileWriter.append(execp.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        NotificationGroup notificationGroup = new NotificationGroup("action_switch_id", NotificationDisplayType.TOOL_WINDOW, false);
        Notification notification = notificationGroup.createNotification("坐姿检测开启", MessageType.INFO);
        Notifications.Bus.notify(notification);
    }
}
