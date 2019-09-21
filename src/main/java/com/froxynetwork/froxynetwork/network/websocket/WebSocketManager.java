package com.froxynetwork.froxynetwork.network.websocket;

import java.net.URISyntaxException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.java_websocket.enums.ReadyState;

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
/**
 * This class implements all method to interact with the WebSocket Server.
 * <br />
 * To create your custom WebSocket, just create a class that implements
 * {@link IWebSocket}
 */
public class WebSocketManager implements IWebSocket {
	private IWebSocket webSocket;
	private String url;

	public WebSocketManager(String url) throws URISyntaxException {
		webSocket = new WebSocketImpl(url);
		this.url = url;
	}

	public WebSocketManager(IWebSocket webSocket, String url) {
		this.webSocket = webSocket;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public void connect(String id, String clientId, String token) {
		webSocket.connect(id, clientId, token);
	}

	@Override
	public boolean isConnected() {
		return webSocket.isConnected();
	}

	@Override
	public void reconnect(String id, String clientId, String token) {
		webSocket.reconnect(id, clientId, token);
	}

	@Override
	public void disconnect() {
		webSocket.disconnect();
	}

	@Override
	public void registerWebSocketConnection(Consumer<Boolean> run) {
		webSocket.registerWebSocketConnection(run);
	}

	@Override
	public void unregisterWebSocketConnection(Consumer<Boolean> run) {
		webSocket.unregisterWebSocketConnection(run);
	}

	@Override
	public void registerWebSocketAuthentified(Runnable run) {
		webSocket.registerWebSocketAuthentified(run);
	}

	@Override
	public void unregisterWebSocketAuthentified(Runnable run) {
		webSocket.unregisterWebSocketAuthentified(run);
	}

	@Override
	public void registerWebSocketDisconnection(Consumer<Boolean> run) {
		webSocket.registerWebSocketDisconnection(run);
	}

	@Override
	public void unregisterWebSocketDisconnection(Consumer<Boolean> run) {
		webSocket.unregisterWebSocketDisconnection(run);
	}

	@Override
	public ReadyState getConnectionState() {
		return webSocket.getConnectionState();
	}

	@Override
	public void sendChannelMessage(String channel, String message) {
		webSocket.sendChannelMessage(channel, message);
	}

	@Override
	public void addChannelListener(String channel, BiConsumer<String, String> listener) {
		webSocket.addChannelListener(channel, listener);
	}

	@Override
	public void removeChannelListener(String channel, BiConsumer<String, String> listener) {
		webSocket.removeChannelListener(channel, listener);
	}

	@Override
	public void removeChannelListener(String channel) {
		webSocket.removeChannelListener(channel);
	}
}
