package com.froxynetwork.froxynetwork.network.websocket.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput.ServerTester;
import com.froxynetwork.froxynetwork.network.websocket.IWebSocket;
import com.froxynetwork.froxynetwork.network.websocket.IWebSocketCommander;

/**
 * MIT License
 *
 * Copyright (c) 2020 FroxyNetwork
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
/**
 * Authenticate by sending a token to the WebSocket
 */
public class WebSocketTokenAuthentication implements WebSocketAuthentication {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	public static final String TOKEN = "TOKEN_ID";

	private NetworkManager networkManager;
	private IWebSocket webSocket;
	private Thread authThread;
	private boolean authenticated;

	private String id;

	public WebSocketTokenAuthentication(NetworkManager networkManager) {
		this.networkManager = networkManager;
		this.authenticated = false;
	}

	@Override
	public void init(IWebSocket webSocket) {
		this.webSocket = webSocket;
		if (webSocket.isClient()) {
			// This webSocket is the client
			webSocket.registerCommand(new IWebSocketCommander() {

				@Override
				public String name() {
					return "auth";
				}

				@Override
				public String description() {
					return "Handler for accepted connection from WebSocket";
				}

				@Override
				public void onReceive(String message) {
					authenticated = true;
					webSocket.save(TOKEN, id);
					// Fire event
					webSocket.onAuthentication();
				}
			});
		} else {
			webSocket.registerCommand(new IWebSocketCommander() {

				@Override
				public String name() {
					return "auth";
				}

				@Override
				public String description() {
					return "Handler for WebSocket token authentication";
				}

				@Override
				public void onReceive(String message) {
					if (isAuthenticated())
						// Say to the client that he's already authenticated
						webSocket.sendCommand("auth", "");
					if (message == null || "".equalsIgnoreCase(message.trim()))
						return;
					String[] msgs = message.split(" ");
					if (msgs.length != 2)
						return;
					networkManager.getNetwork().getServerTesterService().asyncCheck(msgs[0], msgs[1],
							new Callback<ServerTesterDataOutput.ServerTester>() {

								@Override
								public void onResponse(ServerTester response) {
									if (response.isOk()) {
										authenticated = true;
										// Save
										webSocket.save(TOKEN, msgs[0]);
										// Say that this server is authenticated
										webSocket.sendCommand("auth", "");
										// Fire event
										webSocket.onAuthentication();
									}
								}

								@Override
								public void onFailure(RestException ex) {
									LOG.error("Error while checking token {} for server {}", msgs[1], msgs[0]);
								}

								@Override
								public void onFatalFailure(Throwable t) {
									LOG.error("Fatal error while checking token {} for server {}", msgs[1], msgs[0]);
								}
							});
				}
			});
		}
		webSocket.registerWebSocketDisconnection(remote -> {
			authenticated = false;
		});
	}

	@Override
	public void registerAuthenticationListener() {
		if (!webSocket.isClient())
			return;
		webSocket.registerWebSocketConnection(first -> {
			authThread = new Thread(() -> {
				while (!isAuthenticated() && !Thread.interrupted()) {
					authenticate();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException ex) {
					}
				}
			});
			authThread.start();
		});
	}

	@Override
	public void stop() {
		// Make a copy
		Thread authThread = this.authThread;
		if (authThread != null && authThread.isAlive())
			new Thread(() -> {
				authThread.interrupt();
			}).start();
	}

	@Override
	public void authenticate() {
		if (authenticated || !webSocket.isClient() || !webSocket.isConnected())
			return;
		// Ask a token
		networkManager.getNetwork().getServerTesterService()
				.asyncAsk(new Callback<ServerTesterDataOutput.ServerTester>() {

					@Override
					public void onResponse(ServerTester response) {
						id = response.getId();
						String token = response.getToken();
						if (webSocket.isConnected()) {
							// Send auth command
							webSocket.sendCommand("auth", id + " " + token);
						}
					}

					@Override
					public void onFailure(RestException ex) {
						LOG.error("Error while asking for a token: ", ex);
						LOG.error("" + ex.getError());
					}

					@Override
					public void onFatalFailure(Throwable t) {
						LOG.error("Fatal error whiel asking for a token: ", t);
					}
				});
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}
}
