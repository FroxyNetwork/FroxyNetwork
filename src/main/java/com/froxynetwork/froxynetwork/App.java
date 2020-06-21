package com.froxynetwork.froxynetwork;

import java.net.InetSocketAddress;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.websocket.IWebSocketCommander;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketClientImpl;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketFactory;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketServer;
import com.froxynetwork.froxynetwork.network.websocket.auth.WebSocketTokenAuthentication;
import com.froxynetwork.froxynetwork.network.websocket.modules.WebSocketAutoReconnectModule;

/**
 * MIT License
 *
 * Copyright (c) 2019 FroxyNetwork
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * @author 0ddlyoko
 */
public class App {
	private Logger LOG = LoggerFactory.getLogger(getClass());

	private NetworkManager nm;
	private WebSocketServer wss;

	public App() throws Exception {
		// This is just for TESTING

		// TODO URL in config file
		String url = "http://0ddlyoko.alwaysdata.net";
		String clientId = "VPS_01";
		String clientSecret = "<id>";
		nm = new NetworkManager(url, clientId, clientSecret);
		wss = WebSocketFactory.server(new InetSocketAddress("localhost", 25566), new WebSocketTokenAuthentication(nm));
		wss.registerWebSocketConnection(wssi -> {
			System.out.println("Server: New connection !");
			wssi.registerCommand(new IWebSocketCommander() {

				@Override
				public String name() {
					return "test";
				}

				@Override
				public String description() {
					return "Just a test";
				}

				@Override
				public void onReceive(String message) {
					System.out.println("Server: Got message ! name = test, message = " + message);
					wssi.sendCommand("lol", "Heyy oui et toi ?");
					wssi.disconnect();
				}
			});
			wssi.registerWebSocketAuthentication(() -> {
				System.out.println("Server: Authentified !");
			});
		});
		wss.start();

		WebSocketClientImpl wsci = WebSocketFactory.client(new URI("ws://localhost:25566"),
				new WebSocketTokenAuthentication(nm));
		wsci.registerWebSocketConnection(first -> {
			System.out.println("Client: Connected !");
		});
		wsci.registerWebSocketAuthentication(() -> {
			System.out.println("Client: Authentified !");
			wsci.sendCommand("test", "Heyyyyyy ça va ?");
		});
		wsci.registerCommand(new IWebSocketCommander() {

			@Override
			public String name() {
				return "lol";
			}

			@Override
			public String description() {
				return "LoL";
			}

			@Override
			public void onReceive(String message) {
				System.out.println("Client: Got message ! name = lol, message = " + message);
			}
		});
		wsci.addModule(new WebSocketAutoReconnectModule(5 * 1000));
		wsci.tryConnect();
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					break;
				}
				System.out.println("Number of Threads: " + Thread.activeCount());
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}
