package com.froxynetwork.froxynetwork;

import com.froxynetwork.froxynetwork.network.NetworkManager;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.Server;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.ServerStatus;
import com.froxynetwork.froxynetwork.network.service.ServiceManager;

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

	public App() throws Exception {
		// TODO URL in config file
		String clientId = "WEBSOCKET_045cfff18fe0ab8393178e7b7826f227";
		String clientSecret = "SECRET_ecfdc21a8d5022e2db64b1315b087aaf";
		NetworkManager nm = new NetworkManager("http://localhost/", clientId, clientSecret);
		ServiceManager sm = nm.getNetworkServiceManager();
		// Add server
		sm.getServerService().asyncAddServer("koth_1", 20001, new Callback<Server>() {
			@Override
			public void onResponse(Server server) {
				System.out.println(server);
				server.setStatus(ServerStatus.STARTED.name());
				try {
					System.out.println("Editing server");
					Server editedServer = sm.getServerService().syncEditServer(server);
					System.out.println("Deleting server");
					sm.getServerService().syncDeleteServer(editedServer.getId());
					sm.getServerService().asyncGetServer(editedServer.getId(), new Callback<Server>() {

						@Override
						public void onResponse(Server response) {
							System.out.println(response);
							// Shutdown at the end
							nm.shutdown();
						};

						@Override
						public void onFailure(RestException ex) {
							ex.printStackTrace();
						}

						@Override
						public void onFatalFailure(Throwable t) {
							t.printStackTrace();
						}
					});
				} catch (RestException ex) {
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};

			@Override
			public void onFailure(RestException ex) {
				ex.printStackTrace();
			}

			@Override
			public void onFatalFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new App();
	}
}
