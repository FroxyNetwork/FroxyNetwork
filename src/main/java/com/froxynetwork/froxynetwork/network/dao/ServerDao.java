package com.froxynetwork.froxynetwork.network.dao;

import com.froxynetwork.froxynetwork.network.output.data.EmptyDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.Server;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerDataOutput.ServerDocker;
import com.froxynetwork.froxynetwork.network.output.data.server.ServerListDataOutput;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
public interface ServerDao {

	/**
	 * Get a server from his id
	 * 
	 * @param id
	 *            The id of the server
	 * @return Specific server if exists
	 */
	@GET("server/{id}")
	public Call<ServerDataOutput> getServer(@Path("id") String id);

	/**
	 * Get all server connected
	 * 
	 * @return a list of all server connected
	 */
	@GET("server")
	public Call<ServerListDataOutput> getServers();

	/**
	 * Register a new server to the REST service
	 * 
	 * @param server
	 *            The server
	 * @return The response
	 */
	@Headers("Content-Type: application/json")
	@POST("server")
	public Call<ServerDataOutput> createServer(@Body Server server);

	/**
	 * Edit an existing server to the REST service
	 * 
	 * @param id
	 *            The id of the server
	 * @param server
	 *            The server
	 * @return The response
	 */
	@Headers("Content-Type: application/json")
	@PUT("server/{id}")
	public Call<ServerDataOutput> updateServer(@Path("id") String id, @Body Server server);

	/**
	 * Set the id of the server containing the docker and the id of the docker
	 * 
	 * @param id           The id of the server
	 * @param serverDocker The id if the server containing the docker and the id of
	 *                     the docker
	 * @return Empty response
	 */
	@Headers("Content-Type: application/json")
	@PUT("server/{id}/id")
	public Call<EmptyDataOutput> updateServerDocker(@Path("id") String id, @Body ServerDocker serverDocker);

	/**
	 * Delete (close) an existing server to the REST service
	 * 
	 * @param id
	 *            The id of the server
	 * @return The response
	 */
	@DELETE("server/{id}")
	public Call<EmptyDataOutput> deleteServer(@Path("id") String id);
}
