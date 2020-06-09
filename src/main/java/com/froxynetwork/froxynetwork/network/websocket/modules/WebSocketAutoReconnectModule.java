package com.froxynetwork.froxynetwork.network.websocket.modules;

import com.froxynetwork.froxynetwork.network.websocket.IWebSocket;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketClientImpl;

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
public class WebSocketAutoReconnectModule implements WebSocketModule {
	private IWebSocket webSocket;
	private Thread thread;
	private int time;

	public WebSocketAutoReconnectModule(int time) {
		this.time = time;
	}

	@Override
	public String getName() {
		return "AutoReconnect";
	}

	@Override
	public void init(IWebSocket webSocket) {
		this.webSocket = webSocket;
	}

	@Override
	public void load() {
		if (!webSocket.isClient() || !(webSocket instanceof WebSocketClientImpl))
			return;
		WebSocketClientImpl wssi = (WebSocketClientImpl) webSocket;
		thread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException ex) {
					break;
				}
				if (!webSocket.isConnected()) {
					// Not connected
					wssi.reconnect();
				}
			}
		});
		thread.start();
	}

	@Override
	public void unload() {
		if (!webSocket.isClient())
			return;
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}
}
