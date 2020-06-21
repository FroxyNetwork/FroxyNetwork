package com.froxynetwork.froxynetwork.network.websocket.auth;

import com.froxynetwork.froxynetwork.network.websocket.IWebSocket;

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
 * Used to authenticate WebSocket client with WebSocket Server
 */
public interface WebSocketAuthentication {

	/**
	 * Initialize this WebSockerAuthentication
	 * 
	 * @param webSocket The WebSocket associated to this Authentication
	 */
	public void init(IWebSocket webSocket);

	/**
	 * Used to register the authentication listener<br />
	 * This method is called after {@link #init(IWebSocket)} and will register
	 * events that will result in a call to {@link #authentificate()}<br />
	 * Example: Register connection event and call {@linkplain #authentificate()}
	 */
	public void registerAuthenticationListener(IWebSocket webSocket);

	/**
	 * Authentificate this WebSocket
	 * 
	 * @param webSocket The WebSocket
	 */
	public void authenticate(IWebSocket webSocket);

	/**
	 * @return true if this WebSocket is authentificated
	 */
	public boolean isAuthenticated(IWebSocket webSocket);

	/**
	 * Called when this WebSocket is stopped and will not be reused
	 */
	public default void stop(IWebSocket webSocket) {
		// Nothing to do
	}
}
