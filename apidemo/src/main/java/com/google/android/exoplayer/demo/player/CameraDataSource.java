package com.google.android.exoplayer.demo.player;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.TransferListener;
import com.google.android.exoplayer.upstream.UdpDataSource;
import com.google.android.exoplayer.upstream.UriDataSource;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;


/**
 * A UDP {@link DataSource}.
 */
public class CameraDataSource implements UriDataSource {

    /**
     * Thrown when an error is encountered when trying to read from a {@link UdpDataSource}.
     */
    public static final class UdpDataSourceException extends IOException {

        public UdpDataSourceException(String message) {
            super(message);
        }

        public UdpDataSourceException(IOException cause) {
            super(cause);
        }

    }

    /**
     * The default maximum datagram packet size, in bytes.
     */
    public static final int DEFAULT_MAX_PACKET_SIZE = 2000;

    /**
     * The default socket timeout, in milliseconds.
     */
    public static final int DEAFULT_SOCKET_TIMEOUT_MILLIS = 8 * 1000;

    private final TransferListener listener;
    private final DatagramPacket packet;
    private final int socketTimeoutMillis;

    private DataSpec dataSpec;
    private DatagramSocket socket;
    private MulticastSocket multicastSocket;
    private InetAddress address;
    private InetSocketAddress socketAddress;
    private boolean opened;

    private byte[] packetBuffer;
    private int packetRemaining;

    /**
     * @param listener An optional listener.
     */
    public CameraDataSource(TransferListener listener) {
        this(listener, DEFAULT_MAX_PACKET_SIZE);
    }

    /**
     * @param listener An optional listener.
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     */
    public CameraDataSource(TransferListener listener, int maxPacketSize) {
        this(listener, maxPacketSize, DEAFULT_SOCKET_TIMEOUT_MILLIS);
    }

    /**
     * @param listener An optional listener.
     * @param maxPacketSize The maximum datagram packet size, in bytes.
     * @param socketTimeoutMillis The socket timeout in milliseconds. A timeout of zero is interpreted
     *     as an infinite timeout.
     */
    public CameraDataSource(TransferListener listener, int maxPacketSize, int socketTimeoutMillis) {
        this.listener = listener;
        this.socketTimeoutMillis = socketTimeoutMillis;
        packetBuffer = new byte[maxPacketSize];
        packet = new DatagramPacket(packetBuffer, 0, maxPacketSize);
    }

    @Override
    public long open(DataSpec dataSpec) throws UdpDataSourceException {
        this.dataSpec = dataSpec;
        String host = dataSpec.uri.getHost();
        int port = dataSpec.uri.getPort();

        try {
            address = InetAddress.getByName(host);
            socketAddress = new InetSocketAddress(address, port);
            if (address.isMulticastAddress()) {
                multicastSocket = new MulticastSocket(socketAddress);
                multicastSocket.joinGroup(address);
                socket = multicastSocket;
            } else {
                //@LGE_TEAM to fix socket exception, socketAddress to port num
                socket = new DatagramSocket(port);
            }
        } catch (IOException e) {
            throw new UdpDataSourceException(e);
        }

        try {
            socket.setSoTimeout(socketTimeoutMillis);
        } catch (SocketException e) {
            throw new UdpDataSourceException(e);
        }

        opened = true;
        if (listener != null) {
            listener.onTransferStart();
        }
        return C.LENGTH_UNBOUNDED;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws UdpDataSourceException {
        if (packetRemaining == 0) {
            // We've read all of the data from the current packet. Get another.
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new UdpDataSourceException(e);
            }

            packetRemaining = packet.getLength();
            if (listener != null) {
                listener.onBytesTransferred(packetRemaining);
            }
        }

        int packetOffset = packet.getLength() - packetRemaining;
        int bytesToRead = Math.min(packetRemaining, readLength);
        System.arraycopy(packetBuffer, packetOffset, buffer, offset, bytesToRead);
        packetRemaining -= bytesToRead;
        return bytesToRead;
    }

    @Override
    public void close() {
        if (multicastSocket != null) {
            try {
                multicastSocket.leaveGroup(address);
            } catch (IOException e) {
                // Do nothing.
            }
            multicastSocket = null;
        }
        if (socket != null) {
            socket.close();
            socket = null;
        }
        address = null;
        socketAddress = null;
        packetRemaining = 0;
        if (opened) {
            opened = false;
            if (listener != null) {
                listener.onTransferEnd();
            }
        }
    }

    @Override
    public String getUri() {
        return dataSpec == null ? null : dataSpec.uri.toString();
    }

}
