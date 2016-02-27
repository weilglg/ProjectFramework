package wll.com.okhttputils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import wll.com.okhttputils.callback.BitmapCallback;
import wll.com.okhttputils.callback.FileCallBack;
import wll.com.okhttputils.callback.ListUserCallback;
import wll.com.okhttputils.callback.StringCallback;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private String mBaseUrl = "http://10.138.114.147:8080/okHttpServer/";

    private class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e) {
            mTv.setText("onError : " + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            mTv.setText("onResponse : " + response);
        }

        @Override
        public void onAfter() {
            super.onAfter();
            setTitle("Sample-okHttp");
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            setTitle("loading....");
        }

        @Override
        public void inProgress(float progress) {
            Log.e("TAG", "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getHtml(View v) {
        String url = "http://www.csdn.net/";
        OkHttpUtil.get()
                .url(url)
                .build()
                .execute(new MyStringCallback());
    }

    public void postString(View view) {
        String url = mBaseUrl + "user!postString";
        OkHttpUtil
                .postString()
                .url(url)
                .content(new Gson().toJson(new User("zhy", "123")))
                .build()
                .execute(new MyStringCallback());
    }


    public void getUsers(View view) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "zhy");
        String url = mBaseUrl + "user!getUsers";
        OkHttpUtil//
                .postForm()//
                .url(url)//
                .params(params)//
                .build()//
                .execute(new ListUserCallback()//
                {

                    @Override
                    public void onError(Call call, Exception e) {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(List<User> response) {
                        mTv.setText("onResponse:" + response);
                    }
                });
    }


    public void getHttpsHtml(View view) {
        String url = "https://kyfw.12306.cn/otn/";

        OkHttpUtil
                .get()//
                .url(url)//
                .build()//
                .execute(new MyStringCallback());

    }

    public void getImage(View view) {
        mTv.setText("");
        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtil
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }


    public void uploadFile(View view) {

        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = mBaseUrl + "user!uploadFile";

        OkHttpUtil.postForm()//
                .addFile("mFile", "messenger_01.png", file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new MyStringCallback());
    }


    /*
      Post表单形式上传文件
      支持单个多个文件，addFile的第一个参数为文件的key，即类别表单中<input type="file" name="mFile"/>的name属性。
    */
    public void multiFileUpload(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1.txt");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtil.postForm()//
                .addFile("mFile", "messenger_01.png", file)//
                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }

    public void postFile(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = mBaseUrl + "user!postFile";
        OkHttpUtil
                .postFile()
                .url(url)
                .file(file)
                .build()
                .execute(new MyStringCallback());

    }

    public void downloadFile(View view) {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/gson-2.2.1.jar?raw=true";
        OkHttpUtil//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1" +
                        ".jar")//
                {

                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void inProgress(float progress) {
                        mProgressBar.setProgress((int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file) {
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        OkHttpUtil.cancelTag(this);
    }


}
