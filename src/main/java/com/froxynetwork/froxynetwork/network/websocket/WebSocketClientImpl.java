package com.froxynetwork.froxynetwork.network.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
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
 * The Client implementation for WebSocket<br />
 * An instance of this class represent a link between this client and a server
 */
public class WebSocketClientImpl extends WebSocketClient implements IWebSocket {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private List<Consumer<Boolean>> listenerConnection;
	private List<Consumer<Boolean>> listenerDisconnection;
	private List<Runnable> listenerAuthentified;
	private WebSocketAuthentication authentication;
	private HashMap<String, List<IWebSocketCommander>> listeners;
	// Thread used to connect to WebSocket
	private Thread connectionThread;
	protected boolean firstConnection = true;
	private HashMap<String, Object> saved;

	public WebSocketClientImpl(URI uri, WebSocketAuthentication authentication) throws URISyntaxException {
		super(uri);
		this.authentication = authentication;
		// Listeners
		this.listenerConnection = new ArrayList<>();
		this.listenerDisconnection = new ArrayList<>();
		this.listenerAuthentified = new ArrayList<>();
		this.listeners = new HashMap<>();
		this.saved = new HashMap<>();
		authentication.init(this);
		authentication.registerAuthenticationListener();
	}

	@Override
	public boolean isConnected() {
		return super.isOpen();
	}

	@Override
	public boolean isAuthenticated() {
		return isConnected() && authentication.isAuthenticated();
	}

	/**
	 * Try to connect to WebSocket.<br />
	 * This will create a new Thread and will try to connect 5 times to a WebSocket
	 * server
	 */
	public void tryConnect() {
		// We don't want to reconnect if we're already connected
		if (isConnected())
			return;
		LOG.info("Connecting ...");
		if (connectionThread != null && connectionThread.isAlive())
			connectionThread.interrupt();
		connectionThread = new Thread(() -> {
			try {
				// We'll try to connect 5 times to the WebSocket
				boolean ok = false;
				for (int i = 1; i <= 5 && !ok && !Thread.interrupted(); i++) {
					LOG.info("Trying to connect #{}", i);
					if (i == 1)
						ok = connectBlocking();
					else
						ok = reconnectBlocking();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}, "WebSocketImpl-Connect");
		connectionThread.start();
	}

	/**
	 * Disconnect if already connected and reconnect again.<br />
	 * This method create a new Thread
	 * 
	 * @see #registerWebSocketDisconnection(Consumer)
	 * @see #registerWebSocketConnection(Consumer)
	 * 
	 * @param id       The id of the server
	 * @param clientId The client_id of the server
	 * @param token    The token of the server
	 */
	public void reconnect() {
		// Async
		new Thread(() -> {
			LOG.info("Reconnecting ...");
			while (isConnected()) {
				LOG.info("Already connected, disconnecting ...");
				try {
					closeBlocking();
					LOG.info("Disconnected");
				} catch (InterruptedException ex) {
				}
			}
			connect();
		}, "WebSocketImpl-Reconnect").start();
	}

	/**
	 * Interrupt the connection Thread if running
	 */
	public void stopThread() {
		if (connectionThread != null && connectionThread.isAlive() && !connectionThread.isInterrupted())
			connectionThread.interrupt();
	}

	@Override
	public void disconnect() {
		close();
	}

	@Override
	public void disconnect(int code) {
		super.close(code);
	}

	@Override
	public void disconnect(int code, String message) {
		super.close(code, message);
	}

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void registerWebSocketConnection(Consumer<Boolean> run) {
		if (!listenerConnection.contains(run))
			listenerConnection.add(run);
	}

	@Override
	public void unregisterWebSocketConnection(Consumer<Boolean> run) {
		listenerConnection.remove(run);
	}

	@Override
	public void registerWebSocketAuthentication(Runnable run) {
		if (!listenerAuthentified.contains(run))
			listenerAuthentified.add(run);
	}

	@Override
	public void unregisterWebSocketAuthentication(Runnable run) {
		listenerAuthentified.remove(run);
	}

	@Override
	public void onAuthentication() {
		for (Runnable r : listenerAuthentified)
			r.run();
	}

	@Override
	public void registerWebSocketDisconnection(Consumer<Boolean> run) {
		if (!listenerDisconnection.contains(run))
			listenerDisconnection.add(run);
	}

	@Override
	public void unregisterWebSocketDisconnection(Consumer<Boolean> run) {
		listenerDisconnection.remove(run);
	}

	@Override
	public ReadyState getConnectionState() {
		return super.getReadyState();
	}

	@Override
	public void sendCommand(String channel, String message) {
		super.send(channel + " " + message);
	}

	@Override
	public void registerCommand(IWebSocketCommander commander) {
		List<IWebSocketCommander> commanders = listeners.get(commander.name());
		if (commanders == null)
			// Empty, create a new List
			commanders = new ArrayList<>();
		commanders.add(commander);
		listeners.put(commander.name(), commanders);
	}

	@Override
	public void unregisterCommand(IWebSocketCommander commander) {
		List<IWebSocketCommander> commanders = listeners.get(commander.name());
		if (commanders != null)
			commanders.remove(commander);
		if (commanders.size() == 0)
			// Empty so remove it
			listeners.remove(commander.name());
	}

	@Override
	public void onCommand(String canal, String msg) {
		// Notify listeners
		List<IWebSocketCommander> commanders = listeners.get(canal);
		if (commanders == null)
			return;
		for (IWebSocketCommander commander : commanders)
			commander.onReceive(msg);
	}

	/**
	 * Try to authenticate this client
	 */
	public void authenticate() {
		// Test if it's not already authenticated
		if (isAuthenticated())
			return;
		authentication.authenticate();
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		LOG.info("Connected to the WebSocket");
		boolean copy = firstConnection;
		firstConnection = false;
		for (Consumer<Boolean> c : listenerConnection)
			c.accept(copy);
	}

	@Override
	public void onMessage(String message) {
		if (message == null || "".equalsIgnoreCase(message.trim()))
			return;
		LOG.debug("Got message {}", message);

		// Get index of first space
		int index = message.indexOf(' ');
		String canal = (index == -1) ? message : message.substring(0, index);
		String msg = (index == -1) ? "" : message.substring(index + 1);

		onCommand(canal, msg);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		LOG.info("WebSocker closed, code = {}, reason = {}, remote = {}", code, reason, remote);
		for (Consumer<Boolean> r : listenerDisconnection)
			r.accept(remote);
	}

	@Override
	public void onError(Exception ex) {
		LOG.error("Error: ", ex);
	}

	@Override
	public void save(String key, Object obj) {
		saved.put(key, obj);
	}

	@Override
	public Object get(String key) {
		return saved.get(key);
	}
}
