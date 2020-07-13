package com.froxynetwork.froxynetwork.network.websocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

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
 * WebSocket Factory<br />
 * Be the client or the server !
 */
public class WebSocketFactory {

	public static WebSocketClientImpl client(URI uri, WebSocketAuthentication webSocketAuthentication)
			throws URISyntaxException {
		return new WebSocketClientImpl(uri, webSocketAuthentication);
	}

	public static WebSocketServer server(InetSocketAddress address, WebSocketAuthentication webSocketAuthentication) {
		return new WebSocketServer(address, webSocketAuthentication);
	}
}
