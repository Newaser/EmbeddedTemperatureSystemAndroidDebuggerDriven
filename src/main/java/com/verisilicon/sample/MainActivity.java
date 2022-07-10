package com.verisilicon.sample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.verisilicon.sample.presenters.TestCasePresenter;
import com.verisilicon.sample.views.ITestCaseView;
import com.verisilicon.vsbleclient.proxy.DeviceProxy;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ITestCaseView {
    /** the main presenter */
    private TestCasePresenter testCasePresenter;

    /* scenes and its transition effect set */
    private Scene disconnectScene;
    private Scene connectScene;
    private TransitionSet commonTransitionSet;

    /** message box showing logs */
    private TextView msgBox;
    /** the buffer of the message box */
    private final List<String> buffer = new ArrayList<>();

    /** disconnect button listener */
    private final View.OnClickListener disconnectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            testCasePresenter.disConnect();
        }
    };

    /** connect button listener */
    private final View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText editText = findViewById(R.id.et_device_name);
            String name = editText.getText().toString().trim();

            // empty device name warning
            if (name.isEmpty()) {
                showEmptyDeviceNameDialog();
                return;
            }

//            testCasePresenter.connect(name);
            enterScene(connectScene);
            resetDialWebView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        // initialize
        init();
    }

    /** warning function that be called when device name is empty */
    private void showEmptyDeviceNameDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Tips")
                .setMessage("Please enter the name of the device to be connected!")
                .setNegativeButton("Got it", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    /** enter a specific scene */
    private void enterScene(Scene scene) {
        // scene switch
        TransitionManager.go(scene, commonTransitionSet);

        // reset elements
        resetElements();
    }

    /** reset elements every time scene switched */
    private void resetElements() {
        // set up click listeners
        findViewById(R.id.bt_connect).setOnClickListener(connectListener);
        findViewById(R.id.bt_disconnect).setOnClickListener(disconnectListener);

        // reset msgBox and its buffer
        // set log message box movement method: Scrolling
        msgBox = findViewById(R.id.tv_msg);
        flushBuffer();
        if (msgBox != null)
            msgBox.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    /** reset temperature dial webView */
    @SuppressLint("SetJavaScriptEnabled")
    private void resetDialWebView() {
        // reset dail web view with JavaScript enabled
        WebView webView = findViewById(R.id.dail_web_view);
        webView.getSettings().setJavaScriptEnabled(true);

        // load local HTML into a WebViewClient
        webView.loadUrl("file:///android_asset/dial.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // debug the dial
                new DialDebugger(
                        MainActivity.this,
                        MainActivity.this,
                        1500, view).start();
            }
        });
    }

    /** initialize */
    private void init() {
        // init presenter
        testCasePresenter = new TestCasePresenter(this, this);

        // init scenes and click listeners
        ViewGroup sceneRoot = findViewById(R.id.scene_root);
        disconnectScene = Scene.getSceneForLayout(sceneRoot, R.layout.disconnect_scene, this);
        connectScene = Scene.getSceneForLayout(sceneRoot, R.layout.connect_scene, this);
        resetElements();

        // init transition set
        commonTransitionSet = new TransitionSet();
        commonTransitionSet.addTransition(new ChangeBounds());
        commonTransitionSet.addTransition(new Fade());
        commonTransitionSet.setDuration(600);
    }

    /** flush buffer into msgBox */
    private void flushBuffer() {
        if ((msgBox != null) && (! buffer.isEmpty())) {
            for (String msgBuf : buffer) {
                msgBox.append(msgBuf);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        testCasePresenter.refreshPermission();
    }

    @Override
    public void onConnected(DeviceProxy deviceProxy) {
        enterScene(connectScene);

        // once connected, set the title text to the device name
        TextView titleView = findViewById(R.id.tv_title);
        titleView.setText(deviceProxy.getDeviceName());
    }

    @Override
    public void onDisconnected(DeviceProxy deviceProxy) {
        enterScene(disconnectScene);
    }

    @Override
    public void drawLog(String message) {
        if (msgBox != null) {
            flushBuffer();
            msgBox.append(message + "\n");
        } else {
            buffer.add(message);
        }
    }

    @Override
    public void updateDialDisplay(double temperature) {
        WebView webView = findViewById(R.id.dail_web_view);

        if(temperature < 0) {
            webView.loadUrl("javascript: alert('Temperature too low!')");
        } else if(temperature > 60) {
            webView.loadUrl("javascript: alert('Temperature too high!')");
        } else {
            webView.loadUrl("javascript: setTemperature(" + temperature + ")");
        }
    }
}