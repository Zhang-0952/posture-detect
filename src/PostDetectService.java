import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author: zx
 * @date: 2022/11/4
 *
 */
@State(name = "postDetect")
public class PostDetectService implements PersistentStateComponent<PostDetectService> {

    public String path;
    public String distance;

    @Nullable
    @Override
    public PostDetectService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PostDetectService postDetectService) {
        XmlSerializerUtil.copyBean(postDetectService,this);
    }
}
