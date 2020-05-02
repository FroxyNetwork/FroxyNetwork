package com.froxynetwork.froxynetwork.network.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.websocket.auth.WebSocketAuthentication;
import com.froxynetwork.froxynetwork.network.websocket.modules.WebSocketModule;

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
 * An instance of this class represent a link between this server and a client
 */
public class WebSocketServerImpl extends WebSocketImpl implements IWebSocket {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private List<Consumer<Boolean>> listenerDisconnection;
	private List<Runnable> listenerAuthentication;
	private WebSocketAuthentication authentication;
	private HashMap<String, List<IWebSocketCommander>> listeners;
	private HashMap<String, Object> saved;
	private List<WebSocketModule> modules;

	public WebSocketServerImpl(WebSocketListener listener, List<Draft> drafts, WebSocketAuthentication authentication) {
		super(listener, drafts);
		this.authentication = authentication;
		// Listeners
		this.listenerDisconnection = new ArrayList<>();
		this.listenerAuthentication = new ArrayList<>();
		this.listeners = new HashMap<>();
		this.saved = new HashMap<>();
		this.modules = new ArrayList<>();
		authentication.init(this);
		authentication.registerAuthenticationListener();
	}

	public WebSocketServerImpl(WebSocketListener listener, Draft d, WebSocketAuthentication authentication) {
		super(listener, d);
		this.authentication = authentication;
		authentication.init(this);
		// Listeners
		this.listenerDisconnection = new ArrayList<>();
		this.listenerAuthentication = new ArrayList<>();
		this.listeners = new HashMap<>();
	}

	@Override
	public boolean isConnected() {
		return super.isOpen();
	}

	@Override
	public boolean isAuthenticated() {
		return isConnected() && authentication.isAuthenticated();
	}

	@Override
	public void disconnect() {
		super.close();
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
		return false;
	}

	@Override
	public void registerWebSocketConnection(Consumer<Boolean> run) {
		// Nothing to do here
	}

	@Override
	public void unregisterWebSocketConnection(Consumer<Boolean> run) {
		// Nothing to do here
	}

	@Override
	public void registerWebSocketAuthentication(Runnable run) {
		if (!listenerAuthentication.contains(run))
			listenerAuthentication.add(run);
	}

	@Override
	public void unregisterWebSocketAuthentication(Runnable run) {
		listenerAuthentication.remove(run);
	}

	@Override
	public void onAuthentication() {
		for (Runnable r : listenerAuthentication)
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
	public void sendCommand(String channel, String msg) {
		super.send(channel + " " + msg);
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

	public void onClose(int code, String reason, boolean remote) {
		LOG.info("WebSocker closed, code = {}, reason = {}, remote = {}", code, reason, remote);
		for (Consumer<Boolean> r : listenerDisconnection)
			r.accept(remote);
	}

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

	@Override
	public void addModule(WebSocketModule module) {
		modules.add(module);
		module.init(this);
	}

	@Override
	public void removeModule(WebSocketModule module) {
		modules.remove(module);
		module.unload();
	}

	public List<WebSocketModule> getModules() {
		return new ArrayList<>(modules);
	}
}
