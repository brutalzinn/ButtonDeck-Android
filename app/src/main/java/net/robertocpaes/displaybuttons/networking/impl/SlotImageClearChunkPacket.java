package net.robertocpaes.displaybuttons.networking.impl;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import net.robertocpaes.displaybuttons.networking.INetworkPacket;
import net.robertocpaes.displaybuttons.networking.io.ArchitectureAnnotation;
import net.robertocpaes.displaybuttons.networking.io.PacketArchitecture;
import net.robertocpaes.displaybuttons.networking.io.SocketServer;
import net.robertocpaes.displaybuttons.networking.io.TcpClient;
import net.robertocpaes.displaybuttons.utils.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.robertocpaes.displaybuttons.ButtonDeckActivity.height;
import static net.robertocpaes.displaybuttons.ButtonDeckActivity.width;

/**
 * Created by NickAc on 06/01/2018.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
@ArchitectureAnnotation(PacketArchitecture.CLIENT_TO_SERVER)
public class SlotImageClearChunkPacket implements INetworkPacket {
    List<Integer> toClear = new ArrayList<>();

    @Override
    public void execute(TcpClient client, boolean received) {
        if (Constants.buttonDeckContext != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int slot : toClear) {
                        Button view = Constants.buttonDeckContext.getButtonByTag(slot);
                        // TextView view_teste = Constants.buttonDeckContext.getTextViewyTag(slot);
                        if (view != null) {
                            Log.i("ButtonDeck", "Setting button [CHUNK]!");
                            //    view.setScaleX(ImageView.ScaleType.FIT_XY);

                            //  button.setAdjustViewBounds(true);


                            //   view.setCompoundDrawablesWithIntrinsicBounds(null, null , null, null);
                            view.setBackground(null);
                            view.setText("");
                            view.setGravity(0);

                        }
                        System.gc();
                    }
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (runnable) {
                Constants.buttonDeckContext.runOnUiThread(runnable);
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void execute(SocketServer client, boolean received) {
        if (Constants.buttonDeckContext != null) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for (int slot : toClear) {
                        Button view = Constants.buttonDeckContext.getButtonByTag(slot);
                       // TextView view_teste = Constants.buttonDeckContext.getTextViewyTag(slot);
                        if (view != null) {
                            Log.i("ButtonDeck", "Setting button [CHUNK]!");
                        //    view.setScaleX(ImageView.ScaleType.FIT_XY);
                         //   view.setCompoundDrawablesWithIntrinsicBounds(null, null , null, null);
                            view.setBackground(null);
                            view.setText("");
                            view.setGravity(0x00000000);
                        }
                      System.gc();
                    }
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (runnable) {
                Constants.buttonDeckContext.runOnUiThread(runnable);
                try {
                 runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public INetworkPacket clonePacket() {
        return new SlotImageClearChunkPacket();
    }

    @Override
    public long getPacketId() {
        return 10;
    }

    @Override
    public void toOutputStream(DataOutputStream writer) {

    }

    @Override
    public void fromInputStream(DataInputStream reader) throws IOException {
        int number = reader.readInt();
        for (int i = 0; i < number; i++) {
            toClear.add(reader.readInt());
        }
    }
}
