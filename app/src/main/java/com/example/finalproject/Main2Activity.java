package com.example.finalproject;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Main2Activity extends AppCompatActivity {
    TabHost tabHost;
    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;

    TextView textView;
    Button startButton;
    Button stopButton;

    Thread thread;

    TextView idView;
    EditText productBox;
    EditText quantityBox;

    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("Ramen");

        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tm = tabHost.newTabSpec("TabSpec1").setContent(R.id.Timer).setIndicator("타이머");
        TabHost.TabSpec ri = tabHost.newTabSpec("TabSpec2").setContent(R.id.Inventory).setIndicator("라면 재고");
        TabHost.TabSpec srch = tabHost.newTabSpec("TabSpec3").setContent(R.id.Search).setIndicator("검색");

        tabHost.addTab(tm);
        tabHost.addTab(ri);
        tabHost.addTab(srch);

        textView = (TextView) findViewById(R.id.textView5);
        startButton = (Button) findViewById(R.id.button);
        stopButton = (Button) findViewById(R.id.button2);

        // Thread Strat 버튼을 클릭했을 때 Thread를 시작
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread = new Thread();
                thread.start();
            }
        });
        // Thread 를 Stop 시키며 handler에게 SEND_STOP 메시지를 보냄
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(SEND_STOP);
            }
        });

        idView = (TextView) findViewById(R.id.productID);
        productBox = (EditText) findViewById(R.id.productName);
        quantityBox =
                (EditText) findViewById(R.id.productQuantity);



    }

    public void goURL(View view){
        TextView tvURL = (TextView)findViewById(R.id.txtURL);
        String url = tvURL.getText().toString();
        Log.i("URL", "Opening URL with WebView :" + url);

        final long startTime = System.currentTimeMillis();
        WebView webView = (WebView)findViewById(R.id.webView);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                long elapsedTime = System.currentTimeMillis()-startTime;
//                TextView tvSec = (TextView) findViewById(R.id.tvSec);
//                tvSec.setText(String.valueOf(elapsedTime));
            }
        });
        webView.loadUrl(url);

    }

    public void newProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int quantity =
                Integer.parseInt(quantityBox.getText().toString());
        Product product =
                new Product(productBox.getText().toString(), quantity);
        dbHandler.addProduct(product);
        productBox.setText("");
        quantityBox.setText("");
    }

    public void lookupProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        Product product =
                dbHandler.findProduct(productBox.getText().toString());
        if (product != null) {
            idView.setText(String.valueOf(product.getID()));
            quantityBox.setText(String.valueOf(product.getQuantity()));
        } else {
            idView.setText("No Match Found");
        }
    }

    public void removeProduct (View view) {
        MyDBHandler dbHandler = new MyDBHandler(this, null,
                null, 1);
        boolean result = dbHandler.deleteProduct(
                productBox.getText().toString());
        if (result)
        {
            idView.setText("Record Deleted");
            productBox.setText("");
            quantityBox.setText("");
        }
        else
            idView.setText("No Match Found");
    }

    final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case SEND_INFORMATION:
                    textView.setText(Integer.toString(msg.arg1) + msg.obj);
                    break;

                case SEND_STOP:
                    thread.stopThread();
                    textView.setText("타이머 종료");
                    break;

                default:
                    break;
            }


        }
    };
    class Thread extends java.lang.Thread {

        boolean stopped = false;
        int i = 270;

        public Thread(){
            stopped = false;
        }

        public void stopThread() {
            stopped = true;
        }

        @Override
        public void run() {
            super.run();

            while(stopped == false) {
                i--;

                // 메시지 얻어오기
                Message message = handler.obtainMessage();

                // 메시지 ID 설정
                message.what = SEND_INFORMATION;

                // 메시지 내용 설정 (int)
                message.arg1 = i;

                // 메시지 내용 설정 (Object)
                String information = new String("초 남았습니다.");
                message.obj = information;

                // 메시지 전
                handler.sendMessage(message);

                try {
                    // 1초 씩 딜레이 부여
                    sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }
}
