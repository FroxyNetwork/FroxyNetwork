package com.froxynetwork.froxynetwork.network.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class WebSocketImpl implements IWebSocket {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private String url;
	private WebSocketClient client;

	private List<Consumer<Boolean>> listenerConnection;
	private List<Consumer<Boolean>> listenerDisconnection;
	private List<Runnable> listenerAuthentified;
	private boolean authentified;
	private HashMap<String, List<BiConsumer<String, String>>> listeners;
	private boolean firstConnection = true;

	public WebSocketImpl(String url) throws URISyntaxException {
		this.url = url;
		this.listenerConnection = new ArrayList<>();
		this.listenerDisconnection = new ArrayList<>();
		this.listenerAuthentified = new ArrayList<>();
		this.listeners = new HashMap<>();
		URI uri = new URI(url);
		client = new WebSocketClient(uri) {

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
				String[] split = message.split(" ");
				if (split.length < 2) {
					// Error
					LOG.warn("Ignoring message {}", message);
				}
				String srv = split[0];
				String channel = split[1];
				String msg = "";
				if (split.length > 2)
					msg = String.join(" ", Arrays.copyOfRange(split, 2, split.length));
				if ("MAIN".equalsIgnoreCase(srv) && "connection".equalsIgnoreCase(channel)
						&& "ok".equalsIgnoreCase(msg)) {
					// The app is authentified
					if (!authentified) {
						authentified = true;
						if (listenerAuthentified.size() == 0)
							return;
						for (Runnable run : listenerAuthentified)
							run.run();
						return;
					}
				}
				if (!listeners.containsKey(channel))
					return;
				for (BiConsumer<String, String> consumer : listeners.get(channel))
					consumer.accept(srv, msg);
			}

			@Override
			public void onError(Exception ex) {
				LOG.error("Error: ", ex);
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				LOG.info("WebSocket closed, code = {}, reason = {}, remote = {}", code, reason, remote);
				listeners = new HashMap<>();
				authentified = false;
				for (Consumer<Boolean> r : listenerDisconnection)
					r.accept(remote);
			}
		};
	}

	@Override
	public boolean isConnected() {
		return client.isOpen();
	}

	@Override
	public boolean isAuthentified() {
		return isConnected() && authentified;
	}

	@Override
	public void connect(String id, String clientId, String token) {
		// We don't want to reconnect if we're already connected
		if (isConnected())
			return;
		LOG.info("Connecting ...");
		new Thread(() -> {
			try {
				// We'll try 10 times to connect to the WebSocket
				boolean ok = false;
				for (int i = 1; i <= 10 && !ok; i++) {
					LOG.info("Trying to connect #{}", i);
					ok = client.connectBlocking();
				}
				if (ok) {
					// Connected, sending an authentication request
					sendChannelMessage("auth", id + " " + clientId + " " + token);
				} else {
					// TODO Find something to execute if not connected
					LOG.error("Connection failed !");
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}, "WebSocketImpl-Connect").start();
	}

	@Override
	public void reconnect(String id, String clientId, String token) {
		// Async
		new Thread(() -> {
			LOG.info("Reconnecting ...");
			while (isConnected()) {
				LOG.info("Already connected, disconnecting ...");
				try {
					client.closeBlocking();
					LOG.info("Disconnected");
				} catch (InterruptedException ex) {
				}
			}
			connect(id, clientId, token);
		}, "WebSocketImpl-Reconnect").start();
	}

	@Override
	public void disconnect() {
		client.close();
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
	public void registerWebSocketAuthentified(Runnable run) {
		if (!listenerAuthentified.contains(run))
			listenerAuthentified.add(run);
	}

	@Override
	public void unregisterWebSocketAuthentified(Runnable run) {
		listenerAuthentified.remove(run);
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
		return client.getReadyState();
	}

	@Override
	public void sendChannelMessage(String channel, String message) {
		client.send(channel + " " + message);
	}

	@Override
	public void addChannelListener(String channel, BiConsumer<String, String> listener) {
		List<BiConsumer<String, String>> list = listeners.getOrDefault(channel, new ArrayList<>());
		if (list.contains(listener))
			return;
		list.add(listener);
		listeners.put(channel, list);
	}

	@Override
	public void removeChannelListener(String channel, BiConsumer<String, String> listener) {
		List<BiConsumer<String, String>> list = listeners.getOrDefault(channel, new ArrayList<>());
		list.remove(listener);
		listeners.put(channel, list);
	}

	@Override
	public void removeChannelListener(String channel) {
		listeners.remove(channel);
	}
}
