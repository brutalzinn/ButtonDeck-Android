package net.robertocpaes.displaybuttons.networking;

import net.robertocpaes.displaybuttons.networking.io.SocketServer;
import net.robertocpaes.displaybuttons.networking.io.TcpClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by NickAc on 23/12/2017.
 * This project is licensed with the MIT license.
 * Please see the project root to find the LICENSE file.
 */
public interface INetworkPacket {
    void execute(TcpClient client, boolean received);
    void execute(SocketServer client, boolean received);
    INetworkPacket clonePacket();

    long getPacketId();

    void toOutputStream(DataOutputStream writer) throws IOException;

    void fromInputStream(DataInputStream reader) throws IOException;
}
