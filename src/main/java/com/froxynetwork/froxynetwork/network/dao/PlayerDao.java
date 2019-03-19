package com.froxynetwork.froxynetwork.network.dao;

import com.froxynetwork.froxynetwork.network.output.PlayerDataOutput;
import com.froxynetwork.froxynetwork.network.output.PlayerDataOutput.Player;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
public interface PlayerDao {

	/**
	 * Get a player from his UUID or his name
	 * 
	 * @param uuid
	 *			The uuid of the player, or his name
	 * @return Specific player if exists
	 */
	@GET("player/{uuid}")
	public Call<PlayerDataOutput> getPlayer(@Path("uuid") String uuid);

	/**
	 * Register a new player to the REST service
	 * 
	 * @param player The player
	 * @return The response
	 */
	@Headers("Content-Type: application/json")
	@POST("player")
	public Call<PlayerDataOutput> createPlayer(@Body Player player);
}
