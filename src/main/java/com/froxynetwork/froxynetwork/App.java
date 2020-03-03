package com.froxynetwork.froxynetwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.ServerTesterDataOutput.ServerTester;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;
import com.froxynetwork.froxynetwork.network.websocket.CustomInteraction;
import com.froxynetwork.froxynetwork.network.websocket.WebSocketManager;

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
public class App {
	private Logger LOG = LoggerFactory.getLogger(getClass());

	private NetworkManager nm;
	private WebSocketManager wsm;
	private CustomInteraction customInteraction;

	public App() throws Exception {
		// This is just for TESTING

		// TODO URL in config file
		String url = "http://localhost/";
		String clientId = "WEBSOCKET_5538f57946961ad1c06064b89112d74b";
		String clientSecret = "SECRET_1b49eda57b597a055973dd6f87ac3983";
		initCustomInteraction();
		wsm = new WebSocketManager("ws://localhost", customInteraction);
		nm = new NetworkManager(url, clientId, clientSecret);
		ServiceManager sm = nm.getNetwork();

		String idTester = "CLIENT_KOTH_4c72fb1d585c25dfcaf5fdbb218eed87";
		String clientIdTester = "";
		String tokenTester = "def019ee9f4af62223d9f81ae86e5f8a6c78431e";

		sm.getServerTesterService().asyncCheckServer(idTester, clientIdTester, tokenTester,
				new Callback<ServerTesterDataOutput.ServerTester>() {

					@Override
					public void onResponse(ServerTester response) {
						System.out.println("Isok ? " + response.isOk());
						// Shutdown at the end
						stop();
					}

					@Override
					public void onFailure(RestException ex) {
						ex.printStackTrace();
						// Shutdown at the end
						stop();
					}

					@Override
					public void onFatalFailure(Throwable t) {
						t.printStackTrace();
						// Shutdown at the end
						stop();
					}
				});
	}

	private void stop() {
		if (nm != null)
			nm.shutdown();
		if (wsm != null)
			wsm.disconnect();
	}

	private void initCustomInteraction() {
		customInteraction = new CustomInteraction() {

			@Override
			public void stop(String msg) {
				// Nothing to do here
				LOG.info("stop({})", msg);
			}
		};
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}
