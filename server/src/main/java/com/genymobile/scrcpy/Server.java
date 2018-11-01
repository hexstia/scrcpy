package com.genymobile.scrcpy;

import android.graphics.Rect;
import android.text.GetChars;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public final class Server {
	public static String hostAddress;
	private static ClientInfoThread cit;

	private Server() {
		// not instantiable
	}

	private static void scrcpy(Options options) throws IOException {
		final Device device = new Device(options);
		boolean tunnelForward = options.isTunnelForward();
		try (DesktopConnection connection = DesktopConnection.open(device, tunnelForward)) {
			ScreenEncoder screenEncoder = new ScreenEncoder(options.getBitRate());

			// asynchronous
			startEventController(device, connection);
			try {
				// synchronous
				screenEncoder.streamScreen(device, connection.getFd());
				System.out.println("streamScreen()...");
			} catch (IOException e) {
				// this is expected on close
				Ln.d("Screen streaming stopped");
			}
//        if(!connection.socket.isConnected()){
			connection.socket.close();
			// connection = null;
			try {
				main("480", "8000000", "false");
			} catch (Exception e) {
			}
//}        
		}
	}

	private static void startEventController(final Device device, final DesktopConnection connection) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new EventController(device, connection).control();
				} catch (IOException e) {
					// this is expected on close
					Ln.d("Event controller stopped");
				}
			}
		}).start();
	}

	@SuppressWarnings("checkstyle:MagicNumber")
	private static Options createOptions(String... args) {
		Options options = new Options();
		if (args.length < 1) {
			return options;
		}
		int maxSize = Integer.parseInt(args[0]) & ~7; // multiple of 8
		options.setMaxSize(maxSize);

		if (args.length < 2) {
			return options;
		}
		int bitRate = Integer.parseInt(args[1]);
		options.setBitRate(bitRate);

		if (args.length < 3) {
			return options;
		}
		// use "adb forward" instead of "adb tunnel"? (so the server must listen)
		boolean tunnelForward = Boolean.parseBoolean(args[2]);
		options.setTunnelForward(tunnelForward);

		if (args.length < 4) {
			return options;
		}
		Rect crop = parseCrop(args[3]);
		options.setCrop(crop);

		return options;
	}

	private static Rect parseCrop(String crop) {
		if (crop.isEmpty()) {
			return null;
		}
		// input format: "width:height:x:y"
		String[] tokens = crop.split(":");
		if (tokens.length != 4) {
			throw new IllegalArgumentException("Crop must contains 4 values separated by colons: \"" + crop + "\"");
		}
		int width = Integer.parseInt(tokens[0]);
		int height = Integer.parseInt(tokens[1]);
		int x = Integer.parseInt(tokens[2]);
		int y = Integer.parseInt(tokens[3]);
		return new Rect(x, y, x + width, y + height);
	}

	public static void main(String... args) throws Exception {
		// 解析xml文件 获得服务器数据
//		ArrayList<Type> parse = parsexml.parse(null);；//解析函数
		ServerData sd = ServerData.getInstatce();
		sd.setIP("10.0.69.254");
		sd.setPort_heart(28001);// 心跳包 状态信息通信
		sd.setPort_Scem_cont(27183);// 屏幕与反控
		cit = new ClientInfoThread(sd.getIP(), sd.getPort_heart());
		cit.start();

		Thread ts = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					Socket socket = cit.getSocket();// 心跳包socket
					if (socket == null || !socket.isConnected()) {
						continue;
					}
					InputStream is;
					try {
						is = socket.getInputStream();
						byte[] b = new byte[16];
						is.read(b, 0, 16);
						String str = new String(b);
						System.out.println("接受pc数据:" + str);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		});
		ts.start();
		ts.join();
		Thread.sleep(1000);
		System.out.println("coming core code");
		try {
//					Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//
//						@Override
//						public void uncaughtException(Thread t, Throwable e) {
//							Ln.e("Exception on thread " + t, e);
//							System.out.println("Exception on thread ");
//						}
//					});

			Options options = createOptions("480", "8000000", "false");
			System.out.println("createOptions() ");
			scrcpy(options);
			System.out.println("scrcpy() ");
		} catch (Exception e) {
			System.out.println("main exception");
		}
		System.out.println("main end");
	}

}
