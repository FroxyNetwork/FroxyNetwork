package com.froxynetwork.froxynetwork.network.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.froxynetwork.froxynetwork.network.dao.ServerDao;
import com.froxynetwork.froxynetwork.network.output.Callback;
import com.froxynetwork.froxynetwork.network.output.RestException;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput.Empty;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.Server;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerListDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerListDataOutput.ServerList;

import retrofit2.Response;
import retrofit2.Retrofit;

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
public class ServerService {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private ServerDao serverDao;

	public ServerService(Retrofit retrofit) {
		serverDao = retrofit.create(ServerDao.class);
	}

	public void asyncGetServer(int id, Callback<Server> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncGetServer: Retrieving server {}", id);
		serverDao.getServer(id).enqueue(ServiceHelper.callback(callback, ServerDataOutput.class));
	}

	public Server syncGetServer(int id) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncGetServer: Retrieving server {}", id);
		Response<ServerDataOutput> response = serverDao.getServer(id).execute();
		ServerDataOutput body = ServiceHelper.response(response, ServerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncGetServers(Callback<ServerList> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncGetServers: Retrieving all servers");
		serverDao.getServers().enqueue(ServiceHelper.callback(callback, ServerListDataOutput.class));
	}

	public ServerList syncGetServers() throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncGetServers: Retrieving all servers");
		Response<ServerListDataOutput> response = serverDao.getServers().execute();
		ServerListDataOutput body = ServiceHelper.response(response, ServerListDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncAddServer(String name, int port, Callback<Server> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncAddServer: Adding new server, name = {}, port = {}", name, port);
		serverDao.createServer(new Server(0, name, port, null, null))
				.enqueue(ServiceHelper.callback(callback, ServerDataOutput.class));
	}

	public Server syncAddServer(String name, int port) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncAddServer: Adding new server, name = {}, port = {}", name, port);
		Response<ServerDataOutput> response = serverDao.createServer(new Server(0, name, port, null, null)).execute();
		ServerDataOutput body = ServiceHelper.response(response, ServerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncEditServer(Server server, Callback<Server> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncEditServer: Editing Server {} ({}), new status = {}", server.getName(), server.getId(),
					server.getStatus());
		serverDao.updateServer(server.getId(), server)
				.enqueue(ServiceHelper.callback(callback, ServerDataOutput.class));
	}

	public Server syncEditServer(Server server) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncEditServer: Editing Server {} ({}), new status = {}", server.getName(), server.getId(),
					server.getStatus());
		Response<ServerDataOutput> response = serverDao.updateServer(server.getId(), server).execute();
		ServerDataOutput body = ServiceHelper.response(response, ServerDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}

	public void asyncDeleteServer(int id, Callback<Empty> callback) {
		if (LOG.isDebugEnabled())
			LOG.debug("asyncDeleteServer: Deleting Server {}", id);
		serverDao.deleteServer(id).enqueue(ServiceHelper.callback(callback, EmptyDataOutput.class));
	}

	public Empty syncDeleteServer(int id) throws RestException, Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("syncDeleteServer: Deleting Server {}", id);
		Response<EmptyDataOutput> response = serverDao.deleteServer(id).execute();
		EmptyDataOutput body = ServiceHelper.response(response, EmptyDataOutput.class);
		if (body.isError())
			throw new RestException(body);
		else
			return body.getData();
	}
}
