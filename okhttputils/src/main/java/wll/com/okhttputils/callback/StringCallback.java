package wll.com.okhttputils.callback;

import okhttp3.Response;

/**
 * Created by WLL on 2016/2/19.
 */
public abstract class StringCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }
}
