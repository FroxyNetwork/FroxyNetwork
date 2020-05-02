package com.froxynetwork.froxynetwork.network.websocket;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.websocket.auth.WebSocketAuthentication;

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
 * The Server implementation for WebSocket<br />
 * This class will listen for incoming WebSocket connection<br />
 * If you want to create your own WebSocketFactory, you MUST use
 * {@link WebSocketServerImpl} or a class that extends
 * {@link WebSocketServerImpl}
 */
public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private List<WebSocketServerImpl> servers;
	private List<Consumer<WebSocketServerImpl>> listenerConnection;
	private List<Consumer<WebSocketServerImpl>> listenerDisconnection;

	public WebSocketServer(InetSocketAddress address, WebSocketAuthentication webSocketAuthentication) {
		super(address);
		setWebSocketFactory(new CustomWebSocketServerFactory(webSocketAuthentication));
		this.servers = new ArrayList<>();
		this.listenerConnection = new ArrayList<>();
		this.listenerDisconnection = new ArrayList<>();
		registerAuthenticationTimeout();
	}

	/**
	 * Register WebSocket Authentication Timeout (Default is 3 seconds)
	 */
	protected void registerAuthenticationTimeout() {
		registerWebSocketConnection(webSocket -> {
			new Thread(() -> {
				try {
					Thread.sleep(3000);
					if (webSocket.isConnected() && !webSocket.isAuthenticated()) {
						// Connected and not authenticated, disconnecting
						webSocket.disconnect(CloseFrame.NORMAL, "Authentication Timeout");
					}
				} catch (InterruptedException ex) {

				}
			}).start();
		});
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		if (!(conn instanceof WebSocketServerImpl)) {
			// Not a WebSocketServerImpl
			LOG.info("A server from {} has connected but client is not a WebSocketServerImpl class ! Closing it ...",
					conn.getRemoteSocketAddress().getAddress().getHostAddress());
			conn.close();
			return;
		}
		LOG.info("A server from {} is connected",
				conn.getRemoteSocketAddress().getAddress().getHostAddress());
		WebSocketServerImpl wssi = (WebSocketServerImpl) conn;
		servers.add(wssi);
		for (Consumer<WebSocketServerImpl> c : listenerConnection)
			c.accept(wssi);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		if (!(conn instanceof WebSocketServerImpl)) {
			// Not a WebSocketServerImpl
			LOG.info("A server from {} has disconnected but client is not a WebSocketServerImpl class !",
					conn.getRemoteSocketAddress().getAddress().getHostAddress());
			return;
		}
		LOG.info("A server from {} has disconnected",
				conn.getRemoteSocketAddress().getAddress().getHostAddress());
		WebSocketServerImpl wssi = (WebSocketServerImpl) conn;
		servers.remove(wssi);
		for (Consumer<WebSocketServerImpl> c : listenerDisconnection)
			c.accept(wssi);
		wssi.onClose(code, reason, remote);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if (!(conn instanceof WebSocketServerImpl)) {
			// Not a WebSocketServerImpl
			LOG.info(
					"A server from {} has sent a message but client is not a WebSocketServerImpl class ! Closing it ...",
					conn.getRemoteSocketAddress().getAddress().getHostAddress());
			conn.close();
			return;
		}
		WebSocketServerImpl wssi = (WebSocketServerImpl) conn;
		wssi.onMessage(message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		if (!(conn instanceof WebSocketServerImpl)) {
			// Not a WebSocketServerImpl
			LOG.warn("A server from {} got an error but client is not a WebSocketServerImpl class ! Closing it ...",
					conn.getRemoteSocketAddress().getAddress().getHostAddress());
			LOG.warn("", ex);
			conn.close();
			return;
		}
		WebSocketServerImpl wssi = (WebSocketServerImpl) conn;
		wssi.onError(ex);
	}

	@Override
	public void onStart() {
		LOG.info("Server started");
	}

	public void registerWebSocketConnection(Consumer<WebSocketServerImpl> run) {
		if (!listenerConnection.contains(run))
			listenerConnection.add(run);
	}

	public void unregisterWebSocketConnection(Consumer<WebSocketServerImpl> run) {
		listenerConnection.remove(run);
	}

	public void registerWebSocketDisconnection(Consumer<WebSocketServerImpl> run) {
		if (!listenerDisconnection.contains(run))
			listenerDisconnection.add(run);
	}

	public void unregisterWebSocketDisconnection(Consumer<WebSocketServerImpl> run) {
		listenerDisconnection.remove(run);
	}
}
