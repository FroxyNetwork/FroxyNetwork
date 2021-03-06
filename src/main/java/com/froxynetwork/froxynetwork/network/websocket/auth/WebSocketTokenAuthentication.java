package com.froxynetwork.froxynetwork.network.websocket.auth;

import org.java_websocket.framing.CloseFrame;
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
	public static final String AUTHTHREAD = "AUTH_THREAD";
	public static final String AUTHENTICATED = "AUTHENTICATED";

	private NetworkManager networkManager;

	public WebSocketTokenAuthentication(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	public void init(IWebSocket webSocket) {
		webSocket.save(AUTHENTICATED, false);
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
					webSocket.save(AUTHENTICATED, true);
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
					LOG.debug("Server: GOT auth {}", message);
					if (isAuthenticated(webSocket)) {
						// Say to the client that he's already authenticated
						webSocket.sendCommand("auth", "");
						return;
					}
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
										webSocket.save(AUTHENTICATED, true);
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
									LOG.error("Error while checking token {} for server {}, closing it", msgs[1],
											msgs[0]);
									webSocket.disconnect(CloseFrame.NORMAL, "Error while checking token");
								}

								@Override
								public void onFatalFailure(Throwable t) {
									LOG.error("Fatal error while checking token {} for server {}, closing it", msgs[1],
											msgs[0]);
									webSocket.disconnect(CloseFrame.NORMAL, "Fatal error while checking token");
								}
							});
				}
			});
		}
		webSocket.registerWebSocketDisconnection(remote -> {
			webSocket.save(AUTHENTICATED, false);
		});
	}

	@Override
	public void registerAuthenticationListener(IWebSocket webSocket) {
		if (!webSocket.isClient())
			return;
		webSocket.registerWebSocketConnection(first -> {
			Object obj = webSocket.get(AUTHTHREAD);
			Thread authThread = (obj == null) ? null : (Thread) obj;
			if (authThread != null && authThread.isAlive())
				authThread.interrupt();
			authThread = new Thread(() -> {
				try {
					while (!isAuthenticated(webSocket)) {
						authenticate(webSocket);
						Thread.sleep(5000);
					}
				} catch (InterruptedException ex) {
				}
			});
			authThread.start();
			webSocket.save(AUTHTHREAD, authThread);
		});
		webSocket.registerWebSocketDisconnection(remote -> {
			Object obj = webSocket.get(AUTHTHREAD);
			Thread authThread = (obj == null) ? null : (Thread) obj;
			if (authThread != null && authThread.isAlive())
				authThread.interrupt();
		});
	}

	@Override
	public void stop(IWebSocket webSocket) {
	}

	@Override
	public void authenticate(IWebSocket webSocket) {
		Object auth = webSocket.get(AUTHENTICATED);
		boolean authenticated = auth != null && (boolean) auth;
		LOG.debug("Authenticate(), authenticated = {}, isClient = {}, isConnected = {}", authenticated,
				webSocket.isClient(), webSocket.isConnected());
		if (authenticated || !webSocket.isClient() || !webSocket.isConnected())
			return;
		// Ask a token
		networkManager.getNetwork().getServerTesterService()
				.asyncAsk(new Callback<ServerTesterDataOutput.ServerTester>() {

					@Override
					public void onResponse(ServerTester response) {
						String id = response.getId();
						webSocket.save(TOKEN, id);
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
						LOG.error("Fatal error while asking for a token: ", t);
					}
				});
	}

	@Override
	public boolean isAuthenticated(IWebSocket webSocket) {
		Object auth = webSocket.get(AUTHENTICATED);
		return auth != null && (boolean) auth;
	}
}
