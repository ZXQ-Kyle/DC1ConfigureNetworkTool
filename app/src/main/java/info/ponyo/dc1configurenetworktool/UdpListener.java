package info.ponyo.dc1configurenetworktool;

/**
 * @author zxq
 * @Date 2019/4/11.
 * @Description:
 */
public interface UdpListener {
    void onSuccess(String content);
    void onFail(String message);
}
