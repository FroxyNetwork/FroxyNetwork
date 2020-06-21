package com.froxynetwork.froxynetwork.network.websocket;

import java.util.List;
import java.util.function.Consumer;

import org.java_websocket.enums.ReadyState;

import com.froxynetwork.froxynetwork.network.websocket.modules.WebSocketModule;

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
 * Represent a connection between this client and another client via
 * WebSocket<br />
 * This could be the client as well as the server
 */
public interface IWebSocket {

	/**
	 * @return true if the client is connected
	 */
	public boolean isConnected();

	/**
	 * @return true if the client is connected and authenticated
	 */
	public boolean isAuthenticated();

	/**
	 * Disconnect if already connected
	 */
	public void disconnect();

	/**
	 * Disconnect if already connected
	 * 
	 * @param code The disconnection code
	 */
	public void disconnect(int code);

	/**
	 * Disconnect if already connected
	 * 
	 * @param code    The disconnection code
	 * @param message The disconnection message
	 */
	public void disconnect(int code, String message);

	/**
	 * @return true if this WebSocket is the client
	 */
	public boolean isClient();

	/**
	 * Register an event that is called when the client is connected to the
	 * WebSocket<br />
	 * This doesn't work if isClient() is false
	 * 
	 * @param run The action to execute. Parameter depends on the first connection
	 *            or on a reconnection. True means it's the first time the app is
	 *            connected.
	 */
	public void registerWebSocketConnection(Consumer<Boolean> run);

	/**
	 * Unregister the registered event<br />
	 * This doesn't work if isClient() is false
	 * 
	 * @param run
	 */
	public void unregisterWebSocketConnection(Consumer<Boolean> run);

	/**
	 * Register an event that is called when the app is connected and authenticated
	 * to the WebSocket
	 * 
	 * @param run The action to execute.
	 */
	public void registerWebSocketAuthentication(Runnable run);

	/**
	 * Unregister the registered event
	 * 
	 * @param run
	 */
	public void unregisterWebSocketAuthentication(Runnable run);

	/**
	 * Called when this WebSocked is authenticated
	 */
	public void onAuthentication();

	/**
	 * Register an event that is called when the app is connected and authenticated
	 * to the WebSocket
	 * 
	 * @param run The action to execute. Parameter depends on the closing of the
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
	 * Send a message via WebSocket and with specific channel
	 * 
	 * @param channel The channel to use
	 * @param msg     The message
	 */
	public void sendCommand(String channel, String msg);

	/**
	 * Register a new handler for a specific command received via WebSocket
	 * 
	 * @param commander The commander
	 */
	public void registerCommand(IWebSocketCommander commander);

	/**
	 * Unregister a registered command handler
	 * 
	 * @param commander The commander
	 */
	public void unregisterCommand(IWebSocketCommander commander);

	/**
	 * Called when this WebSocket got a command
	 * 
	 * @param channel The channel
	 * @param msg     The message
	 */
	public void onCommand(String channel, String msg);

	/**
	 * Save an object to this WebSocket
	 * 
	 * @param key The key
	 * @param obj The object
	 */
	public void save(String key, Object obj);

	/**
	 * @param key The key
	 * @return The object associated with this key
	 */
	public Object get(String key);

	/**
	 * Add specific module
	 * 
	 * @param module The module
	 */
	public void addModule(WebSocketModule module);

	/**
	 * Remove specific module
	 * 
	 * @param module The module
	 */
	public void removeModule(WebSocketModule module);

	/**
	 * @return All modules
	 */
	public List<WebSocketModule> getModules();

	/**
	 * Disconnect this webSocket and unload modules
	 */
	public void closeAll();
}
