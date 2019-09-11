package com.froxynetwork.froxynetwork.network.websocket;

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
public interface IWebSocket {

	/**
	 * @return true if the app is connected with the WebSocket server
	 */
	public boolean isConnected();

	/**
	 * Connect to the server
	 */
	public void connect();

	/**
	 * Disconnect if already connected and reconnect again
	 */
	public void reconnect();

	/**
	 * Disconnect if already connected
	 */
	public void disconnect();

	/**
	 * Register an event that is called when the app is connected to the WebSocket
	 * 
	 * @param run
	 *            The action to execute. Parameter depends on the first connection
	 *            or on a reconnection. True means it's the first time the app is
	 *            connected.
	 */
	public void registerWebSocketConnection(Consumer<Boolean> run);

	/**
	 * Unregister the registered event
	 * 
	 * @param run
	 */
	public void unregisterWebSocketConnection(Consumer<Boolean> run);

	/**
	 * Register an event that is called when the app is disconnected to the
	 * WebSocket
	 * 
	 * @param run
	 *            The action to execute. Parameter depends on the closing of the
	 *            connection that was initiated or not by the WebSocket server
	 */
	public void registerWebSocketDisconnection(Consumer<Boolean> run);

	/**
	 * Unregister the registered event
	 * 
	 * @param run
	 */
	public void unregisterWebSocketDisconnection(Consumer<Boolean> run);

	/**
	 * @return The current state of the connection with the WebSocket
	 */
	public ReadyState getConnectionState();

	/**
	 * Send a message to specific channel
	 * 
	 * @param channel
	 *            The channel
	 * @param message
	 *            The message
	 */
	public void sendChannelMessage(String channel, String message);

	/**
	 * Add listener when incoming message
	 * 
	 * @param channel
	 *            The channel
	 * @param listener
	 *            The listener
	 */
	public void addChannelListener(String channel, Consumer<String> listener);

	/**
	 * Remove listener when incoming message
	 * 
	 * @param channel
	 *            The channel
	 * @param listener
	 *            The listener to remove
	 */
	public void removeChannelListener(String channel, Consumer<String> listener);

	/**
	 * Remove all listeners for specific channel when incoming message
	 * 
	 * @param channel
	 *            The channel
	 */
	public void removeChannelListener(String channel);
}
