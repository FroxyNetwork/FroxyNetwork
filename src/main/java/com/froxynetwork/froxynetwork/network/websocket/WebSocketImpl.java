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

import com.froxynetwork.froxynetwork.network.websocket.IWebSocketCommander.From;

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
	private URI uri;
	private CustomInteraction customInteraction;
	private WebSocketClient client;

	private List<Consumer<Boolean>> listenerConnection;
	private List<Consumer<Boolean>> listenerDisconnection;
	private List<Runnable> listenerAuthentified;
	private boolean authentified;
	private HashMap<String, List<IWebSocketCommander>> listeners;
	private boolean firstConnection = true;

	public WebSocketImpl(String url, CustomInteraction customInteraction) throws URISyntaxException {
		this.url = url;
		this.customInteraction = customInteraction;
		this.listenerConnection = new ArrayList<>();
		this.listenerDisconnection = new ArrayList<>();
		this.listenerAuthentified = new ArrayList<>();
		this.listeners = new HashMap<>();
		uri = new URI(url);
	}

	private WebSocketClient client() {
		return new WebSocketClient(uri) {

			@Override
			public void onOpen(ServerHandshake handshakedata) {
				LOG.info("Connected to the WebSocket");
				boolean copy = firstConnection;
				firstConnection = false;
				registerInternalCommands();
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
				// If there is only the server, we'll ignore the message
				if (index == -1) {
					// Error
					LOG.warn("Ignoring message {}", message);
				}
				// If -1 set all the message here, otherwise set the first word
				String srv = message.substring(0, index == -1 ? message.length() : index);
				// Get the index of second space
				int secondSpaceIndex = message.indexOf(' ', index + 1);
				// If index is -1 set empty string otherwise if secondSpaceIndex is -1 set the
				// rest of the message otherwise set the second word
				String channel = index == -1 ? ""
						: (secondSpaceIndex == -1 ? message.substring(index + 1)
								: message.substring(index + 1, secondSpaceIndex));
				String msg = index == -1 || secondSpaceIndex == -1 ? "" : message.substring(secondSpaceIndex + 1);
				if (!listeners.containsKey(channel))
					return;
				for (IWebSocketCommander commander : listeners.get(channel)) {
					// Commander only accept from WebSocket
					if (commander.from() == From.WEBSOCKET && !"MAIN".equalsIgnoreCase(srv))
						continue;
					commander.onReceive(srv, msg);
				}
			}

			@Override
			public void onError(Exception ex) {
				LOG.error("Error: ", ex);
			}

			@Override
			public void onClose(int code, String reason, boolean remote) {
				LOG.info("WebSocket closed, code = {}, reason = {}, remote = {}", code, reason, remote);
				// Clear listeners
				listeners = new HashMap<>();
				authentified = false;
				for (Consumer<Boolean> r : listenerDisconnection)
					r.accept(remote);
			}
		};
	}

	@Override
	public boolean isConnected() {
		return client != null && client.isOpen();
	}

	@Override
	public boolean isAuthentified() {
		return isConnected() && authentified;
	}

	private Thread t;

	@Override
	public void connect(String id, String clientId, String token) {
		// We don't want to reconnect if we're already connected
		if (isConnected())
			return;
		LOG.info("Connecting ...");
		if (t != null && t.isAlive())
			t.interrupt();
		t = new Thread(() -> {
			try {
				// We'll try 10 times to connect to the WebSocket
				boolean ok = false;
				client = client();
				for (int i = 1; i <= 10 && !ok; i++) {
					LOG.info("Trying to connect #{}", i);
					if (i == 1)
						ok = client.connectBlocking();
					else
						ok = client.reconnectBlocking();
				}
				if (ok) {
					// Connected, sending an authentication request
					String identifiers = id;
					if (clientId != null)
						identifiers += " " + clientId;
					sendChannelMessage("auth", identifiers + " " + token);
				} else {
					// TODO Find something to execute if not connected
					LOG.error("Connection failed !");
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}, "WebSocketImpl-Connect");
		t.start();
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
	public void removeChannelListener(String channel) {
		listeners.remove(channel);
	}

	@Override
	public void registerCommand(IWebSocketCommander commander) {
		List<IWebSocketCommander> commanders = listeners.get(commander.name());
		if (commanders == null) {
			// Empty list
			commanders = new ArrayList<>();
		}
		commanders.add(commander);
		listeners.put(commander.name(), commanders);
		if (isAuthentified()) {
			// If authentified, send a request to the WebSocket
			sendChannelMessage("register", commander.name());
		}
	}

	@Override
	public void unregisterCommand(IWebSocketCommander commander) {
		List<IWebSocketCommander> commanders = listeners.get(commander.name());
		if (commanders != null)
			commanders.remove(commander);
		listeners.put(commander.name(), commanders);
		if (isAuthentified()) {
			// If authentified, send a request to the WebSocket
			sendChannelMessage("unregister", commander.name());
		}
	}

	/**
	 * Create all internal handler for specific commands
	 */
	private void registerInternalCommands() {
		// Connection ok
		registerCommand(new IWebSocketCommander() {

			@Override
			public String name() {
				return "connection";
			}

			@Override
			public String description() {
				return "Handler for accepted connection from WebSocket";
			}

			@Override
			public From from() {
				return From.WEBSOCKET;
			}

			@Override
			public void onReceive(String from, String message) {
				if ("ok".equalsIgnoreCase(message)) {
					// The app is authentified
					if (!authentified) {
						authentified = true;
						if (listenerAuthentified.size() == 0)
							return;
						for (Runnable run : listenerAuthentified)
							run.run();
					}

				}
			}
		});
		// Stop request
		registerCommand(new IWebSocketCommander() {

			@Override
			public String name() {
				return "stop";
			}

			@Override
			public String description() {
				return "Handler for stopping the client";
			}

			@Override
			public From from() {
				return From.WEBSOCKET;
			}

			@Override
			public void onReceive(String from, String message) {
				customInteraction.stop(message);
			}
		});
	}
}
