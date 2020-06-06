package net.nickac.buttondeck.networking.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.nickac.buttondeck.ButtonDeckActivity;
import net.nickac.buttondeck.MainActivity;
import net.nickac.buttondeck.R;
import net.nickac.buttondeck.networking.INetworkPacket;
import net.nickac.buttondeck.networking.io.SocketServer;
import net.nickac.buttondeck.networking.io.TcpClient;
import net.nickac.buttondeck.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Handler;

import static android.support.v4.content.ContextCompat.startActivity;
import static java.lang.Integer.valueOf;

/**
 * Created by NickAc on 27/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public class MatrizPacket implements INetworkPacket {
    public static int  NUM_ROWS = 3;
    public static  int NUM_COLS = 5;

    @Override
    public void execute(TcpClient client, boolean received) {

    }
    @Override
    public void execute_server(SocketServer client, boolean received) {

    }

    @Override
    public INetworkPacket clonePacket() {
        return new MatrizPacket();
    }

    @Override
    public long getPacketId() {
        return 11;
    }

    @Override
    public void toOutputStream(DataOutputStream writer) throws IOException {

    }

    @Override
    public void fromInputStream(DataInputStream reader) throws IOException {
        NUM_ROWS = reader.readInt() ;
        NUM_COLS = reader.readInt()  ;
        if (Constants.buttonDeckContext != null) {
            //Start a new thread to create a bitmap
            Thread th = new Thread(() -> {


                Constants.buttonDeckContext.runOnUiThread(() -> {
                    //ButtonDeckActivity teste = new ButtonDeckActivity();
                  //  teste.populateButtons();

               Constants.buttonDeckContext.limpar();
                    Constants.buttonDeckContext.populateButtons(valueOf(MainActivity.mode_init));

                });
            });
            th.start();
        }


     //


    }
}
