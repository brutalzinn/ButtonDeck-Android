package net.nickac.buttondeck;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.nickac.buttondeck.networking.impl.AlternativeHelloPacket;
import net.nickac.buttondeck.networking.impl.ButtonInteractPacket;
import net.nickac.buttondeck.networking.impl.DeviceIdentityPacket;
import net.nickac.buttondeck.networking.impl.HeartbeatPacket;
import net.nickac.buttondeck.networking.impl.HelloPacket;
import net.nickac.buttondeck.networking.impl.UsbInteractPacket;
import net.nickac.buttondeck.networking.io.SocketServer;
import net.nickac.buttondeck.networking.io.TcpClient;
import net.nickac.buttondeck.utils.Constants;

import java.io.IOException;

import static net.nickac.buttondeck.utils.Constants.sharedPreferences;
import static net.nickac.buttondeck.utils.Constants.sharedPreferencesName;

public class ButtonDeckActivity extends AppCompatActivity {

    public static final String TEXT = "text";
    public static final String EXTRA_IP = "net.nickac.buttondeck.networking.IP";
    public static final String EXTRA_MODE = "0";
    private static final int IDLE_DELAY_MINUTES = 5;
    private static TcpClient client;
    public static final String SHARED_PREFS = "sharedPrefs";
    private static SocketServer server;
    //private static final int mode = 1;
    Handler _idleHandler = new Handler();
    Runnable _idleRunnable = () -> {
        dimScreen(1.0f);
    };

    Vibrator vibe;

    public void dimScreen(float dim) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = dim;
        getWindow().setAttributes(lp);
    }

    public ImageButton getImageButton(int id) {
        return findViewById(getResources().getIdentifier("button" + id, "id", Constants.buttonDeckContext.getPackageName()));
    }

    @TargetApi(19)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT < 19) return;
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Default activity creation
        super.onCreate(savedInstanceState);

        //Request full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadData();
        setContentView(R.layout.activity_button_deck);

        //Save our reference on a variable. This will allow us to access this activity later.
        Constants.buttonDeckContext = this;


        Intent intent = getIntent();
        String connectIP = intent.getStringExtra(EXTRA_IP);
        int what_is_the_mode = Integer.valueOf(intent.getStringExtra(EXTRA_MODE));
        int connectPort = Constants.PORT_NUMBER;

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //Ask android to keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //Ask android to set the app to landscape only.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        //Ask android to remove the action bar, since we don't need it.
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        if (sharedPreferences == null) {
            sharedPreferences = this.getSharedPreferences(sharedPreferencesName, MODE_PRIVATE);
        }


        if (savedInstanceState == null && server == null) {
            if(what_is_the_mode  == 0) {
                client = new TcpClient(connectIP, connectPort);
                Log.d("DEBUG", "Escolhido conexão por wifi, na porta " + connectPort);
                try {
                    client.connect();
                    client.onConnected(() -> client.sendPacket(new HelloPacket()));
                } catch (IOException e) {
                }
            }
            else {

                try {
                    Log.d("DEBUG", "Escolhido conexão por usb, por redirecionamneto na porta," + connectPort);
                   server = new SocketServer( connectPort);
                 //   socket.setCreateNewThread(false);
          //          socket.StartServer();
                    server.connect();
                    server.onConnected(() -> server.sendPacket(new HelloPacket()));
               //     server.onConnected(() -> server.sendPacket(new AlternativeHelloPacket()));
         //   server.waitForDisconnection();
                //    server.waitForDisconnection();

               //server.waitForDisconnection();
                } catch (Exception e) {
                }
            }

        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int height = size.y;

        int optimalSize = ((height - (85 * 2)) - (40 * 3)) / 3;

        int optimalFinal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, optimalSize, getResources().getDisplayMetrics());

        for (int i = 0; i < 15; i++) {
            final boolean[] mDownTouch = {false};

            ImageView button = getImageButton(i + 1);
            if (button != null) {
                ViewGroup.LayoutParams params = button.getLayoutParams();
                button.setAdjustViewBounds(true);
                button.setMaxWidth(optimalSize);
                button.setMaxHeight(optimalSize);
                params.width = optimalFinal;
                params.height = optimalFinal;

                button.setLayoutParams(params);

                    int finalI = i;
                    button.setOnTouchListener((view, event) -> {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mDownTouch[0] = true;
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                                if(what_is_the_mode == 1) {
                                    if (server != null) {
                                        server.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_DOWN));
                                    }
                                }
                                else {
                                    if (client != null) {
                                        client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_DOWN));
                                    }
                                }
                                return false;

                            case MotionEvent.ACTION_UP:
                                if(what_is_the_mode == 1) {
                                    if (server != null) {
                                        server.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_UP));
                                    }
                                    if (mDownTouch[0]) {
                                        mDownTouch[0] = false;
                                        if (server != null) {
                                            server.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_CLICK));
                                        }
                                        return true;
                                    }
                                }
                                else {
                                    if (client != null) {
                                        client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_UP));
                                    }
                                    if (mDownTouch[0]) {
                                        mDownTouch[0] = false;
                                        if (client != null) {
                                            client.sendPacket(new ButtonInteractPacket(finalI, ButtonInteractPacket.ButtonAction.BUTTON_CLICK));
                                        }
                                        return true;
                                    }

                                }
                        }
                        return false;
                    });
                }
            }

    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Constants.PORT_NUMBER = Integer.valueOf(sharedPreferences.getString(TEXT, "5095"));
        // switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
    }
    @Override
    public void onUserInteraction() {
        dimScreen(0.0f);
        super.onUserInteraction();
        delayedIdle(IDLE_DELAY_MINUTES);

    }

    private void delayedIdle(int delayMinutes) {
        _idleHandler.removeCallbacks(_idleRunnable);
        _idleHandler.postDelayed(_idleRunnable, (delayMinutes * 1000 * 60));
    }


    @Override
    protected void onPause() {
      Constants.buttonDeckContext = null;
        super.onPause();

    }

    @Override
    protected void onStop() {
        Constants.buttonDeckContext = null;
        super.onStop();
  if (server != null) server.close();
  server = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.buttonDeckContext = this;
    }
}
