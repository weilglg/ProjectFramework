package wll.com.okhttputils.callback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by WLL on 2016/2/19.
 */
public abstract class Callback<T> {
    /**
     * 请求之前会调用此方法（UI线程）
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * 请求完成后会调用此方法（UI线程）
     */
    public void onAfter() {
    }

    /**
     * 更新请求进度（UI线程）
     *
     * @param progress
     */
    public void inProgress(float progress) {
    }

    /**
     * 解析网络数据
     *
     * @param response
     * @return
     * @throws Exception
     */
    public abstract T parseNetworkResponse(Response response) throws Exception;

    /**
     * 请求失败时调用此方法（UI线程）
     *
     * @param call
     * @param e
     */
    public abstract void onError(Call call, Exception e);

    /**
     * 请求成功时调用此方法（UI线程）
     *
     * @param response
     */
    public abstract void onResponse(T response);

    /**
     * 默认Callback实现类
     */
    public static Callback CALLBACK_DEFAULT = new Callback() {
        @Override
        public Object parseNetworkResponse(Response response) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}
