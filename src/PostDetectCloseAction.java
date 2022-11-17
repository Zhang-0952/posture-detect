import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author: zx
 * @date: 2022/10/29
 *
 */
public class PostDetectCloseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        JavaExecutePythonUtils.proc.destroyForcibly();
    }
}
