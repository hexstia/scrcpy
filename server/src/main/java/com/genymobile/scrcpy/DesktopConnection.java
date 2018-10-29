package com.genymobile.scrcpy;

import java.io.File;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class DesktopConnection implements Closeable {

	private static final int DEVICE_NAME_FIELD_LENGTH = 64;

	private static  String HOST_NAME = "10.0.68.254";

	private static final int PORT = 27183;

	public final Socket socket;
	private final InputStream inputStream;

	private final ControlEventReader reader = new ControlEventReader();

	private final FileDescriptor fd;

	private DesktopConnection(Socket socket) throws IOException {
		this.socket = socket;
		inputStream = socket.getInputStream();
		fd = getFileDescriptor(socket);
	}

	public FileDescriptor getFileDescriptor(Socket socket) {
		Class<? extends Socket> claz = socket.getClass();
		try {
			Field impl_field = claz.getDeclaredField("impl");
			impl_field.setAccessible(true);
			SocketImpl object = (SocketImpl) impl_field.get(socket);
			Class<? extends SocketImpl> class1 = object.getClass();
			Class<?> superclass = class1.getSuperclass();
			Class<?> superclass2 = superclass.getSuperclass();
			Class<?> superclass3 = superclass2.getSuperclass();
			Method declaredMethod = superclass3.getDeclaredMethod("getFileDescriptor");
			declaredMethod.setAccessible(true);
			FileDescriptor fd = (FileDescriptor) declaredMethod.invoke(object);
			return fd;

		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static Socket connect(String hostname, int port) throws IOException {
		Socket socket = new Socket(hostname, port);
//		new SocketAddress(abstractName)
		boolean connected = socket.isConnected();
		System.out.println(connected);
		return socket;
	}

	private static Socket listenAndAccept(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		try {
			return serverSocket.accept();
		} finally {
			serverSocket.close();
		}
	}

	public static DesktopConnection open(Device device, boolean tunnelForward) throws IOException {
		System.out.println("come in open()....");
		Socket socket = null;
		if (tunnelForward) {
			// if(false){
			System.out.println("is tunnelForward :true status");
			socket = listenAndAccept(PORT);
			socket.getOutputStream().write(0);
		} else {
            HOST_NAME = Server.hostAddress;
		    System.out.println("ubuntu ip :"+HOST_NAME);
                socket = connect(HOST_NAME, PORT);
			System.out.println("link socket  status : " + socket);
		}
		DesktopConnection connection = new DesktopConnection(socket);
		Size videoSize = device.getScreenInfo().getVideoSize();
		connection.send(Device.getDeviceName(), videoSize.getWidth(), videoSize.getHeight());
		return connection;
	}

	public void close() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
        System.out.println("close()()()");
        try{
            Server.main("480","8000000","false");
            System.out.println("main()");
        }catch(Exception e)
        {
        }
	}

	@SuppressWarnings("checkstyle:MagicNumber")
	private void send(String deviceName, int width, int height) throws IOException {
		byte[] buffer = new byte[DEVICE_NAME_FIELD_LENGTH + 4];

		byte[] deviceNameBytes = deviceName.getBytes(StandardCharsets.UTF_8);
		int len = Math.min(DEVICE_NAME_FIELD_LENGTH - 1, deviceNameBytes.length);
		System.arraycopy(deviceNameBytes, 0, buffer, 0, len);
		// byte[] are always 0-initialized in java, no need to set '\0' explicitly

		buffer[DEVICE_NAME_FIELD_LENGTH] = (byte) (width >> 8);
		buffer[DEVICE_NAME_FIELD_LENGTH + 1] = (byte) width;
		buffer[DEVICE_NAME_FIELD_LENGTH + 2] = (byte) (height >> 8);
		buffer[DEVICE_NAME_FIELD_LENGTH + 3] = (byte) height;
		IO.writeFully(fd, buffer, 0, buffer.length);
	}

	public FileDescriptor getFd() {
		return fd;
	}

	public ControlEvent receiveControlEvent() throws IOException {
		ControlEvent event = reader.next();
		while (event == null) {
			reader.readFrom(inputStream);
			event = reader.next();
		}
		return event;
	}
}
